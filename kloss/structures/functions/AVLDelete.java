package kloss.structures.functions;

import kloss.graphics.functions.Function;

import java.awt.Point;

import java.util.Observer;
import java.util.Observable;

import kloss.graphics.images.ImageFrame;
import kloss.graphics.images.ImageObject;

import kloss.structures.nodes.Node;
import kloss.structures.nodes.TreeNode;
import kloss.structures.nodes.AVLNode;
import kloss.structures.StructureCanvas;
import kloss.structures.BinaryCanvas;

public class AVLDelete extends Function implements Observer {

  final static int CASE_ONE   = 0;
  final static int CASE_TWO   = 1;
  final static int CASE_THREE = 2;

  int caseTest;

  BinaryCanvas canvas = null;

  TreeNode deleteNode = null;
  AVLNode  parentNode = null;

  TreeNode leftChild  = null;
  TreeNode rightChild = null;
  TreeNode parent     = null;

  boolean searchDone = false;
  boolean casesDone  = false;
  boolean nodesSet   = false;
  boolean inAdjust   = false;

  boolean blocked = false;

  int value;

  int startLevel;

  public AVLDelete(int value, BinaryCanvas canvas) {
    this.canvas = canvas;
    this.value  = value;

    startLevel = canvas.getLevel();
  }

  public void update(Observable observed, Object arg) {
    if ( (observed instanceof Search) && (arg == null)) {
      searchDone = true;
      blocked    = false;
    }
    else if ( (observed instanceof Search) && (arg instanceof TreeNode)) {
      deleteNode = (TreeNode) arg;
      parentNode = (AVLNode) deleteNode.getParent();

      searchDone = true;
      blocked    = false;
    }
    else if (observed instanceof MoveSubtree) {
      casesDone = true;
      blocked   = false;
    }
    else if (observed instanceof AdjustTree) {
      inAdjust  = true;
      blocked   = false;
    }
    else if (observed instanceof ContractTree) {
      inAdjust  = true;
      blocked   = false;
    }
    else if (observed instanceof AVLLeftRotation) {
      blocked   = false;
    }
    else if (observed instanceof AVLRightRotation) {
      blocked   = false;
    }
  }

  public void performFunction() {
    if (blocked) {
    }
    else if (searchDone == false) {
      
	Function search = new Search(value, canvas);
	search.addObserver(this);
	search.addObserver(canvas);

	canvas.addFunction(search);

	blocked = true;
    }
    else if ( (searchDone) && (deleteNode == null)) {
      
      setChanged();
      notifyObservers((Object) ("Deletion Failed"));

      finished = true;
    }
    else if (casesDone && inAdjust == false) {

      setChanged();
      notifyObservers((Object) ("Deleted Node : " + deleteNode.getValue()));

      canvas.clearDeleteNode();

      if (startLevel > canvas.getLevel()) {
	  Function mover = new ContractTree(canvas);
	  mover.addObserver(this);

	  canvas.addFunction(mover);
      }
      else {
	  Function mover = new AdjustTree(canvas);
	  mover.addObserver(this);

	  canvas.addFunction(mover);
      }

      blocked = true;

    } else if (inAdjust) {

      AVLNode rootNode = (AVLNode) canvas.getRootNode();
      if (rootNode != null) {

	  rootNode.adjustBalanceFactor();
	  
	  AVLNode trapse = parentNode;
	  while ( (trapse != null) && 
	      ( (trapse.getBalanceFactor() !=  2) &&
		(trapse.getBalanceFactor() != -2))) 
	      trapse = (AVLNode) trapse.getParent();
      
	  parentNode = trapse;

	  if (trapse != null) {
	      Function rotator;

	      if (trapse.getBalanceFactor() == 2) 
		  rotator = new AVLLeftRotation(trapse, canvas);
	      else
		  rotator = new AVLRightRotation(trapse, canvas);

	      canvas.addFunction(rotator);
	      blocked = true;

	  } else {

	      setChanged();
	      notifyObservers((Object) "Deletion Done");

	      finished = true;
	  }
      } else {

	  setChanged();
	  notifyObservers((Object) "Deletion Done");

	  finished = true;
      }      
    }
    else {

      if (nodesSet == false) {
	leftChild  = deleteNode.getLeftChild();
	rightChild = deleteNode.getRightChild();
	parent     = deleteNode.getParent();

	nodesSet = true;
      }

      if ( (leftChild == null) && (rightChild == null)) {

	caseTest = CASE_ONE;

	canvas.setDeleteNode(deleteNode);
	canvas.removeNode(deleteNode);

	////////////////////////////////////////
	// Detach parent from node.

	if (parent != null) {
	  if (deleteNode == parent.getLeftChild())
	    parent.setLeftChild(null);
	  else
	    parent.setRightChild(null);
	}

	////////////////////////////////////////
	// Detach child from parent.

	deleteNode.setParent(null);

	Function mover = new MoveSubtree(new Point(deleteNode.origin().x,
	  canvas.size().height +  canvas.getNodeSize()), deleteNode);
	mover.addObserver(this);

	canvas.addFunction(mover);
	
	blocked = true;
      }
      else if (leftChild == null) {

	caseTest = CASE_TWO;

	canvas.setDeleteNode(deleteNode);
	canvas.removeNode(deleteNode);

	////////////////////////////////////////
	// Detach parent from node and attach to
	// node's right child.

	if (parent != null) {
	  if (deleteNode == parent.getLeftChild())

	    parent.setLeftChild(rightChild);
	  else
	    parent.setRightChild(rightChild);

	  rightChild.setParent(parent);
	}
	else
	  rightChild.setParent(null);

	////////////////////////////////////////
	// Detach node from parent and rightChild
	// and attach right child to parent.

	deleteNode.setParent(null);
	deleteNode.setRightChild(null);

	rightChild.setRank(deleteNode.getRank());
	canvas.addToRankList(rightChild);

	Function moverOne = new MoveSubtree(new Point(deleteNode.origin().x,
	  canvas.size().height + canvas.getNodeSize()), deleteNode);
	Function moverTwo = new MoveSubtree(new Point(deleteNode.origin().x,
	  deleteNode.origin().y), rightChild);
	moverOne.addObserver(this);
	moverTwo.addObserver(this);

	canvas.addFunction(moverOne);
	canvas.addFunction(moverTwo);

	blocked = true;
      }
      else if (rightChild == null) {

	caseTest = CASE_TWO;

	canvas.setDeleteNode(deleteNode);
	canvas.removeNode(deleteNode);

	////////////////////////////////////////
	// Detach parent from node and attach to
	// node's right child.

	if (parent != null) {
	  if (deleteNode == parent.getLeftChild()) 
	    parent.setLeftChild(leftChild);
	  else
	    parent.setRightChild(leftChild);

	  leftChild.setParent(parent);
	}
	else
	  leftChild.setParent(null);

	////////////////////////////////////////
	// Detach node from parent and rightChild

	deleteNode.setParent(null);
	deleteNode.setLeftChild(null);

	leftChild.setRank(deleteNode.getRank());
	canvas.addToRankList(leftChild);

	Function moverOne = new MoveSubtree(new Point(deleteNode.origin().x,
	  canvas.size().height + canvas.getNodeSize()), deleteNode);
	Function moverTwo = new MoveSubtree(new Point(deleteNode.origin().x,
	  deleteNode.origin().y), leftChild);
	moverOne.addObserver(this);
	moverTwo.addObserver(this);

	canvas.addFunction(moverOne);
	canvas.addFunction(moverTwo);

	blocked = true;
      }
      else {

	caseTest = CASE_THREE;

	canvas.setDeleteNode(deleteNode);
	canvas.removeNode(deleteNode);

	TreeNode successor = rightChild.getLeastChild();
	if (successor != rightChild)
	  successor.getParent().setLeftChild(null);

	TreeNode succChild = successor.getRightChild();
	
	if (succChild != null) {
	  succChild.setParent(successor.getParent());
	  successor.getParent().setLeftChild(succChild);

	  Function moverOne = new MoveSubtree(new Point(successor.origin().x,
	    successor.origin().y), succChild);
	  moverOne.addObserver(this);

	  canvas.addFunction(moverOne);
	}

	////////////////////////////////////////
	// Detach parent from node and attach to
	// node's right child.

	if (parent != null) {
	  if (deleteNode == parent.getLeftChild()) 
	    parent.setLeftChild(successor);
	  else
	    parent.setRightChild(successor);

	  successor.setParent(parent);
	}
	else
	  successor.setParent(null);

	leftChild.setParent(successor);
	successor.setLeftChild(leftChild);

	if (rightChild != successor) {
	  rightChild.setParent(successor);
	  successor.setRightChild(rightChild);
	}

	deleteNode.setParent(null);
	deleteNode.setLeftChild(null);
	deleteNode.setRightChild(null);

	successor.setRank(deleteNode.getRank());
	canvas.addToRankList(successor);

	Function moverTwo   = new MoveSubtree(new Point(deleteNode.origin().x,
	  canvas.size().height + canvas.getNodeSize()), deleteNode);
	Function moverThree = new MoveNode(new Point(deleteNode.origin().x,
	  deleteNode.origin().y), successor);
	moverTwo.addObserver(this);
	moverThree.addObserver(this);

	canvas.addFunction(moverTwo);
	canvas.addFunction(moverThree);

	blocked = true;
      }
    }
  }
}
