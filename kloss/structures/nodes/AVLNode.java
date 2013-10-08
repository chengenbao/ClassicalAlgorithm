package kloss.structures.nodes;

import java.awt.Point;

import kloss.graphics.images.ImageFrame;

public class AVLNode extends TreeNode {

  public final static int UNBALANCED_LEFT  =  1;
  public final static int UNBALANCED_RIGHT = -1;
  public final static int BALANCED         =  0;

  protected int balanceFactor = 0;

  public void setBalanceFactor(int factor) {
    balanceFactor = factor;
  }

  public int getBalanceFactor() {
    return balanceFactor;
  }

  public int adjustBalanceFactor() {
    AVLNode leftChild  = (AVLNode) getLeftChild();
    AVLNode rightChild = (AVLNode) getRightChild();

    if ( (leftChild == null) && (rightChild == null))
      return (balanceFactor = 0);

    int leftChildBalance  = 0;
    int rightChildBalance = 0;

    if (leftChild != null)
      leftChildBalance  = leftChild.adjustBalanceFactor() + 1;
    
    if (rightChild != null)
      rightChildBalance = rightChild.adjustBalanceFactor() + 1;

    balanceFactor = (leftChildBalance - rightChildBalance);

    if (leftChildBalance > rightChildBalance)
      return leftChildBalance;
    else
      return rightChildBalance;
  }


  public AVLNode(Point origin, ImageFrame frame, int value) {
    super(origin, frame, value);
  }


  public AVLNode(AVLNode node) {
    super(node);

    this.balanceFactor = node.balanceFactor;
  }


  public AVLNode(Point origin) {
    super(origin);
  }

}
