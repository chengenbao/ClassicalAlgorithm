package kloss.structures.functions;

import java.awt.Point;

import java.util.Observer;
import java.util.Observable;

import kloss.graphics.functions.Function;

import kloss.structures.BinaryCanvas;
import kloss.structures.nodes.AVLNode;

public class AVLRightRotation extends Function implements Observer {

    final static int RR = 1;
    final static int RL = 2;

    protected int rotation;

    protected BinaryCanvas canvas;

    protected AVLNode rotateNode;
    protected AVLNode child;

    protected boolean blocked           = false;
    protected boolean rotationDone      = false;
    protected boolean rightRotationDone = false;

  public AVLRightRotation(AVLNode node, BinaryCanvas canvas) {
    this.canvas     = canvas;
    this.rotateNode = node;

    this.child = (AVLNode) rotateNode.getRightChild();

    if (child.getBalanceFactor() == AVLNode.UNBALANCED_RIGHT)
	rotation = RR;
    else
	rotation = RL;
  }


  public void update(Observable observed, Object arg) {
      if (observed instanceof LeftRotation) {
	  rotationDone      = true;
	  blocked           = false;
      } else if (observed instanceof RightRotation) {
	  rightRotationDone = true;
	  blocked           = false;
      }
  }

  public void performFunction() {
      if (blocked == false) {
	  if (rotationDone)
	      finished = true;
	  else if (rightRotationDone == true || rotation == RR) {

	      System.out.println("LeftRotation");

	      Function rotator = new LeftRotation(rotateNode, canvas);
	      rotator.addObserver(this);

	      canvas.addFunction(rotator);

	      blocked = true;

	  } else {

	      System.out.println("RightRotation");

	      Function rotator = new RightRotation(child, canvas);
	      rotator.addObserver(this);

	      canvas.addFunction(rotator);

	      blocked = true;
	  }
      }
  }
}
