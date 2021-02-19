import java.awt.*;
import java.util.Collection;
import java.util.Random;
import static java.lang.StrictMath.*;

public class Splash {
    public static Random rand = new Random();
    double x;
    double y;
    double speed;
    double angle;

    double radius = 0;
    double range;

    int count = 0;

    boolean flagForRemoval = false;

    Color color = new Color(255, 224, 214);

    public Splash(double x, double y, double speed, double angle, int minRange, int maxRange) {

        this.x = x;
        this.y = y;
        this.speed = speed;
        this.angle = angle;

        this.range = rand.nextInt(maxRange - minRange + 1) + minRange;
    }


    public void move() {
        count++;
        int new_alpha = color.getAlpha() - 3;
        if (new_alpha < 0) {
            new_alpha = 0;
            flagForRemoval = true;
        }
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), new_alpha);
        if (Game.count % 2 == 0) {
            radius++;
        }
    }
}