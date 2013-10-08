package kloss.graphics.nodes;

import java.awt.Point;
import java.awt.Dimension;

import kloss.graphics.objects.GraphicsObject;

public abstract class GraphicsNode extends GraphicsObject {

  String nodeValue;


  public GraphicsNode(Point origin, String value) {
    super.setBaseOrigin(origin);
    this.nodeValue = value;
  }


  public String getValue() {
    return nodeValue;
  }

  final protected void setBaseValue(String value) {
    this.nodeValue = value;
  }

  
  public void move(int newX, int newY) {
    super.moveBaseOrigin(newX, newY);
  }

  public void translate(int x, int y) {
    super.translateBaseOrigin(x, y);
  }
}
