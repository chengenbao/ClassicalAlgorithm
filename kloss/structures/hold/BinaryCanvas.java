package kloss.structures;

import java.awt.Point;
import java.awt.Image;
import java.awt.Event;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Dimension;

import java.util.Vector;

import kloss.graphics.images.ImageObject;
import kloss.graphics.images.ImageFrame;
import kloss.graphics.functions.Function;

import kloss.structures.nodes.Node;
import kloss.structures.nodes.TreeNode;


public class BinaryCanvas extends StructureCanvas {

  ////////////////////////////////////////////////////////////
  // For DEPTH_MULT and WIDTH_MULT the best results are
  // achieved when the width multiplier is one fourth the 
  // value of the depth.

  /** Multiplier for node size to determine how far down a new node
   *  drops froms its parent upon creation (graphics routine).
   */
  final static double DEPTH_MULT = 3.0;

  /** Multiplier for node size to determine how far left/right a new
   *  node spreads froms its parent upon creation (graphics routine).
   */
  final static double WIDTH_MULT = 0.75;

  /** Vector which holds 2 member int[] arrays. Each index in the 
   *  vector corresponds to a level within the binary tree (zero being
   *  the first level, one being the second, etc.). The int arrays 
   *  hold information on the amount a new node drops and spreads from
   *  its parent upon creation (graphics routine).
   */
  Vector levelInfo = new Vector();

  int levelDepth;
  int levelWidth;

  /**
   * The rank list holds references to all the nodes in the binary
   * tree sorted by rank. Rank is defined to be
   * <PRE>
   *   left child rank  = (2 * parent rank)
   *   right child rank = (2 * parent rank) + 1
   * </PRE>
   * where the root node has rank 1. This allows all nodes to be
   * ordered linearly and accessed via a rank index in constant time.
   */
  TreeNode[] rankList;

  public BinaryCanvas(ImageFrame frame, int nodeSize) {
    super(frame, nodeSize);

    levelDepth = (int) (DEPTH_MULT * (double) nodeSize);
    levelWidth = (int) (WIDTH_MULT * (double) nodeSize);

    ////////////////////////////////////////
    // Hack to insure that graphics rendering
    // is smooth and symmetrical.

    if ((int) (levelDepth / levelWidth) != 4)
      levelWidth = (int) (levelDepth / 4.0);
  }


  /** Add a new node to the rank list. 
   */
  public void addToRankList(TreeNode node) {
    int rank = node.getRank() - 1;

    ////////////////////////////////////////
    // If rankList not yet created then make
    // it and initialize index rank to ref-
    // erence node.

    if (rankList == null) {
      rankList = new TreeNode[rank + 1];
      rankList[rank] = node;
    }

    ////////////////////////////////////////
    // Else if rankList not large enough for
    // expanded tree, remake the list and add
    // new node reference to rank index.

    else if (rankList.length <= rank) {
      TreeNode[] tempList = new TreeNode[rank + 1];

      System.arraycopy(rankList, 0, tempList, 0, rankList.length);

      rankList = tempList;
      rankList[rank] = node;
    }

    ////////////////////////////////////////
    // Else just set rank index to reference
    // the node

    else
      rankList[rank] = node;
  }


  /** Retrieve the node with a particular rank. 
   *
   * @param  nodeRank   The rank of the requested node.
   * @return            The node with the requested rank or null if
   *                    no such node exists.
   */
  public TreeNode getNodeAtRank(int nodeRank) {
    int rank = nodeRank - 1;

    if (rank > rankList.length)
      return null;
    else
      return rankList[rank];
  }



  public void setLevelValues(int level, int[] values) {
    levelInfo.setElementAt(values, level);
  }

  public int[] getLevelValues(int level) {
    int size = levelInfo.size();

    if (size == level) {
      int[] values = new int[2];

      ////////////////////////////////////////
      // Assign the new level with default 
      // values.

      values[0] = levelWidth;
      values[1] = levelDepth;

      levelInfo.addElement(values);
    }
    
    if (size < level)
      return null;
    else
      return (int[]) levelInfo.elementAt(level);
  }
}
