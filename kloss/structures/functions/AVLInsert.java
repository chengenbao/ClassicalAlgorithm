package kloss.structures.functions;

import kloss.graphics.functions.Function;

import java.awt.Point;

import java.util.Observer;
import java.util.Observable;

import kloss.graphics.images.ImageFrame;
import kloss.graphics.images.ImageObject;

import kloss.structures.nodes.AVLNode;
import kloss.structures.StructureCanvas;
import kloss.structures.BinaryCanvas;

public class AVLInsert extends Function implements Observer {

  protected AVLNode rotateNode = null;
  protected AVLNode rootNode   = null;

  protected BinaryCanvas canvas;

  protected boolean blocked    = false;
  protected boolean insertDone = false;

  protected String updateString = "";

  public AVLInsert(int value, BinaryCanvas canvas) {
    this.canvas   = canvas;

    AVLNode newNode = new AVLNode(new Point((int) (canvas.size().width / 2),
					    (int) (canvas.getNodeSize() / 2)),
				  canvas.frame, value);

    Function insert = new Insert(newNode, canvas);
    insert.addObserver(this);

    canvas.addFunction(insert);

    blocked = true;
  }


  public void update(Observable observed, Object arg) {
    if (observed instanceof Insert) {
      if ( (arg == null) || (arg instanceof AVLNode)) {
	rotateNode = (AVLNode) arg;
	blocked = false;
      }
      else if (arg instanceof String) {
	if ( ("".equals(arg)) == false)
	  updateString = (String) arg;
      }
    }
  }

  
  public void performFunction() {
    if (blocked) {

      setChanged();
      notifyObservers(updateString);

    }
    else {

      AVLNode rootNode = (AVLNode) canvas.getRootNode();

      if (insertDone == false) {
	if ( (rotateNode == null) || (rootNode == null)) {
	  finished = true;
	  return;
	}
	else
	  insertDone = true;
      }

      rootNode.adjustBalanceFactor();
	  
      AVLNode trapse = (AVLNode) rotateNode.getParent();
      while ( (trapse != null) && 
	      ( (trapse.getBalanceFactor() !=  2) &&
		(trapse.getBalanceFactor() != -2))) 
	trapse = (AVLNode) trapse.getParent();

      
      if (trapse != null) {
	Function rotator;

	if (trapse.getBalanceFactor() == 2) 
	  rotator = new AVLLeftRotation(rotateNode = trapse, canvas);
	else
	  rotator = new AVLRightRotation(rotateNode = trapse, canvas);

	canvas.addFunction(rotator);
      }

      finished = true;
    }
  }
}
