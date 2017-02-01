package game.projectiles;

import game.GameProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by th174 on 1/13/2017.
 */
public class Laser extends Projectile {

    public Laser(double x, double y, double width, double height, double movespeed, int damage, Color color) {
        super(x, y, movespeed, damage);
        setHitBox(new Rectangle(getX(), getY(), color));
        setSprite(getHitBox());
        ((Rectangle) getSprite()).setWidth(width * GameProperty.getWidth() * GameProperty.SCALE_X);
        ((Rectangle) getSprite()).setHeight(height * GameProperty.getHeight() * GameProperty.SCALE_Y);
        ((Rectangle) getSprite()).setArcHeight(5);
        ((Rectangle) getSprite()).setArcWidth(5);
    }

    @Override
    public boolean move() {
        setY(getY() + getMoveSpeed());
        return super.move();
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        ((Rectangle) getSprite()).setX(x);
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        ((Rectangle) getSprite()).setY(y);
    }
}
