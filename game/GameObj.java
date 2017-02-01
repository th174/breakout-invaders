package game;

import javafx.scene.Node;

/**
 * Created by th174 on 1/18/2017.
 */
public interface GameObj {

    public abstract double getX();

    public abstract void setX(double y);

    public abstract double getY();

    public abstract void setY(double x);

    public abstract double getWidth();

    public abstract double getHeight();

    public abstract Node getHitBox();

    public abstract Node getSprite();

}
