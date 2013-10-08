package kloss.graphics.objects;

import java.awt.*;
import kloss.graphics.objects.GraphicsObject;

/** A simple GraphicsObject extension for drawing filled Ovals on
 *  a graphics context. Unlike GraphicsObject, this class is not
 *  abstract and may be instantiated.  However, the Circle class
 *  cannot be modified once it has been created. It is mainly used
 *  as an example of the type of class that might extend GraphicsObject.
 *  <P>
 *  An example of how the Circle class might prove useful is in
 *  quick painting operations
 *  <PRE>
 *  public void paint(Graphics g) {
 *      Circle c1 = new Circle(new Point(20,20), Color.blue, 20);
 *      Circle c2 = new Circle(new Point(40,30), Color.red, 15);
 *      Circle c3 = new Circle(new Point(34,45), Color.gree, 27);
 *  <BR>
 *      c1.draw(g);
 *      c2.draw(g);
 *      c3.draw(g);
 *  }
 *  </PRE>
 *
 * @version 1.1 March 27, 1997
 * @author  John Kloss
 */

public class Circle extends GraphicsObject {

  /** The color of the circle.
   */
  protected Color color;

  /** The radius of the circle.
   */
  protected int radius;

  /** Constructor. Note that once a Circle is initialized, it cannot
   *  be modified.
   *
   * @param origin            The Point around which the Circle is
   *                          centered.
   * @param color             The color of the circle when it is drawn.
   * @param radius            The radius of the circle.
   */
  public Circle(Point origin, Color color, int radius) {
    super(origin);

    this.color = color;
    this.radius = radius;
  }

  
  /** Returns the color of the Circle.
   * @return                  The color of the circle.
   */
  public Color getColor() {
    return color;
  }

  /** Returns the radius of the Circle. Note that this method is 
   *  different from the size method below which returns the bounding
   *  rectangle of the circle.
   *
   * @return                  The radius of the circle.
   */
  public int getRadius() {
    return radius;
  }

  /** Returns the bounding rectangle of the circle. This rectangle is
   *  represented by a Dimension object with width and height twice that
   *  of the circle's radius.
   *
   * @return                   The bounding rectangle of the circle.
   */
  public Dimension size() {
    return new Dimension( (2*radius), (2*radius));
  }

  /** Paints the circle on a graphics context. Note that the circle is
   *  centered around the Point object passed to it during initialization.
   *
   * @param                    A Graphics object (the graphics context).
   */
  public void draw(Graphics g) {
    Color c = g.getColor();

    int x = origin.x - radius;
    int y = origin.y - radius;

    g.setColor(color);
    g.fillOval(x, y, (2*radius), (2*radius));
    g.setColor(c);
  }

  public synchronized Object clone() {
    return new Circle(origin, color, radius);
  }
}
    
    
