import logic.*; //<>//
import graphic.GUIController;
import static java.awt.event.KeyEvent.*;

static UserInterface2D ChessController;
static ArrayList<Particle>[][] particles;
static Pos selection;
static int menuOpen;
static String[] menuData;
static boolean[][] highlighted;
static int[][] threadsToKing;
static boolean gameOver;
static boolean remisClaimable;
static int sqWidth;
static int sqHeight;
static int lastWidth;
static int lastHeight;
static boolean advancedGraphics = true;        //use particles instead of images to display pieces
static boolean debugging = false;              //in combination with advancedGraphics == true shows alpha values of pieces (particle-boundaries)
static PImage pieceOverlay;
static int maxNumParticles = 300;              //number of particles per piece
static int FPS = 30;                           //targeted frames per second

static PImage kingBlack;
static PImage queenBlack;
static PImage bishopBlack;
static PImage knightBlack;
static PImage rookBlack;
static PImage pawnBlack;
static PImage kingWhite;
static PImage queenWhite;
static PImage bishopWhite;
static PImage knightWhite;
static PImage rookWhite;
static PImage pawnWhite;
static PImage undoAction;
static PImage redoAction;
static PImage closeAction;
static PImage remisAction;
static PImage newGameAction;

void setup() {
  
  size(1500, 1500, JAVA2D);
  frameRate(FPS);

  noSmooth();
  noStroke();
  
  sqWidth = width / 9;
  sqHeight = height / 9;
  lastWidth = width - (sqWidth * 9) + (sqWidth / 2);
  lastHeight = height - (sqHeight * 9) + (sqHeight / 2);
  
  //loading images  
  kingBlack = loadImage("./Icons240p/Chess_tile_kd.svg.png");
  queenBlack = loadImage("./Icons240p/Chess_tile_qd.svg.png");
  bishopBlack = loadImage("./Icons240p/Chess_tile_bd.svg.png");
  knightBlack = loadImage("./Icons240p/Chess_tile_nd.svg.png");
  rookBlack = loadImage("./Icons240p/Chess_tile_rd.svg.png");
  pawnBlack = loadImage("./Icons240p/Chess_tile_pd.svg.png");
  kingWhite = loadImage("./Icons240p/Chess_tile_kl.svg.png");
  queenWhite = loadImage("./Icons240p/Chess_tile_ql.svg.png");
  bishopWhite = loadImage("./Icons240p/Chess_tile_bl.svg.png");
  knightWhite = loadImage("./Icons240p/Chess_tile_nl.svg.png");
  rookWhite = loadImage("./Icons240p/Chess_tile_rl.svg.png");
  pawnWhite = loadImage("./Icons240p/Chess_tile_pl.svg.png");
  undoAction = loadImage("./Icons240p/Action_undo.svg.png");
  redoAction = loadImage("./Icons240p/Action_redo.svg.png");
  closeAction = loadImage("./Icons240p/Action_close.png");
  remisAction = loadImage("./Icons240p/Action_remis.png");
  newGameAction = loadImage("./Icons240p/Action_new2.png");
  kingBlack.resize(sqWidth, sqHeight);
  queenBlack.resize(sqWidth, sqHeight);
  bishopBlack.resize(sqWidth, sqHeight);
  knightBlack.resize(sqWidth, sqHeight);
  rookBlack.resize(sqWidth, sqHeight);
  pawnBlack.resize(sqWidth, sqHeight);
  kingWhite.resize(sqWidth, sqHeight);
  queenWhite.resize(sqWidth, sqHeight);
  bishopWhite.resize(sqWidth, sqHeight);
  knightWhite.resize(sqWidth, sqHeight);
  rookWhite.resize(sqWidth, sqHeight);
  pawnWhite.resize(sqWidth, sqHeight);
  undoAction.resize(sqWidth, sqHeight);
  redoAction.resize(sqWidth, sqHeight);
  closeAction.resize(sqWidth, sqHeight);
  remisAction.resize(sqWidth, sqHeight);
  newGameAction.resize(sqWidth, sqHeight);
  
  particles = new ArrayList[8][8];
  for(int x = 0; x < 8; x++) {
    for(int y = 0; y < 8; y++) {
      particles[x][y] = new ArrayList<Particle>();
    }
  }
  
  menuData = new String[0];
  
  ChessController = new UserInterface2D();
  ChessController.run();
  
}

void draw() {
  try {
    ChessController.drawChessboard();
    ChessController.drawPieces();
    ChessController.drawUI();
  }catch(IllegalStateException e) {
    e.printStackTrace();
  }
}

void mouseReleased() {
  if(mouseX < sqWidth / 2 || mouseX >= width - lastWidth || mouseY < sqHeight / 2 || mouseY >= height - lastHeight) {  //margin
    if(menuOpen != 0) return;
    
    float radius = ((sqWidth <= sqHeight) ? sqWidth / 7 * 3 : sqHeight / 7 * 3) / 2;
    
    //sidebar buttons
    if(PVector.dist(new PVector(width - (lastWidth / 2), sqHeight * 0.5), new PVector(mouseX, mouseY)) <= radius) menuOpen = 3;
    else if(PVector.dist(new PVector(width - (lastWidth / 2), sqHeight * 1.0), new PVector(mouseX, mouseY)) <= radius) ChessController.triggerAction("undo");
    else if(PVector.dist(new PVector(width - (lastWidth / 2), sqHeight * 1.5), new PVector(mouseX, mouseY)) <= radius) ChessController.triggerAction("redo");
    else if(PVector.dist(new PVector(width - (lastWidth / 2), sqHeight * 2.0), new PVector(mouseX, mouseY)) <= radius) ChessController.triggerAction("remis");
    
  } else {  //chessboard
    float radius = ((sqWidth <= sqHeight) ? sqWidth * 0.75 : sqHeight * 0.75) / 2;
    
    switch(menuOpen) {
      case 0: default:  //normal game screen
        int x = (mouseX - (sqWidth / 2)) / sqWidth;
        int y = (mouseY - (sqHeight / 2)) / sqHeight;
        ChessController.update(x, y);
        break;
      case 1:  //checkmate-menu
        //close button
        if(PVector.dist(new PVector(sqWidth * 7.65, sqHeight * 2.6), new PVector(mouseX, mouseY)) <= radius) menuOpen = 0;
        break;
      case 2:  //remis-menu
        //close button
        if(PVector.dist(new PVector(sqWidth * 7.65, sqHeight * 2.6), new PVector(mouseX, mouseY)) <= radius) menuOpen = 0;
        break;
      case 3:  //confirm reset
        if(mouseX >= width/2 - (sqWidth * 3) && mouseX <= width/2 - (sqWidth * 0.5) && mouseY >= sqHeight * 5.5 && mouseY <= sqHeight * 6) {  //yes
          menuOpen = 0;
          ChessController.triggerAction("reset");
        }
        if(mouseX >= width/2 + (sqWidth * 0.5) && mouseX <= width/2 + (sqWidth * 3) && mouseY >= sqHeight * 5.5 && mouseY <= sqHeight * 6) {  //no
          menuOpen = 0;
        }
        break;
      case 4:  //promotion-menu
        if(mouseX >= width/2 - (sqWidth * 2.75) && mouseX <= width/2 - (sqWidth * 1.75) && mouseY >= sqHeight * 4.75 && mouseY <= sqHeight * 5.75) {  //rook //<>//
          if(Gamelogic.sendPromotionChoice("Rook")) {
            menuOpen = 0;
            Gamelogic.checkKings();
          }
        }
        if(mouseX >= width/2 - (sqWidth * 1.25) && mouseX <= width/2 - (sqWidth * 0.25) && mouseY >= sqHeight * 4.75 && mouseY <= sqHeight * 5.75) {  //knight
          if(Gamelogic.sendPromotionChoice("Knight")) {
            menuOpen = 0;
            Gamelogic.checkKings();
          }
        }
        if(mouseX >= width/2 + (sqWidth * 0.25) && mouseX <= width/2 + (sqWidth * 1.25) && mouseY >= sqHeight * 4.75 && mouseY <= sqHeight * 5.75) {  //bishop
          if(Gamelogic.sendPromotionChoice("Bishop")) {
            menuOpen = 0;
            Gamelogic.checkKings();
          }
        }
        if(mouseX >= width/2 + (sqWidth * 1.75) && mouseX <= width/2 + (sqWidth * 2.75) && mouseY >= sqHeight * 4.75 && mouseY <= sqHeight * 5.75) {  //queen
          if(Gamelogic.sendPromotionChoice("Queen")) {
            menuOpen = 0;
            Gamelogic.checkKings();
          }
        }
        break;
    }
  }
}

void keyPressed() {
  if(menuOpen != 0) return;
  
  //println((int)key, keyCode);
  switch(key) {
    case 26:  //Ctrl + Z
      ChessController.triggerAction("undo");
      break;
    case 25:  //Ctrl + Y
      ChessController.triggerAction("redo");
      break;
  }
}

class UserInterface2D implements GUIController {
  
  @Override
  public void run() {
    //preparing new game
    Gamelogic.initialize(this, new Player[]{ new Player("Player1", ChessColor.BLACK), new Player("Player2", ChessColor.WHITE) });
    selection = null;
    menuOpen = 0;
    threadsToKing = new int[8][8];
    highlighted = new boolean[8][8];
    gameOver = false;
    remisClaimable = false;
  }
  
  //clicking on the chessboard to either get possible moves or execute a move
  //(-1, -1) resets all highlights etc.
  public void update(int x, int y) {
    if(x == -1 && y == -1) {
      selection = null;
      for(int i = 0; i < 8; i++) {
        for(int j = 0; j < 8; j++) {
          threadsToKing[i][j] = 0;
          highlighted[i][j] = false;
        }
      }
    } else {
    
      if(gameOver) return;
      
      Piece[][] chessboard = Gamelogic.getChessboard();
      if(selection == null && chessboard[x][y] != null && chessboard[x][y].getOwner().equals(Gamelogic.getCurrentPlayer())) {
        selection = new Pos(x, y);
        Pos[] moves = chessboard[x][y].getPossibleMoves(true);
        for(Pos pos: moves) {
          highlighted[pos.x][pos.y] = true;
        }
        
      } else {
        if(highlighted[x][y]) {
          
          for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
              threadsToKing[i][j] = 0;
            }
          }
          
          if(!Gamelogic.move(chessboard[selection.x][selection.y], new Pos(x, y))) {
            System.out.println("Something is wrong");
          }
          
          //caching value for performance optimization
          remisClaimable = Gamelogic.canClaimRemis();
        }
        
        selection = null;
        for(int i = 0; i < 8; i++) {
          for(int j = 0; j < 8; j++) {
            highlighted[i][j] = false;
          }
        }
      }
    }
  }
  
  //adapter method for better capsuling
  public void triggerAction(String action) {
    switch(action) {
      case "undo":
        update(-1, -1);
        Gamelogic.undo();
        Gamelogic.checkKings();
        break;
      case "redo":
        update(-1, -1);
        Gamelogic.redo();
        Gamelogic.checkKings();
        break;
      case "remis":
        update(-1, -1);
        Gamelogic.checkKings();
        
        //for testing (all possible reasons)
        //remis("A Player has claimed a remis.: (Threefold repetition rule or Fifty-move rule)");
        //remis("Impossibility of checkmate: A checkmate is not possible anymore.");
        //remis("Stalemate: " + Gamelogic.getCurrentPlayer().getName() + " has no legal move but is not in check.");
        //remis("Seventy-five-move rule: No capture or no pawn move has occurred in the last 75 moves (by both players).");
        //remis("Fivefold repetition: The same position has occured for five times during the course of the game.");
        
        if (Gamelogic.canClaimRemis()) remis("A player has claimed a remis.: (Threefold repetition rule or Fifty-move rule)");
        break;
      case "reset":
        update(-1, -1);
        Gamelogic.clearAll();
        ChessController.run();
        break;
      default: break;
    }
  }
  
  @Override
  public void check(Player player, Piece[] threads) {
    Pos kingPos = player.findKing().getPosition();
    threadsToKing[kingPos.x][kingPos.y] = 1;
    for(Piece piece: threads) {
      threadsToKing[piece.getPosition().x][piece.getPosition().y] = 2;
    }
  }
  
  @Override
  public void checkmate(Player player, Piece[] threads) {
    Pos kingPos = player.findKing().getPosition();
    threadsToKing[kingPos.x][kingPos.y] = 1;
    for(Piece piece: threads) {
      threadsToKing[piece.getPosition().x][piece.getPosition().y] = 2;
    }
    if(!gameOver) {
      menuOpen = 1;
      menuData = new String[] { Gamelogic.getOtherPlayer().getName() };
    }
    gameOver = true;
  }
  
  @Override
  public void remis(String reason) {
    if(!gameOver) menuOpen = 2;
    menuData = new String[] { reason };
    gameOver = true;
  }
  
  @Override
  public void choosePromotion() {
    menuOpen = 4;
    menuData = new String[] { (Gamelogic.getCurrentPlayer().getColor().equals(ChessColor.WHITE)) ? "WHITE" : "BLACK" };
  }
  
  //renders the empty chessboard
  public void drawChessboard() {
    int col = 70;
    fill(100);
    rect(0, 0, width, height);
    textAlign(CENTER);
    textSize((int)(((width <= height) ? width : height) / 45));
    for(int y = 0; y < 8; y++) {
      //writing A-H and 1-8 at the side
      fill(255);
      text(8 - y, sqWidth / 4, (y + 1) * sqHeight);
      text((char)(65 + y), (y + 1) * sqWidth, height - (sqHeight / 4));
      
      for(int x = 0; x < 8; x++) {
        //drawing squares in alternive colors as the chessboard
        col = (col == 70) ? 230 : 70;
        fill(col);
        rect(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), sqWidth, sqHeight);
        
        boolean flag = false;
        
        //applying highlights etc.
        if(selection != null && selection.x == x && selection.y == y) { fill(255, 255, 179); flag = true; }
        else if(highlighted[x][y]) { fill(255, 255, 204, 200); flag = true; }
        else if(threadsToKing[x][y] == 1) { fill(255, 77, 77); flag = true; }
        else if(threadsToKing[x][y] == 2) { fill(255, 102, 102, 140); flag = true; }
        
        if(flag) rect(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), sqWidth, sqHeight);
      }
      col = (col == 70) ? 240 : 70;
    }
  }
  
  //renders the pieces on the chessboard
  public void drawPieces() {
    Piece[][] board = Gamelogic.getChessboard();
    pieceOverlay = createImage(width, height, ARGB);
    for(int y = 0; y < 8; y++) {
      for(int x = 0; x < 8; x++) {
        if(board[x][y] != null) {
          switch(board[x][y].getClass().getName()) {
            case "logic.Pawn":
              if(board[x][y].getOwner().getColor().equals(ChessColor.WHITE) || advancedGraphics) pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), pawnWhite);
              else pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), pawnBlack);
              break;
            case "logic.Rook":
              if(board[x][y].getOwner().getColor().equals(ChessColor.WHITE) || advancedGraphics) pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), rookWhite);
              else pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), rookBlack);
              break;
            case "logic.Knight":
              if(board[x][y].getOwner().getColor().equals(ChessColor.WHITE) || advancedGraphics) pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), knightWhite);
              else pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), knightBlack);
              break;
            case "logic.Bishop":
              if(board[x][y].getOwner().getColor().equals(ChessColor.WHITE) || advancedGraphics) pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), bishopWhite);
              else pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), bishopBlack);
              break;
            case "logic.King":
              if(board[x][y].getOwner().getColor().equals(ChessColor.WHITE) || advancedGraphics) pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), kingWhite);
              else pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), kingBlack);
              break;
            case "logic.Queen":
              if(board[x][y].getOwner().getColor().equals(ChessColor.WHITE) || advancedGraphics) pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), queenWhite);
              else pieceOverlay.set(x * sqWidth + (sqWidth / 2), y * sqHeight + (sqHeight / 2), queenBlack);
              break;
            default:
              fill(227, 105, 229);
              ellipse(x * sqWidth + sqWidth, y * sqHeight + sqHeight, sqWidth / 2, sqHeight / 2);
              System.out.println(board[x][y].getClass().getName());
              break;
          }
          
          if(advancedGraphics) {
            if(particles[x][y].size() < maxNumParticles) {
              //create new particles when below the maxNumParticles for this piece
              for(int i = 0; i < maxNumParticles - particles[x][y].size(); i++) {
                particles[x][y].add(new Particle(new PVector((x+1) * sqWidth, (y+1) * sqHeight), (board[x][y].getOwner().getColor().equals(ChessColor.WHITE)) ? color(255) : color(0), (width <= height) ? width / 375f : height / 375f, random(2, 5)));
              }
            }
          }
        }
        
        if(advancedGraphics) {
          //update and render all particles, remove dead particles
          ArrayList<Particle> removable = new ArrayList<Particle>();
          pieceOverlay.loadPixels();
          for(Particle particle: particles[x][y]) {
            if(!particle.active) removable.add(particle);
            else {
              particle.update(board[x][y] == null);
              particle.display();
            }
          }
          particles[x][y].removeAll(removable);
        }
      }
    }
    
    if(!advancedGraphics) {
      image(pieceOverlay, 0, 0);
      
    }else {
      if(debugging) {
        loadPixels();
        for(int i = 0; i < pieceOverlay.pixels.length; i++) {
          pixels[i] = color(alpha(pieceOverlay.pixels[i]));
        }        
        updatePixels();
      }
    }
  }
  
  //renders UI elements like menus and sidebar buttons
  public void drawUI() {
    float diameter = (sqWidth <= sqHeight) ? sqWidth / 7 * 3 : sqHeight / 7 * 3;
    float imageWidth = diameter / 7 * 6;
    float textOrientation = (width <= height) ? width : height;
    float closeImageWidth = (sqWidth <= sqHeight) ? sqWidth * 0.75 : sqHeight * 0.75;
    
    //sidebar buttons
    fill(230);
    circle(width - (lastWidth / 2), sqHeight * 0.5, diameter);
    image(newGameAction, width - (lastWidth/2) - (imageWidth/2), sqHeight * 0.5 - (imageWidth/2), imageWidth, imageWidth);
    circle(width - (lastWidth / 2), sqHeight * 1.0, diameter);
    image(undoAction, width - (lastWidth/2) - (imageWidth/2), sqHeight * 1.0 - (imageWidth/2), imageWidth, imageWidth);
    circle(width - (lastWidth / 2), sqHeight * 1.5, diameter);
    image(redoAction, width - (lastWidth/2) - (imageWidth/2), sqHeight * 1.5 - (imageWidth/2), imageWidth, imageWidth);
    if(!remisClaimable) fill(160);
    circle(width - (lastWidth / 2), sqHeight * 2.0, diameter);
    image(remisAction, width - (lastWidth/2) - (imageWidth/2), sqHeight * 2.0 - (imageWidth/2), imageWidth, imageWidth);
    
    //menus
    switch(menuOpen) {
      case 1:  //checkmate-menu
        fill(241, 241, 218);
        rect(sqWidth * 0.75, sqHeight * 2, sqWidth * 7.5, sqHeight * 5);
        fill(0);
        textSize((int)(textOrientation / 9));
        text("Checkmate", width / 2, sqHeight * 4.2);
        textSize((int)(textOrientation / 21));
        text(menuData[0] + " has won!", width / 2, sqHeight * 6);
        fill(230);
        circle(sqWidth * 7.65, sqHeight * 2.6, (sqWidth <= sqHeight) ? sqWidth * 0.75 : sqHeight * 0.75);
        image(closeAction, sqWidth * 7.65 - (closeImageWidth/2), sqHeight * 2.6 - (closeImageWidth/2), closeImageWidth, closeImageWidth);
        break;
      case 2:  //remis-menu
        fill(241, 241, 218);
        rect(sqWidth * 0.75, sqHeight * 2, sqWidth * 7.5, sqHeight * 5);
        fill(0);
        textSize((int)(textOrientation / 9));
        text("Remis", width / 2, sqHeight * 4.2);
        textSize((int)(textOrientation / 50));
        String[] lines = menuData[0].split(": ");
        text(lines[0] + ((lines[1].charAt(0) == '(') ? "" : ":"), width / 2, sqHeight * 5.5);
        text(lines[1], width / 2, sqHeight * 6);
        fill(230);
        circle(sqWidth * 7.65, sqHeight * 2.6, (sqWidth <= sqHeight) ? sqWidth * 0.75 : sqHeight * 0.75);
        image(closeAction, sqWidth * 7.65 - (closeImageWidth/2), sqHeight * 2.6 - (closeImageWidth/2), closeImageWidth, closeImageWidth);
        break;
      case 3:  //confirm reset
        fill(241, 241, 218);
        rect(sqWidth * 0.75, sqHeight * 2, sqWidth * 7.5, sqHeight * 5);
        fill(0);
        textSize((int)(textOrientation / 21));
        text("Do you want to start", width / 2, sqHeight * 3.5);
        text("a new game?", width / 2, sqHeight * 4);
        fill(230);
        rect(width/2 - (sqWidth * 3), sqHeight * 5.5, sqWidth * 2.5, sqHeight / 2);
        rect(width/2 + (sqWidth * 0.5), sqHeight * 5.5, sqWidth * 2.5, sqHeight / 2);
        fill(0);
        textSize((int)(textOrientation / 30));
        text("Yes", width/2 - (sqWidth * 1.75), sqHeight * 5.85);
        text("No", width/2 + (sqWidth * 1.75), sqHeight * 5.85);
        break;
      case 4:  //promotion-menu
        fill(241, 241, 218);
        rect(sqWidth * 0.75, sqHeight * 2, sqWidth * 7.5, sqHeight * 5);
        fill(0);
        textSize((int)(textOrientation / 21));
        text("Choose your Promotion!", width / 2, sqHeight * 3.5);
        fill(230);
        rect(width/2 - (sqWidth * 2.75), sqHeight * 4.75, sqWidth, sqHeight);
        rect(width/2 - (sqWidth * 1.25), sqHeight * 4.75, sqWidth, sqHeight);
        rect(width/2 + (sqWidth * 0.25), sqHeight * 4.75, sqWidth, sqHeight);
        rect(width/2 + (sqWidth * 1.75), sqHeight * 4.75, sqWidth, sqHeight);
        
        if(menuData[0].equals("WHITE")) {
          image(rookWhite, width/2 - (sqWidth * 2.75), sqHeight * 4.75);
          image(knightWhite, width/2 - (sqWidth * 1.25), sqHeight * 4.75);
          image(bishopWhite, width/2 + (sqWidth * 0.25), sqHeight * 4.75);
          image(queenWhite, width/2 + (sqWidth * 1.75), sqHeight * 4.75);
        } else {
          image(rookBlack, width/2 - (sqWidth * 2.75), sqHeight * 4.75);
          image(knightBlack, width/2 - (sqWidth * 1.25), sqHeight * 4.75);
          image(bishopBlack, width/2 + (sqWidth * 0.25), sqHeight * 4.75);
          image(queenBlack, width/2 + (sqWidth * 1.75), sqHeight * 4.75);
        }
        break;
      case 0: default: break;
    }
  }
  
}


class Particle {
  
  color rgb;        //color
  PVector pos;      //position
  PVector dir;      //direction
  float vel;        //velocity
  float radius;     //size
  int age;          //existed since ... frames
  int lifeTime;     //age to die
  boolean active;   //is alive or not
  
  Particle(PVector pos, color rgb, float vel, float lifeTimeInSec) {
    this.pos = pos;
    this.rgb = rgb;
    this.vel = vel;
    this.age = 0;
    radius = (sqWidth <= sqHeight) ? sqWidth / 10 : sqHeight / 10;
    this.dir = new PVector(random(-1, 1), random(-1, 1)).normalize();
    this.lifeTime = (int)lifeTimeInSec * FPS;
    active = true;
  }
  
  //renders the particle
  void display() {
    fill(rgb);
    circle(pos.x, pos.y, radius);
  }
  
  //updates movement and age of the particle
  void update(boolean dissolve) {
    age++;
    pos.x += dir.x * vel;
    pos.y += dir.y * vel;
    
    if(!dissolve && pos.x >= 0 && pos.x < width && pos.y >= 0 && pos.y < height) {
      //stay within the boundaries of the piece
      float alphaValue = alpha(pieceOverlay.pixels[(int)pos.x + (width * (int)pos.y)]);
      if(alphaValue < 250) {
        dir.x *= -1;  pos.x += dir.x * vel;
        dir.y *= -1;  pos.y += dir.y * vel;
        dir.rotate(random(-30, 30)).normalize();
      }
    } else {
      //if dissolving => faster decay
      age += 2;
    }
    
    if(pos.x < 0 || pos.x > width || pos.y < 0 || pos.y > height || age >= lifeTime) active = false;
  }
  
}
