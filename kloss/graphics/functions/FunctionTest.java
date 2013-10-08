package demos.graphics.functions;

import java.awt.*;
import java.applet.*;
import java.util.Vector;

import kloss.graphics.functions.Function;
import kloss.graphics.objects.MoveableCircle;

class SideToSideFunction extends Function {

  final static int MAX_SHIFT = 40;

  int shiftValue = 1;
  int currShift  = 0;

  MoveableCircle circle;

  SideToSideFunction(MoveableCircle circle) {
    this.circle    = circle;
  }

  public void performFunction() {
    
    if (currShift < -MAX_SHIFT)
      shiftValue = 1;
    else if (currShift > MAX_SHIFT)
      shiftValue = -1;

    currShift += shiftValue;

    circle.translate(shiftValue,0);
  }
}
  
class UpAndDownFunction extends Function {

  final static int MAX_SHIFT = 40;

  int shiftValue = 1;
  int currShift  = 0;

  MoveableCircle circle;

  UpAndDownFunction(MoveableCircle circle) {
    this.circle = circle;
  }

  public void performFunction() {
    
    if (currShift < -MAX_SHIFT)
      shiftValue = 1;
    else if (currShift > MAX_SHIFT)
      shiftValue = -1;

    currShift += shiftValue;

    circle.translate(0, shiftValue);
  }
}    
  
class WiggleFunction extends Function {

  int wiggleValue = 3;

  MoveableCircle circle;

  WiggleFunction(MoveableCircle circle) {
    this.circle = circle;
  }

  public void performFunction() {

    wiggleValue *= -1;
    circle.translate(wiggleValue, wiggleValue);
  }
}    

public class FunctionTest extends Applet implements Runnable {

  Canvas canvas = new Canvas();

  Vector functionVector = new Vector();
  Thread functionThread;

  MoveableCircle circle;

  Dimension offDim;
  Graphics  offGrp;
  Image     offImg;

  int delay = 40;
  boolean frozen = false;

  public void init() {
    setLayout(new BorderLayout());

    Panel p = new Panel();
    p.setLayout(new GridLayout(4,0));
    p.add(new Button("Side to Side"));
    p.add(new Button("Up and Down"));
    p.add(new Button("Wiggle"));
    p.add(new Button("Reset"));

    add("East", p);
    add("Center", canvas);

    validate();

    Dimension d = canvas.size();
    circle = new MoveableCircle(new Point(d.width/2, d.height/2),
				Color.red, 20);
    
  }

  public void start() {
    if (!frozen) {
      if (functionThread == null)
	functionThread = new Thread(this);
      functionThread.start();
    } 
    else
      repaint();
  }

  public void stop() {
    functionThread = null;
    
    offGrp.dispose();
    offImg.flush();

    offGrp = null;
    offImg = null;
  }
    
  public boolean mouseDown(Event evt, int x, int y) {
    if (frozen) {
      frozen = false;
      start();
    }
    else {
      functionThread = null;
      frozen = true;
    }
    return true;
  }

  public boolean action(Event evt, Object arg) {
    if ("Side to Side".equals(arg)) {
      functionVector.addElement(new SideToSideFunction(circle));

      if ((functionThread != null) && (functionThread.isAlive()))
	functionThread.resume();
      else
	start();
    }
    else if ("Up and Down".equals(arg)) {
      functionVector.addElement(new UpAndDownFunction(circle));

      if ((functionThread != null) && (functionThread.isAlive()))
	functionThread.resume();
      else
	start();
    }
    else if ("Wiggle".equals(arg)) {
      functionVector.addElement(new WiggleFunction(circle));

      if ((functionThread != null) && (functionThread.isAlive()))
	functionThread.resume();
      else
	start();
    }
    else if ("Reset".equals(arg)) {
      if ((functionThread != null) && (functionThread.isAlive()))
	  functionThread.suspend();

      functionVector.removeAllElements();
      
      Dimension d = canvas.size();
      circle.move(d.width/2, d.height/2);
      repaint();
    }
    else
      return false;

    return true;
  }

  public void run() {
    while (Thread.currentThread() == functionThread) {
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

      long startTime = System.currentTimeMillis();

      doFunctions();

      try {
	startTime += delay;
	Thread.sleep(Math.max(0, startTime - System.currentTimeMillis()));
      } catch (InterruptedException e) {
      }
    }
  }

  private void doFunctions() {
    int i = 0;

    while (i < functionVector.size()) {
      if ( ( (Function)functionVector.elementAt(i)).functionDone())
	functionVector.removeElementAt(i);
      else {
	( (Function)functionVector.elementAt(i)).performFunction();
	i++;
      }
    }

    repaint();
  }
    
  public void paint(Graphics g) {
    update(g);
  }
 
  public void update(Graphics g) {
    Dimension d = canvas.size();

    if ((offGrp == null) ||
	(d.width != offDim.width) ||
	(d.height != offDim.height)) {

      offDim = d;
      offImg = createImage(d.width, d.height);
      offGrp = offImg.getGraphics();
    }

    offGrp.setColor(getBackground());
    offGrp.fillRect(0, 0, d.width, d.height);
    
    circle.draw(offGrp);

    Graphics canvasGraph = canvas.getGraphics();
    canvasGraph.drawImage(offImg, 0, 0, null);
    canvasGraph.dispose();
  }
}
