import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    // image attributes
    int frameWidth = 360;
    int frameHeight = 640;

    Image backgroundImage;
    Image birdImage;
    Image lowerPipeImage;
    Image upperPipeImage;

    // player
    int playerStartPosX = frameWidth / 8;
    int playerStartPosY = frameHeight / 2;

    int playerWidth = 34;
    int playerHeight = 24;

    Player player;

    // pipes attributes
    int pipeStartPosX = frameWidth;
    int pipeStartPosY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    ArrayList<Pipe> pipes;

    // game logic
    Timer gameLoop;
    Timer pipesCoolDown;
    int gravity = 1;
    boolean gameOver = false;

    int score = 0;

    // constructor
    public FlappyBird() {
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        setFocusable(true);
        addKeyListener(this);

        // load images
        backgroundImage = new ImageIcon(getClass().getResource("assets/background.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("assets/bird.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("assets/lowerPipe.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("assets/upperPipe.png")).getImage();

        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);

        pipes = new ArrayList<Pipe>();
        pipesCoolDown = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("pipa");
                placePipes();
            }
        });
        pipesCoolDown.start();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void placePipes() {
        int randomPosY = (int) (pipeStartPosY - pipeHeight/4 - Math.random() * (pipeHeight/2));
        int openingSpace = frameHeight/4;

        Pipe upperPipe = new Pipe(pipeStartPosX, randomPosY, pipeWidth, pipeHeight, upperPipeImage);
        pipes.add(upperPipe);

        Pipe lowerPipe = new Pipe(pipeStartPosX, (randomPosY + openingSpace + pipeHeight), pipeWidth, pipeHeight, lowerPipeImage);
        pipes.add(lowerPipe);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, frameWidth, frameHeight, null);
        g.drawImage(player.getImage(), player.getPosX(),  player.getPosY(), player.getWidth(), player.getHeight(), null);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.getImage(), pipe.getPosX(), pipe.getPosY(), pipe.getWidth(), pipe.getHeight(), null);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 10, 25);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", frameWidth / 2 - 100, frameHeight / 2);

        }
    }

    public void move() {
        int pipeCounter = 0;

        if (!gameOver) {
            player.setVelocityY((player.getVelocityY() + gravity));
            player.setPosY(player.getPosY() + player.getVelocityY());
            player.setPosY(Math.max(player.getPosY(), 0));

            for (int i = 0; i < pipes.size(); i++) {
                Pipe pipe = pipes.get(i);
                pipe.setPosX(pipe.getPosX() + pipe.getVelocityX());

                if (player.getPosX() < pipe.getPosX() + pipe.getWidth() &&
                        player.getPosX() + player.getWidth() > pipe.getPosX() &&
                        player.getPosY() < pipe.getPosY() + pipe.getHeight() &&
                        player.getPosY() + player.getHeight() > pipe.getPosY()) {

                    gameOver = true;
                    gameLoop.stop();
                    pipesCoolDown.stop();
                }


                if (pipe.getPosX() + pipe.getWidth() < player.getPosX() && !pipe.isPassed()) {
                    pipeCounter++;
                    pipe.setPassed(true);

                    if (pipeCounter % 2 == 0) {
                        score++;
                    }
                }
            }
        }
    }


    public void restartGame() {
        player = new Player(playerStartPosX, playerStartPosY, playerWidth, playerHeight, birdImage);
        pipes = new ArrayList<Pipe>();
        gameOver = false;
        score = 0;
        gameLoop.start();
        pipesCoolDown.start();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        } else {
            gameLoop.stop();
            pipesCoolDown.stop();
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.setVelocityY(-15);
        }

        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

