package kloss.structures.functions;

import java.awt.Point;

import java.util.Observer;
import java.util.Observable;

import kloss.graphics.functions.Function;

import kloss.structures.BinaryCanvas;
import kloss.structures.nodes.AVLNode;

public class AVLLeftRotation extends Function implements Observer {

    final static int LL = 1;
    final static int LR = 2;

    protected int rotation;

    protected BinaryCanvas canvas;

    protected AVLNode rotateNode;
    protected AVLNode child;

    protected boolean blocked           = false;
    protected boolean rotationDone      = false;
    protected boolean rightRotationDone = false;

  public AVLLeftRotation(AVLNode node, BinaryCanvas canvas) {
    this.canvas     = canvas;
    this.rotateNode = node;

    this.child = (AVLNode) rotateNode.getLeftChild();

    if (child.getBalanceFactor() == AVLNode.UNBALANCED_LEFT)
	rotation = LL;
    else
	rotation = LR;
  }


  public void update(Observable observed, Object arg) {
      if (observed instanceof RightRotation) {
	  rotationDone      = true;
	  blocked           = false;
      } else if (observed instanceof LeftRotation) {
	  rightRotationDone = true;
	  blocked           = false;
      }
  }

  public void performFunction() {
      if (blocked == false) {
	  if (rotationDone)
	      finished = true;
	  else if (rightRotationDone == true || rotation == LL) {

	      System.out.println("RightRotation");

	      Function rotator = new RightRotation(rotateNode, canvas);
	      rotator.addObserver(this);

	      canvas.addFunction(rotator);

	      blocked = true;

	  } else {

	      System.out.println("LeftRotation");

	      Function rotator = new LeftRotation(child, canvas);
	      rotator.addObserver(this);

	      canvas.addFunction(rotator);

	      blocked = true;
	  }
      }
  }
}
