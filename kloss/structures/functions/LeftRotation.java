package kloss.structures.functions;

import java.awt.Point;

import java.util.Observer;
import java.util.Observable;

import kloss.graphics.functions.Function;

import kloss.structures.BinaryCanvas;
import kloss.structures.nodes.AVLNode;

public class LeftRotation extends Function implements Observer {

  protected BinaryCanvas canvas;

  protected AVLNode rotateNode;

  protected boolean blocked      = false;
  protected boolean rotationDone = false;

  protected int level;
  protected int startLevel;

  public LeftRotation(AVLNode node, BinaryCanvas canvas) {
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

	AVLNode rotateParentRC = (AVLNode) rotateParent.getRightChild();

	AVLNode rotateChildRC = (AVLNode) rotateParentRC.getRightChild();
	AVLNode rotateChildLC = (AVLNode) rotateParentRC.getLeftChild();


	rotateParent.setRightChild(rotateChildLC);
	if (rotateChildLC != null) {
	    rotateChildLC.setParent(rotateParent);
	    
	    xPosition = rotateParentRC.origin().x;
	    yPosition = rotateParentRC.origin().y;

	    Function mover = new MoveSubtree(new Point(xPosition, yPosition),
					     rotateChildLC);
	    mover.addObserver(this);
	    
	    canvas.addFunction(mover);
	}

	AVLNode parent = (AVLNode) rotateParent.getParent();
	
	rotateParentRC.setParent(parent);
	if (parent != null) {
	    if (rotateParent == (AVLNode) parent.getLeftChild())
		parent.setLeftChild(rotateParentRC);
	    else
		parent.setRightChild(rotateParentRC);
	}

	rotateParentRC.setLeftChild(rotateParent);
	rotateParent.setParent(rotateParentRC);

	rotateParentRC.setRank(rotateParent.getRank());
	canvas.addToRankList(rotateParentRC);

	xPosition = rotateParent.origin().x - upperLevelValues[0];
	yPosition = rotateParent.origin().y + upperLevelValues[1];

	Function moverOne = new MoveNode(new Point(rotateNode.origin().x,
				  rotateNode.origin().y), rotateParentRC);
	Function moverTwo = new MoveSubtree(new Point(xPosition, yPosition),
					    rotateParent);

	if (rotateChildRC != null) {
	  Function mover = new MoveSubtree(new Point(rotateParentRC.origin().x,
					     rotateParentRC.origin().y),
					   rotateChildRC);
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
