package game.ship;

import game.GameObj;
import game.GameProperty;
import game.projectiles.Missile;
import game.projectiles.PlasmaBall;
import game.projectiles.Projectile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by th174 on 1/13/2017.
 */
public abstract class Ship implements GameObj {
    public static final String[] DEFAULT_DEATH_ANIMATION_LOCATION = new String[]{
            "resources/explosion1.png",
            "resources/explosion2.png",
            "resources/explosion3.png",
            "resources/explosion4.png",
            "resources/explosion5.png",};
    public static final String EXPLOSION_SFX_LOCATION = "resources/explosion.wav";
    public static final double EXPLOSION_SFX_VOLUME = 0.4;
    public static final int DEATH_ANIMATION_DURATION = 80;
    public static final int DEATH_ANIMATION_STEP = DEATH_ANIMATION_DURATION / 5;
    public static final int POST_DEATH_ANIMATION_DELAY = 10;
    public static final double DEFAULT_HITBOX_OFFSET = 1.0 / 14;
    public static final double HITBOX_REGION_LEFT = .25;
    public static final double HITBOX_REGION_RIGHT = 1 - HITBOX_REGION_LEFT;
    public static final double HITBOX_REGION_TOP = HITBOX_REGION_LEFT;
    public static final double HITBOX_REGION_BOTTOM = 1 - HITBOX_REGION_TOP;
    public static final int BALL_BOUNCE_COOLDOWN = 20;
    private final Image[] deathAnimation;
    private final int maximumHealth;
    private final Collection<Projectile> myProjectiles = new HashSet<>();
    private final Ellipse myHitBox;
    private final ImageView mySprite;
    private double remainingHealth;
    private boolean isAlive = true;
    private int deathTimer;
    private int ballBounceCooldownTimer;
    private PlasmaBall previousBallHit;

    public Ship(double x, double y, int hp, double size, Image sprite) {
        mySprite = new ImageView(sprite);
        mySprite.setPreserveRatio(true);
        mySprite.setSmooth(true);
        mySprite.setCache(true);
        mySprite.setFitWidth(size * GameProperty.getWidth() * GameProperty.SCALE_X);
        mySprite.setX(x);
        mySprite.setY(y);
        isAlive = true;
        remainingHealth = hp;
        maximumHealth = hp;
        ballBounceCooldownTimer = 0;
        myHitBox = new Ellipse(x + getWidth() / 2 * GameProperty.getWidth() * GameProperty.SCALE_X, y + getHeight() / 2 * GameProperty.getHeight() * GameProperty.SCALE_Y, 0, 0);
        myHitBox.setRadiusX(getWidth() / 2 - getWidth() * DEFAULT_HITBOX_OFFSET);
        myHitBox.setRadiusY(getHeight() / 2 - getHeight() * DEFAULT_HITBOX_OFFSET);
        myHitBox.setFill(Color.CYAN);
        deathAnimation = Arrays.stream(DEFAULT_DEATH_ANIMATION_LOCATION).map(GameProperty.getAbsolutePath()::concat).map(Image::new).toArray(Image[]::new);
    }

    public boolean update(Collection<Ship> ships) {
        ballBounceCooldownTimer--;
        updateProjectiles();
        if (isAlive) {
            move();
            checkHit(ships);
        } else {
            return handleDeath();
        }
        return false;
    }

    protected abstract void move();

    protected void updateProjectiles() {
        myProjectiles.removeIf(Projectile::update);
    }

    protected void checkHit(Collection<Ship> ships) {
        ships.parallelStream()
                .map(Ship::getProjectiles)
                .flatMap(Collection::stream).parallel()
                .filter(this::checkProjectileHit)
                .forEach(this::takeHit);
        if (remainingHealth <= 0) {
            handleDeath();
        }
    }

    private boolean checkProjectileHit(Projectile p) {
        Shape intersection = Shape.intersect(p.getHitBox(), myHitBox);
        boolean hit = intersection.getBoundsInLocal().getWidth() != -1;
        if (hit && p instanceof PlasmaBall) {
            PlasmaBall pb = (PlasmaBall) p;
            if (pb.getX() + pb.getWidth() / 2 >= getX() + getWidth() * HITBOX_REGION_LEFT && pb.getX() + pb.getWidth() / 2 <= getX() + getWidth() * HITBOX_REGION_RIGHT) {
                if (pb.getY() + pb.getHeight() / 2 < getY() + getHeight() / 2) {
                    pb.setY(Math.min(getY() + getHeight() * HITBOX_REGION_TOP - pb.getHeight() / 2 - 2, pb.getY()));
                } else {
                    pb.setY(Math.max(getY() + getHeight() * HITBOX_REGION_BOTTOM - pb.getHeight() / 2 + 2, pb.getY()));
                }
            } else if (pb.getY() + pb.getHeight() / 2 >= getY() + getHeight() * 1 / 4 && pb.getY() + pb.getHeight() / 2 <= getY() + getHeight() * 3 / 4) {
                if (pb.getX() + pb.getWidth() / 2 < getX() + getWidth() / 2) {
                    pb.setX(Math.min(getX() + getWidth() * HITBOX_REGION_LEFT - pb.getWidth() / 2 - 2, pb.getX()));
                } else {
                    pb.setX(Math.max(getX() + getWidth() * HITBOX_REGION_RIGHT - pb.getWidth() / 2 + 2, pb.getX()));
                }
            }
            pb.bounce(getHitBox(), intersection);
            pb.move();
        }
        return hit;
    }

    @Override
    public Ellipse getHitBox() {
        return myHitBox;
    }

    protected void takeHit(Projectile p) {
        if (p instanceof PlasmaBall) {
            if (ballBounceCooldownTimer <= 0 || !p.equals(previousBallHit)) {
                takeDamage(p.getDamage());
                ballBounceCooldownTimer = BALL_BOUNCE_COOLDOWN;
                previousBallHit = (PlasmaBall) p;
            }
        } else {
            takeDamage(p.getDamage());
        }
        if (!(p instanceof Missile) && !(p instanceof PlasmaBall)) {
            p.remove();
        }
    }

    protected void takeDamage(double dmg) {
        remainingHealth -= dmg;
        if (remainingHealth > maximumHealth) {
            remainingHealth = maximumHealth;
        }
    }

    @Override
    public ImageView getSprite() {
        return mySprite;
    }

    protected boolean handleDeath() {
        if (isAlive) {
            isAlive = false;
            myHitBox.setRadiusX(0);
            myHitBox.setRadiusY(0);
            mySprite.setY(getY() + getHeight() / 2 - getWidth() / 2);
            deathTimer = DEATH_ANIMATION_DURATION;
            new AudioClip(GameProperty.getAbsolutePath() + EXPLOSION_SFX_LOCATION).play(EXPLOSION_SFX_VOLUME);
        }
        mySprite.setImage(deathAnimation[Math.min((DEATH_ANIMATION_DURATION - deathTimer) / DEATH_ANIMATION_STEP, 4)]);
        deathTimer--;
        if (!myProjectiles.isEmpty() && deathTimer <= 0) {
            deathTimer = 0;
        }
        return deathTimer <= -POST_DEATH_ANIMATION_DELAY;
    }

    public double getMaximumHealth() {
        return maximumHealth;
    }

    public double getRemainingHealth() {
        return remainingHealth;
    }

    public void setRemainingHealth(double health) {
        remainingHealth = Math.min(Math.max(health, 0), maximumHealth);
    }

    public Collection<Projectile> getProjectiles() {
        return myProjectiles;
    }

    @Override
    public double getX() {
        return mySprite.getX();
    }

    @Override
    public void setX(double x) {
        mySprite.setX(x);
        myHitBox.setCenterX(x + getWidth() / 2);
    }

    @Override
    public double getY() {
        return mySprite.getY();
    }

    @Override
    public void setY(double y) {
        mySprite.setY(y);
        myHitBox.setCenterY(y + getHeight() / 2);
    }

    @Override
    public double getWidth() {
        return mySprite.getBoundsInParent().getWidth();
    }

    @Override
    public double getHeight() {
        return mySprite.getBoundsInParent().getHeight();
    }

    protected boolean isAlive() {
        return isAlive;
    }
}
