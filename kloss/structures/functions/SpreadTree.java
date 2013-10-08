package kloss.structures.functions;

import kloss.graphics.functions.Function;

import java.awt.Point;

import java.util.Observer;
import java.util.Observable;

import kloss.structures.nodes.TreeNode;
import kloss.structures.StructureCanvas;
import kloss.structures.BinaryCanvas;

public class SpreadTree extends Function implements Observer {

  boolean blocked = false;

  public SpreadTree(BinaryCanvas canvas) {
    int level = canvas.getLevel() - 1;

    int[] values;

    for (int i = 0; i < level; i++) {
      values = canvas.getLevelValues(i);

      if (values[2] == BinaryCanvas.LEVEL_ONE) {
	values[0] = 2 * values[0];
	values[1] = (int) (values[1] / 2);
	values[2] = BinaryCanvas.LEVEL_TWO;
      }
      else {
	values[0] = 2 * values[0];

	if (values[2] == BinaryCanvas.LEVEL_TWO)
	  values[2] = BinaryCanvas.LEVEL_THREE;
      }

      canvas.setLevelValues(i, values);
    }

    TreeNode parent;
    TreeNode leftChild;
    TreeNode rightChild;

    int startCheck;
    int endCheck;

    int xMove;
    int yMove;

    boolean functionsRun = false;

    for (int i = 0; i < level; i++) {

      startCheck = (int) Math.pow(2, i);
      endCheck   = (int) Math.pow(2, (i + 1));

      values = canvas.getLevelValues(i);

      for (int j = startCheck; j < endCheck; j++) {
	if ( (parent = canvas.getNodeAtRank(j)) != null) {

	  xMove = (int) (values[0] / 2);
	  yMove = (values[2] == BinaryCanvas.LEVEL_TWO) ? values[1] : 0;

	  if ( (leftChild = parent.getLeftChild()) != null) {
	    Function mover = new MoveSubtree(new Point(leftChild.origin().x -
	      xMove, leftChild.origin().y - yMove), leftChild);
	    mover.addObserver(this);

	    canvas.addFunction(mover);

	    functionsRun = true;
	  }

	  if ( (rightChild = parent.getRightChild()) != null) {
	    Function mover = new MoveSubtree(new Point(rightChild.origin().x +
	      xMove, rightChild.origin().y - yMove), rightChild);
	    mover.addObserver(this);

	    canvas.addFunction(mover);

	    functionsRun = true;
	  }
	}
      }
    }

    if (functionsRun)
      blocked = true;
  }

  public void update(Observable observed, Object arg) {
    if (observed instanceof MoveSubtree) {
      blocked = false;
    }
  }

  public void performFunction() {
    if (blocked == false) {
      finished = true;
    }
  }
}
