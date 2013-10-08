package kloss.structures.nodes;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;

import java.util.Hashtable;
import java.util.Vector;

import kloss.graphics.images.ImageFrame;
import kloss.graphics.images.ImageObject;


public abstract class Node extends ImageObject implements Orderable {

  ///////////////////////////////////
  // Orderable value used for compar-
  // isons in data structures

  protected int value;

  ///////////////////////////////////
  // Links to other and all nodes

  protected Hashtable nodes;


  public Node(Point origin, ImageFrame frame, int value) {
    super(origin, frame, "" + value);

    this.value = value;
    this.nodes = new Hashtable();
  }

  /** Null constructor. When this constructor is called a pure graphics
   *  object is created (only the point concept is implemented). This is
   *  used for graphics operations which require a location but no actual
   *  image or node.
   */
  public Node(Point origin) {
    super(origin, null, null);
  }

  public Node(Node node) {
    super( (ImageObject) node);

    this.value = value;
    this.nodes = new Hashtable();
  }


  public int getValue() {
    return value;
  }

  ///////////////////////////////////////////////////////////////////////////
  // Methods required by Orderable interface.
  //
  // These methods satisfy orderable requirements for the various algorithms
  // and data structures.
  ///////////////////////////////////////////////////////////////////////////

  public boolean lessThan(Orderable object) {
    return ( ( (Node) object).value < value);
  }

  public boolean greaterThan(Orderable object) {
    return ( ( (Node) object).value > value);
  }

  public boolean equal(Orderable object) {
    return ( ( (Node) object).value == value);
  }

  abstract public Vector cloneStructure();
}
    
