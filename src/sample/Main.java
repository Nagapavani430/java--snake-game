package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static final int WIDTH = 700;
    private static final int HEIGHT = WIDTH;
    private static final int ROWS = 20;
    private static final int COLUMNS = ROWS;
    private static final int SQUARE_SIZE = WIDTH / ROWS;
    private static final String[] FOODS_IMAGE = new String[]{ "/img/ic_bird.png","/img/ic_orange.png", "/img/ic_apple.png",
            "/img/ic_cherry.png", "/img/ic_rat.png", "/img/ic_berry.png","/img/ic_rat.png","/img/ic_bird.png","/img/ic_cat.png",
            "/img/ic_coconut_.png", "/img/ic_peach.png","/img/ic_cat.png", "/img/ic_watermelon.png", "/img/ic_orange.png",
            "/img/ic_pomegranate.png"};

    private static final Image RESET_IMAGE= new Image("/img/reset.png");//Rishi`
    private static String SNAKE_HEAD_IMAGE ;
    private static String SNAKE_BODY_IMAGE ;

    private Image snakeHeadImage;
    private Image snakeBodyImage;
    private static final String[] TREE_IMAGE = new String[]{"/img/tree.png"};

    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private GraphicsContext gc;
    private List<Point> snakeBody = new ArrayList();
    private Point snakeHead;
    private Image foodImage;
    private int foodX;
    private int foodY;
    private List<Integer> treeListX = new ArrayList<>();
    private List<Integer> treeListY = new ArrayList<>();
    private Image treeImage;
    private boolean gameOver;
    private int currentDirection;
    private int score = 0;
    private Scene scene;
    private Timeline timeline;
    private int treeCount = 0;
    private boolean isTreeEnabled = false;
    Text Reset;//Rishi
    private boolean gameRestart = false;//Rishi
    private boolean cheat_mode = false;

    public void createScene(Stage primaryStage){
        primaryStage.setTitle("Snake");
        Group root = new Group();
        javafx.scene.canvas.Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        gc = canvas.getGraphicsContext2D();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        createScene(primaryStage);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    if (currentDirection != LEFT) {
                        currentDirection = RIGHT;
                    }
                } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                    if (currentDirection != RIGHT) {
                        currentDirection = LEFT;
                    }
                } else if (code == KeyCode.UP || code == KeyCode.W) {
                    if (currentDirection != DOWN) {
                        currentDirection = UP;
                    }
                } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                    if (currentDirection != UP) {
                        currentDirection = DOWN;
                    }
                } else if(code == KeyCode.TAB){
                    isTreeEnabled = !isTreeEnabled;
                }

                else if (code == KeyCode.R) {//Rishi
                    gameOver=false;
                    gameRestart=true;
                    reset(gc);
                }
                else if (code == KeyCode.C) {
                    cheat();
                }
            }
        });

        for (int i = 0; i < 3; i++) {
            snakeBody.add(new Point(5, ROWS / 2));
        }
        snakeHead = snakeBody.get(0);

        generateFood();
        generateTree();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(300), e -> run(gc)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void run(GraphicsContext gc) {
        if (gameOver) {
//            gc.setFill(Color.BLUEVIOLET);
            gc.setFill(Color.BLACK);
            gc.setFont(new Font("Digital-7", 25));
//            gc.fillText("Game Over!" + "\n Final Score: " + score + "\n Press c to Cheat",
//                    WIDTH / 3.5, HEIGHT / 2);
            gc.fillText("Oops, Game Over!" + "\n Your Final Score is " + score +
                            "\n Hurry... Press 'c/C' to cheat and save the game" +
                            "\n *score will be deducted* for cheat",
                    WIDTH / 3.5, HEIGHT / 2);
            return;
        }

        drawBackground(gc);
        drawFood(gc);

        if(isTreeEnabled) {
            drawTree(gc);
        }else{
            treeListX = new ArrayList<>();
            treeListY = new ArrayList<>();
        }
        drawSnake(gc, currentDirection);
        drawScore();

        for (int i = snakeBody.size() - 1; i >= 1; i--) {
            snakeBody.get(i).x = snakeBody.get(i - 1).x;
            snakeBody.get(i).y = snakeBody.get(i - 1).y;
        }

        switch (currentDirection) {
            case RIGHT:
                moveRight();
                break;
            case LEFT:
                moveLeft();
                break;
            case UP:
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
        }

        gameOver();
        eatFood();
    }

    ////Rishi start
    public void reset(GraphicsContext gc) {

        snakeBody.clear();
        for(int i=0;i<3;i++){
            snakeBody.add(new Point(5, ROWS / 2));

        }
        snakeHead = snakeBody.get(0);
        gc.setFill(Color.BLUE);
        gc.setFont(new javafx.scene.text.Font("Arial", 30));

        Light.Point pointLight = new Light.Point(500, 500, 500, Color.BLACK);
        Lighting lighting = new Lighting(pointLight);
        gc.applyEffect(lighting);
        gc.fillText("Restarting New Game", 600/2, HEIGHT / 4);
        gc.drawImage(RESET_IMAGE,250,250,250,250);
        score=0;
        currentDirection=0;
    }

    /////Rishi End

    private void drawBackground(GraphicsContext gc) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if ((i + j) % 2 == 0) {
                   // gc.setFill(Color.web("AAD751"));
                    gc.setFill(Color.web("WHITE"));//#c3e0b3//  fba01a  //b3bef2
                } else {
                    //gc.setFill(Color.web("A2D149"));
                    gc.setFill(Color.web("#9bd7d5")); //#ffffff//  9bd7d5  //9bd7d5
                }
                gc.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void generateFood() {
        start:
        while (true) {
            foodX = (int) (Math.random() * ROWS);
            foodY = (int) (Math.random() * COLUMNS);

            for (Point snake : snakeBody) {
                if (snake.getX() == foodX && snake.getY() == foodY) {
                    continue start;
                }
            }
            foodImage = new Image(FOODS_IMAGE[(int) (Math.random() * FOODS_IMAGE.length)]);
            break;
        }
    }


    private void generateTree() {
        start:
        while (true) {
            int treeX = (int) (Math.random() * ROWS);
            int treeY = (int) (Math.random() * COLUMNS);

            for (Point snake : snakeBody) {
                if ((snake.getX() == treeX && snake.getY() == treeY) || (foodX == treeX && foodY == treeY)) {
                    continue start;
                }
            }

            for (int i=0; i < treeListX.size() ; i++) {
                if ((treeListX.get(i) == treeX && treeListY.get(i) == treeY)) {
                    continue start;
                }
            }
            treeListX.add(treeX);
            treeListY.add(treeY);
            treeImage = new Image(TREE_IMAGE[0]);
            break;
        }
    }


    private void drawFood(GraphicsContext gc) {
        gc.drawImage(foodImage, foodX * SQUARE_SIZE, foodY * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
    }

    private void drawTree(GraphicsContext gc) {
        for(int i=0; i < treeListX.size() ; i++){
            gc.drawImage(treeImage, treeListX.get(i) * SQUARE_SIZE, treeListY.get(i) * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }

    }

    private void drawSnake(GraphicsContext gc, int currentDirection) {
        if (currentDirection  == 0){//RIGHT
            SNAKE_HEAD_IMAGE = "img/snake_face_right.png";
            snakeHeadImage = new Image(SNAKE_HEAD_IMAGE);
            gc.drawImage(snakeHeadImage, SQUARE_SIZE*snakeHead.getX(), SQUARE_SIZE*snakeHead.getY(), SQUARE_SIZE+1, SQUARE_SIZE+1);
        }
        if (currentDirection  == 1){//LEFT
            SNAKE_HEAD_IMAGE = "img/snake_face_left.png";
            snakeHeadImage = new Image(SNAKE_HEAD_IMAGE);
            gc.drawImage(snakeHeadImage, SQUARE_SIZE*snakeHead.getX(), SQUARE_SIZE*snakeHead.getY(), SQUARE_SIZE+1, SQUARE_SIZE+1);
        }
        if (currentDirection  == 2){//UP
            SNAKE_HEAD_IMAGE = "img/snake_face_up.png";
            snakeHeadImage = new Image(SNAKE_HEAD_IMAGE);
            gc.drawImage(snakeHeadImage, SQUARE_SIZE*snakeHead.getX(), SQUARE_SIZE*snakeHead.getY(), SQUARE_SIZE+1, SQUARE_SIZE+1);
        }
        if (currentDirection  == 3){//DOWN
            SNAKE_HEAD_IMAGE = "img/snake_face_down.png";
            snakeHeadImage = new Image(SNAKE_HEAD_IMAGE);
            gc.drawImage(snakeHeadImage, SQUARE_SIZE*snakeHead.getX(), SQUARE_SIZE*snakeHead.getY(), SQUARE_SIZE+1, SQUARE_SIZE+1);
        }

        for (int i = 1; i < snakeBody.size(); i++) {
            SNAKE_BODY_IMAGE = "img/snake_body.png";
            snakeBodyImage = new Image(SNAKE_BODY_IMAGE);
            gc.drawImage(snakeBodyImage , snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE,
                    SQUARE_SIZE-3, SQUARE_SIZE-3);
        }
    }

    private void moveRight() {
        snakeHead.x++;
    }

    private void moveLeft() {
        snakeHead.x--;
    }

    private void moveUp() {
        snakeHead.y--;
    }

    private void moveDown() {
        snakeHead.y++;
    }

    public void gameOver() {
        if (snakeHead.x < 0 || snakeHead.y < 0 || snakeHead.x * SQUARE_SIZE >= WIDTH || snakeHead.y * SQUARE_SIZE >= HEIGHT) {
            gameOver = true;
        }
        for(int i=0; i < treeListX.size() ; i++) {
            if (snakeHead.x == treeListX.get(i) && snakeHead.y == treeListY.get(i)) {
                gameOver = true;
                break;
            }
        }
        //destroy itself
        for (int i = 1; i < snakeBody.size(); i++) {
            if (snakeHead.x == snakeBody.get(i).getX() && snakeHead.getY() == snakeBody.get(i).getY()) {
                gameOver = true;
                break;
            }
        }
    }

    private void eatFood() {
        if (snakeHead.getX() == foodX && snakeHead.getY() == foodY) {
            snakeBody.add(new Point(-1, -1));
            generateFood();
            generateTree();
            score += 5;
            if(treeCount > 0 && isTreeEnabled){
                score = score + (5*(treeCount));
            }
            treeCount++;
        }
    }

    private void drawScore() {
//        gc.setFill(Color.BLUEVIOLET);
//        gc.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
//        gc.setTextAlign(TextAlignment.JUSTIFY);
//        gc.fillText("Score: " + score, 10, 35);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("Digital-7", 30));
        gc.fillText("Score: " + score, 20, 30);
    }

    private void cheat(){
        gameOver=false;
        cheat_mode=!cheat_mode;
        score=score/2; // should we round off here?
        snakeBody.clear();
        for(int i=0;i<3;i++){
            snakeBody.add(new Point(5, ROWS / 2));

        }
        snakeHead = snakeBody.get(0);
    }
}
