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

  StructureCanvas canvas;

  FunctionThread(StructureCanvas canvas) {
    super();

    this.canvas = canvas;
    setPriority(Thread.MIN_PRIORITY);

    start();
  }

  public void run() {
    while (true) 
      canvas.doFunctions();
  }
}

class BufferThread extends Thread {

  StructureCanvas canvas;

  BufferThread(StructureCanvas canvas) {
    super();

    this.canvas = canvas;
    setPriority(Thread.MIN_PRIORITY);

    start();
  }

  public void run() {
    while (true) 
      canvas.loadBuffer();
  }
}

class AnimatorThread extends Thread {

  final static int delay = 20;

  StructureCanvas canvas;

  AnimatorThread(StructureCanvas canvas) {
    super();

    this.canvas = canvas;
    setPriority(Thread.MIN_PRIORITY);

    start();
  }

  public void run() {
    while (true) {
      long startTime = System.currentTimeMillis() + delay;

      canvas.paintAnimation();

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
abstract public class StructureCanvas extends Canvas implements Observer {

  ///////////////////////////////////
  // Vector of functions to be per-
  // formed upon data structure nodes

  protected Vector functionVector = new Vector();

  protected int    nodeSize;

  ///////////////////////////////////
  // ImageFrame reference so that
  // image objects can be easily
  // created by Functions

  public ImageFrame frame;

  ///////////////////////////////////
  // Double buffering instance var's

  protected Dimension offDimension;
  protected Graphics  offGraphics;
  protected Image     offImage;

  ///////////////////////////////////
  // Vector to hold copys of the 
  // data structure nodes (each copy
  // is represented as a Vector).
  
  protected Vector copyVector = new Vector();

  protected boolean functionSuspended = true;
  protected boolean bufferLoaded      = false;
  protected boolean paintingDone      = true;

  protected Vector updateVector = new Vector();
  protected String updateString = "";

  protected FunctionThread functionThread = new FunctionThread(this);
  protected BufferThread   bufferThread   = new BufferThread(this);
  protected AnimatorThread animatorThread = new AnimatorThread(this);

  /**
   * Constructor.
   */
  public StructureCanvas(ImageFrame frame, int nodeSize) {
    this.frame    = frame;
    this.nodeSize = nodeSize;
  }

  ///////////////////////////////////////////////////////////////////////////
  // Add/Remove functions for StructureCanvas Vectors
  //
  //  functionVector
  //  nodeVector
  //
  // These Vectors are protected and should not be mutatable by outside objects
  // to insure proper functioning of animations.
  ///////////////////////////////////////////////////////////////////////////

  public synchronized void addFunction(Function function) {
    functionVector.addElement(function);

    functionSuspended = false;
    notifyAll();
  }

  public void removeFunction(Function function) {
    functionVector.removeElement(function);
  }

  public int numberFunctions() {
    return functionVector.size();
  }

  public int getNodeSize() {
    return nodeSize;
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

  protected synchronized void doFunctions() {
    int i = 0;

    while (functionSuspended) {
      try {
	wait();
      } catch (InterruptedException e) {
      }
    }

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
      functionSuspended = true;
  }

  abstract protected synchronized void cloneNodes();

  abstract public synchronized void loadBuffer();
  abstract public synchronized void paintAnimation();

  public void update(Observable observed, Object arg) {
    if (arg instanceof String) {
      updateVector.addElement(arg);
    }
  }

}
