import game.*;
import game.projectiles.Laser;
import game.projectiles.Projectile;
import game.ship.enemies.EnemyDoppleganger;
import game.ship.enemies.EnemyShip;
import game.ship.PaddleShip;
import game.ship.PlayerShip;
import game.ship.Ship;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class BreakoutInvadersMain extends Application {
    public static final int STARTING_LEVEL = 1;
    public static final int NUM_OF_LEVELS = 3;
    public static final double FRAMES_PER_SECOND = 120;
    public static final String TITLE = "BreakoutInvasion";
    public static final double DEFAULT_WIDTH = 1920;
    public static final double DEFAULT_HEIGHT = 1080;
    public static final double MILLISECOND_DELAY = 1000 / FRAMES_PER_SECOND;
    public static final int FONT_SIZE_LARGE = 120;
    public static final int FONT_SIZE_MEDIUM = 20;
    public static final int FONT_SIZE_SMALL = 15;
    public static final int LEVEL_BONUS_SCORE = 1000;
    public static final String BACKGROUND = "resources/background.png";
    public static final String[] BGM_LOCATIONS = {"resources/BGM1.mp3", "resources/BGM2.mp3", "resources/BGM3.wav"};
    public static final String[] LEVELS = {"game/levels/level1.lvl", "game/levels/level2.lvl", "game/levels/level3.lvl"};
    public static final String[] LEVEL_TIPS = {
            "TIP: Hit the enemy ships with your PlasmaBall to damage them. They will change color when their HP falls below half.\n" +
                    "     Getting hit will damage your shields and hp. Your shields also slowly deplete on their own.\n" +
                    "     Use the mouse to control your ship. Click to release the ball.\n" +
                    "     Use headphones for the best experience.\n\n" +
                    "                                  Game is paused. Press the spacebar to continue.",
            "TIP: Contact with the ball will regenerate your shields. Don't be afraid to take some damage getting to the ball.\n" +
                    "     Be careful not to drop the ball, or you'll take a lot of damage to directly to your health.\n\n\n\n" +
                    "                                  Game is paused. Press the spacebar to continue.",
            "TIP: Against enemies you can't see, try and guess their position from their attacks, and listen for the thunk when you hit them.\n" +
                    "     Against enemies you can't hit, focus on avoiding attacks. Even if you can't hit them, they can hit back, and hard.\n\n\n\n" +
                    "                                  Game is paused. Press the spacebar to continue.",
            "TIP: Missiles will follow you and will detonate for massive damage. Try to get out of the explosion as quickly as possible.\n" +
                    "     The key is to use positioning, both yours and the enemies' to your advantage. Plan ahead, then execute the plan.\n\n\n\n" +
                    "                                  Game is paused. Press the spacebar to continue.",
            "TIP: Congratulations on beating the game!                                                    Final Score: %5d\n" +
                    "     The game will now restart from level 1 as soon as you unpause.\n" +
                    "     However, if you do want to play again, I would highly recommend you close out java and run it again.\n" +
                    "     This program has more leaks than the Titanic, so you're gonna have serious performance issues if you don't."};
    private boolean showHitBoxes;
    private boolean isPaused = true;
    private boolean isFullscreen = true;
    private boolean gameOver;
    private int currentLevel = STARTING_LEVEL - 1;
    private MediaView BGM;
    private Media[] BGM_Tracks;
    private Collection<Ship> myEnemies;
    private Collection<Ship> myPlayers;
    private Collection<Projectile> myProjectiles;
    private Rectangle myShieldBar;
    private Rectangle myHealthBar;
    private Text myPowerupStatus;
    private Text myPauseText1;
    private Text myPauseText2;
    private Text myLevelAndScore;
    private int totalScore;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        primaryStage.setTitle(TITLE);
        readArgs(getParameters());
        Scene myScene = new Scene(root, GameProperty.getWidth(), GameProperty.getHeight(), Color.BLACK);
        initializeScene(myScene);
        primaryStage.setScene(myScene);
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(isFullscreen);
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
        KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> update(myScene));
        Timeline animation = new Timeline();
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.getKeyFrames().add(frame);
        animation.play();
    }

    private void readArgs(Parameters argv) {
        String option;
        System.out.println(argv.getNamed());
        if ((option = argv.getNamed().get("dimensions")) != null) {
            String[] dim = option.split("x");
            GameProperty.set(Integer.parseInt(dim[0]), Integer.parseInt(dim[1]));
        } else if ((option = argv.getNamed().get("no-fit-screen")) != null && Boolean.parseBoolean(option)) {
            GameProperty.set(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } else {
            GameProperty.set(Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
        }
        if ((option = argv.getNamed().get("hitboxes")) != null) {
            showHitBoxes = Boolean.parseBoolean(option);
        }
        if ((option = argv.getNamed().get("fullscreen")) != null) {
            isFullscreen = Boolean.parseBoolean(option);
        }
    }

    private void initializeScene(Scene myScene) {
        Pane root = (Pane) myScene.getRoot();
        GameProperty.setAbsolutePath(getClass().getResource("game/").toExternalForm());
        myPlayers = new ArrayList<>();
        myEnemies = new HashSet<>();
        myProjectiles = new HashSet<>();
        PaddleShip myPlayerShip = new PaddleShip(GameProperty.getWidth() / 2 - PaddleShip.DEFAULT_SIZE / 2, GameProperty.getHeight() * 6 / 7 - 30);
        myHealthBar = new Rectangle(GameProperty.getWidth() * 3 / 7, GameProperty.getHeight() / 100, Color.RED);
        myShieldBar = new Rectangle(GameProperty.getWidth() * 3 / 7, GameProperty.getHeight() / 100, Color.CYAN);
        myShieldBar.setX(GameProperty.getWidth() / 2 - GameProperty.getWidth() / 400 - myShieldBar.getWidth());
        myHealthBar.setX(GameProperty.getWidth() / 2 + GameProperty.getWidth() / 400);
        myShieldBar.setY(GameProperty.getHeight() * 139 / 140);
        myHealthBar.setY(GameProperty.getHeight() * 139 / 140);
        ImageView backgroundImage = new ImageView(GameProperty.getAbsolutePath() + BACKGROUND);
        myPauseText1 = new Text(0, GameProperty.getHeight() / 2 - 50 * GameProperty.getHeight() * GameProperty.SCALE_Y, "");
        myPauseText1.setFont(new Font("Consolas", FONT_SIZE_LARGE));
        myPauseText1.setFill(Color.WHITE);
        myPauseText2 = new Text(GameProperty.getWidth() / 6, GameProperty.getHeight() / 2 + 50 * GameProperty.getHeight() * GameProperty.SCALE_Y, LEVEL_TIPS[0]);
        myPauseText2.setFont(new Font("Consolas", FONT_SIZE_MEDIUM));
        myPauseText2.setFill(Color.WHITE);
        myPowerupStatus = new Text(GameProperty.getLeft(), GameProperty.getBottom() * 41 / 42, "");
        myPowerupStatus.setFont(new Font("Consolas", FONT_SIZE_SMALL));
        myPowerupStatus.setFill(Color.WHITE);
        myLevelAndScore = new Text(GameProperty.getLeft(), FONT_SIZE_SMALL, "Level ");
        myLevelAndScore.setFont(new Font("Consolas", FONT_SIZE_SMALL));
        myLevelAndScore.setFill(Color.WHITE);
        BGM = new MediaView();
        BGM_Tracks = new Media[BGM_LOCATIONS.length];
        for (int i = 0; i < BGM_LOCATIONS.length; i++) {
            BGM_Tracks[i] = new Media(GameProperty.getAbsolutePath() + BGM_LOCATIONS[i]);
        }
        root.getChildren().add(backgroundImage);
        myPlayers.add(myPlayerShip);
        myPlayers.forEach(p -> addToScene(p, myScene));
        root.getChildren().add(BGM);
        root.getChildren().add(myHealthBar);
        root.getChildren().add(myShieldBar);
        root.getChildren().add(myPowerupStatus);
        root.getChildren().add(myLevelAndScore);
        root.getChildren().add(myPauseText1);
        root.getChildren().add(myPauseText2);
        myScene.setOnMouseMoved(this::handleMouseInput);
        myScene.setOnMouseDragged(this::handleMouseInput);
        myScene.setOnMouseClicked(this::handleMouseInput);
        myScene.setOnKeyPressed(this::handleKeyPress);
    }

    private void update(Scene myScene) {
        GameProperty.set(myScene.getWidth(), myScene.getHeight());
        if (isPaused) {
            pause(myScene);
        } else {
            unPause(myScene);
            if (myEnemies.isEmpty()) {
                isPaused = true;
                totalScore += LEVEL_BONUS_SCORE * currentLevel;
                currentLevel++;
                myPlayers.forEach(e -> ((PaddleShip) e).newLevel());
                startNextLevel(myScene);
            }
            if (myPlayers.isEmpty()) {
                gameOver(myScene);
            }
            myProjectiles.forEach(e -> removeFromScene(e, myScene));
            myProjectiles.clear();
            updateShips(myPlayers, myEnemies, myScene);
            updateShips(myEnemies, myPlayers, myScene);
            myProjectiles.forEach(e -> addToScene(e, myScene));
        }
        updateUIText(myScene);
        updateHealthAndShieldBar(myScene);
    }

    private void pause(Scene myScene) {
        Pane root = (Pane) myScene.getRoot();
        root.getChildren().remove(myPauseText1);
        root.getChildren().add(myPauseText1);
        root.getChildren().remove(myPauseText2);
        root.getChildren().add(myPauseText2);
        if (currentLevel == 0) {
            myPauseText1.setText("     -BREAKOUT INVADERS-");
        } else if (currentLevel == 4) {
            myPauseText1.setText("     -INVADERS DEFEATED-");
        } else {
            myPauseText1.setText("          -LEVEL " + currentLevel + "-");
        }
        myPauseText2.setText(String.format(LEVEL_TIPS[currentLevel], totalScore));
        myScene.setCursor(Cursor.WAIT);
    }

    private void unPause(Scene myScene) {
        myPauseText1.setText("");
        myPauseText2.setText("");
        myScene.setCursor(Cursor.CROSSHAIR);
    }

    private void updateUIText(Scene myScene) {
        myLevelAndScore.setText(String.format("Level:\t%d\nScore:\t%05d", currentLevel, totalScore));
        if (!myPlayers.isEmpty() && ((List<Ship>) myPlayers).get(0) instanceof PaddleShip) {
            PaddleShip myPlayer0 = (PaddleShip) ((List<Ship>) myPlayers).get(0);
            int remainingTime = myPlayer0.getPowerupMagnetStatus();
            if (remainingTime > 0) {
                myPowerupStatus.setText(String.format("Ball Magnet: %2.1f", remainingTime / FRAMES_PER_SECOND));
            } else {
                myPowerupStatus.setText("");
            }
        }
    }

    private void gameOver(Scene myScene) {
        gameOver = true;
        Pane root = (Pane) myScene.getRoot();
        root.getChildren().remove(myPauseText1);
        root.getChildren().add(myPauseText1);
        myPauseText1.setText("          GAME OVER\n" + "         >TRY AGAIN?");
    }

    private void updateHealthAndShieldBar(Scene myScene) {
        Pane root = (Pane) myScene.getRoot();
        if (!myPlayers.isEmpty()) {
            PaddleShip myPlayer0 = (PaddleShip) ((List<Ship>) myPlayers).get(0);
            myShieldBar.setWidth(GameProperty.getWidth() * 3 / 7 * myPlayer0.getRemainingShield() / myPlayer0.getMaximumShield());
            myHealthBar.setWidth(GameProperty.getWidth() * 3 / 7 * myPlayer0.getRemainingHealth() / myPlayer0.getMaximumHealth());
            myShieldBar.setX(GameProperty.getWidth() / 2 - 5 - myShieldBar.getWidth());
        } else {
            root.getChildren().remove(myHealthBar);
            root.getChildren().remove(myShieldBar);
        }
    }

    private void updateShips(Collection<Ship> group1, Collection<Ship> group2, Scene myScene) {
        for (Iterator<Ship> it = group1.iterator(); it.hasNext(); ) {
            Ship s = it.next();
            if (s.update(group2)) {
                if (s instanceof EnemyShip) {
                    totalScore += ((EnemyShip) s).getScore();
                }
                it.remove();
                removeFromScene(s, myScene);
            }
            myProjectiles.addAll(s.getProjectiles());
        }
    }

    private void startNextLevel(Scene myScene) {
        myEnemies.clear();
        if (currentLevel == NUM_OF_LEVELS + 2) {
            currentLevel = 0;
        } else if (currentLevel > 0 && currentLevel <= NUM_OF_LEVELS) {
            changeBGM();
            readLevel(LEVELS[currentLevel - 1]);
        }
        myEnemies.forEach(e -> addToScene(e, myScene));
    }

    //the most disgusting method i've ever written
    private void readLevel(String levelFile) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(levelFile)))) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] words = line.split("\\s+");
                if (words[0].contains("Enemy")) {
                    try {
                        Class<EnemyShip> enemyClass = (Class<EnemyShip>) Class.forName("game." + words[0]);
                        Class[] constructorTypes = new Class[words.length - 1];
                        Object[] constructorArgs = new Object[words.length - 1];
                        if (enemyClass.equals(EnemyDoppleganger.class)) {
                            myPlayers.forEach(p -> myEnemies.add(new EnemyDoppleganger((PaddleShip) p)));
                        } else {
                            for (int i = 0; i < constructorArgs.length; i++) {
                                try {
                                    constructorArgs[i] = Double.parseDouble(words[i + 1]);
                                    constructorTypes[i] = double.class;
                                } catch (NumberFormatException nfe) {
                                    constructorArgs[i] = getClass().getDeclaredField(words[i + 1]).get(this);
                                    constructorTypes[i] = getClass().getDeclaredField(words[i + 1]).getType();
                                }
                            }
                            constructorArgs[0] = ((Double) constructorArgs[0]) * GameProperty.getWidth() * GameProperty.SCALE_X - (double) enemyClass.getDeclaredField("DEFAULT_SIZE").get(null) / 2;
                            constructorArgs[1] = ((Double) constructorArgs[1]) * GameProperty.getHeight() * GameProperty.SCALE_Y;
                            myEnemies.add(enemyClass.getDeclaredConstructor(constructorTypes).newInstance(constructorArgs));
                        }
                    } catch (Exception e) {
                        System.out.println("Instantiation failed for class: " + words[0] + "\n\n");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeBGM() {
        if (BGM.getMediaPlayer() != null) {
            BGM.getMediaPlayer().stop();
        }
        BGM.setMediaPlayer(new MediaPlayer(BGM_Tracks[currentLevel - 1]));
        BGM.getMediaPlayer().setVolume(0.5);
        BGM.getMediaPlayer().play();
    }

    private void handleKeyPress(KeyEvent e) {
        if (!gameOver) {
            if (e.getCode().equals(KeyCode.SPACE)) {
                isPaused = !isPaused;
            } else {
                myPlayers.forEach(p -> ((PlayerShip) p).keyInput(e.getCode()));
            }
        }
    }

    private void handleMouseInput(MouseEvent e) {
        myPlayers.forEach(p -> {
            if (p instanceof PlayerShip) {
                ((PlayerShip) p).mouseInput(e.getX(), e.getY(), e.getEventType().equals(MouseEvent.MOUSE_CLICKED) && !isPaused);
            }
        });
    }

    private void addToScene(GameObj obj, Scene myScene) {
        Pane root = (Pane) myScene.getRoot();
        if (showHitBoxes && !(obj instanceof Laser)) {
            if (obj instanceof PaddleShip) {
                ((PaddleShip) obj).getPaddleHitBoxes().forEach(root.getChildren()::add);
            }
            root.getChildren().add(obj.getHitBox());
        }
        root.getChildren().add(obj.getSprite());
    }

    private void removeFromScene(GameObj obj, Scene myScene) {
        Pane root = (Pane) myScene.getRoot();
        if (showHitBoxes) {
            root.getChildren().remove(obj.getHitBox());
        }
        root.getChildren().remove(obj.getSprite());
    }
}
