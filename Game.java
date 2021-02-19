
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class Game implements ActionListener {
    public static Game game;
    public static Random rand = new Random();
    public final int WIDTH = 1800, HEIGHT = 1000;
    public static int MX = 0, MY = 0;
    public static boolean DOWN_HELD = false;
    public static boolean UP_HELD = false;
    public static boolean LEFT_HELD = false;
    public static boolean RIGHT_HELD = false;
    public static boolean G_HELD = false;
    public static Renderer renderer;
    public static String input = "null";
    public static int count = 0;
    public static long timeElapsed = 0;
    public static int xoff; public static int yoff;
    public static int boxx1; int boxx2; int boxy1; int boxy2;
    public static boolean drawingSelector = false;
    public static boolean shiftHeld = false;
    public static boolean spaceHeld = false;
    public static boolean eHeld = false;
    public static boolean qHeld = false;
    public static Ship ship = new Ship(1);
    public static double zf = 2;
    public static BufferedImage ship2 = null;
    public static double stearingfactor = 0;
    public static ArrayList<Ship> enemyShips = new ArrayList<>();
    public static ArrayList<Wave> waves = new ArrayList<>();
    public static ArrayList<Particle> particles = new ArrayList<>();
    public static ArrayList<Splash> splashes = new ArrayList<>();
    public static ArrayList<Cannonball> cannonBalls = new ArrayList<>();
    public static ArrayList<Terrain> terrains = new ArrayList<>();
    public Game() {
        JFrame jframe = new JFrame();
        jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //jframe.setUndecorated(true);
        jframe.setVisible(true);
        Timer timer = new Timer(20, this);
        renderer = new Renderer();
        jframe.setSize(WIDTH, HEIGHT);
        jframe.setVisible(true);
        jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
        //jframe.setResizable(false);

        jframe.add(renderer);
        addKeyListenerHere(jframe);
        addMouseListenerHere(jframe);
        timer.start();
        jframe.setFocusable(true);
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        jframe.getContentPane().setCursor(blankCursor);

        loadWorld();

        try {
            ship2 = ImageIO.read(new File("SHIP.png"));
        } catch (IOException e) {
            System.out.println("ERROR LOADING PNG");
        }
        for (int j = 0; j < 100; j++) {
            terrains.add(new Terrain(rand.nextInt(5000) - 2500, rand.nextInt(5000) - 2500, rand.nextInt(100)));
        }
        enemyShips.add(new Ship(2));
    }

    public static void main(String[] args) {
        game = new Game();
    }
    public void repaint(Graphics2D g) {
        Instant start = Instant.now();
        yoff = (int) (ship.y + enemyShips.get(0).y)/2- 450;
        xoff = (int) (ship.x + enemyShips.get(0).x)/2- 900;

        g.setColor(new Color(12, 151, 231));
        g.fillRect(0,0,WIDTH,HEIGHT);

        g.setColor(Color.gray);
        for (Terrain terrain : terrains) {
            g.fillPolygon(terrain.xCornersAligned(), terrain.yCornersAligned(), 4);
        }

        statsUpdate(start, g);
        paintCursor(g);
        paintSelectBox(g);
        Instant finish = Instant.now();

        for (Wave wave : waves) {
            g.setColor(wave.color);
            g.drawLine((int) wave.x - xoff, (int) wave.y - yoff, (int) wave.wavex2() - xoff, (int) wave.wavey2() - yoff);
            wave.move();
        }
        for (int j = 0; j < waves.size(); j++) {
            if (waves.get(j).flagForRemoval) {
                waves.remove(j);
                j--;
            }
        }

        cannonBalls(g);
        splashes(g);
        enemyShips(g);
        ship.move(getAngleChange(), getSpeedChange());

        g.setColor(new Color(94, 54, 5));
        //g.fillPolygon(ship.xCorners(), ship.yCorners(), 4);

        AffineTransform backup = g.getTransform();
        AffineTransform trans = new AffineTransform();
        trans.translate(ship.x - xoff - ship2.getWidth() / 2.0, ship.y - yoff - ship2.getHeight() /2);
        trans.rotate( ship.angle, ship2.getWidth()/2.0, ship2.getHeight()/2.0 ); // the points to rotate around (the center in my example, your left side for your problem)
        g.transform( trans );
        g.drawImage( ship2, (int)0, (int)0,null );  // the actual location of the sprite
        g.setTransform( backup ); // restore previous transform

        particles(g);

        //UI
        g.setColor(new Color(0, 0, 0));
        g.drawString("angle:" + Math.round(ship.angle*100), 100, 100);
        g.drawString("speed:" + Math.round(ship.s*100) , 100, 120);
        g.drawString("SF:" + Math.round(stearingfactor*1000) , 100, 160);
        g.drawString( MX + " " + MY, 100, 190);
        timeElapsed = Duration.between(start, finish).toMillis();
        g.drawString("delay:" + timeElapsed , 100, 140);
        count++;

    }

    private double getAngleChange() {
        double a = 0;

        if (stearingfactor > 0.02) {
            stearingfactor = 0.02;
        }
        if (stearingfactor < -0.02) {
            stearingfactor = -0.02;
        }

        if (LEFT_HELD) {
            /*
            stearingfactor -= 0.0003;

             */
            a = -ship.turn_speed;
        }
        if (RIGHT_HELD) {
            /*
            stearingfactor += 0.0003;

             */
            a = ship.turn_speed;
        }

        a += stearingfactor;
        return a;
    }
    private double getSpeedChange() {
        double s = 0;
        if (UP_HELD) {
            s = ship.max_acceleration;
        }
        if (DOWN_HELD) {
            s = -ship.max_acceleration * 2;
        }
        return s;
    }

    public void loadWorld() {

    }
    //VAR MANAGEMENT
    public void statsUpdate(Instant start, Graphics2D g) {
        g.setFont(new Font("Monospaced",Font.BOLD,  11));
        input = "default";
        if (true) {
            if (UP_HELD) {
                input = "w";
            }
            if (DOWN_HELD) {
                input = "s";
            }
            if (LEFT_HELD) {
                input = "d";
            }
            if (RIGHT_HELD) {
                input = "a";
            }
        } //handle key inputs
        MX = MouseInfo.getPointerInfo().getLocation().x;
        MY = MouseInfo.getPointerInfo().getLocation().y;

    } //tracks ping, count, resets inputs, processes key inputs
    public void cannonBalls(Graphics2D g) {
        if (spaceHeld) {
            int cannonRange = 350;
            int cannonRangeMax = 650;
            int speed = 15;

            if (ship.cannon1cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[0], ship.yCannonCorners()[0], speed, ship.angle + 3.14 / 2, cannonRange, cannonRangeMax, ship.team));
                ship.cannon1cd = 0;
            }
            if (ship.cannon2cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[1], ship.yCannonCorners()[1], speed, ship.angle + 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon2cd = 0;
            }
            if (ship.cannon3cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[2], ship.yCannonCorners()[2], speed, ship.angle + 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon3cd = 0;
            }
            if (ship.cannon4cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[3], ship.yCannonCorners()[3], speed, ship.angle - 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon4cd = 0;
            }
            if (ship.cannon5cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[4], ship.yCannonCorners()[4], speed, ship.angle - 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon5cd = 0;
            }
            if (ship.cannon6cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[5], ship.yCannonCorners()[5], speed, ship.angle - 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon6cd = 0;
            }
        }
        if (eHeld) {
            int cannonRange = 350;
            int cannonRangeMax = 650;
            int speed = 15;

            if (ship.cannon1cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[0], ship.yCannonCorners()[0], speed, ship.angle + 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon1cd = 0;
            }
            if (ship.cannon2cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[1], ship.yCannonCorners()[1], speed, ship.angle + 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon2cd = 0;
            }
            if (ship.cannon3cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[2], ship.yCannonCorners()[2], speed, ship.angle + 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon3cd = 0;
            }
        }
        if (qHeld) {
            int cannonRange = 350;
            int cannonRangeMax = 650;
            int speed = 15;

            if (ship.cannon4cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[3], ship.yCannonCorners()[3], speed, ship.angle - 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon4cd = 0;
            }
            if (ship.cannon5cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[4], ship.yCannonCorners()[4], speed, ship.angle - 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon5cd = 0;
            }
            if (ship.cannon6cd >= ship.cannon_CD) {
                cannonBalls.add(new Cannonball(ship.xCannonCorners()[5], ship.yCannonCorners()[5], speed, ship.angle - 3.14 / 2, cannonRange, cannonRangeMax,ship.team));
                ship.cannon6cd = 0;
            }
        }
        if (rand.nextInt(3) == 0) {
            ship.cannon1cd++;
        }
        if (rand.nextInt(3) == 0) {
            ship.cannon2cd++;
        }
        if (rand.nextInt(3) == 0) {
            ship.cannon3cd++;
        }
        if (rand.nextInt(3) == 0) {
            ship.cannon4cd++;
        }
        if (rand.nextInt(3) == 0) {
            ship.cannon5cd++;
        }
        if (rand.nextInt(3) == 0) {
            ship.cannon6cd++;
        }

        for (Cannonball cannonball : cannonBalls) {
            for (int z = 0; z < cannonball.speed; z++) {
                if (!cannonball.flagForRemoval) {
                    cannonball.move();
                }
            }
        }
        for (Cannonball cannonball : cannonBalls) {
            g.setColor(cannonball.color);
            g.fillRect((int)cannonball.x - xoff, (int) cannonball.y - yoff, 3, 3);
        }
        for (int j = 0; j < cannonBalls.size(); j++) {
            if (cannonBalls.get(j).flagForRemoval) {
                cannonBalls.remove(j);
                j--;
            }
        }

    }
    public void particles(Graphics2D g) {
        for (Particle particle : particles) {
            for (int z = 0; z < particle.speed; z++) {
                particle.move();
            }
        }
        for (Particle particle : particles) {
            g.setColor(particle.color);
            g.fillRect((int)particle.x - xoff, (int) particle.y - yoff, 2, 2);
        }
        for (int j = 0; j < particles.size(); j++) {
            if (particles.get(j).flagForRemoval) {
                particles.remove(j);
                j--;
            }
        }

    }
    public void splashes(Graphics2D g) {
        for (Splash splash: splashes) {
            splash.move();
        }
        for (Splash splash: splashes) {
            g.setColor(splash.color);
            g.drawOval((int) (splash.x - splash.radius) - xoff, (int) (splash.y - splash.radius) - yoff,  (int) (splash.radius * 2),  (int) (splash.radius * 2));
        }
        for (int j = 0; j < splashes.size(); j++) {
            if (splashes.get(j).flagForRemoval) {
                splashes.remove(j);
                j--;
            }
        }

    }
    public void enemyShips(Graphics2D g) {
        for (Ship ship : enemyShips) {
            ship.moveAI();
        }
        for (Ship ship : enemyShips) {
            AffineTransform backup = g.getTransform();
            AffineTransform trans = new AffineTransform();
            trans.translate(ship.x - ship2.getWidth() / 2.0 - xoff, ship.y - ship2.getHeight() /2.0 - yoff);
            trans.rotate( ship.angle, ship2.getWidth()/2.0, ship2.getHeight()/2.0 ); // the points to rotate around (the center in my example, your left side for your problem)
            g.transform( trans );
            g.drawImage( ship2, (int)0, (int)0,null );  // the actual location of the sprite
            g.setTransform( backup ); // restore previous transform
        }
        for (int j = 0; j < enemyShips.size(); j++) {
            if (enemyShips.get(j).flagForRemoval) {
                enemyShips.remove(j);
                j--;
            }
        }

    }
    //DISPLAY
    public void paintCursor(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(MX, MY, 5, 5);
    } //mouse
    public void paintSelectBox(Graphics2D g) {
        if (drawingSelector) {
            boxx2 = MX;
            boxy2 = MY;
            g.setColor(Color.red.darker());
            int quadrship = 0;
            if (boxx2 > boxx1) {
                if (boxy2 > boxy1) {
                    quadrship = 1;
                    g.drawRect(boxx1, boxy1, boxx2 - boxx1, boxy2 - boxy1);
                } else {
                    quadrship = 2;
                    g.drawRect(boxx1, boxy2, boxx2 - boxx1, boxy1 - boxy2);
                }
            } else {
                if (boxy2 > boxy1) {
                    g.drawRect(boxx2, boxy1, boxx1 - boxx2, boxy2 - boxy1);
                    quadrship = 3;
                } else {
                    g.drawRect(boxx2, boxy2, boxx1 - boxx2, boxy1 - boxy2);
                    quadrship = 4;
                }
            }
            g.setColor(Color.red.darker());
            /*
            selectedShips.clear();
            for (Ship ship : renderedShips) {
                if (quadrship == 1) {
                    if ((ship.x - XOFF) > boxx1 && (ship.x - XOFF) < boxx2 && (ship.y - YOFF) < boxy2 && (ship.y - YOFF) > boxy1) {
                        selectedShips.add(ship);
                    }
                }
                if (quadrship == 2) {
                    if ((ship.x - XOFF) > boxx1 && (ship.x - XOFF)  < boxx2 && (ship.y - YOFF) < boxy1 && (ship.y - YOFF) > boxy2) {
                        selectedShips.add(ship);
                    }
                }
                if (quadrship == 3) {
                    if ((ship.x - XOFF) > boxx2 && (ship.x - XOFF) < boxx1 && (ship.y - YOFF) < boxy2 && (ship.y - YOFF) > boxy1) {
                        selectedShips.add(ship);
                    }
                }
                if (quadrship == 4) {
                    if ((ship.x - XOFF) > boxx2 && (ship.x - XOFF) < boxx1 && (ship.y - YOFF) < boxy1 && (ship.y - YOFF) > boxy2) {
                        selectedShips.add(ship);
                    }
                }
            }
             */
        }
    } //select box
    public void drawObject(Graphics2D g) {

    }
    //LOGIC FUNCTIONS
    public boolean checkWithinRadius(double x, double y, double x2, double y2, double radius) {
        double distance = Math.sqrt(Math.pow((x2 - x), 2) + Math.pow((y2 - y), 2));
        return distance < radius;
    }
    public double getDistance(double x, double y, double x2, double y2) {
        return Math.sqrt(Math.pow((x2 - x), 2) + Math.pow((y2 - y), 2));
    }
    /*
    public boolean checkCollisionGrav() {
        boolean check = false;
        if (proj.x < object.x + object.width &&
                proj.x + proj.width > object.x &&
                proj.y + proj.height > object.y) {
            check = true;
        }

        return check;
    }
     */
    //CONTROLS
    public void addKeyListenerHere(JFrame jframe) {
        jframe.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {

                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    /*
                    case (KeyEvent.VK_W) : {
                        UP_HELD = true;
                    }
                    case (KeyEvent.VK_S) : {
                        DOWN_HELD = true;
                    }
                     */
                    case (KeyEvent.VK_SHIFT) -> {
                        shiftHeld = true;
                    }
                    case (KeyEvent.VK_A) -> {
                        LEFT_HELD = true;
                    }
                    case (KeyEvent.VK_D) -> {
                        RIGHT_HELD = true;
                    }
                    case (KeyEvent.VK_S) -> {
                        DOWN_HELD = true;
                    }
                    case (KeyEvent.VK_W) -> {
                        UP_HELD = true;
                    }
                    case (KeyEvent.VK_G) -> {
                        G_HELD = true;
                    }
                    case (KeyEvent.VK_SPACE) -> {

                        spaceHeld = true;
                    }
                    case (KeyEvent.VK_E) -> {

                       eHeld = true;
                    }
                    case (KeyEvent.VK_Q) -> {

                        qHeld = true;
                    }
                    case (KeyEvent.VK_I) -> {
                        zf += 0.1;
                    }
                    case (KeyEvent.VK_O) -> {
                        zf -= 0.1;
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case (KeyEvent.VK_SHIFT) -> {
                        shiftHeld = false;
                    }
                    case (KeyEvent.VK_SPACE) -> {
                        spaceHeld = false;
                    }
                    case (KeyEvent.VK_E) -> {

                        eHeld = false;
                    }
                    case (KeyEvent.VK_Q) -> {

                        qHeld = false;
                    }
                    case (KeyEvent.VK_W) -> {
                        UP_HELD = false;
                    }
                    case (KeyEvent.VK_S) -> {
                        DOWN_HELD = false;
                    }
                    case (KeyEvent.VK_A) -> {
                        LEFT_HELD = false;
                    }
                    case (KeyEvent.VK_D) -> {
                        RIGHT_HELD = false;
                    }
                    case (KeyEvent.VK_G) -> {
                        G_HELD = false;
                    }
                }
            }

        });

    }
    public void addMouseListenerHere(JFrame jframe) {
        jframe.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                ;
            }
            @Override
            public void mousePressed(MouseEvent e) {
                drawingSelector = true;
                boxx1 = MX;
                boxy1 = MY;
                boxx2 = MX;
                boxy2 = MY;
            }
            @Override
            public void mouseReleased(MouseEvent e) {


                drawingSelector = false;
                /*
                double closest_distance = 100000;
                if (selectedShips.size() == 0) {
                    for (Ship ship : renderedShips) {

                        if (getDistance(MX, MY, ship.x, ship.y) < closest_distance) {
                            closest_distance = getDistance(MX, MY, ship.x, ship.y);
                            selectedShips.clear();
                            selectedShips.add(ship);
                        }
                    }
                }

                 */

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
    @Override
    public void actionPerformed(ActionEvent e) {

        renderer.repaint();

    }
}
