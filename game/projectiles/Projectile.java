package game.projectiles;

import game.GameObj;
import game.GameProperty;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

/**
 * Created by th174 on 1/14/2017.
 */
public abstract class Projectile implements GameObj {
    private Node mySprite;
    private double moveSpeed;
    private double damage;
    private boolean isActive;
    private Shape myHitBox;
    private double xPos;
    private double yPos;

    public Projectile(double x, double y, double movespeed, int dmg) {
        xPos = x;
        yPos = y;
        damage = dmg;
        moveSpeed = movespeed * GameProperty.getHeight() * GameProperty.SCALE_Y;
        isActive = true;
    }

    public boolean update() {
        return move();
    }

    public boolean move() {
        setX(xPos);
        setY(yPos);
        return getY() + getHeight() < GameProperty.getTop() || getY() > GameProperty.getBottom() || !isActive;
    }

    @Override
    public Node getSprite() {
        return mySprite;
    }

    protected void setSprite(Node sprite) {
        mySprite = sprite;
    }

    @Override
    public Shape getHitBox() {
        return myHitBox;
    }

    protected void setHitBox(Shape hitBox) {
        myHitBox = hitBox;
    }

    public void remove() {
        isActive = false;
    }

    protected boolean isActive() {
        return isActive;
    }

    public double getDamage() {
        return damage;
    }

    protected void setDamage(double dmg) {
        damage = dmg;
    }

    protected double getMoveSpeed() {
        return moveSpeed;
    }

    @Override
    public double getX() {
        return xPos;
    }

    @Override
    public void setX(double x) {
        xPos = x;
    }

    @Override
    public double getY() {
        return yPos;
    }

    @Override
    public void setY(double y) {
        yPos = y;
    }

    @Override
    public double getWidth() {
        return mySprite.getBoundsInParent().getWidth();
    }

    @Override
    public double getHeight() {
        return mySprite.getBoundsInParent().getHeight();
    }
}
