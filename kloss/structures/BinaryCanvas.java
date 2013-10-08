package kloss.structures;

import java.awt.Point;
import java.awt.Image;
import java.awt.Event;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Dimension;

import java.util.Vector;

import kloss.graphics.images.ImageObject;
import kloss.graphics.images.ImageFrame;
import kloss.graphics.functions.Function;

import kloss.structures.nodes.Node;
import kloss.structures.nodes.TreeNode;


public class BinaryCanvas extends StructureCanvas {

  ////////////////////////////////////////////////////////////
  // For DEPTH_MULT and WIDTH_MULT the best results are
  // achieved when the width multiplier is one fourth the 
  // value of the depth.

  /** Multiplier for node size to determine how far down a new node
   *  drops froms its parent upon creation (graphics routine).
   */
  final static double DEPTH_MULT = 3.0;

  /** Multiplier for node size to determine how far left/right a new
   *  node spreads froms its parent upon creation (graphics routine).
   */
  final static double WIDTH_MULT = 0.75;

  /** Vector which holds 2 member int[] arrays. Each index in the 
   *  vector corresponds to a level within the binary tree (zero being
   *  the first level, one being the second, etc.). The int arrays 
   *  hold information on the amount a new node drops and spreads from
   *  its parent upon creation (graphics routine).
   */
  Vector levelInfo = new Vector();

  int levelDepth = 80;
  int levelWidth = 20;

  /** The current number of levels in the tree. Levels begin at zero and
   *  progress from there (i.e. root is at level zero, root's children at
   *  level one, etc.). A value of negative one indicates an empty tree.
   */
  int level = -1;

  public final static int LEVEL_ONE   = 0;
  public final static int LEVEL_TWO   = 1;
  public final static int LEVEL_THREE = 2;

  /** The number of nodes in tree (soley).
   */
  int treeNodes = 0;

  /** The total nodes held in class. This includes all the nodes in the
   *  tree plus the highlight node and delete node if they exist.
   */
  int totalNodes = 0;

  /** The rank list holds references to all the nodes in the binary
   *  tree sorted by rank. Rank is defined to be
   *  <PRE>
   *    left child rank  = (2 * parent rank)
   *    right child rank = (2 * parent rank) + 1
   *  </PRE>
   *  where the root node has rank 1. This allows all nodes to be
   *  ordered linearly and accessed via a rank index in constant time.
   */
  TreeNode[] rankList;

  /** The delete node holds a reference to the node which is currently 
   *  being deleted. Although the abstract representation of a deleted
   *  node may be removed from the tree immediately, the graphical rep-
   *  resentation often lingers (e.g. due to some animation). Only one
   *  node can be deleted at a time so only a single reference is used.
   */
  TreeNode deleteNode;

  /** The highlight node holds a reference to the highlight image object.
   *  Only one node can be highlighted at a time so only a single ref-
   *  erence is used.
   */
  ImageObject highLight;

  /** Reference to a node in the tree which is being dragged (via a mouse
   *  drag). When this reference is not null, drag bounds checking is 
   *  skipped and the drag object is moved and repainted immediately, thus
   *  greatly improving performance.
   */
  ImageObject dragObject = null;

  public BinaryCanvas(ImageFrame frame, int nodeSize) {
    super(frame, nodeSize);
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }


  /** Add a new node to the rank list. <STRONG>Note:</STRONG> if the node
   *  has any children, they too are added to the rank list in their cor-
   *  responding rank positions (recursively).
   *
   * @param node        The node to add to the rank list.
   */
  public void addToRankList(TreeNode node) {
    int levelCheck;
    int rank = node.getRank();

    TreeNode leftChild  = node.getLeftChild();
    TreeNode rightChild = node.getRightChild();

    ////////////////////////////////////////
    // Recurse first so that as we filter back
    // up the tree, the rank list is either
    // already created or of large enough 
    // size to contain new node.

    if (leftChild != null)
      addToRankList(leftChild);

    if (rightChild != null)
      addToRankList(rightChild);

    ////////////////////////////////////////
    // If rankList not yet created then make
    // it.

    if (rankList == null) 
      rankList = new TreeNode[rank];

    ////////////////////////////////////////
    // Else if rankList not large enough for
    // expanded tree, remake the list.

    else if (rankList.length < rank) {
      TreeNode[] tempList = new TreeNode[rank];
      System.arraycopy(rankList, 0, tempList, 0, rankList.length);
      rankList = tempList;
    }

    ////////////////////////////////////////
    // Set rank index to reference the node

    rankList[rank - 1] = node;

    ////////////////////////////////////////
    // Increase the level number of tree if
    // necessary.

    if ( (levelCheck = 
	  (int) Math.floor((double) Math.log((double) rank) /
			   (double) Math.log((double) 2)))
	 > level)
      level = levelCheck;
  }

  /** Remove a node and all its children from the rank list.
   *
   * @param node        The node to be removed from the rank list.
   */
  public void removeFromRankList(TreeNode node) {
    int startCheck = (int) Math.pow(2, level) - 1;
    int endCheck   = (int) Math.pow(2, level + 1) - 1;

    int size;

    TreeNode leastChild     = node.getLeastChild();
    TreeNode greatestChild  = node.getGreatestChild();

    boolean reduceLevel = true;

    if ( (size = leastChild.getRank()) < greatestChild.getRank())
      size = greatestChild.getRank();

    if ( (rankList != null) && (rankList.length >= size))
      clearRank(node);

    ////////////////////////////////////////
    // If the deleted node was the only node
    // on its tree level, then decrement the
    // level instance variable.

    for (int i = startCheck; (i < rankList.length) && (i < endCheck); i++)
      if (rankList[i] != null) {
	reduceLevel = false;
	break;
      }
    
    if (reduceLevel) {
      level--;

      if ( (level > -1) && (levelInfo.size() > level))
	levelInfo.removeElementAt(level);
    }
  }

  /** Recursively set the associated indexes in the rank list of the 
   *  current node and its children to null. This method is called by
   *  <CODE>removeFromRankList()</CODE> above.
   *
   * @param node        The node to be removed from the rank list.
   */
  protected void clearRank(TreeNode node) {
    TreeNode leftChild;
    TreeNode rightChild;

    if ( (leftChild = node.getLeftChild()) != null)
      clearRank(leftChild);
    if ( (rightChild = node.getRightChild()) != null)
      clearRank(rightChild);

    rankList[node.getRank() - 1] = null;
  }

  /** Retrieve the node with a particular rank. 
   *
   * @param  nodeRank   The rank of the requested node.
   * @return            The node with the requested rank or null if
   *                    no such node exists.
   */
  public TreeNode getNodeAtRank(int nodeRank) {
    int rank = nodeRank - 1;

    if (rank > rankList.length)
      return null;
    else
      return rankList[rank];
  }



  public void setLevelValues(int level, int[] values) {
    levelInfo.setElementAt(values, level);
  }

  public int[] getLevelValues(int level) {
    int size = levelInfo.size();

    if (size == level) {
      int[] values = new int[3];

      ////////////////////////////////////////
      // Assign the new level with default 
      // values (indicated by LEVEL_ONE ident-
      // ifier).

      values[0] = levelWidth;
      values[1] = levelDepth;
      values[2] = LEVEL_ONE;

      levelInfo.addElement(values);
    }
    
    if (size < level)
      return null;
    else
      return (int[]) levelInfo.elementAt(level);
  }

  protected synchronized void cloneNodes() {
    ImageObject[] temp = new ImageObject[totalNodes];

    int index = totalNodes - 1;
    int count = 0;

    if (highLight != null)
      temp[count++] = new ImageObject(highLight);
    if (deleteNode != null)
      temp[count]   = new TreeNode(deleteNode);

    TreeNode[] tempList = new TreeNode[rankList.length];

    int rankLeft;
    int rankRight;

    for (int i = 0; i < tempList.length; i++) {
      if (rankList[i] != null) {
	tempList[i] = new TreeNode(rankList[i]);

	rankLeft  = ((i + 1) * 2) - 1;
	rankRight = ((i + 1) * 2);

	if (rankLeft < tempList.length)
	  tempList[i].setLeftChild(tempList[rankLeft]);

	if (rankRight < tempList.length)
	  tempList[i].setRightChild(tempList[rankRight]);

	if (rankList[i].getParent() != null)
	  tempList[i].setParent(tempList[(int) Math
				  .floor((double) (i + 1) / 2) - 1]);

	temp[index--] = tempList[i];
      }
    }

    copyVector.addElement(temp);
    notifyAll();
  }

  public boolean mouseDrag(Event evt, int x, int y) {
    
    if (dragObject == null) {
      for (int i = 0; i < rankList.length; i++) {
	if (rankList[i] != null) {
	  if (rankList[i].inBounds(x,y)) {
	    dragObject = rankList[i];
	    break;
	  }
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

  public synchronized void loadBuffer() {
    while (bufferLoaded || (copyVector.size() == 0)) {
      try {
	wait();
      } catch (InterruptedException e) {
      }
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

    ImageObject[] objects = (ImageObject[]) copyVector.firstElement();
    copyVector.removeElementAt(0);

    for (int i = 0; i < objects.length; i++) {
      if (objects[i] != null)
	objects[i].draw(offGraphics);
      else
	System.out.println("objects[" + i + "] is null");
    }

    bufferLoaded = true;
    notifyAll();
  }

  public synchronized void paintAnimation() {
    while ( (bufferLoaded == false) ||
	    (paintingDone == false)){
      try {
	wait();
      } catch (InterruptedException e) {
      }
    }
    paintingDone = false;

    repaint();
  }

  public void update(Graphics g) {
    paint(g);
  }

  public synchronized void paint(Graphics g) {
    if (bufferLoaded) {

      g.drawImage(offImage, 0, 0, this);

      if (updateVector.size() > 0) {

	////////////////////////////////////////
	// Hack used to update the label element
	// in the BinaryTree container.

	if (updateString
	    .equals((String) updateVector. firstElement()) == false) {
	  ((BinaryTree) frame)
	    .updateLabel((String) updateVector.firstElement());
	  updateString = (String) updateVector.firstElement();
	}
	updateVector.removeElementAt(0);
      }

      bufferLoaded = false;
      paintingDone = true;

      notifyAll();
    }
    else {

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

      if (rankList != null)
	for (int i = (rankList.length - 1); i > -1; i--) {
	  if (rankList[i] != null)
	    rankList[i].draw(offGraphics);
	}

      g.drawImage(offImage, 0, 0, this);
    }
  }

    
  /** Return the root of the tree (if it exists).
   */
  public TreeNode getRootNode() {
    if ( (rankList == null) || rankList[0] == null)
      return null;
    else
      return rankList[0];
  }

  public void addNode(ImageObject node) {
    if (node instanceof TreeNode) {
      addToRankList((TreeNode) node);

      treeNodes++;
      totalNodes++;
    }
    else {
      highLight = node;

      totalNodes++;
    }
  }

  public void removeNode(ImageObject node) {
    if (node instanceof TreeNode) {
      removeFromRankList((TreeNode) node);

      treeNodes--;
      totalNodes--;
    }
    else {
      highLight = null;

      totalNodes--;
    }
  }

  /** Sets the delete node reference to node.
   */
  public void setDeleteNode(TreeNode node) {
    deleteNode = node;
    totalNodes++;
  }

  /** Clears the delete node reference.
   */
  public void clearDeleteNode() {
    deleteNode = null;
    totalNodes--;
  }

}
