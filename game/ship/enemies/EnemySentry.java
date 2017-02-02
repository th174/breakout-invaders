package game.ship.enemies;

import game.GameProperty;
import javafx.scene.image.Image;

/**
 * Created by th174 on 1/14/2017.
 */
public class EnemySentry extends EnemyShip {
    public static final double DEFAULT_SIZE = 90;
    public static final int BONUS_SCORE = 50;
    public static final int DEFAULT_MOVETIMER_MIN = Integer.MAX_VALUE;
    public static final int DEFAULT_MOVETIMER_VARIANCE = 0;
    public static final int DEFAULT_MOVESPEED_X = 0;
    public static final int DEFAULT_MOVESPEED_Y = 0;
    public static final int DEFAULT_MAX_HEALTH = 100;
    public static final String[] DEFAULT_SPRITE_LOCATIONS = {"resources/sentry.png", "resources/sentry2.png"};

    public EnemySentry(double x, double y) {
        this(x, y, DEFAULT_MAX_HEALTH, DEFAULT_SIZE, DEFAULT_MOVESPEED_X, DEFAULT_MOVESPEED_Y, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[0]), new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[1]));
    }

    public EnemySentry(double x, double y, int hp, double size, Image sprite) {
        this(x, y, hp, size, DEFAULT_MOVESPEED_X, DEFAULT_MOVESPEED_Y, sprite, null);
    }

    public EnemySentry(double x, double y, int hp, double size, double msx, double msy, Image sprite, Image sprite2) {
        super(x, y, hp, size, msx, msy, DEFAULT_MOVETIMER_MIN, DEFAULT_MOVETIMER_VARIANCE, sprite, sprite2);
    }

    @Override
    public int getScore() {
        return BONUS_SCORE + super.getScore();
    }
}

