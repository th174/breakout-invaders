package game.projectiles.powerup;

import game.GameProperty;
import game.ship.PaddleShip;
import game.ship.PlayerShip;
import javafx.scene.image.Image;

/**
 * Created by th174 on 1/17/2017.
 */
public class PowerupExtraBall extends Powerup {

    public static final String DEFAULT_SPRITE_LOCATION = "resources/extraball.png";
    public static final int DEFAULT_MOVESPEED = 2;
    public static final int DEFAULT_SIZE = 50;

    public PowerupExtraBall(double x, double y) {
        this(x, y, DEFAULT_SIZE, DEFAULT_MOVESPEED, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATION));
    }

    public PowerupExtraBall(double x, double y, double size, double movespeed, Image sprite) {
        super(x, y, size, movespeed, sprite);
    }

    @Override
    public void usePowerup(PlayerShip playerShip) {
        if (playerShip instanceof PaddleShip) {
            ((PaddleShip) playerShip).newBall();
            super.usePowerup(playerShip);
        }
    }
}
