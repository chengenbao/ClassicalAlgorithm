import java.awt.Point;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;

import kloss.graphics.objects.GraphicsObject;
import kloss.graphics.nodes.GraphicsNode;

public class SimpleGraphicsNode extends GraphicsNode {

  final static int NODE_RADIUS = 20;

  Color nodeColor;

  
  public SimpleGraphicsNode(Point origin, String value, Color color) {
    super(origin, value);
    this.nodeColor = color;
  }


  public Dimension size() {
    return new Dimension(NODE_RADIUS*2, NODE_RADIUS*2);
  }

  public void draw(Graphics g){
    Color backColor = g.getColor();
    Font  backFont  = g.getFont();
    String value    = getValue();

    g.setFont(new Font("TimesRoman", Font.PLAIN, 14));
    FontMetrics fm = g.getFontMetrics();

    Point p = getOrigin();
    g.setColor(nodeColor);
    g.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS,
	       NODE_RADIUS*2, NODE_RADIUS*2);

    int x = p.x - NODE_RADIUS;
    x += ((NODE_RADIUS*2) - fm.stringWidth(value))/2;
    
    int y = p.y - NODE_RADIUS;
    y += ((NODE_RADIUS*2) - fm.getHeight())/2 + fm.getAscent();

    g.setColor(Color.black);
    g.drawString(value, x+1, y+1);
    g.setColor(Color.white);
    g.drawString(value, x, y);

    g.setColor(backColor);
    g.setFont(backFont);
  }
}
    
