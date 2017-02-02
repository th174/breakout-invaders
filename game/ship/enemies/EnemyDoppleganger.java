package game.ship.enemies;

import game.GameProperty;
import game.projectiles.PlasmaBall;
import game.ship.PaddleShip;
import javafx.scene.image.Image;

/**
 * Created by th174 on 1/16/2017.
 */
public class EnemyDoppleganger extends EnemyShip {
    public static final int BONUS_SCORE = 500;
    public static final int SHOOTTIMER_MAX = 180;
    public static final double BALL_SPEED = 5;
    public static final int BALL_DAMAGE = 20;
    public static final int DEFAULT_MAX_HEALTH = 1200;
    public static final int SHOOTTIMER_BALL_UNTERTHER_TIMER = 60;
    public static final String[] DEFAULT_SPRITE_LOCATIONS = {"resources/doppleganger.png", "resources/doppleganger2.png"};
    public static final String DEFAULT_PROJECTILE_SPRITE_LOCATION = "resources/shadowball.png";
    private final PaddleShip targetShip;
    private final Image myBallSprite;

    public EnemyDoppleganger(PaddleShip player) {
        this(player, DEFAULT_MAX_HEALTH, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[0]), new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[1]), new Image(GameProperty.getAbsolutePath() + DEFAULT_PROJECTILE_SPRITE_LOCATION));
    }

    public EnemyDoppleganger(PaddleShip player, int hp, Image sprite, Image ballsprite) {
        this(player, hp, sprite, null, ballsprite);
    }

    public EnemyDoppleganger(PaddleShip player, int hp, Image sprite1, Image sprite2, Image ballsprite) {
        super(player.getX(), GameProperty.getBottom() - player.getY() - player.getHeight(), hp, player.getWidth(), sprite1, sprite2);
        targetShip = player;
        getSprite().setFitWidth(player.getWidth());
        myBallSprite = ballsprite;
    }

    @Override
    protected void updateProjectiles() {
        if (resetShootTimer(SHOOTTIMER_MAX) && isAlive()) {
            PlasmaBall pb = new PlasmaBall(this, BALL_SPEED, Math.toRadians(Math.random() * 120 + 210), BALL_DAMAGE, myBallSprite);
            getProjectiles().add(pb);
        } else if (getShootTimer() == SHOOTTIMER_BALL_UNTERTHER_TIMER) {
            getProjectiles().forEach(p -> {
                if (p instanceof PlasmaBall) {
                    ((PlasmaBall) p).untether();
                }
            });
        }
        super.updateProjectiles();
    }

    @Override
    protected void move() {
        setX(targetShip.getX());
        double y = GameProperty.getBottom() - targetShip.getY() - targetShip.getHeight();
        y = targetShip.getY() - y - getHeight() > 150 ? y : targetShip.getY() - getHeight() - 150;
        y = y > GameProperty.getHeight() / 17 ? y : GameProperty.getHeight() / 17;
        setY(y);
    }

    @Override
    public int getScore() {
        return BONUS_SCORE + super.getScore();
    }

    @Override
    protected boolean handleDeath() {
        if (isAlive()) {
            getProjectiles().stream()
                    .filter(PlasmaBall.class::isInstance)
                    .map(PlasmaBall.class::cast)
                    .forEach(PlasmaBall::untether);
        }
        return super.handleDeath();
    }
}
