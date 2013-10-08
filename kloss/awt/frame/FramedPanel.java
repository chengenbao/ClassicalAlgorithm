package kloss.awt.frame;

import java.awt.*;

/** 
 * FramedPanel is a Panel which borderers itself with a three-dimensional
 * rectangle.  The size and color of this frame are modifiable and are
 * set during construction.  The color of the frame does <B>not</B> effect
 * the background of the panel which defaults to the background color of its
 * parent Container.  A FramedPanel's background color may be changed via the
 * standard Component call <TT>setBackground( )</TT>.  In all other respects,
 * FramedPanel performs exactly like a Panel.
 * <P>
 * @version 1.0 February 22, 1997
 * @author  John Kloss
 */
 
public class FramedPanel extends Panel {

  final static int DEFAULT_FRAME_WIDTH = 2;

  private int   frameWidth;
  private Color frameColor = null;


  /** Constructs a FramedPanel with set color and width. 
   *
   * @param frameWidth        The pixel width of the bordering frame.
   * @param frameColor        The color of the bordering frame. This value 
   *                          may be set to null (in which case the parent's 
   *                          background is used).
   */

  public FramedPanel( int frameWidth, Color frameColor) {
    this.frameWidth = frameWidth;
    this.frameColor = frameColor;
  }

  /** Constructs a FramedPanel using the default values.
   */

  public FramedPanel( ) {
    this( DEFAULT_FRAME_WIDTH, null);
  }


  /** Retrieves the FramedPanel's insets.  The Insets object returned takes
   *  into account the width of the FramedPanel's frame. Layout managers use 
   *  this value to determine the bounds for component placement. Overiding 
   *  insets() insures that the panel's frame is not painted over.
   *
   * @return                   The insets of the FramedPanel.
   */

  public Insets insets( ) {
    Insets insets = super.insets( );

    // Hack to insure that frame isn't painted
    // over by layout manager. Discovered by 
    // trial and error.
    insets.top    += frameWidth + 2;
    insets.bottom += frameWidth + 2;
    insets.left   += frameWidth + 2;
    insets.right  += frameWidth + 2;

    return insets;
  }

  /** Called for paint actions by FramedPanel's parent Container.  This 
   *  method paints the frame on the FramedPanel.
   *
   * @param g                  The graphics context for the paint operation.
   */

  public void paint( Graphics g) {
    int w = size( ).width;
    int h = size( ).height;

    int f = Math.round( frameWidth / 2);
    if( ( f % 2) != 0)  f++;

    if( frameColor != null)
      g.setColor( frameColor);
    else
      g.setColor( getBackground( ));

    for( int i = 0; i < f; i++) {
      g.draw3DRect( i, i, (w-(i*2)-1), (h-(i*2)-1), true);
      g.draw3DRect( (f+i), (f+i), (w-( (f+i)*2)-1),
		    (h-( (f+i)*2)-1), false);
    }

    g.setColor( getBackground( ));
  }
}
  
