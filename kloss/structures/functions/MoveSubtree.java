package kloss.structures.functions;

import kloss.graphics.functions.Function;

import java.awt.Point;

import java.util.Observer;
import java.util.Observable;

import kloss.graphics.images.ImageFrame;
import kloss.graphics.images.ImageObject;

import kloss.structures.nodes.Node;
import kloss.structures.nodes.TreeNode;
import kloss.structures.StructureCanvas;
import kloss.structures.BinaryCanvas;

public class MoveSubtree extends Function {

  boolean leftAndAbove  = false;
  boolean rightAndAbove = false;
  boolean leftAndBelow  = false;
  boolean rightAndBelow = false;

  TreeNode moveNode = null;

  int xLimit;
  int yLimit;

  int xMove;
  int yMove;

  int xTotalMove = 0;
  int yTotalMove = 0;

  public MoveSubtree(Point point, TreeNode node) {
    moveNode = node;

    Point testPoint = node.origin();

    int xCurr = testPoint.x;
    int yCurr = testPoint.y;

    xLimit = (int) Math.abs((double) (point.x - xCurr));
    yLimit = (int) Math.abs((double) (point.y - yCurr));

    xMove = (int) Math.ceil((double) (xLimit / 10));
    yMove = (int) Math.ceil((double) (yLimit / 10));

    if ( (xCurr > point.x) && (yCurr > point.y))
      rightAndBelow = true;
    else if (xCurr > point.x)
      rightAndAbove = true;
    else if ( (xCurr < point.x) && (yCurr > point.y))
      leftAndBelow  = true;
    else
      leftAndAbove  = true;
  }

  public void performFunction() {
    if ( (xTotalMove == xLimit) && (yTotalMove == yLimit)) {

      ////////////////////////////////////////
      // Notify any observers that the func-
      // tion is finished.

      setChanged();
      notifyObservers();

      finished = true;

    }

    else {

      if (rightAndBelow)
	moveNode.translate(-xMove, -yMove);
      else if (rightAndAbove)
	moveNode.translate(-xMove, yMove);
      else if (leftAndBelow)
	moveNode.translate(xMove, -yMove);
      else
	moveNode.translate(xMove, yMove);

      xTotalMove += xMove;
      yTotalMove += yMove;

      xMove = ( (xLimit - xTotalMove) < xMove) ? xLimit - xTotalMove: xMove;
      yMove = ( (yLimit - yTotalMove) < yMove) ? yLimit - yTotalMove: yMove;
    }
  }
}
