package game;

import java.nio.file.Path;

/**
 * Created by th174 on 1/13/2017.
 */
public final class GameProperty {
    public static final double SCALE_X = 1.0 / 1920;
    public static final double SCALE_Y = 1.0 / 1080;
    private static double WIDTH;
    private static double HEIGHT;
    private static String basePath;

    private GameProperty() {
    }

    public static void set(double width, double height) {
        WIDTH = width;
        HEIGHT = height;
    }

    public static void setAbsolutePath(Path path) {
        basePath = path.toString();
    }

    public static String getAbsolutePath() {
        return basePath;
    }

    public static void setAbsolutePath(String path) {
        basePath = path;
    }

    public static double getWidth() {
        return WIDTH;
    }

    public static double getHeight() {
        return HEIGHT;
    }

    public static double getLeft() {
        return 0;
    }

    public static double getRight() {
        return WIDTH;
    }

    public static double getTop() {
        return 0;
    }

    public static double getBottom() {
        return HEIGHT;
    }
}
