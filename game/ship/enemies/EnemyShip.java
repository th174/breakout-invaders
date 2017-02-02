package game.ship.enemies;

import game.GameProperty;
import game.projectiles.powerup.PowerupBallMagnet;
import game.projectiles.powerup.PowerupExtraBall;
import game.projectiles.powerup.PowerupHealthPack;
import game.projectiles.Projectile;
import game.ship.Ship;
import javafx.scene.image.Image;

/**
 * Created by th174 on 1/13/2017.
 */
public abstract class EnemyShip extends Ship {
    public static final int STARTING_SHOOT_TIMER = 100;
    public static final double BASE_POWERUP_DROP_CHANCE = .015;
    private static double powerupDropChancePRD = BASE_POWERUP_DROP_CHANCE; //single global drop chance to be modified when any enemyship is destoryed. single copy of variable avoids race condition when two enemy ships die at the same time
    private final Image[] mySprites;
    private final int moveTimerVariance;
    private final int moveTimerMin;
    private double xMoveSpeed;
    private double yMoveSpeed;
    private int moveTimer = -1;
    private int shootTimer;

    public EnemyShip(double x, double y, int hp, double size, Image sprite1) {
        this(x, y, hp, size, 0, 0, sprite1, null);
    }

    public EnemyShip(double x, double y, int hp, double size, Image sprite1, Image sprite2) {
        this(x, y, hp, size, 0, 0, sprite1, sprite2);
    }

    public EnemyShip(double x, double y, int hp, double size, double msx, double msy, Image sprite) {
        this(x, y, hp, msx, msy, size, sprite, null);
    }

    public EnemyShip(double x, double y, int hp, double size, double msx, double msy, Image sprite1, Image sprite2) {
        this(x, y, hp, size, msx, msy, Integer.MAX_VALUE, 0, sprite1, sprite2);
    }

    public EnemyShip(double x, double y, int hp, double size, double msx, double msy, int moveMin, int moveVar, Image sprite1, Image sprite2) {
        super(x, y, hp, size, sprite1);
        xMoveSpeed = Math.random() > 0.5 ? msx : -msx;
        yMoveSpeed = msy;
        moveTimerMin = moveMin;
        moveTimerVariance = moveVar;
        mySprites = new Image[]{sprite1, sprite2};
        shootTimer = STARTING_SHOOT_TIMER;
    }

    @Override
    protected void move() {
        if (isAlive()) {
            double x = getX();
            double y = getY();
            if (moveTimer <= 0 || x < GameProperty.getLeft() || x > GameProperty.getRight() - getWidth()) {
                moveTimer = (int) (Math.random() * moveTimerVariance + moveTimerMin);
                y += yMoveSpeed;
                xMoveSpeed = -1 * xMoveSpeed;
            }
            x += xMoveSpeed;
            moveTimer--;
            setX(x);
            setY(y);
        }
    }

    @Override
    protected void takeHit(Projectile p) {
        super.takeHit(p);
        updateDamagedSprite();
    }

    protected void updateDamagedSprite() {
        if (mySprites.length > 1 && mySprites[1] != null && getRemainingHealth() <= getMaximumHealth() / 2 && mySprites.length > 1) {
            getSprite().setImage(mySprites[1]);
        }
    }

    @Override
    protected void updateProjectiles() {
        shootTimer--;
        super.updateProjectiles();
    }

    @Override
    protected boolean handleDeath() {
        if (isAlive()) {
            dropPowerUp();
        }
        return super.handleDeath();
    }

    protected void dropPowerUp() {
        if (Math.random() <= powerupDropChancePRD) {
            getProjectiles().add(new PowerupExtraBall(getX() + getWidth() / 2 - PowerupExtraBall.DEFAULT_SIZE / 2, getY() + getHeight() / 2 - PowerupExtraBall.DEFAULT_SIZE / 2));
            powerupDropChancePRD = BASE_POWERUP_DROP_CHANCE;
        } else if (Math.random() <= powerupDropChancePRD) {
            getProjectiles().add(new PowerupBallMagnet(getX() + getWidth() / 2 - PowerupHealthPack.DEFAULT_SIZE / 2, getY() + getHeight() / 2 - PowerupHealthPack.DEFAULT_SIZE / 2));
            powerupDropChancePRD = BASE_POWERUP_DROP_CHANCE;
        } else if (Math.random() <= powerupDropChancePRD) {
            getProjectiles().add(new PowerupHealthPack(getX() + getWidth() / 2 - PowerupHealthPack.DEFAULT_SIZE / 2, getY() + getHeight() / 2 - PowerupHealthPack.DEFAULT_SIZE / 2));
            powerupDropChancePRD = BASE_POWERUP_DROP_CHANCE;
        } else {
            powerupDropChancePRD += BASE_POWERUP_DROP_CHANCE;
        }
    }

    protected boolean resetShootTimer(int max) {
        if (shootTimer <= 0) {
            shootTimer = max;
            return true;
        }
        return false;
    }

    protected int getShootTimer() {
        return shootTimer;
    }

    public int getScore() {
        return (int) getMaximumHealth();
    }
}
