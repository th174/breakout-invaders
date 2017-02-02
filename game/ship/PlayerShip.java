package game.ship;

import game.GameProperty;
import game.projectiles.powerup.Powerup;
import game.projectiles.Projectile;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

import java.util.Collection;

/**
 * Created by th174 on 1/22/2017.
 */
public abstract class PlayerShip extends Ship {
    public static final double MOVE_BOTTOM_BOUND = 35.0 / 36;
    public static final double MOVE_UPPER_BOUND = 3.0 / 7;
    private final int maximumShield;
    private double xMoveTarget;
    private double yMoveTarget;
    private double remainingShield;
    private double moveSpeed;
    private boolean cheatMaxHP;
    private boolean cheatNukeAllEnemies;

    public PlayerShip(double x, double y, int sp, int hp, double size, double movespeed, Image sprite) {
        super(x, y, hp, size, sprite);
        remainingShield = sp;
        maximumShield = sp;
        moveSpeed = movespeed * GameProperty.getWidth() * GameProperty.SCALE_X;
    }

    protected void newLevel() {
        remainingShield = maximumShield;
    }

    @Override
    protected void move() {
        yMoveTarget = yMoveTarget < GameProperty.getHeight() * MOVE_UPPER_BOUND ? GameProperty.getHeight() * MOVE_UPPER_BOUND : yMoveTarget;
        yMoveTarget = yMoveTarget > GameProperty.getHeight() * MOVE_BOTTOM_BOUND ? GameProperty.getBottom() * MOVE_BOTTOM_BOUND : yMoveTarget;
        double dx = xMoveTarget - getX();
        double dy = yMoveTarget - getY();
        double mag = Math.hypot(dx, dy);
        setX(mag > moveSpeed ? getX() + dx / mag * moveSpeed : xMoveTarget);
        setY(mag > moveSpeed ? getY() + dy / mag * moveSpeed : yMoveTarget);
    }

    @Override
    protected void takeHit(Projectile p) {
        super.takeHit(p);
        if (p instanceof Powerup) {
            ((Powerup) p).usePowerup(this);
        }
    }

    @Override
    protected void takeDamage(double dmg) {
        if (remainingShield > dmg) {
            remainingShield -= dmg;
        } else {
            dmg -= remainingShield;
            remainingShield = 0;
            super.takeDamage(dmg);
        }
    }

    protected void updateCheats(Collection<Ship> enemyShips) {
        if (cheatMaxHP) {
            setRemainingHealth(Integer.MAX_VALUE);
        }
        if (cheatNukeAllEnemies) {
            enemyShips.forEach(Ship::handleDeath);
            cheatNukeAllEnemies = false;
        }
    }

    public void mouseInput(double x, double y, boolean mouseClicked) {
        xMoveTarget = x - getWidth() / 2;
        yMoveTarget = y;
    }

    public void keyInput(KeyCode code) {
        if (code.equals(KeyCode.N)) {
            cheatNukeAllEnemies = true;
            System.out.println("NukeAll = " + cheatNukeAllEnemies);
        } else if (code.equals(KeyCode.H)) {
            cheatMaxHP = !cheatMaxHP;
            System.out.println("MaxHP = " + cheatMaxHP);
        }
    }

    public double getRemainingShield() {
        return remainingShield;
    }

    protected void setRemainingShield(double shield) {
        remainingShield = Math.max(Math.min(maximumShield, shield), 0);
    }

    public double getMaximumShield() {
        return maximumShield;
    }
}
