package kloss.structures.functions;

import kloss.graphics.functions.Function;

import kloss.structures.nodes.Node;
import kloss.structures.StructureCanvas;
import kloss.structures.BinaryCanvas;

public class NewRoot extends Function {

  /** Reference to the new node created (necessary to move the node
   *  across the screen).
   */
  Node newNode;

  /** The amount the new root node is shifted down each iteration
   *  of the function.
   */
  int move;

  /** Total amount the new root has shifted currently.
   */
  int totalMove = 0;

  /** The maximum amount that the new root is to shift down.
   */
  int limit;


  public NewRoot(Node newNode, StructureCanvas canvas) {

    ////////////////////////////////////////
    // Set the limit to be twice the size of
    // the node.

    limit = canvas.getNodeSize() * 2;

    ////////////////////////////////////////
    // Initially place the node just of the
    // edge of the screen and centered length
    // wise.

    int width  = canvas.size().width / 2;
    int height = canvas.getNodeSize() / 2;

    ////////////////////////////////////////
    // Set move value so that the entire 
    // graphics operation can be done in ten
    // steps (fast).

    move = (int) Math.ceil((double) (limit / 10));

    this.newNode = newNode;
    this.newNode.move(width, -height);
  }

  public void performFunction() {

    ////////////////////////////////////////
    // If we have moved our node to its final
    // spot (or past), then terminate.

    if (totalMove >= limit)
      finished = true;

    ////////////////////////////////////////
    // Else continue to move node.

    else {

      newNode.translate(0,move);
      totalMove += move;
    }
  }
}
