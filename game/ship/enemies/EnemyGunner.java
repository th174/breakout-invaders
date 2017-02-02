package game.ship.enemies;

import game.GameProperty;
import game.projectiles.Laser;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Created by th174 on 1/15/2017.
 */
public class EnemyGunner extends EnemyShip {
    public static final int BONUS_SCORE = 200;
    public static final double DEFAULT_SIZE = 120;
    public static final int SHOOTTIMER_MAX = 200;
    public static final int STRAFE_INTERVAL = 7;
    public static final int STRAFE_DURATION = 63;
    public static final double LASER_WIDTH = 3;
    public static final double LASER_HEIGHT = 30;
    public static final double LASER_SPEED = 7;
    public static final int LASER_DAMAGE = 4;
    public static final Color LASERCOLOR = Color.YELLOW;
    public static final double LASER_FIRE_OFFSET_X1 = 144.0 / 348;
    public static final double LASER_FIRE_OFFSET_X2 = 1 - LASER_FIRE_OFFSET_X1;
    public static final double LASER_FIRE_OFFSET_Y = 298.0 / 315;
    public static final int DEFAULT_MOVESPEED_X = 4;
    public static final int DEFAULT_MOVESPEED_Y = 0;
    public static final int DEFAULT_MAX_HEALTH = 150;
    public static final int DEFAULT_MOVETIMER_MIN = Integer.MAX_VALUE;
    public static final int DEFAULT_MOVETIMER_VARIANCE = 0;
    public static final String[] DEFAULT_SPRITE_LOCATIONS = {"resources/gunner.png", "resources/gunner2.png"};

    public EnemyGunner(double x, double y) {
        this(x, y, DEFAULT_MAX_HEALTH, DEFAULT_SIZE, DEFAULT_MOVESPEED_X, DEFAULT_MOVESPEED_Y, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[0]), new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[1]));
    }

    public EnemyGunner(double x, double y, int hp, double size, double msx, double msy, Image sprite) {
        this(x, y, hp, size, msx, msy, sprite, null);
    }

    public EnemyGunner(double x, double y, int hp, double size, double msx, double msy, Image sprite1, Image sprite2) {
        super(x, y, hp, size, msx, msy, DEFAULT_MOVETIMER_MIN, DEFAULT_MOVETIMER_VARIANCE, sprite1, sprite2);
    }

    @Override
    protected void updateProjectiles() {
        if (isAlive()) {
            if (getShootTimer() <= STRAFE_DURATION && getShootTimer() % STRAFE_INTERVAL == 0) {
                getProjectiles().add(new Laser(getX() + getWidth() * LASER_FIRE_OFFSET_X1 - LASER_WIDTH / 2, getY() + getHeight() * LASER_FIRE_OFFSET_Y, LASER_WIDTH, LASER_HEIGHT, LASER_SPEED, LASER_DAMAGE, LASERCOLOR));
                getProjectiles().add(new Laser(getX() + getWidth() * LASER_FIRE_OFFSET_X2 - LASER_WIDTH / 2, getY() + getHeight() * LASER_FIRE_OFFSET_Y, LASER_WIDTH, LASER_HEIGHT, LASER_SPEED, LASER_DAMAGE, LASERCOLOR));
            }
        }
        resetShootTimer(SHOOTTIMER_MAX);
        super.updateProjectiles();
    }

    @Override
    public int getScore() {
        return BONUS_SCORE + super.getScore();
    }
}
