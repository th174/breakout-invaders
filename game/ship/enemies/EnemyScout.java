package game.ship.enemies;

import game.GameProperty;
import javafx.scene.image.Image;

/**
 * Created by th174 on 1/14/2017.
 */
public class EnemyScout extends EnemyShip {
    public static final double DEFAULT_SIZE = 80;
    public static final int DEFAULT_MOVETIMER_MIN = 200;
    public static final int DEFAULT_MOVETIMER_VARIANCE = 200;
    public static final int DEFAULT_MOVESPEED_X = 0;
    public static final int DEFAULT_MOVESPEED_Y = 0;
    public static final int DEFAULT_MAX_HEALTH = 50;
    public static final String[] DEFAULT_SPRITE_LOCATIONS = {"resources/scout.png", "resources/scout2.png"};

    public EnemyScout(double x, double y) {
        this(x, y, DEFAULT_MAX_HEALTH, DEFAULT_SIZE, DEFAULT_MOVESPEED_X, DEFAULT_MOVESPEED_Y, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[0]), new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[1]));
    }

    public EnemyScout(double x, double y, double msx, double msy) {
        this(x, y, DEFAULT_MAX_HEALTH, DEFAULT_SIZE, msx, msy, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[0]), new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[1]));
    }

    public EnemyScout(double x, double y, int hp, double size, double msx, double msy, Image sprite) {
        this(x, y, hp, size, msx, msy, sprite, null);
    }

    public EnemyScout(double x, double y, int hp, double size, double msx, double msy, Image sprite1, Image sprite2) {
        super(x, y, hp, size, msx, msy, DEFAULT_MOVETIMER_MIN, DEFAULT_MOVETIMER_VARIANCE, sprite1, sprite2);
    }
}
