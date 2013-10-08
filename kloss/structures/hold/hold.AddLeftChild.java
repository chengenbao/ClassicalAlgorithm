package kloss.structures.functions;

import kloss.graphics.functions.Function;

import kloss.structures.nodes.TreeNode;
import kloss.structures.StructureCanvas;

public class AddLeftChild extends Function {

  TreeNode newNode;
  BinaryCanvas canvas;

  int level;
  int limit;
  int move = 0;

  public AddLeftChild(TreeNode node, BinaryCanvas canvas, int level) {
    this.newNode = node;
    this.canvas  = canvas;
    this.level   = level;

    limit = (int) (1.5 * canvas.getNodeSize());
  }

  public void performFunction() {
    if (move >= limit) {

      TreeNode checkNode = 
	newNode.crossesCenterLeft(newNode.origin().x);

      if (checkNode != null) 
	canvas.addFunction(new SpreadTree(checkNode, canvas, level));

      finished = true;
    }
    else 
      newNode.translate(-3,3);

    move += 3;
  }
}
  
