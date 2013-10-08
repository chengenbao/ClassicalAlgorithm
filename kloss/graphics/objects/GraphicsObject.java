package kloss.graphics.objects;

import java.awt.*;

/**
 * An abstract class for displaying graphics objects. A graphics
 * object is loosely defined as any object that can be drawn on
 * a graphics context. A GraphicsObject is centered upon an origin 
 * which is an instance of Point. This origin is passed to the 
 * GraphicsObject during construction and represents the (x,y) 
 * location of the object within the graphics context.
 * <P>
 * A GraphicsObject guarantees only three methods
 * <BLOCKQUOTE><UL>
 * <LI><CODE>getOrigin()</CODE>
 * </UL></BLOCKQUOTE>
 * which is defined by GraphicsObject, and two abstract methods
 * <BLOCKQUOTE><UL>
 * <LI><CODE>draw(Graphics g)</CODE>
 * <LI><CODE>size()</CODE>
 * </UL></BLOCKQUOTE>
 * Any other functions as well as all abstract methods are the 
 * responsibility of GraphicsObject subclasses.
 * <P>
 * Here is an example (albeit, inefficient) of how a GraphicsObject
 * might be used:
 * <P>
 * <PRE>
 * GraphicsObject object;
 * <BR>
 *   . . .
 * <BR>
 * public void paint(Graphics g) {
 *   Dimension d = size();
 * <BT>
 *   Point gPoint = object.getOrigin();
 *   Dimension gSize = object.size();
 *
 *   if( ( ( gPoint.x - gSize.width/2) > 0) &&
 *       ( ( gPoint.x + gSize.width/2) < d.width) &&
 *       ( ( gPoint.y - gSize.height/2) > 0) &&
 *       ( ( gPoint.y + gSize.height/2) < d.height))
 *     object.draw(g);
 * }
 * </PRE>
 * <P>
 * @version 1.1 March 27, 1997
 * @author  John Kloss
 */

public abstract class GraphicsObject {

  /** The origin of the GraphicsObject. The GraphicsObject is
   *  centered upon this point. 
   */
  protected Point origin;


  /** Constructor. This method instantiates the GraphicsObject
   *  and sets its origin to the origin. This method is protected 
   *  so that only subclasses of GraphicsObject can call it. 
   *
   * @param origin          The Point to center the GraphicsObject
   *                        upon.
   */
  protected GraphicsObject (Point origin) {
    this.origin = origin;
  }

  /** Return the origin of the GraphicsObject. The Point object 
   *  returned is a new instance of Point. This insures that the 
   *  GraphicsObject's origin is not mistakenly changed by this
   *  call.
   * 
   * @return                The location of the GraphicsObject
   *                        in a graphics context.
   */
  public Point origin() {
    return new Point(origin.x, origin.y);
  }

  /** Abstract method which draws the GraphicsObject. It is the
   *  responsibility of GraphicsObject's subclasses to implement
   *  this method.
   *
   * @param g                 The graphics context upon which the
   *                          GraphicsObject will draw itself.
   */
  public abstract void draw(Graphics g);

  /** Abstract method which returns the size of the GraphicsObject.
   *  It is the responsibility of GraphicsObject's subclasses to
   *  implement this method.
   *
   * @return                  A Dimension object the size of the 
   *                          GraphicsObject
   */
  public abstract Dimension size();
}


