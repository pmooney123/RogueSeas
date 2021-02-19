import java.awt.*;
import java.util.Random;
import static java.lang.StrictMath.*;

public class Cannonball {
    public static Random rand = new Random();
    double x;
    double y;
    double speed;
    double angle;

    double range;

    int count = 0;
    int team = 0;

    boolean flagForRemoval = false;

    Color color = new Color(38, 22, 14);

    public Cannonball(double x, double y, double speed, double angle, int minRange, int maxRange) {
        for (int j = 0; j < 100; j++) {
            double angle2 = angle + ((rand.nextInt(15) - 7) / 100.0);
            Game.particles.add(new Particle(x, y, rand.nextInt(5) + 2, angle2, 10, 25, "flash"));
        }
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.angle = angle;

        this.range = rand.nextInt(maxRange - minRange) + minRange;



    }
    public Cannonball(double x, double y, double speed, double angle, int minRange, int maxRange, int team) {
        for (int j = 0; j < 100; j++) {
            double angle2 = angle + ((rand.nextInt(15) - 7) / 100.0);
            Game.particles.add(new Particle(x, y, rand.nextInt(5) + 2, angle2, 10, 25, "flash"));
        }
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.angle = angle;

        this.range = rand.nextInt(maxRange - minRange) + minRange;



    }

    public void move() {
        count += 1;

        x += cos(angle) * 1;
        y += sin(angle) * 1;

        if (count > range) {
            Game.splashes.add(new Splash(x, y, 0, 0, 8, 10));
            flagForRemoval = true;
        }

        for (Ship ship : Game.enemyShips) {
            if (ship.team != team) {
                if (checkCollision(new Polygon(ship.xCorners(), ship.yCorners(), 4), new Point((int) x, (int) y))) {
                    flagForRemoval = true;
                    for (int j = 0; j < 100; j++) {
                        //double angle2 = 3.14 + angle + ((rand.nextInt(145) - 77) / 100.0);
                        double angle2 = rand.nextInt(629) / 100.0;
                        Game.particles.add(new Particle(x, y, rand.nextInt(5) + 2, angle2, 10, 25, "wood"));
                    }
                }
            }
        }

        for (Terrain terrain: Game.terrains) {
            if (checkCollision(new Polygon(terrain.xCorners(), terrain.yCorners(), 4), new Point((int)x,(int)y))) {
                flagForRemoval = true;
                terrain.width--;
                terrain.length--;
                for (int j = 0; j < 100; j++) {
                    //double angle2 = 3.14 + angle + ((rand.nextInt(145) - 77) / 100.0);
                    double angle2 = rand.nextInt(629) / 100.0;
                    Game.particles.add(new Particle(x, y, rand.nextInt(5) + 2, angle2, 10, 25, "rock"));
                }
            }
        }
    }


    public boolean checkCollision(Polygon poly, Point p) {
        if (poly.contains(p)) {
            return true;
        }

        return false;
    }
}