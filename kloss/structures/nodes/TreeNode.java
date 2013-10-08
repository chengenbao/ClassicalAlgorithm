package kloss.structures.nodes;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;

import java.util.Vector;

import kloss.graphics.images.ImageFrame;


public class TreeNode extends Node {


  /** The rank allows the binary tree to linearly order all the nodes.
   *  Rank is defined to be
   *  <PRE>
   *    left child rank  = (2 * parent rank)
   *    right child rank = (2 * parent rank) + 1
   *  </PRE>
   *  where the root node has rank 1. A node may be accessed via its
   *  rank index in constant time. The default rank of a node is set
   *  to a negative value to indicate that it has not yet been defined.
   *  Rank cannot be assigned until the placement of a node within
   *  the binary tree has been determined.
   */
  protected int rank = -1;


  /** Root Constructor.
   */
  public TreeNode(Point origin, ImageFrame frame, int value) {
    super(origin, frame, value);
  }


  /** Copy Constructor. This constructor creates a near exact copy of the 
   *  TreeNode value passed as a parameter. The child and parent references,
   *  however, are <STRONG>NOT</STRONG> copied (see Node superclass).
   *
   * @param node        The TreeNode to be copied.
   */
  public TreeNode(TreeNode node) {
    super(node);

    this.rank = node.rank;
  }


  /** Null Constructor. This constructor creates an abstract TreeNode. 
   *  That is, the node has no graphical representation. The returned node
   *  is used mainly for graphics operation which require a location on
   *  the graphics context but no physical representation (e.g. the move-
   *  ment of edges).
   */
  public TreeNode(Point origin) {
    super(origin);
  }


  /** Retrieve the rank of the current node. Note that if this value
   *  has not been set then a negative rank will be returned (indicating
   *  an error).
   *
   * @return            The rank of the current node within the tree.
   */
  public int getRank() {
    return rank;
  }


  /** Set the rank of the current node. This value should be set as soon
   *  as the parent of the current node is known (since rank depends upon
   *  the parent's rank). <STRONG>Note:</STRONG> this function recursively
   *  sets the rank of all the current node's children, too (if the node
   *  has any children).
   *
   * @param rank        The rank to set the current node to.
   */
  public void setRank(int rank) {
    this.rank = rank;

    TreeNode leftChild;
    TreeNode rightChild;

    if ( (leftChild = getLeftChild()) != null)
      leftChild.setRank(rank * 2);

    if ( (rightChild = getRightChild()) != null)
      rightChild.setRank((rank * 2) + 1);
  }


  /** Set the left child of the current node to node (this value may be
   *  null).
   *
   * @param node        The node value the current node's left child 
   *                    reference is set to.
   */
  public void setLeftChild(TreeNode node) {

    ////////////////////////////////////////
    // All tree references are elements in a
    // hashtable, and thus cannot be set to
    // null. They must be removed.

    if (node == null)
      nodes.remove("leftChild");
    else
      nodes.put("leftChild", node);
  }


  /** Retrieve the current node's left child reference (either null or
   * another node).
   *
   * @return            The current node's left child reference.
   */         
  public TreeNode getLeftChild() {
    return (TreeNode) nodes.get("leftChild");
  }


  /** Set the right child of the current node to node (this value may be
   *  null).
   *
   * @param node        The node value the current node's right child 
   *                    reference is set to.
   */
  public void setRightChild(TreeNode node) {

    ////////////////////////////////////////
    // All tree references are elements in a
    // hashtable, and thus cannot be set to
    // null. They must be removed.

    if (node == null)
      nodes.remove("rightChild");
    else
      nodes.put("rightChild", node);
  }


  /** Retrieve the current node's right child reference (either null or
   * another node).
   *
   * @return            The current node's right child reference.
   */         
  public TreeNode getRightChild() {
    return (TreeNode) nodes.get("rightChild");
  }


  /** Set the left parent of the current node to node (this value may be
   *  null).
   *
   * @param node        The node value the current node's parent reference 
   *                    is set to.
   */
  public void setParent(TreeNode node) {

    ////////////////////////////////////////
    // All tree references are elements in a
    // hashtable, and thus cannot be set to
    // null. They must be removed.

    if (node == null)
      nodes.remove("parent");
    else
      nodes.put("parent", node);
  }


  /** Retrieve the current node's parent reference (either null or
   * another node).
   *
   * @return            The current node's parent reference.
   */         
  public TreeNode getParent() {
    return (TreeNode) nodes.get("parent");
  }


  /** Return the node with the least value in the current sub-tree.
   *  If the root of the sub-tree has no left children then the root
   *  is returned (the root is the node with the least value).
   *
   * @return            The node with the least value in the current
   *                    sub-tree.
   */
  public TreeNode getLeastChild() {
    TreeNode check;

    ////////////////////////////////////////
    // If no left child then the current 
    // node is the least child.

    if ( (check = (TreeNode) nodes.get("leftChild")) == null)
      return this;

    ////////////////////////////////////////
    // Else iterate until the least child is
    // found, and return reference.

    else
      while (check.nodes.get("leftChild") != null) {
	check = (TreeNode) check.nodes.get("leftChild");
      }

    return check;
  }


  /** Return the node with the greatest value in the current sub-tree.
   *  If the root of the sub-tree has no right children then the root
   *  is returned (the root is the node with the greatest value).
   *
   * @return            The node with the greatest value in the current
   *                    sub-tree.
   */
  public TreeNode getGreatestChild() {
    TreeNode check;

    ////////////////////////////////////////
    // If no right child then the current 
    // node is the greatest child.

    if ( (check = (TreeNode) nodes.get("rightChild")) == null)
      return this;

    ////////////////////////////////////////
    // Else iterate until the greatest child
    // is found, and return reference.

    else
      while (check.nodes.get("rightChild") != null) {
	check = (TreeNode) check.nodes.get("rightChild");
      }

    return check;
  }


  /** Translates a node and its subtree. That is, both the node and its 
   *  children are shifted vertically and horizontally by x and y amounts.
   *
   * @param x          The lateral amount to shift the subtree.
   * @param y          The vertical amount to shift the subtree.
   */
  public void translate(int x, int y) {
    origin.translate(x, y);

    TreeNode leftChild  = (TreeNode) nodes.get("leftChild");
    TreeNode rightChild = (TreeNode) nodes.get("rightChild");
 
    if (leftChild != null)
      leftChild.translate(x, y);

    if (rightChild != null)
      rightChild.translate(x, y);
  }


  /** Translates a single node. <STRONG>Note:</STRONG> this method does
   *  <I>not</I> translate the node's subtree.
   *
   * @param x           The lateral amount to shift the current node.
   * @param y           the vertical amount to shift the current node.
   */
  public void translateNode(int x, int y) {
    origin.translate(x, y);
  }


  /** Move a node and its subtree to a new location. That is, both the node
   *  and its children are moved to a new location, (x, y). The whole sub-
   *  structure (tree) is moved intacted centered upon the head (root) node.
   *  That is, the distances between the current node and its children are
   *  maintained.
   * 
   * @param x          The lateral point to move the subtree to.
   * @param y          The vertical point to move the subtree to.
   */
  public void move(int x, int y) {
    TreeNode leftChild  = (TreeNode) nodes.get("leftChild");
    TreeNode rightChild = (TreeNode) nodes.get("rightChild");

    int width;
    int depth;

    if (leftChild != null) {
      width = origin.x - leftChild.origin.x;
      depth = leftChild.origin.y - origin.y;

      leftChild.move(x - width, y + depth);
    }
      
    if (rightChild != null) {
      width = rightChild.origin.x - origin.x;
      depth = rightChild.origin.y - origin.y;

      rightChild.move(x + width, y + depth);
    }

    origin.move(x, y);
  }


  /** Move a single node. <STRONG>Note:</STRONG> this method does <I>not</I>
   *  move the node's subtree.
   *
   * @param x           The lateral location the current node is moved to.
   * @param y           the vertical location the current node is moved to.
   */
  public void moveNode(int x, int y) {
    origin.move(x, y);
  }


  public void draw(Graphics g) {
    Color backColor = g.getColor();
    
    TreeNode parent  = (TreeNode) nodes.get("parent");

    g.setColor(Color.black);
    if (parent != null) {

      g.setColor(Color.blue);
      g.drawLine(origin.x, origin.y, parent.origin.x, parent.origin.y);

      g.setColor(Color.blue.brighter());
      g.drawLine(origin.x, origin.y + 1, parent.origin.x,
		 parent.origin.y + 1);

      g.setColor(Color.gray);
      g.drawLine(origin.x, origin.y + 4, parent.origin.x,
		 parent.origin.y + 4);
    }
    g.setColor(backColor);

    super.draw(g);
  }


  protected TreeNode cloneNode(Vector returnVector, TreeNode parent) {
    TreeNode copy = new TreeNode(this);

    if (parent != null)
      copy.setParent(parent);

    TreeNode leftChild  = (TreeNode) nodes.get("leftChild");
    TreeNode rightChild = (TreeNode) nodes.get("rightChild");

    if (leftChild != null)
      copy.setLeftChild(leftChild.cloneNode(returnVector, copy));

    if (rightChild != null)
      copy.setRightChild(rightChild.cloneNode(returnVector, copy));

    returnVector.addElement(copy);
    return copy;
  }


  public synchronized Vector cloneStructure() {
    Vector treeVector = new Vector();
    cloneNode(treeVector, null);
    return treeVector;
  }
    
}
