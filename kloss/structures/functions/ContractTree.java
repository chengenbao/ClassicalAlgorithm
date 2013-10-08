package kloss.structures.functions;

import kloss.graphics.functions.Function;

import java.awt.Point;

import java.util.Observer;
import java.util.Observable;

import kloss.structures.nodes.TreeNode;
import kloss.structures.StructureCanvas;
import kloss.structures.BinaryCanvas;

public class ContractTree extends Function implements Observer {

  boolean blocked = false;

  public ContractTree(BinaryCanvas canvas) {
    int level = canvas.getLevel();

    int[] values;

    for (int i = 0; i < level; i++) {
      values = canvas.getLevelValues(i);

      if (values[2] == BinaryCanvas.LEVEL_TWO) {
	values[0] = (int) (values[1] / 2);
	values[1] = values[1] * 2;
	values[2] = BinaryCanvas.LEVEL_ONE;
      }
      else {
	values[0] = (int) (values[0] / 2);

	if (values[0] == values[1])
	  values[2] = BinaryCanvas.LEVEL_TWO;
      }

      canvas.setLevelValues(i, values);
    }

    TreeNode parent;
    TreeNode leftChild;
    TreeNode rightChild;

    int startCheck;
    int endCheck;

    int xPosition;
    int yPosition;

    boolean functionsRun = false;

    for (int i = 0; i < level; i++) {

      startCheck = (int) Math.pow(2, i);
      endCheck   = (int) Math.pow(2, (i + 1));

      values = canvas.getLevelValues(i);

      for (int j = startCheck; j < endCheck; j++) {
	if ( (parent = canvas.getNodeAtRank(j)) != null) {

	  if ( (leftChild = parent.getLeftChild()) != null) {
	    xPosition = parent.origin().x - values[0];
	    yPosition = parent.origin().y + values[1];
	    
	    if ( (leftChild.origin().x != xPosition) ||
		 (leftChild.origin().y != yPosition)) {

	      Function mover = new MoveSubtree(new Point(xPosition, yPosition),
					       leftChild);
	      mover.addObserver(this);

	      canvas.addFunction(mover);
	      
	      functionsRun = true;
	    }
	  }

	  if ( (rightChild = parent.getRightChild()) != null) {
	    xPosition = parent.origin().x + values[0];
	    yPosition = parent.origin().y + values[1];

	    if ( (rightChild.origin().x != xPosition) ||
		 (rightChild.origin().y != yPosition)) {

	      Function mover = new MoveSubtree(new Point(xPosition, yPosition),
					       rightChild);
	      mover.addObserver(this);

	      canvas.addFunction(mover);

	      functionsRun = true;
	    }
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
