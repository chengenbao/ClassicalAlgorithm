import java.awt.Font;
import java.awt.Point;
import java.awt.Event;
import java.awt.Image;
import java.awt.Color;
import java.awt.Canvas;
import java.awt.Button;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.BorderLayout;

import java.util.Vector;

import kloss.graphics.images.ImageFrame;
import kloss.graphics.images.ImageObject;

class NodeCanvas extends Canvas {

  ////////////////////////////////////////
  // Vector of ImageObjects to be drawn
  // upon the canvas.

  private Vector objects = new Vector();

  ////////////////////////////////////////
  // Allow ImageObjects access to an Image-
  // Frame (mainly for new ImageObject
  // creation).

  private NodeImageFrame frame;

  ////////////////////////////////////////
  // Private instance variable used for
  // double buffering (smooth animation).

  private Dimension offDimension;
  private Graphics  offGraphics;
  private Image     offImage;

  ////////////////////////////////////////
  // Next node number to be created. Pack-
  // age access so that NodeImageFrame can
  // access this variable.

  int nodeNumber = 1;

  ////////////////////////////////////////
  // The current ImageObject being dragged
  // (if any).

  private ImageObject dragObject = null;

  ////////////////////////////////////////
  // Whether a request to make a new node
  // has been made. Package access so that
  // NodeImageFrame can access this vari-
  // able

  boolean newNode = false;

  /**
   * Constructor.
   */
  NodeCanvas(NodeImageFrame frame) {
    super();

    this.frame = frame;
  }

  /**
   * Overriden method to allow dragging of ImageObjects across the
   * NodeCanvas.
   */
  public boolean mouseDrag(Event evt, int x, int y) {
    
    ////////////////////////////////////////
    // If a node has not already been selec-
    // ted to be dragged and we are not cur-
    // rently creating a new node, determine 
    // which node (if any) has been newly 
    // selected.

    if ( (dragObject == null) && (newNode == false)) {
      for (int i = 0; i < objects.size(); i++) {
	if ( ( (ImageObject)objects.elementAt(i)).inBounds(x, y)) {

	  ////////////////////////////////////////
	  // Found node which was selected. Set
	  // current drag item to node and break
	  // out of for loop.

	  dragObject = (ImageObject) objects.elementAt(i);
	  break;
	}
      }
    }

    ////////////////////////////////////////
    // If dragObject has been selected then
    // move it and repaint the canvas.

    if (dragObject != null) {
      dragObject.move(x,y);
      repaint();
    }

    return true;
  }

  /**
   * Overriden method to release a dragged object after it has
   * been dragged.
   */
  public boolean mouseUp(Event evt, int x, int y) {

    ////////////////////////////////////////
    // If a dragObject has been selected,
    // deselect it.

    if (dragObject != null)
      dragObject = null;

    return true;
  }

  /**
   * Overriden method to allow the creation of new nodes.
   */
  public boolean mouseDown(Event evt, int x, int y) {

    ////////////////////////////////////////
    // If a request to make a new node has 
    // been made then create it and at the
    // node to the objects Vector.
    // 
    // NOTE: the ImageObject constructor re-
    // quires an instance of ImageFrame.

    if (newNode) {
      objects.addElement(new ImageObject(new Point(x, y), frame, 
				       "node" + nodeNumber));
    
      nodeNumber++;
      repaint();

      newNode = false;
    }
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

      ////////////////////////////////////////
      // Paint allow image objects on the 
      // canvas.

      for (int i = 0; i < objects.size(); i++)
	( (ImageObject) objects.elementAt(i)).draw(offGraphics);

      g.drawImage(offImage, 0, 0, this);
  }
}
  

public class NodeImageFrame extends ImageFrame {

  private NodeCanvas nodeCanvas;

  private void createMainNode() {
    Image image = createImage(40,40);

    Graphics graphics = image.getGraphics();
    graphics.setColor(TRANSPARENT_COLOR);
    graphics.fillRect(0, 0, 40, 40);

    graphics.setColor(Color.blue);
    graphics.fillOval(0, 0, 40, 40);

    graphics.dispose();

    image = filterImage(image);

    putImage("main node", image);
  }

  public void createNewNode(int nodeNumber) {
    Image image = copyImage("main node");

    Graphics graphics = image.getGraphics();

    graphics.setFont(new Font("TimesRoman", Font.PLAIN, 14));
    FontMetrics fontMetrics = graphics.getFontMetrics();

    int width  = image.getWidth(this);
    int height = image.getHeight(this);

    int xLocation = (width/2) - (fontMetrics.stringWidth("" + nodeNumber)/2);
    int yLocation = (height/2) - (fontMetrics.getHeight()/2) +
      fontMetrics.getAscent();

    graphics.setColor(Color.black);
    graphics.drawString("" + nodeNumber, (xLocation + 1), (yLocation + 1));
    graphics.setColor(Color.white);
    graphics.drawString("" + nodeNumber, xLocation, yLocation);

    graphics.dispose();
    fontMetrics = null;

    image = filterImage(image);
    putImage("node" + nodeNumber, image);
  }


  public void init () {

    nodeCanvas = new NodeCanvas(this);

    setLayout(new BorderLayout());
    add("North", new Button("Create Node"));
    add("Center", nodeCanvas);

    validate ();
    createMainNode();
  }

  public boolean action(Event evt, Object arg) {
    if (("Create Node".equals(arg)) && (nodeCanvas.newNode == false)) {
      createNewNode(nodeCanvas.nodeNumber);
      nodeCanvas.newNode = true;

      return true;
    }
    return false;
  }
}
