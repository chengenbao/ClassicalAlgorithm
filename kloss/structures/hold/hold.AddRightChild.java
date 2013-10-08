package kloss.structures.functions;

import kloss.graphics.functions.Function;

import kloss.structures.nodes.TreeNode;
import kloss.structures.StructureCanvas;

public class AddRightChild extends Function {

  private TreeNode newNode;
  private StructureCanvas canvas;

  private int limit;
  private int move = 0;

  public AddRightChild(TreeNode node, StructureCanvas canvas) {
    this.newNode = node;
    this.canvas  = canvas;

    limit = (int) (1.5 * canvas.getNodeSize());
  }

  public void performFunction() {
    if (move >= limit) {

      TreeNode checkNode = 
	newNode.crossesCenterRight(newNode.origin().x);

      if (checkNode != null) {
	int amount = newNode.origin().x - checkNode.origin().x;
	canvas.addFunction(new SpreadTreeLeft(checkNode, amount, canvas));
      }

      finished = true;
    }
    else
      newNode.translate(3,3);

    move += 3;
  }
}
  
