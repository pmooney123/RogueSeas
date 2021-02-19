import java.awt.*;
import java.util.Collection;
import java.util.Random;
import static java.lang.StrictMath.*;

public class Particle {
    public static Random rand = new Random();
    double x;
    double y;
    double speed;
    double angle;

    double range;

    int count = 0;

    boolean flagForRemoval = false;

    Color color;

    public Particle(double x, double y, double speed, double angle, int minRange, int maxRange, String type) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.angle = angle;

        this.range = rand.nextInt(maxRange - minRange) + minRange;
        if (type.equals("wood")) {
            switch (rand.nextInt(6)) {
                case (0) -> {
                    color = new Color(82, 33, 24);
                }
                case (1) -> {
                    color = new Color(182, 103, 70);
                }
                case (2) -> {
                    color = new Color(108, 75, 39);
                }
                case (3) -> {
                    color = new Color(104, 88, 88);
                }
                case (4) -> {
                    color = new Color(56, 56, 34);
                }
                case (5) -> {
                    color = new Color(113, 80, 0);
                }

            }
        }
        if (type.equals("flash")) {
            switch (rand.nextInt(6)) {
                case (0) -> {
                    color = new Color(153, 85, 85);
                }
                case (1) -> {
                    color = new Color(106, 12, 12);
                }
                case (2) -> {
                    color = new Color(231, 138, 20);
                }
                case (3) -> {
                    color = new Color(99, 98, 98);
                }
                case (4) -> {
                    color = new Color(139, 138, 131);
                }
                case (5) -> {
                    color = new Color(196, 196, 196);
                }

            }
        }
        if (type.equals("rock")) {
            color = Color.gray;
        }
    }
/*
            case (6) -> {
                color = new Color(170, 170, 164);
                this.range = range * 2;
                this.speed = rand.nextInt(2) + 1;
            }
            case (7) -> {
                color = new Color(109, 109, 108);
                this.range = range * 2;
                this.speed = rand.nextInt(2) + 1;
            }
 */

    public void move() {
        count += 1;

        x += cos(angle) * 1;
        y += sin(angle) * 1;
        if (rand.nextInt(2) == 0) {
            x+=0.5;
        } else {
            x-=0.5;
        }
        if (rand.nextInt(2) == 0) {
            y+=0.5;
        } else {
            y-=0.5;
        }
        if (count > range) {
            flagForRemoval = true;
        }

    }
}