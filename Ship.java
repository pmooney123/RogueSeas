import java.awt.*;
import java.util.Random;

import static java.lang.StrictMath.*;

public class Ship {
    public static Random rand = new Random();
    double x = 20;
    double y = 200;

    double s = 2;
    double a = 1;
    double angle = 0;

    double width = 120;
    double length = 40;

    int cannon_CD = 15;
    int cannon1cd = 0;
    int cannon2cd = 0;
    int cannon3cd = 0;
    int cannon4cd = 0;
    int cannon5cd = 0;
    int cannon6cd = 0;

    int team = 0;

    double turn_speed = 0.02;
    double max_acceleration = 0.01;

    boolean flagForRemoval = false;


    //AI
    String turning = "straight";
    String speed = "down";

    public Ship(int team) {
        this.team = team;
    }

    public int x() {

        return (int) x;
    }
    public int y() {

        return (int) y;
    }
    public void move(double angle_change, double speed_change) {
        s += speed_change;
        if (s < 0) {
            s = 0;
        }
        if (s > 3) {
            s = 3;
        }

        angle += angle_change;
        if (angle > 3.14) {
            angle -= 6.28;
        }
        if (angle < -3.14) {
            angle += 6.28;
        }

        x += cos(angle) * s;
        y += sin(angle) * s;

        //wavephsyics
        if (Game.count % 1 == 0 && s > 1) {
            int xx2 = xCorners()[3];
            int yy2 = yCorners()[3];
            int xx = xCorners()[0];
            int yy = yCorners()[0];
            int xx3 = backCenterX();
            int yy3 = backCenterY();

            Game.waves.add(new Wave(xx, yy, angle - 0.2, 4, Math.sqrt(s) - 0.5, angle + 3.14/2, new Color(29, 37, 248, 255)));
            Game.waves.add(new Wave(xx2, yy2, angle + 0.2, 4, Math.sqrt(s)- 0.5,angle - 3.14/2, new Color(29, 37, 248, 255)));

            Game.waves.add(new Wave(xx, yy, angle - 0.2, 4, Math.sqrt(s)- 1, angle + 3.14/2, new Color(199, 199, 232, 255)));
            Game.waves.add(new Wave(xx2, yy2,  angle + 0.2, 4, Math.sqrt(s)- 1, angle - 3.14/2, new Color(205, 207, 229, 255)));
        }


        //outofbounds behavior
        int outofbounds = 50000;
        if (x < -outofbounds || x > outofbounds || y < -outofbounds || y > outofbounds) {
            x = 0; y = 0;
        }
    }
    public void moveAI() {
        double accel = 0;
        double turn = 0;
        if (turning.equals("left")) {
            turn = -turn_speed;
        }
        if (turning.equals("right")) {
            turn = turn_speed;
        }
        if (speed.equals("up")) {
            accel = max_acceleration;
        }
        if (speed.equals("down")) {
            accel = -2*max_acceleration;
        }

        //System.out.println("TURNING: " + turning + " SPEEDING:" + s + " XY " + x + " " + y);

        turn = 0;
        accel = 0.0;
        move(turn, accel);



    }

    public void corners() {
        double c = cos(angle);
        double s = sin(angle);

        double rx = -width/2 * c - length/2 * s;
        double ry = -width/2 * s - length/2 * c;


    }
    public int[] xCorners() {
        double c = cos(angle);
        double s = sin(angle);

        double rx = -width/2 * c - length/2 * s;
        double rx2 = width/2 * c - length/2 * s;
        return new int[]{(int) (x + rx), (int) (x + rx2),(int) (x - rx), (int) (x - rx2) };
    }
    public int[] yCorners() {
        double c = cos(angle);
        double s = sin(angle);

        double ry = -width/2 * s + length/2 * c;
        double ry2 = width/2 * s + length/2 * c;
        return new int[]{(int) (y + ry), (int) (y + ry2),(int) (y - ry), (int) (y - ry2) };
    }

    public int[] xCornersAligned() {
        double c = cos(angle);
        double s = sin(angle);

        double rx = -width/2 * c - length/2 * s;
        double rx2 = width/2 * c - length/2 * s;
        return new int[]{(int) ((x - Game.xoff) + rx), (int) ((x - Game.xoff)  + rx2),(int) ((x - Game.xoff)  - rx), (int) ((x - Game.xoff)  - rx2) };
    }
    public int[] yCornersAligned() {
        double c = cos(angle);
        double s = sin(angle);

        double ry = -width/2 * s + length/2 * c;
        double ry2 = width/2 * s + length/2 * c;
        return new int[]{(int) ((y - Game.yoff) + ry) , (int) ((y - Game.yoff) + ry2),(int) ((y - Game.yoff) - ry), (int) ((y - Game.yoff) - ry2) };
    }



    public int[] xCannonCorners() {
        double c = cos(angle);
        double s = sin(angle);

        double rx = -width/5 * c - length/2 * s;
        double rx2 = width/5 * c - length/2 * s;
        double rx3 = 0  - length/2 * s;
        return new int[]{(int) (x + rx), (int) (x + rx2), (int) (x + rx3),(int) (x - rx), (int) (x - rx2), (int) (x - rx3)};
    }
    public int[] yCannonCorners() {
        double c = cos(angle);
        double s = sin(angle);

        double ry = -width/5 * s + length/2 * c;
        double ry2 = width/5 * s + length/2 * c;
        double ry3 = 0 + length/2 * c;
        return new int[]{(int) (y + ry), (int) (y + ry2), (int) (y + ry3),(int) (y - ry), (int) (y - ry2), (int) (y - ry3)};
    }

    public int frontCenterX() {

        return (int) (x + cos(angle) * width/2);
    }
    public int frontCenterY() {

        return (int) (y + sin(angle) * width/2);
    }
    public int cannonFrontX() {
        return (int) (x + cos(angle) * 21);
    }
    public int cannonFrontY() {

        return (int) (y + sin(angle) * 23);
    }
    public int cannonBackX() {

        return (int) (x - cos(angle) * 17);
    }
    public int cannonBackY() {

        return (int) (y - sin(angle) * 17);
    }
    public int backCenterX() {

        return (int) (x - cos(angle) * width/2);
    }
    public int backCenterY() {

        return (int) (y - sin(angle) * width/2);
    }
}
