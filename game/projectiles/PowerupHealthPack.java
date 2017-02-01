package game.projectiles;

import game.GameProperty;
import game.ship.PlayerShip;
import javafx.scene.image.Image;

/**
 * Created by th174 on 1/19/2017.
 */
public class PowerupHealthPack extends Powerup {
    public static final String DEFAULT_SPRITE_LOCATION = "resources/healthpack.png";
    public static final int DEFAULT_MOVESPEED = 2;
    public static final int DEFAULT_SIZE = 40;
    public static final double DEFAULT_HEAL = 25;
    private final double healAmount;

    public PowerupHealthPack(double x, double y) {
        this(x, y, DEFAULT_SIZE, DEFAULT_MOVESPEED, DEFAULT_HEAL, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATION));
    }

    public PowerupHealthPack(double x, double y, double size, double movespeed, double heal, Image sprite) {
        super(x, y, size, movespeed, sprite);
        healAmount = heal;
    }

    @Override
    public void usePowerup(PlayerShip playership) {
        playership.setRemainingHealth(playership.getRemainingHealth() + healAmount);
    }
}


