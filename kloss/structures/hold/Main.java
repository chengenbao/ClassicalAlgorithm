import java.awt.*;

class Main extends Frame implements Runnable {
  Main() {
    super("getClipRect Example");
    resize(200,200);
    show();

    (new Thread(this)).start();
    (new Thread(this)).start();
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paint(Graphics g) {
    Insets insets = this.insets();
    int w = size().width-insets.left-insets.right;
    int h = size().height-insets.top-insets.bottom;

    g.setXORMode(Color.red);
    g.setColor(getBackground());
    g.fillOval(0,0,w,h);
  }

  public void run() {
    while (true) {
      try {
	Thread.sleep((int)(Math.random()*400));
	if (Math.random() > .5) {
	  repaint(0,0,40,40);
	} else {
	  repaint(80,80,40,40);
	}
      } catch (Exception e) {}
    }
  }

  public static void main(String[] args) {
    new Main();
  }
}
