package game.ship.enemies;

import game.GameProperty;
import game.projectiles.Laser;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Created by th174 on 1/15/2017.
 */
public class EnemyWraith extends EnemyShip {
    public static final int BONUS_SCORE = 150;
    public static final double DEFAULT_SIZE = 160;
    public static final int SHOOTTIMER_MIN = 20;
    public static final int SHOOTTIMER_VARIANCE = 150;
    public static final double LASER_WIDTH = 3;
    public static final double LASER_HEIGHT = 45;
    public static final double LASER_SPEED = 5;
    public static final int LASER_DAMAGE = 20;
    public static final Color LASERCOLOR = Color.RED;
    public static final int DEFAULT_MOVETIMER_MIN = 130;
    public static final int DEFAULT_MOVETIMER_VARIANCE = 300;
    public static final int DEFAULT_MAX_HEALTH = 150;
    public static final int DEFAULT_MOVESPEED_X = 3;
    public static final int DEFAULT_MOVESPEED_Y = 5;
    public static final String[] DEFAULT_SPRITE_LOCATIONS = {"resources/wraith.png"};

    public EnemyWraith(double x, double y) {
        this(x, y, DEFAULT_MAX_HEALTH, DEFAULT_SIZE, DEFAULT_MOVESPEED_X, DEFAULT_MOVESPEED_Y, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[0]));
    }

    public EnemyWraith(double x, double y, int hp, double size, double msx, double msy, Image sprite) {
        this(x, y, hp, size, msx, msy, sprite, null);
    }

    public EnemyWraith(double x, double y, int hp, double size, double msx, double msy, Image sprite1, Image sprite2) {
        super(x, y, hp, size, msx, msy, DEFAULT_MOVETIMER_MIN, DEFAULT_MOVETIMER_VARIANCE, sprite1, sprite2);
    }

    @Override
    protected void updateProjectiles() {
        if (isAlive()) {
            if (resetShootTimer((int) (Math.random() * SHOOTTIMER_VARIANCE + SHOOTTIMER_MIN))) {
                getProjectiles().add(new Laser(getX() + getWidth() / 2 - LASER_WIDTH / 2, getY() + getHeight(), LASER_WIDTH, LASER_HEIGHT, LASER_SPEED, LASER_DAMAGE, LASERCOLOR));
            }
        }
        super.updateProjectiles();
    }

    @Override
    public int getScore() {
        return BONUS_SCORE + super.getScore();
    }
}
