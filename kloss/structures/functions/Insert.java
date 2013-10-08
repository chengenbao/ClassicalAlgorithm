package kloss.structures.functions;

import kloss.graphics.functions.Function;

import java.awt.Point;

import java.util.Observer;
import java.util.Observable;

import kloss.graphics.images.ImageFrame;
import kloss.graphics.images.ImageObject;

import kloss.structures.nodes.Node;
import kloss.structures.nodes.TreeNode;
import kloss.structures.StructureCanvas;
import kloss.structures.BinaryCanvas;

public class Insert extends Function implements Observer {

  ////////////////////////////////////////
  // Reference to BinaryCanvas used exten-
  // sively since insert performs does 
  // many modifications to the Binary Tree

  protected BinaryCanvas canvas;

  ////////////////////////////////////////
  // Reference to new node created

  protected TreeNode newNode;

  ////////////////////////////////////////
  // References used to trapse down the
  // binary tree

  protected TreeNode currentNode;
  protected TreeNode nextNode;

  ////////////////////////////////////////
  // Whether or not the current function 
  // is blocking (not executing) until 
  // another function completes its task.

  protected boolean blocked        = false;
  protected boolean treeAdjustDone = false;

  ////////////////////////////////////////
  // The current level in the tree

  protected int level = 0;

  /** The number of levels in the binary tree at the start of the
   *  insert operation. If the number of levels changes by the end
   *  of the operation then the SpreadTree function will be called
   *  to uncrowd the tree.
   */
  protected int startLevel;

  protected String updateInfo = "";

  public Insert(int value, BinaryCanvas canvas) {
    this.canvas     = canvas;
    this.startLevel = canvas.getLevel();

    newNode = new TreeNode(new Point((int) (canvas.size().width / 2),
				     (int) (canvas.getNodeSize() / 2)),
				     canvas.frame, value);
  }


  public Insert(TreeNode newNode, BinaryCanvas canvas) {
    this.canvas     = canvas;
    this.startLevel = canvas.getLevel();

    this.newNode    = newNode;
  }


  public void update(Observable observed, Object arg) {
    if (observed instanceof HighLight) {
      blocked = false;
    }
    else if (observed instanceof MoveNode) {
      blocked        = false;
      treeAdjustDone = true;
    }
  }

  public void performFunction() {

    if (blocked) {

      setChanged();
      notifyObservers(updateInfo);

    }
    else if (treeAdjustDone) {

      setChanged();
      notifyObservers((Object) newNode);

      finished = true;
    }
    else {

      ////////////////////////////////////////
      // The tree is empty so create the root
      // node.

      if (canvas.getRootNode() == null) {

	////////////////////////////////////////
	// The root node always has rank 1.

	newNode.setRank(1);
	
	canvas.addNode(newNode);

	Function mover = new MoveNode(
			   new Point((int) (canvas.size().width / 2),
				     (int) (canvas.getNodeSize() * 4)), 
			   newNode);
	mover.addObserver(this);
	
	canvas.addFunction(mover);

	////////////////////////////////////////
	// Notify observers that a new node has
	// been inserted.

	updateInfo = "Root Node Created with Value : " + newNode.getValue();

	setChanged();
	notifyObservers(updateInfo);

	////////////////////////////////////////
	// Nothing else to do, so wait until 
	// tree adjustment completes.

	blocked = true;
      }

      ////////////////////////////////////////
      // Else if we have just started the in-
      // sertion function, retrieve the root
      // node and highlight it (indication of
      // comparison).

      else if (currentNode == null) {
	currentNode = canvas.getRootNode();

	Function highLight = new HighLight(currentNode, canvas);
	highLight.addObserver(this);

	canvas.addFunction(highLight);

	updateInfo = "Beginning Search at Root: " + currentNode.getValue();

	setChanged();
	notifyObservers(updateInfo);

	////////////////////////////////////////
	// Wait until the HighLight function
	// terminates (Insert will receive noti-
	// fication of this via functionFinished)

	blocked = true;
      }

      ////////////////////////////////////////
      // Else, do comparison on current node
      // and either insert new node as a child
      // or continue Insertion process on the
      // next node (either left or right child
      // of current node).

      else {

	if (currentNode.lessThan(newNode)) {
	  
	  nextNode = currentNode.getLeftChild();

	  ////////////////////////////////////////
	  // Insert new node as a left child.

	  if (nextNode == null) {

	    ////////////////////////////////////////
	    // Set parent's left child pointer and
	    // new node's parent pointer.

	    currentNode.setLeftChild(newNode);
	    newNode.setParent(currentNode);

	    ////////////////////////////////////////
	    // Set rank to 2 * parent's rank and add
	    // the new node to the tree.

	    newNode.setRank(currentNode.getRank() * 2);
	    canvas.addNode(newNode);

	    ////////////////////////////////////////
	    // Perform the left child insertion func-
	    // tion (graphics operation).

	    newNode.moveNode(currentNode.origin().x,
			     currentNode.origin().y);

	    int[] values = canvas.getLevelValues(level);
	    Function mover = new MoveNode(new Point(newNode.origin().x -
	      values[0], newNode.origin().y + values[1]), newNode);
	    mover.addObserver(this);

	    canvas.addFunction(mover);

	    updateInfo = "New Node : " + newNode.getValue() + 
	      " Added as Left Child of Node : " + currentNode.getValue();

	    setChanged();
	    notifyObservers(updateInfo);

	    if (startLevel < canvas.getLevel())
	      canvas.addFunction(new SpreadTree(canvas));
	      
	    ////////////////////////////////////////
	    // Nothing else to do, so wait until
	    // tree adjustment is done.

	    blocked = true;
	  }

	  ////////////////////////////////////////
	  // Else continue process on the next
	  // node (left child or current node).

	  else {

	    currentNode = nextNode;

	    Function highLight = new HighLight(currentNode, canvas);
	    highLight.addObserver(this);

	    canvas.addFunction(highLight);

	    updateInfo = "Node : " + currentNode.getValue() + 
	      " Compared with Value : " + newNode.getValue();

	    setChanged();
	    notifyObservers(updateInfo);

	    ////////////////////////////////////////
	    // Wait until the HighLight function
	    // terminates (Insert will receive noti-
	    // fication of this via functionFinished)

	    blocked = true;

	    ////////////////////////////////////////
	    // Increment the level variable.

	    level++;
	  }
	}
	else if (currentNode.greaterThan(newNode)) {

	  nextNode = currentNode.getRightChild();

	  ////////////////////////////////////////
	  // Insert new node as a right child.

	  if (nextNode == null) {

	    ////////////////////////////////////////
	    // Set parent's right child pointer and
	    // new node's parent pointer.

	    currentNode.setRightChild(newNode);
	    newNode.setParent(currentNode);

	    ////////////////////////////////////////
	    // Set rank to (2 * parent's rank) + 1
	    // and add new node to tree.

	    newNode.setRank((currentNode.getRank() * 2) + 1);
	    canvas.addNode(newNode);

	    ////////////////////////////////////////
	    // Perform the left child insertion func-
	    // tion (graphics operation).
	    
	    newNode.move(currentNode.origin().x,
			 currentNode.origin().y);

	    int[] values = canvas.getLevelValues(level);
	    Function mover = new MoveNode(new Point(newNode.origin().x +
              values[0], newNode.origin().y + values[1]), newNode);
	    mover.addObserver(this);

	    canvas.addFunction(mover);

	    updateInfo = "New Node : " + newNode.getValue() + 
	      " Added as Right Child of Node : " + currentNode.getValue();

	    setChanged();
	    notifyObservers(updateInfo);

	    if (startLevel < canvas.getLevel())
	      canvas.addFunction(new SpreadTree(canvas));
	    
	    ////////////////////////////////////////
	    // Nothing else to do, so wait until
	    // tree adjustment is done.

	    blocked = true;
	  }

	  ////////////////////////////////////////
	  // Else continue process on the next
	  // node (right child or current node).

	  else {

	    currentNode = nextNode;

	    Function highLight = new HighLight(currentNode, canvas);
	    highLight.addObserver(this);

	    canvas.addFunction(highLight);

	    updateInfo = "Node : " + currentNode.getValue() +
	      " Compared with Value : " + newNode.getValue();

	    setChanged();
	    notifyObservers(updateInfo);

	    ////////////////////////////////////////
	    // Wait until the HighLight function
	    // terminates (Insert will receive noti-
	    // fication of this via functionFinished)

	    blocked = true;

	    ////////////////////////////////////////
	    // Increment the level variable.

	    level++;
	  }
	}

	////////////////////////////////////////
	// We reach this point only if the new
	// node value already exists within the
	// tree. In this case we indicate term-
	// nation and do not modify the tree.

	else {

	  updateInfo = "Node : " + newNode.getValue() + 
	    " Already Exists in Tree";

	  setChanged();
	  notifyObservers(updateInfo);

	  setChanged();
	  notifyObservers((Object) null);

	  finished = true;
	}
      }
    }
  }
}
