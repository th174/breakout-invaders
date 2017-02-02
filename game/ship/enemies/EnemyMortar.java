package game.ship.enemies;

import game.GameProperty;
import game.projectiles.Missile;
import game.ship.Ship;
import javafx.scene.image.Image;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by th174 on 1/16/2017.
 */
public class EnemyMortar extends EnemyShip {
    public static final int SHOOTTIMER_MAX = 800;
    public static final int DEFAULT_MAX_HEALTH = 100;
    public static final String[] DEFAULT_SPRITE_LOCATIONS = {"resources/mortar.png", "resources/mortar2.png"};
    public static final double DEFAULT_SIZE = 100;
    public static final int BONUS_SCORE = 250;
    private final Collection<Ship> targetShips;

    public EnemyMortar(double x, double y, Ship target) {
        this(x, y, DEFAULT_MAX_HEALTH, DEFAULT_SIZE, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[0]), new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[1]), target);
    }

    public EnemyMortar(double x, double y, Collection<Ship> targets) {
        this(x, y, DEFAULT_MAX_HEALTH, DEFAULT_SIZE, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[0]), new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATIONS[1]), targets);
    }

    public EnemyMortar(double x, double y, int hp, double size, Image sprite, Ship target) {
        this(x, y, hp, size, sprite, null, target);
    }

    public EnemyMortar(double x, double y, int hp, double size, Image sprite1, Image sprite2, Ship target) {
        this(x, y, hp, size, sprite1, sprite2, Stream.of(target).collect(Collectors.toSet()));
    }

    public EnemyMortar(double x, double y, int hp, double size, Image sprite1, Image sprite2, Collection<Ship> targets) {
        super(x, y, hp, size, sprite1, sprite2);
        targetShips = targets;
    }

    @Override
    protected void updateProjectiles() {
        if (isAlive()) {
            if (resetShootTimer(SHOOTTIMER_MAX)) {
                targetShips.forEach(target -> getProjectiles().add(new Missile(getX() + getWidth() / 2 - Missile.DEFAULT_SIZE / 2, getY() + getHeight() - 20, target)));
            }
        }
        super.updateProjectiles();
    }

    @Override
    public int getScore() {
        return BONUS_SCORE + super.getScore();
    }
}
