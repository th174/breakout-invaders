package game.ship.enemies;

import game.GameProperty;
import game.projectiles.Laser;
import game.ship.Ship;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Collection;

/**
 * Created by th174 on 1/15/2017.
 */
public class EnemySpectre extends EnemyShip {
    public static final int BONUS_SCORE = 100;
    public static final double DEFAULT_SIZE = 160;
    public static final int SHOOTTIMER_MIN = 20;
    public static final int SHOOTTIMER_VARIANCE = 150;
    public static final double LASER_WIDTH = 4;
    public static final double LASER_HEIGHT = 45;
    public static final double LASER_SPEED = 5;
    public static final int LASER_DAMAGE = 20;
    public static final double HEALTH_DEPLETE_RATE = .03;
    public static final Color LASERCOLOR = Color.RED;
    public static final int DEFAULT_MOVETIMER_MIN = 130;
    public static final int DEFAULT_MOVETIMER_VARIANCE = 300;
    public static final int DEFAULT_MAX_HEALTH = 150;
    public static final int DEFAULT_MOVESPEED_X = 3;
    public static final int DEFAULT_MOVESPEED_Y = 5;
    public static final String[] DEFAULT_SPRITE_LOCATIONS = {"resources/spectre.png", "resources/spectre2.png"};

    public EnemySpectre(double x, double y) {
        this(x, y, DEFAULT_MAX_HEALTH, DEFAULT_SIZE, DEFAULT_MOVESPEED_X, DEFAULT_MOVESPEED_Y, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[0]), new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[1]));
    }

    public EnemySpectre(double x, double y, int hp, double size, double msx, double msy, Image sprite) {
        this(x, y, hp, size, msx, msy, sprite, null);
    }

    public EnemySpectre(double x, double y, int hp, double size, double msx, double msy, Image sprite1, Image sprite2) {
        super(x, y, hp, size, msx, msy, DEFAULT_MOVETIMER_MIN, DEFAULT_MOVETIMER_VARIANCE, sprite1, sprite2);
    }

    @Override
    protected void checkHit(Collection<Ship> set) {
        takeDamage(HEALTH_DEPLETE_RATE);
        if (getRemainingHealth() < 0) {
            handleDeath();
        }
        updateDamagedSprite();
    }

    @Override
    protected void updateProjectiles() {
        if (isAlive()) {
            if (getShootTimer() <= 0) {
                getProjectiles().add(new Laser(getX() + getWidth() / 2 - LASER_WIDTH / 2, getY() + getHeight(), LASER_WIDTH, LASER_HEIGHT, LASER_SPEED, LASER_DAMAGE, LASERCOLOR));
            }
        }
        resetShootTimer((int) (Math.random() * SHOOTTIMER_VARIANCE + SHOOTTIMER_MIN));
        super.updateProjectiles();
    }

    @Override
    public int getScore() {
        return BONUS_SCORE + super.getScore();
    }
}
