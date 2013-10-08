package kloss.structures.functions;

import kloss.graphics.functions.Function;

import kloss.structures.nodes.TreeNode;
import kloss.structures.StructureCanvas;

public class SpreadTreeRight extends Function {

  private StructureCanvas canvas;

  private TreeNode parent;
  private TreeNode rightChild;

  private int move  = 0;
  private int limit = 0;

  public SpreadTreeRight(TreeNode node, int amount, StructureCanvas canvas) {
    this.canvas = canvas;
    this.parent = node;

    if ( (rightChild = parent.getRightChild()) != null) 
      limit = (int) (((amount / canvas.getNodeSize()) + 1) * 
		     canvas.getNodeSize()) +
	(canvas.getNodeSize() / 2);
  }

  public void performFunction() {
    if (move >= limit) {
      TreeNode checkNodeOne;
      TreeNode checkNodeTwo;
      
      if ( ( (checkNodeOne = parent.getGreatestChild())!= null) && 
	   ( (checkNodeTwo  = checkNodeOne.
	      crossesCenterRight(checkNodeOne.origin().x)) != null)) {
	
	int amount = checkNodeOne.origin().x - checkNodeTwo.origin().x;
	canvas.addFunction(new SpreadTreeLeft(checkNodeTwo, amount, canvas));
      }
      
      finished = true;
    }
    else {

      rightChild.translate(3,0);
      move += 3;

    }
  }
}
