package game.projectiles;

import game.GameProperty;
import game.ship.PaddleShip;
import game.ship.PlayerShip;
import javafx.scene.image.Image;

/**
 * Created by th174 on 1/17/2017.
 */
public class PowerupBallMagnet extends Powerup {

    public static final String DEFAULT_SPRITE_LOCATION = "resources/ballmagnet.png";
    public static final int DEFAULT_MOVESPEED = 3;
    public static final int DEFAULT_SIZE = 50;
    public static final int DEFAULT_DURATION = 2400;
    private final int magnetDuration;

    public PowerupBallMagnet(double x, double y) {
        this(x, y, DEFAULT_SIZE, DEFAULT_MOVESPEED, DEFAULT_DURATION, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATION));
    }

    public PowerupBallMagnet(double x, double y, double size, double movespeed, int duration, Image sprite) {
        super(x, y, size, movespeed, sprite);
        magnetDuration = duration;
    }

    @Override
    public void usePowerup(PlayerShip playerShip) {
        if (playerShip instanceof PaddleShip) {
            ((PaddleShip) playerShip).resetBallMagnetTimer(magnetDuration);
        }
    }

}
