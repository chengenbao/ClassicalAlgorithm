package kloss.graphics.objects;

import java.awt.*;
import kloss.graphics.objects.Circle;
import kloss.graphics.objects.GraphicsObject;

public class MoveableCircle extends Circle {

  public MoveableCircle(Point origin, Color color, int radius) {
    super(origin, color, radius);
  }


  public void move(int newX, int newY) {
    origin.move (newX, newY);
  }

  public void translate(int x, int y) {
    origin.translate (x, y);
  }

  public synchronized Object clone() {
    return new MoveableCircle(origin, color, radius);
  }
}
