package kloss.structures;

import java.awt.Event;

//////////////////////////////////////////////////
// Class needed to perform operations upon data
// structure nodes

import kloss.graphics.functions.Function;

//////////////////////////////////////////////////
// Functions used for Binary Tree operations

import kloss.structures.functions.AVLInsert;
import kloss.structures.functions.Delete;
import kloss.structures.functions.Search;


/**
 * Basic form of an Animated Data Structure Applet.
 */
public class AVLTree extends BinaryTree {

  public void init() {
    super.init();
  }

  public boolean action(Event evt, Object arg) {

    ////////////////////////////////////////
    // Retrieve user input (if any) from
    // text field.

    String value = field.getText();
    field.selectAll();

    int numericValue = 0;

    if ("Insert".equals(arg)) {
      
      try {

	numericValue = Math.abs(Integer.parseInt(value) % 100);
	createNewNode("" + numericValue);

	Function insert = new AVLInsert(numericValue, animationCanvas);
	insert.addObserver(animationCanvas);

	animationCanvas.addFunction(insert);

      } catch (NumberFormatException e) {

	numericValue = 0;
	field.setText("");

      }

      return true;
    }
    if ("Search".equals(arg)) {

      try {

	numericValue = Math.abs(Integer.parseInt(value) % 100);
	
	Function search = new Search(numericValue, animationCanvas);
	search.addObserver(animationCanvas);

	animationCanvas.addFunction(search);

      } catch (NumberFormatException e) {

	numericValue = 0;
	field.setText("");

      }

      return true;
    }
    if ("Delete".equals(arg)) {

      try {

	numericValue = Math.abs(Integer.parseInt(value) % 100);

	Function delete = new Delete(numericValue, animationCanvas);
	delete.addObserver(animationCanvas);

	animationCanvas.addFunction(delete);

      } catch (NumberFormatException e) {

	numericValue = 0;
	field.setText("");

      }

      return true;
    }

    return false;
  }
}
	
