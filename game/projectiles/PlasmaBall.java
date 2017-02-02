package game.projectiles;


import game.GameProperty;
import game.ship.PaddleShip;
import game.ship.PlayerShip;
import game.ship.Ship;
import game.ship.enemies.EnemyDoppleganger;
import game.ship.enemies.EnemyShip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;

/**
 * Created by th174 on 1/14/2017.
 * This entire file is part of my masterpiece.
 */
public class PlasmaBall extends Projectile {
    public static final double DEFAULT_SIZE = 45;
    public static final double SPIN_RATE = 6;
    public static final double TETHERED_MOVESPEED = 12;
    public static final double DEFAULT_SPEED = 5;
    public static final double DEFAULT_ANGLE = Math.PI / 2;
    public static final int DEFAULT_DAMAGE = 50;
    public static final String DEFAULT_SPRITE_LOCATION = "resources/plasmaball.png";
    public static final String BOUNCE_ALLY_SFX_LOCATION = "resources/bounce.wav";
    public static final String BOUNCE_ENEMY_SFX_LOCATION = "resources/bounce2.wav";
    public static final double BOUNCE_SFX_VOLUME = 1.0;
    public static final double EDGE_TOLERANCE = 5;
    private final double mySize;
    private boolean isTethered;
    private double moveAngle;
    private Ship parentShip;

    public PlasmaBall(Ship parent) {
        this(parent, DEFAULT_SPEED, DEFAULT_ANGLE, DEFAULT_DAMAGE, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATION));
    }

    public PlasmaBall(double x, double y) {
        this(x, y, DEFAULT_SIZE, DEFAULT_SPEED, DEFAULT_ANGLE, DEFAULT_DAMAGE, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATION));
    }

    public PlasmaBall(Ship parent, double speed, double angle, int dmg, Image sprite) {
        this(parent.getX() + parent.getWidth() / 2 - DEFAULT_SIZE / 2 * GameProperty.getWidth() * GameProperty.SCALE_X,
                parent.getY() + (parent instanceof EnemyShip ? parent.getHeight() * 10 / 11 : -DEFAULT_SIZE * GameProperty.getHeight() * GameProperty.SCALE_Y),
                DEFAULT_SIZE, speed, angle, dmg, sprite);
        parentShip = parent;
        isTethered = true;
    }

    public PlasmaBall(double x, double y, double size, double movespeed, double angle, int dmg, Image sprite) {
        super(x, y, movespeed, dmg);
        isTethered = false;
        moveAngle = angle % (Math.PI * 2);
        mySize = size;
        setSprite(new ImageView(sprite));
        ((ImageView) getSprite()).setPreserveRatio(true);
        ((ImageView) getSprite()).setSmooth(true);
        ((ImageView) getSprite()).setCache(true);
        ((ImageView) getSprite()).setFitWidth(size * GameProperty.getWidth() * GameProperty.SCALE_X);
        setHitBox(new Circle(x + getWidth() / 2, y + getHeight() / 2, getWidth() / 2, Color.CYAN));
    }

    @Override
    public boolean move() {
        if (isTethered) {
            tetheredMove();
        } else {
            checkScreenBorders();
            setX(getX() + getMoveSpeed() * Math.cos(moveAngle));
            setY(getY() - getMoveSpeed() * Math.sin(moveAngle));
        }
        getSprite().setRotate(getSprite().getRotate() + SPIN_RATE);
        return super.move();
    }

    private void checkScreenBorders() {
        if (getX() <= GameProperty.getLeft() - EDGE_TOLERANCE) {
            setX(GameProperty.getLeft() - EDGE_TOLERANCE);
            bounce(-Math.PI / 2);
        } else if (getX() + getWidth() >= GameProperty.getRight() + 5) {
            setX(GameProperty.getRight() - getWidth() + EDGE_TOLERANCE);
            bounce(Math.PI / 2);
        } else if (getY() <= GameProperty.getTop() - EDGE_TOLERANCE && parentShip instanceof PaddleShip) {
            setY(GameProperty.getTop() - EDGE_TOLERANCE);
            bounce(Math.PI);
        } else if (getY() + getHeight() >= GameProperty.getBottom() + EDGE_TOLERANCE && parentShip instanceof EnemyDoppleganger) {
            setY(GameProperty.getBottom() - getHeight() + EDGE_TOLERANCE);
            bounce(0);
        }
    }

    //move toward center of parentShip at maximum euclidian movespeed
    private void tetheredMove() {
        double xTarget = parentShip.getX() + parentShip.getWidth() / 2 - DEFAULT_SIZE / 2 * GameProperty.getWidth() * GameProperty.SCALE_X;
        double yTarget = parentShip.getY() + (parentShip instanceof EnemyShip ? parentShip.getHeight() * 10 / 11 : -DEFAULT_SIZE * GameProperty.getHeight() * GameProperty.SCALE_Y);
        double dx = xTarget - getX();
        double dy = yTarget - getY();
        double mag = Math.hypot(dx, dy);
        setX(mag > TETHERED_MOVESPEED ? getX() + dx / mag * TETHERED_MOVESPEED : xTarget);
        setY(mag > TETHERED_MOVESPEED ? getY() + dy / mag * TETHERED_MOVESPEED : yTarget);
    }

    public void bounce(double wallangle) {
        wallangle = (wallangle + 100 * Math.PI) % (Math.PI * 2);
        moveAngle = (moveAngle + 100 * Math.PI) % (Math.PI * 2);
        //only bounce if diff in angles > 60 degrees
        if (angleDistance(wallangle + Math.PI / 2, moveAngle) >= Math.PI / 2) {
            moveAngle = ((wallangle + Math.PI / 2) * 2 - moveAngle + 101 * Math.PI) % (Math.PI * 2);
            //prevent ball getting stuck horizontally by slightly fudging its bounce angle away from perfectly horizontal
            //if tree too long?
            if (moveAngle > 0 && moveAngle < Math.PI / 36) {
                moveAngle = Math.PI / 36;
            } else if (moveAngle > Math.PI * 35 / 36 && moveAngle < Math.PI) {
                moveAngle = Math.PI * 35 / 36;
            } else if (moveAngle >= Math.PI && moveAngle < Math.PI * 37 / 36) {
                moveAngle = Math.PI * 37 / 36;
            } else if (moveAngle > Math.PI * 71 / 36 && moveAngle <= Math.PI * 2) {
                moveAngle = Math.PI * 71 / 36;
            }
            new AudioClip(GameProperty.getAbsolutePath() + (parentShip instanceof PlayerShip ? BOUNCE_ALLY_SFX_LOCATION : BOUNCE_ENEMY_SFX_LOCATION)).play(BOUNCE_SFX_VOLUME);
        }
    }

    private double angleDistance(double alpha, double beta) {
        alpha %= (2 * Math.PI);
        beta %= (2 * Math.PI);
        double phi = Math.abs(alpha - beta) % (2 * Math.PI);
        return Math.min((2 * Math.PI - phi) % (2 * Math.PI), phi);
    }

    //calculate bounce angle on non-flat surface (calculate derivative of ellipse at midpoint of intersection)
    public void bounce(Ellipse shipHitBox, Shape intersection) {
        double contactPointXLocal = shipHitBox.getCenterX() - (intersection.getBoundsInParent().getMaxX() + intersection.getBoundsInParent().getMaxX()) / 2;
        double contactPointYLocal = shipHitBox.getCenterY() - (intersection.getBoundsInParent().getMaxY() + intersection.getBoundsInParent().getMaxY()) / 2;
        bounce(Math.atan2(Math.pow(shipHitBox.getRadiusY(), 2) * contactPointXLocal, Math.pow(shipHitBox.getRadiusX(), 2) * contactPointYLocal));
    }

    public void setAngle(double angle) {
        moveAngle = angle;
    }

    public void tether() {
        isTethered = true;
    }

    public void untether() {
        isTethered = false;
    }

    public boolean isTethered() {
        return isTethered;
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        ((ImageView) getSprite()).setX(x);
        ((Circle) getHitBox()).setCenterX(getX() + mySize / 2 * GameProperty.SCALE_X * GameProperty.getWidth());
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        ((ImageView) getSprite()).setY(y);
        ((Circle) getHitBox()).setCenterY(getY() + mySize / 2 * GameProperty.SCALE_Y * GameProperty.getHeight());
    }
}
