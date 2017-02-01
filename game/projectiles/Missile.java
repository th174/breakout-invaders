package game.projectiles;

import game.GameProperty;
import game.ship.Ship;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.Arrays;

/**
 * Created by th174 on 1/16/2017.
 */
public class Missile extends Projectile {
    public static final double DEFAULT_SIZE = 100;
    public static final double DIRECTION_DOWN = Math.toRadians(90);
    public static final int EXPLOSION_DURATION = 80;
    public static final int EXPLOSION_DURATION_STEP = 20;
    public static final double DEFAULT_TURNRATE = Math.toRadians(1);
    public static final double DEFAULT_DAMAGE = .6;
    public static final int DEFAULT_SPEED = 2;
    public static final int DEFAULT_DURATION = 1200;
    public static final String DEFAULT_SPRITE_LOCATION = "resources/missile.png";
    public static final String[] DEFAULT_DETONATE_ANIMATION_LOCATION = new String[]{
            "resources/explosion1.png",
            "resources/explosion2.png",
            "resources/explosion3.png",
            "resources/explosion4.png"};
    private final Ship targetShip;
    private final double explosionDamage;
    private final Image[] detonateAnimation;
    private int durationTimer;
    private double moveAngle;
    private double turnRate;
    private boolean hasDetonated;

    public Missile(double x, double y, Ship target) {
        this(x, y, DEFAULT_SIZE, DEFAULT_SPEED, DEFAULT_TURNRATE, DEFAULT_DAMAGE, DEFAULT_DURATION, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATION), target);
    }

    public Missile(double x, double y, double length, double move_speed, double turn_rate, double dmg, int duration, Image sprite, Ship target) {
        super(x, y, move_speed, 0);
        explosionDamage = dmg;
        targetShip = target;
        turnRate = turn_rate;
        durationTimer = duration;
        setSprite(new ImageView(sprite));
        ((ImageView) getSprite()).setPreserveRatio(true);
        ((ImageView) getSprite()).setSmooth(true);
        ((ImageView) getSprite()).setCache(true);
        ((ImageView) getSprite()).setFitWidth(length * GameProperty.getWidth() * GameProperty.SCALE_X);
        setHitBox(new Circle(getX() + getWidth() / 2, getY() + getHeight() / 2, getWidth() / 2));
        getHitBox().setFill(Color.CYAN);
        turnRate = turn_rate;
        moveAngle = DIRECTION_DOWN;
        detonateAnimation = Arrays.stream(DEFAULT_DETONATE_ANIMATION_LOCATION).map(GameProperty.getAbsolutePath()::concat).map(Image::new).toArray(Image[]::new);
    }

    @Override
    public boolean move() {
        if (!checkDetonate()) {
            double dx = targetShip.getX() + targetShip.getWidth() / 2 - ((Circle) getHitBox()).getCenterX();
            double dy = targetShip.getY() + targetShip.getHeight() / 2 - ((Circle) getHitBox()).getCenterY();
            double theta = (Math.atan2(dy, dx) + 2 * Math.PI) % (2 * Math.PI);
            moveAngle += (theta - moveAngle + 2 * Math.PI) % (2 * Math.PI) < (moveAngle - theta + 2 * Math.PI) % (2 * Math.PI) ? turnRate : -turnRate;
            moveAngle = (moveAngle + 2 * Math.PI) % (2 * Math.PI);
            getSprite().setRotate(Math.toDegrees(moveAngle));
            getHitBox().setRotate(Math.toDegrees(moveAngle));
            setX(getX() + getMoveSpeed() * Math.cos(moveAngle));
            setY(getY() + getMoveSpeed() * Math.sin(moveAngle));
        }
        durationTimer--;
        return !isActive() || durationTimer < 0;
    }

    private boolean checkDetonate() {
        if (!hasDetonated && (Shape.intersect(getHitBox(), targetShip.getHitBox()).getBoundsInLocal().getWidth() != -1 || durationTimer <= EXPLOSION_DURATION)) {
            hasDetonated = true;
            ((ImageView) getSprite()).setY(getY() + getSprite().getBoundsInLocal().getHeight() / 2 - getSprite().getBoundsInLocal().getWidth() / 2);
            durationTimer = EXPLOSION_DURATION;
            setDamage(explosionDamage);
        }
        if (hasDetonated) {
            ((ImageView) getSprite()).setImage(detonateAnimation[Math.min((EXPLOSION_DURATION - durationTimer) / EXPLOSION_DURATION_STEP, 3)]);
//            System.out.println((EXPLOSION_DURATION-durationTimer)/EXPLOSION_DURATION_STEP);
        }
        return hasDetonated;
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        ((ImageView) getSprite()).setX(x);
        ((Circle) getHitBox()).setCenterX(getX() + getSprite().getBoundsInLocal().getWidth() / 2);
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        ((ImageView) getSprite()).setY(y);
        ((Circle) getHitBox()).setCenterY(getY() + getSprite().getBoundsInLocal().getHeight() / 2);
    }
}
