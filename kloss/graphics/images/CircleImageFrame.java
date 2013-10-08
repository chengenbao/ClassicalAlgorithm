import java.awt.Point;
import java.awt.Event;
import java.awt.Image;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.MediaTracker;

import kloss.graphics.images.ImageFrame;
import kloss.graphics.images.ImageObject;

public class CircleImageFrame extends ImageFrame {

  private ImageObject object;
  private boolean objectDragged = false;

  ////////////////////////////////////////
  // Private instance variable used for
  // double buffering (smooth animation).

  private Dimension offDimension;
  private Graphics  offGraphics;
  private Image     offImage;

  private void createMainImage() {
    Image image = createImage(40,40);

    Graphics graphics = image.getGraphics();
    graphics.setColor(TRANSPARENT_COLOR);
    graphics.fillRect(0, 0, 40, 40);

    graphics.setColor(Color.blue);
    graphics.fillOval(0, 0, 40, 40);

    graphics.dispose();

    image = filterImage(image);

    putImage("main image", image);
  }

  public void init () {

    validate ();
    createMainImage();

    Dimension d = size();

    object = new ImageObject(new Point(d.width/2, d.height/2), this,
			     "main image");

    repaint();
  }

  public boolean mouseDrag(Event evt, int x, int y) {
    
    if ( (objectDragged == false) && (object.inBounds(x, y))) 
      objectDragged = true;

    if (objectDragged) {
      object.move(x, y);
      repaint();
    }

    return true;
  }

  public boolean mouseUp(Event evt, int x, int y) {
    objectDragged = false;
    return true;
  }

  public void update(Graphics g) {
      paint(g);
  }

  public void paint(Graphics g) {
      Dimension d = size();

      if ( (offGraphics == null) ||
	   (offDimension.width != d.width) ||
	   (offDimension.height != d.height)) {

	  offDimension = d;
	  offImage     = createImage(d.width, d.height);
	  offGraphics  = offImage.getGraphics();
      }

      offGraphics.setColor(getBackground());
      offGraphics.fillRect(0, 0, d.width, d.height);

      object.draw(offGraphics);
      g.drawImage(offImage, 0, 0, this);
  }
}
