package kloss.graphics.images;

import java.applet.Applet;

import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;

import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;

import java.util.Hashtable;

//////////////////////////////////////////////////
// Import AlphaFilter inorder to make image back-
// grounds transparent (for on-line image crea-
// tion).

import kloss.graphics.imageFilters.AlphaFilter;

/**
 * A framework for storing images in an Applet.
 *
 * @version 1.0, April 5, 1997
 * @author  John Kloss
 */
public class ImageFrame extends Applet {

  //////////////////////////////////////////////////
  // Hashtable used for storing images.


  private static Hashtable imageCache = new Hashtable ();

  /**
   * A default color which can be used with the default alphaFilter
   * to make certain sections of an image transparent (the background
   * for instance). The default color is a rarely used color, cyan
   * (who uses cyan?).
   */
  protected final static Color TRANSPARENT_COLOR = Color.cyan;

  /**
   * A default alphaFilter. The default filter will turn any section
   * of an image colored the TRANSPARENT_COLOR transparent. If a 
   * different color needs to be made transparent then alphaFilter can
   * be overriden with a new instance of AlphaFilter which uses the new
   * color (of course, this removes the default filter).
   */
  protected ImageFilter alphaFilter = new AlphaFilter(TRANSPARENT_COLOR);


  /**
   * Returns a copy of an image stored internally within the ImageFrame.
   * Images are not clonable so this method provides a mechanism for such
   * actions.
   * <BLOCKQUOTE>
   * <STRONG>NOTE:</STRONG> The returned image is a new Image instance.
   * No actions taken upon the returned image will have any effect upon
   * the original. The returned image is a complete copy.
   * </BLOCKQUOTE>
   *
   * @param imageName            The name associated with the requested 
   *                             image.
   *
   * @returns                    A copy of the requested image.
   */
  protected Image copyImage(String imageName) {

      ////////////////////////////////////////
      // Get stored image (if it exists).

      Image storedImage = (Image) imageCache.get(imageName);

      ////////////////////////////////////////
      // Image exists in imageCache so copy it
      
      if (storedImage != null) {
	  int width  = storedImage.getWidth(this);
	  int height = storedImage.getHeight(this);

	  ////////////////////////////////////////
	  // Create new image and get its graphics
	  // context

	  Image newImage = createImage(width, height);
	  Graphics graphics = newImage.getGraphics();

	  ////////////////////////////////////////
	  // Set image background color to the 
	  // color to be made transparent by the
	  // AlphaFilter
	  
	  graphics.setColor(TRANSPARENT_COLOR);
	  graphics.fillRect(0, 0, width, height);

	  ////////////////////////////////////////
	  // Copy image and dispose of graphics
	  // context (frees memory quickly)

	  graphics.drawImage(storedImage, 0, 0, this);
	  graphics.dispose();

	  return newImage;
      }
      
      ////////////////////////////////////////
      // No such image in cache so return null

      return null;
  }

  /**
   * Returns an image filtered by AlphaFilter. Those sections of the
   * image which have been set to TRANSPARENT_COLOR will appear transparent
   * in the returned image.
   * <BLOCKQUOTE>
   * <STRONG> NOTE:</STRONG> requesting the graphics context from an image
   * which has been passed through an ImageFilter results in a 
   * ClassCastException.
   * </BLOCKQUOTE>
   *
   * @param image                The image to be filtered.
   *
   * @returns                    A filtered image.
   */
  protected Image filterImage(Image image) {
      ImageProducer producer = new FilteredImageSource
	                             (image.getSource(), alphaFilter);
      return createImage(producer);
  }
      
  /**
   * Return an image stored internally within the ImageFrame. For 
   * use in animation (or other actions) by ImageObjects.
   * 
   * @param imageName            The name associated with an image 
   *                             stored within the ImageFrame.
   *
   * @returns                    The associated image or null if no
   *                             such image exists.
   */
  protected Image getImage(String imageName) {
    return (Image) imageCache.get(imageName);
  }

  /**
   * Store an image in the ImageFrame.
   *
   * @param imageName            The name to be associated with the 
   *                             stored image.
   * @param                      The image to be stored.
   */
  protected void putImage(String imageName, Image image) {
    imageCache.put(imageName, image);
  }
}
