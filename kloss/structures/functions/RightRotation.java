package kloss.structures.functions;

import java.awt.Point;

import java.util.Observer;
import java.util.Observable;

import kloss.graphics.functions.Function;

import kloss.structures.BinaryCanvas;
import kloss.structures.nodes.AVLNode;

public class RightRotation extends Function implements Observer {

  protected BinaryCanvas canvas;

  protected AVLNode rotateNode;

  protected boolean blocked      = false;
  protected boolean rotationDone = false;

  protected int level;
  protected int startLevel;

  public RightRotation(AVLNode node, BinaryCanvas canvas) {
    this.canvas     = canvas;
    this.rotateNode = node;

    startLevel = canvas.getLevel();

    level = (int) Math.floor((double) Math.log((double) node.getRank()) /
			     (double) Math.log((double) 2));
  }


  public void update(Observable observed, Object arg) {
    if (observed instanceof MoveSubtree) {
      rotationDone = true;
      blocked      = false;
    }
  }

  public void performFunction() {
    if (blocked == false) {
      
      if (rotationDone) {

	setChanged();
	notifyObservers();

	if (startLevel > canvas.getLevel())
	  canvas.addFunction(new ContractTree(canvas));
	else
	  canvas.addFunction(new AdjustTree(canvas));

	finished = true;
      }
      else {

	int[] upperLevelValues = canvas.getLevelValues(level);
	int[] lowerLevelValues = canvas.getLevelValues(level + 1);

	int xPosition;
	int yPosition;

	canvas.removeFromRankList(rotateNode);

	AVLNode rotateParent = rotateNode;

	AVLNode rotateParentLC = (AVLNode) rotateParent.getLeftChild();

	AVLNode rotateChildRC = (AVLNode) rotateParentLC.getRightChild();
	AVLNode rotateChildLC = (AVLNode) rotateParentLC.getLeftChild();


	rotateParent.setLeftChild(rotateChildRC);
	if (rotateChildRC != null) {
	    rotateChildRC.setParent(rotateParent);
	    
	    xPosition = rotateParentLC.origin().x;
	    yPosition = rotateParentLC.origin().y;

	    Function mover = new MoveSubtree(new Point(xPosition, yPosition),
					     rotateChildRC);
	    mover.addObserver(this);
	    
	    canvas.addFunction(mover);
	}

	AVLNode parent = (AVLNode) rotateParent.getParent();
	
	rotateParentLC.setParent(parent);
	if (parent != null) {
	    if (rotateParent == (AVLNode) parent.getLeftChild())
		parent.setLeftChild(rotateParentLC);
	    else
		parent.setRightChild(rotateParentLC);
	}

	rotateParentLC.setRightChild(rotateParent);
	rotateParent.setParent(rotateParentLC);

	rotateParentLC.setRank(rotateParent.getRank());
	canvas.addToRankList(rotateParentLC);

	xPosition = rotateParent.origin().x + upperLevelValues[0];
	yPosition = rotateParent.origin().y + upperLevelValues[1];

	Function moverOne = new MoveNode(new Point(rotateParent.origin().x,
				  rotateParent.origin().y), rotateParentLC);
	Function moverTwo = new MoveSubtree(new Point(xPosition, yPosition),
					    rotateParent);

	if (rotateChildLC != null) {
	  Function mover = new MoveSubtree(new Point(rotateParentLC.origin().x,
					     rotateParentLC.origin().y),
					   rotateChildLC);
	  mover.addObserver(this);
	  canvas.addFunction(mover);
	}

	moverOne.addObserver(this);
	moverTwo.addObserver(this);

	canvas.addFunction(moverOne);
	canvas.addFunction(moverTwo);
	
	blocked = true;
      }
    }
  }
}
