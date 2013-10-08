package kloss.structures.functions;

import kloss.graphics.functions.Function;

import kloss.graphics.images.ImageObject;
import kloss.structures.nodes.Node;

import kloss.structures.StructureCanvas;
import kloss.structures.BinaryCanvas;

public class HighLight extends Function {

  ///////////////////////////////////
  // Maximum time to highlight node.

  final static int MAXTIME = 20;

  ///////////////////////////////////
  // Timer used to tally total high-
  // light time.

  private int count = 0;

  ///////////////////////////////////
  // Reference to highlight node

  private ImageObject highlight;

  ///////////////////////////////////
  // Reference to StructureCanvas
  // so that highlight node can be
  // removed when function ends

  private BinaryCanvas canvas;


  public HighLight(Node node, BinaryCanvas canvas) {
    this.canvas   = canvas;

    ///////////////////////////////////
    // Create a new highlight over the
    // requested node and insert into
    // StructureCanvas to be drawn.

    highlight = new ImageObject(node.origin(), canvas.frame, 
				   "HighLight");
    canvas.addNode(highlight);
  }

  public void performFunction() {
    if (count == MAXTIME) {

      setChanged();
      notifyObservers();

      canvas.removeNode(highlight);
      finished = true;

    } else
      count++;
  }
}
