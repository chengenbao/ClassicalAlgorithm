package kloss.graphics.functions;

import java.util.Observable;
import java.util.Observer;

public abstract class Function extends Observable {

  protected boolean finished = false;

  public boolean functionDone() {
    return finished;
  }

  public abstract void performFunction();
}
