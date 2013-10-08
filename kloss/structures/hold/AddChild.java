package kloss.structures.functions;

import kloss.graphics.functions.Function;

import kloss.structures.nodes.TreeNode;
import kloss.structures.StructureCanvas;
import kloss.structures.BinaryCanvas;

public class AddChild extends Function {

  /** Constant value passed during AddChild construction indicating
   *  whether we are graphically creating a left child.
   */
  final static int LEFT  = 0;

  /** Constant value passed during AddChild construction indicating
   *  whether we are graphically creating a right child.
   */
  final static int RIGHT = 1;

  TreeNode newNode;

  BinaryCanvas canvas;

  int side;

  int xMove;
  int yMove;

  int xTotalMove = 0;
  int yTotalMove = 0;

  int xLimit;
  int yLimit;

  public AddChild(TreeNode node, BinaryCanvas canvas, int level, int side) {
    this.newNode = node;
    this.canvas  = canvas;
    this.side    = side;

    int[] values = canvas.getLevelValues(level);

    xLimit = values[0];
    yLimit = values[1];

    ////////////////////////////////////////////////////////////
    // Set move values such that the entire graphics operation 
    // can be completed within ten iterations (fast). This is a
    // rough approximation and will likely not produce the de-
    // sired results if the value for xLimit or yLimit is less
    // than ten.

    xMove = (int) Math.ceil((double) (xLimit / 10));
    yMove = (int) Math.ceil((double) (yLimit / 10));
  }

  public void performFunction() {
    if ( (xTotalMove >= xLimit) || (yTotalMove >= yLimit)) {

      TreeNode node;

      if (side == LEFT) 
	node = newNode.crossesCenterLeft(newNode.origin().x -
					 (int) (canvas.getNodeSize() / 2));
      else
	node = newNode.crossesCenterRight(newNode.origin().x +
					  (int) (canvas.getNodeSize() / 2));

      if (node != null) {
	int level = (int) Math.floor( Math.log(node.getRank()) / 
				      Math.log(2));

	canvas.addFunction(new SpreadTree(canvas, level));
      }
	
      finished = true;
    }
    else {

      xTotalMove += xMove;
      yTotalMove += yMove;

      if (side == LEFT)
	newNode.translate(-xMove, yMove);
      else
	newNode.translate(xMove, yMove);
    }
  }
}
  
