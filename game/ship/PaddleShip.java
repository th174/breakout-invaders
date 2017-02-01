package game.ship;

import game.GameProperty;
import game.projectiles.PlasmaBall;
import game.projectiles.Projectile;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by th174 on 1/14/2017.
 */
public class PaddleShip extends PlayerShip {
    public static final double DEFAULT_SIZE = 300;
    public static final double DEFAULT_MOVESPEED = 24;
    public static final int DEFAULT_SHIELD = 100;
    public static final int DEFAULT_MAX_HEALTH = 100;
    public static final String DEFAULT_SPRITE_LOCATION = "resources/paddleship.png";
    public static final int LOST_BALL_HEALTH_PENALTY = 25;
    public static final double BALL_HIT_SHIELD_RECHARGE = 15;
    public static final double SHIELD_DEPLETE_RATE = BALL_HIT_SHIELD_RECHARGE / 500;
    public static final double SHIELD_RECHARGE_COOLDOWN = 50;
    public static final int BALL_TETHER_DURATION = 400;
    public static final double HITBOX_OFFSET = 64.0 / 650;
    public static final double PADDLE_FLAT_BOUNCE = 0;
    public static final double PADDLE_FLAT_BOUNCE_REVERSE = Math.toRadians(180);
    public static final double PADDLE_HITBOX_LEFT_REGION_X = 209.0 / 650;
    public static final double PADDLE_HITBOX_RIGHT_REGION_X = 1 - PADDLE_HITBOX_LEFT_REGION_X;
    public static final double PADDLE_HITBOX_HEIGHT = 125.0 / 190;
    public static final double PADDLE_HITBOX_LEFT_WIDTH = 215.0 / 650;
    public static final double PADDLE_HITBOX_RIGHT_WIDTH = PADDLE_HITBOX_LEFT_WIDTH;
    private final ArrayList<Shape> myPaddleHitBoxes;
    private int tetherTimer;
    private double shieldRechargeTimer;
    private int ballMagnetTimer;
    private boolean cheatNewBall;
    private boolean cheatBallMagnetOn;

    public PaddleShip(double x, double y, int sp, int hp, double size, double movespeed, Image sprite) {
        super(x, y, sp, hp, size, movespeed, sprite);
        shieldRechargeTimer = 0;
        tetherTimer = BALL_TETHER_DURATION;
        ballMagnetTimer = 0;
        getHitBox().setRadiusX(getWidth() / 2 - getWidth() * HITBOX_OFFSET);
        myPaddleHitBoxes = new ArrayList<>();
        Shape region1 = new Ellipse(getX() + getWidth() * PADDLE_HITBOX_LEFT_REGION_X, getY() + getHeight() * PADDLE_HITBOX_HEIGHT / 2,
                getWidth() * PADDLE_HITBOX_LEFT_WIDTH, getHeight() * PADDLE_HITBOX_HEIGHT / 2);
        region1.setFill(Color.BLUEVIOLET);
        myPaddleHitBoxes.add(region1);
        Shape region2 = new Rectangle(getX() + getWidth() * PADDLE_HITBOX_LEFT_REGION_X, getY(),
                getWidth() * (PADDLE_HITBOX_RIGHT_REGION_X - PADDLE_HITBOX_LEFT_REGION_X), getHeight() * PADDLE_HITBOX_HEIGHT / 2);
        region2.setFill(Color.BLUEVIOLET);
        myPaddleHitBoxes.add(region2);
        Shape region3 = new Ellipse(getX() + getWidth() * PADDLE_HITBOX_RIGHT_REGION_X, getY() + getHeight() * PADDLE_HITBOX_HEIGHT / 2,
                getWidth() * PADDLE_HITBOX_RIGHT_WIDTH, getHeight() * PADDLE_HITBOX_HEIGHT / 2);
        region3.setFill(Color.BLUEVIOLET);
        myPaddleHitBoxes.add(region3);
        Shape region4 = new Rectangle(getX() + getWidth() * PADDLE_HITBOX_LEFT_REGION_X, getY() + getHeight() * PADDLE_HITBOX_HEIGHT / 2,
                getWidth() * (PADDLE_HITBOX_RIGHT_REGION_X - PADDLE_HITBOX_LEFT_REGION_X), getHeight() * PADDLE_HITBOX_HEIGHT / 2);
        region4.setFill(Color.RED);
        myPaddleHitBoxes.add(region4);
        newBall();
    }

    public PaddleShip(double x, double y) {
        this(x, y, DEFAULT_SHIELD, DEFAULT_MAX_HEALTH, DEFAULT_SIZE, DEFAULT_MOVESPEED, new Image(GameProperty.getAbsolutePath() + DEFAULT_SPRITE_LOCATION));
    }

    public void newBall() {
        if (isAlive() && getRemainingHealth() > 0) {
            PlasmaBall pb = new PlasmaBall(this);
            getProjectiles().add(pb);
            pb.move();
        }
        tetherTimer = BALL_TETHER_DURATION;
    }

    @Override
    public boolean update(Collection<Ship> enemyShips) {
        if (getProjectiles().isEmpty() && getRemainingHealth() >= 0) {
            newBall();
            setRemainingHealth(getRemainingHealth() - LOST_BALL_HEALTH_PENALTY);
        }
        updateHealthAndShield();
        updateCheats(enemyShips);
        return super.update(enemyShips);
    }

    @Override
    public void newLevel() {
        int numBalls = (int) getProjectiles().stream().filter(PlasmaBall.class::isInstance).peek(Projectile::remove).count();
        for (int i = 0; i < numBalls; i++) {
            newBall();
        }
        super.newLevel();
    }

    @Override
    protected void updateProjectiles() {
        if (tetherTimer <= 0 && ballMagnetTimer <= 0) {
            untetherBall();
        }
        tetherTimer--;
        ballMagnetTimer--;
        if (isAlive()) {
            getProjectiles().parallelStream()
                    .filter(PlasmaBall.class::isInstance)
                    .map(PlasmaBall.class::cast)
                    .forEach(this::checkBallBounce);
        }
        super.updateProjectiles();
    }

    private boolean checkBallBounce(PlasmaBall p) {
        if (!p.isTethered()) {
            double pCenterXLocal = p.getX() + p.getWidth() / 2 - getX();
            Shape centerIntersection = Shape.intersect(p.getHitBox(), myPaddleHitBoxes.get(1));
            if (centerIntersection.getBoundsInLocal().getWidth() != -1
                    && pCenterXLocal >= getWidth() * PADDLE_HITBOX_LEFT_REGION_X && pCenterXLocal <= getWidth() * PADDLE_HITBOX_RIGHT_REGION_X) {
                p.setY(Math.min(centerIntersection.getBoundsInParent().getMinY() - p.getHeight(), p.getY()));
                p.bounce(PADDLE_FLAT_BOUNCE);
                regenerateShield(p);
                return true;
            }
            Shape bottomIntersection = Shape.intersect(p.getHitBox(), myPaddleHitBoxes.get(3));
            if (bottomIntersection.getBoundsInLocal().getWidth() != -1) {
                p.setY(Math.max(bottomIntersection.getBoundsInParent().getMaxY(), p.getY()));
                p.bounce(PADDLE_FLAT_BOUNCE_REVERSE);
                return true;
            }
            Shape leftIntersection = Shape.intersect(p.getHitBox(), myPaddleHitBoxes.get(0));
            if (leftIntersection.getBoundsInLocal().getWidth() != -1) {
                if (pCenterXLocal > 0 && p.getY() + p.getHeight() / 2 <= getY() + getHeight() * PADDLE_HITBOX_HEIGHT / 2) {
                    p.setY(Math.min(leftIntersection.getBoundsInParent().getMinY() - p.getHeight(), p.getY()));
                } else if (pCenterXLocal > 0) {
                    p.setY(Math.max(leftIntersection.getBoundsInParent().getMaxY(), p.getY()));
                }
                p.bounce((Ellipse) myPaddleHitBoxes.get(0), leftIntersection);
                p.move();
                regenerateShield(p);
                return true;
            }
            Shape rightIntersection = Shape.intersect(p.getHitBox(), myPaddleHitBoxes.get(2));
            if (rightIntersection.getBoundsInLocal().getWidth() != -1) {
                if (pCenterXLocal < getWidth() && p.getY() + p.getHeight() / 2 <= getY() + getHeight() * PADDLE_HITBOX_HEIGHT / 2) {
                    p.setY(Math.min(rightIntersection.getBoundsInParent().getMinY() - p.getHeight(), p.getY()));
                } else if (pCenterXLocal < getWidth()) {
                    p.setY(Math.max(rightIntersection.getBoundsInParent().getMaxY(), p.getY()));
                }
                p.bounce((Ellipse) myPaddleHitBoxes.get(2), rightIntersection);
                p.move();
                regenerateShield(p);
                return true;
            }
        }
        return false;
    }

    private void updateHealthAndShield() {
        setRemainingShield(getRemainingShield() - SHIELD_DEPLETE_RATE);
        shieldRechargeTimer--;
    }

    private void regenerateShield(PlasmaBall pb) {
        if (isAlive()) {
            if (ballMagnetTimer > 0) {
                pb.tether();
                pb.setAngle(Math.toRadians(90));
            }
            if (shieldRechargeTimer <= 0) {
                setRemainingShield(getRemainingShield() + BALL_HIT_SHIELD_RECHARGE);
                shieldRechargeTimer = SHIELD_RECHARGE_COOLDOWN;
            }
        }
    }

    private void untetherBall() {
        for (Projectile p : getProjectiles()) {
            if (p instanceof PlasmaBall && ((PlasmaBall) p).isTethered()) {
                ((PlasmaBall) p).untether();
                p.move();
                tetherTimer = BALL_TETHER_DURATION;
                break;
            }
        }
    }

    @Override
    protected boolean handleDeath() {
        for (int i = 0; i < myPaddleHitBoxes.size(); i += 2) {
            ((Ellipse) myPaddleHitBoxes.get(i)).setRadiusX(0);
            ((Ellipse) myPaddleHitBoxes.get(i)).setRadiusY(0);
        }
        for (int i = 1; i < myPaddleHitBoxes.size(); i += 2) {
            ((Rectangle) myPaddleHitBoxes.get(i)).setWidth(0);
            ((Rectangle) myPaddleHitBoxes.get(i)).setWidth(0);
        }
        if (isAlive()) {
            getProjectiles().stream()
                    .filter(PlasmaBall.class::isInstance)
                    .map(PlasmaBall.class::cast)
                    .forEach(PlasmaBall::untether);
        }
        return super.handleDeath();
    }

    @Override
    protected void updateCheats(Collection<Ship> enemyShips) {
        super.updateCheats(enemyShips);
        if (cheatNewBall) {
            newBall();
            cheatNewBall = false;
        }
        if (cheatBallMagnetOn) {
            ballMagnetTimer = 2;
        }
    }

    @Override
    public void mouseInput(double x, double y, boolean mouseClicked) {
        super.mouseInput(x, y, mouseClicked);
        if (mouseClicked) {
            untetherBall();
        }
    }

    @Override
    public void keyInput(KeyCode code) {
        if (code.equals(KeyCode.B)) {
            cheatNewBall = true;
            System.out.println("NewBall = " + cheatNewBall);
        } else if (code.equals(KeyCode.M)) {
            cheatBallMagnetOn = !cheatBallMagnetOn;
            System.out.println("BallMagnet = " + cheatBallMagnetOn);
        } else {
            super.keyInput(code);
        }
    }

    public void resetBallMagnetTimer(int duration) {
        ballMagnetTimer = duration;
    }

    public ArrayList<Shape> getPaddleHitBoxes() {
        return myPaddleHitBoxes;
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        ((Ellipse) myPaddleHitBoxes.get(0)).setCenterX(x + getWidth() * PADDLE_HITBOX_LEFT_REGION_X);
        ((Ellipse) myPaddleHitBoxes.get(2)).setCenterX(x + getWidth() * PADDLE_HITBOX_RIGHT_REGION_X);
        ((Rectangle) myPaddleHitBoxes.get(1)).setX(x + getWidth() * PADDLE_HITBOX_LEFT_REGION_X);
        ((Rectangle) myPaddleHitBoxes.get(3)).setX(x + getWidth() * PADDLE_HITBOX_LEFT_REGION_X);
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        ((Ellipse) myPaddleHitBoxes.get(0)).setCenterY(y + getHeight() * PADDLE_HITBOX_HEIGHT / 2);
        ((Ellipse) myPaddleHitBoxes.get(2)).setCenterY(y + getHeight() * PADDLE_HITBOX_HEIGHT / 2);
        ((Rectangle) myPaddleHitBoxes.get(1)).setY(y);
        ((Rectangle) myPaddleHitBoxes.get(3)).setY(y + getHeight() * PADDLE_HITBOX_HEIGHT / 2);
    }

    public int getPowerupMagnetStatus() {
        return ballMagnetTimer;
    }
}
