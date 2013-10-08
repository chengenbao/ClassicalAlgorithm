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

public class Search extends Function implements Observer {

  ////////////////////////////////////////
  // Reference to BinaryCanvas so that 
  // Highlight function maybe added to list
  // of functions

  BinaryCanvas canvas;

  ////////////////////////////////////////
  // The node to be found.

  TreeNode searchNode;

  ////////////////////////////////////////
  // Reference used to trapse down the bi-
  // nary tree

  TreeNode trapseNode;

  ////////////////////////////////////////
  // Whether or not the current function 
  // is blocking (not executing) until 
  // another function completes its task.

  boolean blocked = false;

  String updateInfo = "";

  public Search(int value, BinaryCanvas canvas) {
    this.canvas = canvas;
    searchNode = new TreeNode(new Point(0, 0), canvas.frame, value);
  }


  public void update(Observable observed, Object arg) {
    if (observed instanceof HighLight) {
      blocked = false;
    }
  }

  public void performFunction() {

    if (blocked) {

      setChanged();
      notifyObservers(updateInfo);

    }
    else {

      ////////////////////////////////////////
      // The tree is empty or the requested 
      // node was not found so report that 
      // search failed.

      if (canvas.getRootNode() == null) {

	////////////////////////////////////////
	// Notify observers that search failed.

	setChanged();
	notifyObservers((Object) "Search Failed");

	setChanged();
	notifyObservers((Object) null);

	////////////////////////////////////////
	// Nothing else to do, so indicate done.

	finished = true;
      }

      else if (trapseNode == null) {

	trapseNode = canvas.getRootNode();

	updateInfo = "Beginning Search at Root Node : " + trapseNode.
	  getValue();
	
	setChanged();
	notifyObservers((Object) updateInfo);

	Function highLight = new HighLight(trapseNode, canvas);
	highLight.addObserver(this);

	canvas.addFunction(highLight);

	blocked = true;
      }

      ////////////////////////////////////////
      // Else, do comparison on trapse node
      // and either report success or continue
      // Search process on the next node (either
      // left or right child of current node).

      else {
	  
	if (trapseNode.lessThan(searchNode)) {
	  
	  trapseNode = trapseNode.getLeftChild();
	
	  if (trapseNode == null) {
	    
	    ////////////////////////////////////////
	    // Notify observers that search failed.

	    setChanged();
	    notifyObservers((Object) "Search Failed");

	    setChanged();
	    notifyObservers((Object) null);

	    ////////////////////////////////////////
	    // Nothing else to do, so indicate done.

	    finished = true;
	  }
	  else {

	    Function highLight = new HighLight(trapseNode, canvas);
	    highLight.addObserver(this);
	    
	    canvas.addFunction(highLight);

	    updateInfo = "Node : " + trapseNode.getValue() + " Compared with" +
	      " Value : " + searchNode.getValue();
	  
	    setChanged();
	    notifyObservers((Object) updateInfo);

	    ////////////////////////////////////////
	    // Wait until the HighLight function
	    // terminates (Insert will receive noti-
	    // fication of this via functionFinished)

	    blocked = true;
	  }
	}
	else if (trapseNode.greaterThan(searchNode)) {
	  
	  trapseNode = trapseNode.getRightChild();

	  if (trapseNode == null) {
	    
	    ////////////////////////////////////////
	    // Notify observers that search failed.

	    setChanged();
	    notifyObservers((Object) "Search Failed");

	    setChanged();
	    notifyObservers((Object) null);

	    ////////////////////////////////////////
	    // Nothing else to do, so indicate done.

	    finished = true;
	  }
	  else {

	    Function highLight = new HighLight(trapseNode, canvas);
	    highLight.addObserver(this);
	    
	    canvas.addFunction(highLight);

	    updateInfo = "Node : " + trapseNode.getValue() + " Compared with" +
	      " Value : " + searchNode.getValue();
	  
	    setChanged();
	    notifyObservers((Object) updateInfo);

	    ////////////////////////////////////////
	    // Wait until the HighLight function
	    // terminates (Insert will receive noti-
	    // fication of this via functionFinished)

	    blocked = true;
	  }
	}
	else {

	  updateInfo = "Node : " + trapseNode.getValue() + " Found";

	  setChanged();
	  notifyObservers((Object) updateInfo);

	  setChanged();
	  notifyObservers((Object) trapseNode);

	  ////////////////////////////////////////
	  // Nothing else to do, so indicate done.

	  finished = true;
	}
      }
    }
  }
}
