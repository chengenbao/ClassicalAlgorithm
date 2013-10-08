package kloss.structures.functions;

import kloss.graphics.functions.Function;

import kloss.structures.nodes.TreeNode;
import kloss.structures.StructureCanvas;

public class SpreadTreeLeft extends Function {

  private StructureCanvas canvas;

  private TreeNode parent;
  private TreeNode leftChild;

  private int move  = 0;
  private int limit = 0;

  public SpreadTreeLeft(TreeNode node, int amount, StructureCanvas canvas) {
    this.canvas = canvas;
    this.parent = node;

    if ( (leftChild = parent.getLeftChild()) != null) 
      limit = (int) (((amount / canvas.getNodeSize()) + 1)
		     * canvas.getNodeSize()) + 
	(canvas.getNodeSize() / 2);
  }

  public void performFunction() {
    if (move >= limit) {

      TreeNode checkNodeOne;
      TreeNode checkNodeTwo;

      if ( ( (checkNodeOne = parent.getLeastChild()) != null) && 
	   ( (checkNodeTwo = checkNodeOne.
	      crossesCenterLeft(checkNodeOne.origin().x)) != null)) {

	int amount = checkNodeTwo.origin().x - checkNodeOne.origin().x;
	canvas.addFunction(new SpreadTreeRight(checkNodeTwo, amount, canvas));
      }

      finished = true;
    }
    else {

      leftChild.translate(-3,0);
      move += 3;

    }
  }
}
