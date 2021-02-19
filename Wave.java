import java.awt.*;

import static java.lang.StrictMath.*;

public class Wave {
    double x;
    double y;
    double angle;
    double angle2;
    double length;
    double speed;
    double percT = 100;
    Color color = new Color(29, 37, 248, 255);
    boolean flagForRemoval = false;

    public Wave(double x, double y, double angle, double length, double speed, double angle2, Color color) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.length = length;
        this.speed = speed;
        this.angle2 = angle2;
        this.color = color;
        this.color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 150);
    }
    public void move() {
        percT -= 0.5;
        x += speed * cos(angle2);
        y += speed * sin(angle2);
        int new_alpha = color.getAlpha() - 2;
        if (new_alpha < 0) {
            new_alpha = 0;
            flagForRemoval = true;
        }
        if (speed <= 0) {
            flagForRemoval = true;
        }
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), new_alpha);
        speed -= 0.01;
        if (speed < 0) {
            speed = 0;
        }
    }
    public double wavex2(){
        double x;
        x = this.x - length*cos(angle);

        return x;
    }
    public double wavey2(){
        double y;
        y = this.y - length*sin(angle);

        return y;
    }

}
