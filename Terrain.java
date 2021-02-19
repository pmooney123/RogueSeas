import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

public class Terrain {
    double x = 20;
    double y = 200;
    double width;
    double length;
    double angle = 0;

    public Terrain(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.width = size;
        this.length = size;
        this.angle = Game.rand.nextInt(629) / 100;
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

}
