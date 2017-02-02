package game.projectiles;

import game.GameProperty;
import game.ship.PlayerShip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by th174 on 1/16/2017.
 */
public abstract class Powerup extends Projectile {

    public Powerup(double x, double y, double size, double movespeed, Image sprite) {
        super(x, y, movespeed, 0);
        setSprite(new ImageView(sprite));
        ((ImageView) getSprite()).setPreserveRatio(true);
        ((ImageView) getSprite()).setSmooth(true);
        ((ImageView) getSprite()).setCache(true);
        ((ImageView) getSprite()).setFitWidth(size * GameProperty.getWidth() * GameProperty.SCALE_X);
        setHitBox(new Circle(x + getWidth() / 2, y + getHeight() / 2, getWidth() / 2, Color.CYAN));
    }

    @Override
    public boolean move() {
        setY(getY() + getMoveSpeed());
        return super.move();
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        ((ImageView) getSprite()).setX(x);
        ((Circle) getHitBox()).setCenterX(getX() + ((Circle) getHitBox()).getRadius());
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        ((ImageView) getSprite()).setY(y);
        ((Circle) getHitBox()).setCenterY(getY() + ((Circle) getHitBox()).getRadius());
    }

    public abstract void usePowerup(PlayerShip playerShip);
}