package kloss.graphics.images;

import java.awt.Image;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;

//////////////////////////////////////////////////
// ImageObject extends GraphicsObject so load in
// parent.

import kloss.graphics.objects.GraphicsObject;

public class ImageObject extends GraphicsObject {

    /**
     * The image to be drawn.
     */
    protected Image image = null;

    /**
     * The size of the image.
     */
    protected Dimension imageSize = null;

    ////////////////////////////////////////
    // The offset from the origin which 
    // yields the upper left corner where 
    // the image is to be drawn. Note that
    // the variables default to error values.

    protected int widthOffSet  = -1;
    protected int heightOffSet = -1;


    ////////////////////////////////////////
    // Calculate the offsets from the origin

    private void calculateOffSets() {

        ////////////////////////////////////////
        // If image exists then calculate offsets

        if (imageSize != null) {

	    widthOffSet  = imageSize.width  / 2;
	    heightOffSet = imageSize.height / 2;

        ////////////////////////////////////////
        // Else indicate error for future draws

	} else {

	    widthOffSet  = -1;
	    heightOffSet = -1;

	}
    }


    /**
     * Constructor.
     */
    public ImageObject(Point origin, ImageFrame frame, String imageName) {
        super(origin);

	if ( (frame != null) && 
	     (this.image =  frame.getImage(imageName)) != null) {
	  
	    int width  = image.getWidth(frame);
	    int height = image.getHeight(frame);

	    imageSize = new Dimension(width, height);
	
	} else
	    imageSize = null;

	calculateOffSets();
    }

    public ImageObject(ImageObject object) {
        super(new Point(object.origin.x, object.origin.y));

	this.image     = object.image;
	this.imageSize = new Dimension(object.imageSize);

	this.widthOffSet  = object.widthOffSet;
	this.heightOffSet = object.heightOffSet;
    }

    /**
     * Change the image drawn by the ImageObject.
     */
    public synchronized void changeImage(ImageFrame frame, String imageName) {
        if ( (this.image = frame.getImage(imageName)) != null) {

	    int width  = image.getWidth(frame);
	    int height = image.getHeight(frame);

	    if ( (imageSize.width != width) ||
		 (imageSize.height != height)) {
	        
	        imageSize = new Dimension(width, height);
		calculateOffSets();
	    }
	}
    }

    /**
     * Check whether an event occurred within the bounds of the 
     * ImageObject.
     */
    public synchronized boolean inBounds(int x, int y) {
        if ( (widthOffSet == -1) && (heightOffSet == -1))
	  return false;
      
        if ( (x > (origin.x - widthOffSet)) &&
	     (x < (origin.x + widthOffSet)) &&
	     (y > (origin.y - heightOffSet)) &&
	     (y < (origin.y + heightOffSet)))
	  return true;

	return false;
    }

  
    public void move(int x, int y) {
        origin.move(x, y);
    }

    public void translate(int x, int y) {
        origin.translate(x, y);
    }

    public Dimension size() {
        return new Dimension(imageSize);
    }

    public void draw(Graphics g) {
        if (image != null) {
	    g.drawImage(image, origin.x - widthOffSet, origin.y - heightOffSet,
			null);
	}
	else
	  System.out.println("Image is null");
    }
}
