import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener{
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if( gameOver ){
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1){
            velocityX = 0;
            velocityY = -1;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1){
            velocityX = 0;
            velocityY = 1;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1){
            velocityX = -1;
            velocityY = -0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1){
            velocityX = 1;
            velocityY = 0;
        }
        else if(gameOver && e.getKeyCode() == KeyEvent.VK_SPACE ){
           resetGame();
        }
    }

/// NOT NEEDED -----------------------------
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
// ------------------------------------------------
    private class Tile {
        int x;
        int y;
         Tile(int x, int y){
             this.x = x;
             this.y = y;
         }
    }
    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Tile snakeHead;
    ArrayList<Tile> snakeBody;
    Tile food;
    Random random;

    // Game LOGIC
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;
    SnakeGame(int boardWidth, int boardHeight){
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);


        snakeHead = new Tile(5,5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(0, 0);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        for(int i = 0; i< boardWidth/tileSize; i++){
//            g.drawLine(i*tileSize, 0, i*tileSize, boardHeight);  /// VERTICAL LINES
//            g.drawLine(0,i*tileSize, boardWidth, i*tileSize);   /// HORIZONTAL LINES
            g.setColor(Color.darkGray);
        }
        // Food;
        g.setColor(Color.RED);
        g.fillOval(food.x * tileSize+2, food.y * tileSize+2, tileSize-4, tileSize-4);
        // Snake Head
        g.setColor(Color.GREEN);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);
        // snake Body
        for (Tile snakePart : snakeBody) {
            g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }
        /// SCORE
        g.setFont(new Font("Serif", Font.PLAIN,45));
        if(gameOver){
            g.setColor(Color.red);
            g.drawString("Game Over.", 8* tileSize, 11* tileSize);
            g.setFont(new Font("Serif", Font.PLAIN,14));
            g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
            g.drawString("PRESS <SpaceBar> to Retry", 9* tileSize, 12* tileSize);
        }
        else{
            g.setFont(new Font("Serif", Font.PLAIN,16));
            g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
        }
    }
    public void placeFood(){
        food.x= random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }
    public void resetGame(){
        velocityX = 0;
        velocityY = 0;
        placeFood();
        snakeBody.clear();
        snakeHead = new Tile(5,5);
        gameOver = false;
        gameLoop = new Timer(100, this);
        gameLoop.start();
    }
    public boolean collision(Tile tile1, Tile tile2){
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }
    public void move(){
        // eat food
        if (collision(snakeHead, food)){
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }
        // Snake Body
        for(int i = snakeBody.size() - 1; i>=0; i--){
            Tile snakePart = snakeBody.get(i);
            if (i == 0){ /// First member after Snake Head
               snakePart.x = snakeHead.x;
               snakePart.y = snakeHead.y;
            }
            else{
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        // Snake Head;
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        //<----------------------- Game Over Conditions ------------------------------->
        // Snake collides with own body
        for(int i = 0 ; i< snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
            if(collision(snakeHead, snakePart)){
                gameOver = true;
            }
        }
        // Snake Hits wall
        if(snakeHead.x*tileSize < 0 || snakeHead.x * tileSize > boardWidth || snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > boardHeight)
            gameOver = true;
        }
}
