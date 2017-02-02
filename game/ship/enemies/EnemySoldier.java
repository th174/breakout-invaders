package game.ship.enemies;

import game.GameProperty;
import game.projectiles.Laser;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Created by th174 on 1/13/2017.
 */
public class EnemySoldier extends EnemyShip {
    public static final double DEFAULT_SIZE = 150;
    public static final int BONUS_SCORE = 100;
    public static final int DEFAULT_MOVETIMER_MIN = 150;
    public static final int DEFAULT_MOVETIMER_VARIANCE = 400;
    public static final int SHOOTTIMER_MIN = 40;
    public static final int SHOOTTIMER_VARIANCE = 100;
    public static final double LASER_WIDTH = 3;
    public static final double LASER_HEIGHT = 45;
    public static final double LASER_SPEED = 5;
    public static final int LASER_DAMAGE = 20;
    public static final Color LASERCOLOR = Color.RED;
    public static final int DEFAULT_MAX_HEALTH = 200;
    public static final int DEFAULT_MOVESPEED_X = 3;
    public static final int DEFAULT_MOVESPEED_Y = 6;
    public static final String[] DEFAULT_SPRITE_LOCATIONS = {"resources/soldier.png", "resources/soldier2.png"};

    public EnemySoldier(double x, double y) {
        this(x, y, DEFAULT_MAX_HEALTH, DEFAULT_SIZE, DEFAULT_MOVESPEED_X, DEFAULT_MOVESPEED_Y, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[0]), new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[1]));
    }

    public EnemySoldier(double x, double y, int hp, double size, double msx, double msy, Image sprite1) {
        this(x, y, hp, size, msx, msy, sprite1, null);
    }

    public EnemySoldier(double x, double y, int hp, double size, double msx, double msy, Image sprite1, Image sprite2) {
        super(x, y, hp, size, msx, msy, DEFAULT_MOVETIMER_MIN, DEFAULT_MOVETIMER_VARIANCE, sprite1, sprite2);
    }

    @Override
    protected void updateProjectiles() {
        if (isAlive()) {
            if (resetShootTimer((int) (Math.random() * SHOOTTIMER_VARIANCE + SHOOTTIMER_MIN))) {
                getProjectiles().add(new Laser(getX() + getWidth() / 2 - LASER_WIDTH / 2, getY() + getHeight() - 45, LASER_WIDTH, LASER_HEIGHT, LASER_SPEED, LASER_DAMAGE, LASERCOLOR));
            }
        }
        super.updateProjectiles();
    }

    @Override
    public int getScore() {
        return BONUS_SCORE + super.getScore();
    }
}
