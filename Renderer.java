import javax.swing.JPanel;
import java.awt.*;

public class Renderer extends JPanel {
    private static final long serialVersionUID = 1L;

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        super.paintComponent(g2);
        Game.game.repaint(g2);
    }
}
