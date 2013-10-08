package kloss.structures;

import java.awt.Font;
import java.awt.Label;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Panel;
import java.awt.Event;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.GridLayout;
import java.awt.FontMetrics;
import java.awt.BorderLayout;

import java.util.Vector;
import java.util.Observer;
import java.util.Observable;

//////////////////////////////////////////////////
// Classes needed to render objects upon the 
// screen

import kloss.graphics.images.ImageFrame;
import kloss.graphics.images.ImageObject;

import kloss.awt.frame.FramedPanel;

//////////////////////////////////////////////////
// Class needed to perform operations upon data
// structure nodes

import kloss.graphics.functions.Function;

//////////////////////////////////////////////////
// Functions used for Binary Tree operations

import kloss.structures.functions.Insert;
import kloss.structures.functions.Delete;
import kloss.structures.functions.Search;


/**
 * Basic form of an Animated Data Structure Applet.
 */
public class BinaryTree extends ImageFrame {

  final static String BASE_NODE = "Base Node";
  final static String HIGHLIGHT = "HighLight";

  final static int    NODE_SIZE = 25;

  int edgeSize = (int) Math.floor((double) NODE_SIZE / 10);

  ////////////////////////////////////////
  // Canvas to draw animations on

  protected BinaryCanvas animationCanvas;

  ////////////////////////////////////////
  // TextField to recieve user input

  protected TextField field = new TextField();

  Label label = new Label("", Label.CENTER);
  Vector labelVector = new Vector();

  public synchronized final void createBaseNode() {
    Image image = createImage(NODE_SIZE + edgeSize, NODE_SIZE + edgeSize);

    Graphics graphics = image.getGraphics();
    graphics.setColor(TRANSPARENT_COLOR);
    graphics.fillRect(0, 0, NODE_SIZE + edgeSize, NODE_SIZE + edgeSize);

    graphics.setColor(Color.darkGray);
    graphics.fillRect(edgeSize, edgeSize, NODE_SIZE, NODE_SIZE);

    int[] xs = new int[3];
    int[] ys = new int[3];

    xs[0] = 0;
    xs[1] = 0;
    xs[2] = NODE_SIZE;

    ys[0] = 0;
    ys[1] = NODE_SIZE;
    ys[2] = 0;

    graphics.setColor(Color.red);
    graphics.fillPolygon(xs, ys, 3);

    xs[0] = 0;
    xs[1] = NODE_SIZE;
    xs[2] = NODE_SIZE;

    ys[0] = NODE_SIZE;
    ys[1] = 0;
    ys[2] = NODE_SIZE;
    
    graphics.setColor(new Color(153, 0, 0));
    graphics.fillPolygon(xs, ys, 3);

    graphics.setColor(new Color(204, 0, 0));
    graphics.fillRect(edgeSize, edgeSize, NODE_SIZE - ((2 * edgeSize) + 1),
		      NODE_SIZE - ((2 * edgeSize) + 1));

    graphics.dispose();

    image = filterImage(image);
    putImage(BASE_NODE, image);
  }

  
  public synchronized final void createHighLight() {
    Image image = createImage((NODE_SIZE * 2) + 2 , (NODE_SIZE * 2) + 3);

    Graphics graphics = image.getGraphics();
    graphics.setColor(TRANSPARENT_COLOR);
    graphics.fillRect(0, 0, (NODE_SIZE * 2) + 2, (NODE_SIZE * 2) + 3);

    graphics.setColor(new Color(224, 224, 224));
    graphics.fillOval(1, 1, (NODE_SIZE * 2), (NODE_SIZE * 2));

    graphics.dispose();

    image = filterImage(image);
    putImage(HIGHLIGHT, image);
  }


  public synchronized final void createNewNode(String value) {
    Image image = copyImage(BASE_NODE);

    Graphics graphics = image.getGraphics();

    graphics.setFont(new Font("TimesRoman", Font.PLAIN, 14));
    FontMetrics fontMetrics = graphics.getFontMetrics();

    int xLocation = (int) Math.ceil((double) (NODE_SIZE -
      fontMetrics.stringWidth(value)) / 2);
    int yLocation = (int) Math.ceil((double) (NODE_SIZE -
      fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();

    graphics.setColor(Color.black);
    graphics.drawString(value, (xLocation + 1), (yLocation + 1));
    graphics.setColor(Color.white);
    graphics.drawString(value, xLocation, yLocation);

    graphics.dispose();
    fontMetrics = null;

    image = filterImage(image);
    putImage(value, image);
  }
    

  public void init() {

    setLayout(new BorderLayout());

    animationCanvas = new BinaryCanvas(this, (NODE_SIZE + edgeSize));

    Panel buttonPanel = new Panel();
    buttonPanel.setLayout(new GridLayout(0, 4));
    buttonPanel.add(new Button("Insert"));
    buttonPanel.add(new Button("Delete"));
    buttonPanel.add(new Button("Search"));
    buttonPanel.add(field);

    FramedPanel frameOne   = new FramedPanel(4, null);
    FramedPanel frameTwo   = new FramedPanel(4, null);
    FramedPanel frameThree = new FramedPanel(4, null);

    frameOne.setLayout(new GridLayout(1,0));
    frameOne.add(buttonPanel);

    frameTwo.setLayout(new GridLayout(1,0));
    frameTwo.add(label);

    frameThree.setLayout(new GridLayout(1,0));
    frameThree.add(animationCanvas);

    add("North", frameOne);
    add("South", frameTwo);
    add("Center", frameThree);

    ////////////////////////////////////////
    // Create base node image and the high-
    // light image.

    createBaseNode();
    createHighLight();

    validate();
  }

  public void updateLabel(String value) {
      label.setText(value);
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

	Function insert = new Insert(numericValue, animationCanvas);
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
	
