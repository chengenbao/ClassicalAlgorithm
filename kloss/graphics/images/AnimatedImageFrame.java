import java.awt.Point;
import java.awt.Event;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.MediaTracker;

import kloss.graphics.images.ImageFrame;
import kloss.graphics.images.ImageObject;

public class AnimatedImageFrame extends ImageFrame implements Runnable {

  private ImageObject object;
  private boolean objectDragged = false;

  ////////////////////////////////////////
  // Private instance variable used for
  // double buffering (smooth animation).

  private Dimension offDimension;
  private Graphics  offGraphics;
  private Image     offImage;

  private Thread animatorThread = null;
  private int frame = -1;

  public void init () {

    validate ();

    Image[] buffer = new Image[10];

    MediaTracker tracker = new MediaTracker(this);
    for (int i = 0; i < 10; i++) {
      buffer[i] = Toolkit.getDefaultToolkit().
	getImage("T" + (i + 1) + ".gif");
      tracker.addImage(buffer[i], 0);
    }

    try {
      tracker.waitForAll();
    } catch (InterruptedException e) {
    }
     
    for (int i = 0; i < 10; i++) {
      putImage("T" + (i + 1) + ".gif", buffer[i]);
    }

    Dimension d = size();

    object = new ImageObject(new Point(d.width/2, d.height/2), this,
			     "T1.gif");

    repaint();
  }

  public void start() {
    if (animatorThread == null)
      animatorThread = new Thread(this);
    animatorThread.setPriority(Thread.MIN_PRIORITY);
    animatorThread.start();
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

  public void run() {
    while (Thread.currentThread() == animatorThread) {
      long startTime = System.currentTimeMillis();

      frame++;

      object.changeImage(this, "T" + ( (frame % 10) + 1) + ".gif");
      repaint();

      try {
	startTime += 100;
	Thread.sleep(Math.max(0, startTime -
			      System.currentTimeMillis()));
      } catch (InterruptedException e) {
      }
    }
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
