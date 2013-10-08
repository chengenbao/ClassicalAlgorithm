import java.applet.*;
import java.awt.*;

import kloss.graphics.objects.GraphicsObject;
import kloss.graphics.nodes.GraphicsNode;

public class SimpleGraphicsNodeTest extends Frame {

  GraphicsNode n;

  public SimpleGraphicsNodeTest() {
    resize(300,300);
    show();

    Dimension d = size();
    n = new SimpleGraphicsNode(new Point(d.width/2, d.height/2),
			       "123",
			       Color.red);
    repaint();
  }

  public void paint(Graphics g) {
    n.draw(g);
  }

  public static void main(String[] args) {
    new SimpleGraphicsNodeTest();
  }
}
