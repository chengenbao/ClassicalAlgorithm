package kloss.structures;

import java.awt.Point;
import java.awt.Image;
import java.awt.Event;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Dimension;

import java.util.Vector;
import java.util.Observer;
import java.util.Observable;

import kloss.graphics.images.ImageObject;
import kloss.graphics.images.ImageFrame;
import kloss.graphics.functions.Function;

import kloss.structures.nodes.Node;
import kloss.structures.nodes.TreeNode;

class FunctionThread extends Thread {

  ////////////////////////////////////////
  // After a few iterations the function
  // thread yields to allow other threads
  // to run. (Insures smooth animation).

  final static int FUNCTION_ITERATIONS = 20;

  StructureCanvas canvas;

  FunctionThread(StructureCanvas canvas) {
    super();

    this.canvas = canvas;
    setPriority(Thread.MIN_PRIORITY);
  }

  public void run() {
    while (true) {

      for (int i = 0; i < FUNCTION_ITERATIONS; i++)
	canvas.doFunctions();

      ////////////////////////////////////////
      // Allow other threads to run

      yield();
    }
  }
}

class BufferThread extends Thread {

  StructureCanvas canvas;

  BufferThread(StructureCanvas canvas) {
    super();

    this.canvas = canvas;
    setPriority(Thread.MIN_PRIORITY);
  }

  public void run() {
    while (true) 
      canvas.loadBuffer();
  }
}

class AnimatorThread extends Thread {

  final static int delay = 30;

  StructureCanvas canvas;

  AnimatorThread(StructureCanvas canvas) {
    super();

    this.canvas = canvas;
    setPriority(Thread.MIN_PRIORITY);
  }

  public void run() {
    while (true) {
      long startTime = System.currentTimeMillis() + delay;

      canvas.repaint();

      try {
	sleep(Math.max(0, startTime - 
		       System.currentTimeMillis()));
      } catch (InterruptedException e) {
      }
    }
  }
}

/**
 * Basic form of an Animated Data Structure canvas.
 */
public class StructureCanvas extends Canvas implements Observer {

  ///////////////////////////////////
  // Vector of functions to be per-
  // formed upon data structure nodes

  protected Vector functionVector = new Vector();

  ///////////////////////////////////
  // Nodes which represent the actual
  // data structure and their common
  // size

  protected Vector nodeVector = new Vector();
  protected int    nodeSize;

  ///////////////////////////////////
  // ImageFrame reference so that
  // image objects can be easily
  // created by Functions

  public ImageFrame frame;

  ///////////////////////////////////
  // Double buffering instance var's

  private Dimension offDimension;
  private Graphics  offGraphics;
  private Image     offImage;

  ///////////////////////////////////
  // Vector to hold copys of the 
  // data structure nodes (each copy
  // is represented as a Vector).
  
  private Vector copyVector = new Vector();

  private FunctionThread functionThread = new FunctionThread(this);
  private AnimatorThread animatorThread = new AnimatorThread(this);
  private BufferThread   bufferThread   = new BufferThread(this);

  private boolean functionSuspended = true;
  private boolean animatorSuspended = true;
  private boolean bufferSuspended   = true;

  private boolean bufferLoaded = false;

  Vector updateVector = new Vector();
  String updateString = "";

  ///////////////////////////////////
  // Reference to object selected to
  // be moved on screen.

  private ImageObject dragObject = null;

  /**
   * Constructor.
   */
  public StructureCanvas(ImageFrame frame, int nodeSize) {
    this.frame    = frame;
    this.nodeSize = nodeSize;
  }

  ///////////////////////////////////////////////////////
  // The following are functions which allow the three
  // animation threads to work concurrently yet synchro-
  // ized. I found that Thread.suspend() and Thread.
  // resume() do not function properly on a linux system
  // so I contained those actions in the following func-
  // tions.
  //
  // The form is basically this:
  //
  //   xxxDone()     Is the particular thread done with its
  //                 function (whatever that may be)?
  //
  //   setxxxDone()  Set the particular thread to completed
  //                 or not completed (true = completed/sus-
  //                 pend; false = not completed/resume)
  
  synchronized boolean functionDone() {
    return functionSuspended;
  }

  synchronized void setFunctionDone(boolean value) {
    functionSuspended = value;

    if (value == false) {
      functionThread = new FunctionThread(this);
      functionThread.start();
    }
    else if (value == true) {
      functionThread.stop();
      functionThread = null;
    }
  }

  synchronized boolean bufferDone() {
    return bufferSuspended;
  }

  synchronized void setBufferDone(boolean value) {
    bufferSuspended = value;

    if (value == false) {
      bufferThread = new BufferThread(this);
      bufferThread.start();
    }
    else if (value == true) {
      bufferThread.stop();
      bufferThread = null;
    }
  }

  synchronized boolean animatorDone() {
    return animatorSuspended;
  }

  synchronized void setAnimatorDone(boolean value) {
    animatorSuspended = value;

    if (value == false) {
      animatorThread = new AnimatorThread(this);
      animatorThread.start();
    }
    else if (value == true) {
      animatorThread.stop();
      animatorThread = null;
    }
  }

  ////////////////////////////////////////
  // Indicate whether the image buffer has
  // been loaded (and thus ready to be ren-
  // dered upon a graphics context).

  synchronized boolean bufferLoaded() {
    return bufferLoaded;
  }

  ////////////////////////////////////////
  // Set the bufferLoaded boolean value to
  // true/false

  synchronized void setBufferLoaded(boolean value) {
    bufferLoaded = value;
  }


  ///////////////////////////////////////////////////////////////////////////
  // Add/Remove functions for StructureCanvas Vectors
  //
  //  functionVector
  //  nodeVector
  //
  // These Vectors are private and should not be mutatable by outside objects
  // to insure proper functioning of animations.
  ///////////////////////////////////////////////////////////////////////////

  public void addFunction(Function function) {
    functionVector.addElement(function);

    if (functionDone())
      setFunctionDone(false);
    
    if (animatorDone())
      setAnimatorDone(false);

    if (bufferDone())
      setBufferDone(false);
  }

  public void removeFunction(Function function) {
    functionVector.removeElement(function);
  }

  public int numberFunctions() {
    return functionVector.size();
  }

  public void addNode(ImageObject node) {
    nodeVector.insertElementAt(node, 0);
  }

  public void removeNode(ImageObject node) {
    nodeVector.removeElement(node);
  }

  public int numberNodes() {
    return nodeVector.size();
  }

  public int getNodeSize() {
    return nodeSize;
  }

  public Node getRootNode() {
    return (Node) nodeVector.lastElement();
  }

  ///////////////////////////////////////////////////////////////////////////
  // Package access only functions
  //
  //  doFunctions
  //  clean
  //
  // These functions should be accessible only to DataStructure Applets. 
  // Separation has been maintained between image creation, which is the
  // Applet's domain, and image rendering, which is the StructureCanvas'
  // domain.
  //
  // Functions are meant to manipulate the appearance of the Structure-
  // Canvas (simulating a Data Structure) and thus are held by the Canvas.
  //
  // ImagesObjects (the items to be drawn on the canvas) are also held by
  // the Canvas; however, their creation depends upon the ImageFrame. 
  ///////////////////////////////////////////////////////////////////////////

  ///////////////////////////////////////////////////////
  // Perform functions held in functionVector. Remove 
  // function if it has completed, otherwise, perform 
  // function. Redraw canvas when completed.

  void doFunctions() {
    int i = 0;

    while (i < functionVector.size()) {

      ///////////////////////////////////
      // If function has completed then
      // remove it from functionVector.

      if ( ( (Function) functionVector.elementAt(i)).functionDone())
	functionVector.removeElementAt(i);

      else {

	///////////////////////////////////
	// Otherwise, perform function and
	// move on to the next (if any).

	( (Function) functionVector.elementAt(i)).performFunction();
	i++;

      }
    }

    cloneNodes();

    if (functionVector.size() == 0)
      setFunctionDone(true);
  }

  public boolean mouseDrag(Event evt, int x, int y) {
    
    if (dragObject == null) {
      for (int i = 0; i < nodeVector.size(); i++) {
	if ( ( ( ImageObject) nodeVector.elementAt(i)).inBounds(x, y)) {
	  
	  dragObject = (ImageObject) nodeVector.elementAt(i);
	  break;
	}
      }
    }

    if (dragObject != null) {
      dragObject.move(x, y);
      repaint();
    }

    return true;
  }

  public boolean mouseUp(Event evt, int x, int y) {

    if (dragObject != null)
      dragObject = null;

    return true;
  }

  void cloneNodes() {
    Node root = (Node) nodeVector.lastElement();
    Vector vector = root.cloneStructure();

    if (root != null) {
      copyVector.addElement(vector);

      if ( (nodeVector.firstElement() instanceof Node) == false)
	vector.insertElementAt(nodeVector.firstElement(), 0);
    }
  }

  synchronized void loadBuffer() {
    if (copyVector.size() != 0) {

      while (bufferLoaded()) {
	try {
	  wait();
	} catch (InterruptedException e) { }
      }

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

      Vector nodes = (Vector) copyVector.firstElement();
      copyVector.removeElementAt(0);

      for (int i = 0; i < nodes.size(); i++)
	( (ImageObject) nodes.elementAt(i)).draw(offGraphics);
      
      setBufferLoaded(true);
      notifyAll();

      if ( (copyVector.size() == 0) && (functionDone()))
	setBufferDone(true);
    }
    else
      bufferThread.yield();
  }

  public void paint(Graphics g) {
    update(g);
  }

  public synchronized void update(Graphics g) {
    if (animatorDone()) {
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

      for (int i = 0; i < nodeVector.size(); i++)
	( (ImageObject) nodeVector.elementAt(i)).draw(offGraphics);

      g.drawImage(offImage, 0, 0, this);
    }
    else {

      while (bufferLoaded() == false) {
	try {
	  wait();
	} catch (InterruptedException e) { }
      }

      g.drawImage(offImage, 0, 0, this);

      setBufferLoaded(false);
      notifyAll();

      if (updateVector.size() > 0) {

	////////////////////////////////////////
	// Hack used to update the label element
	// in the BinaryTree container.

	if (updateString
	      .equals((String) updateVector. firstElement()) == false) {
	  ((BinaryTree) frame)
	      .updateLabel((String) updateVector.firstElement());
	  updateString = (String) updateVector.firstElement();

	  System.out.println("Updated with " + updateString);

	}
	updateVector.removeElementAt(0);
      }

      if ( bufferDone())
	setAnimatorDone(true);
    }
  }


  public void update(Observable observed, Object arg) {
    if (arg instanceof String) {
      updateVector.addElement(arg);
    }
  }

}
