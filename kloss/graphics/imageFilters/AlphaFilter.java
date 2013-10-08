package kloss.graphics.imageFilters;

import java.awt.image.RGBImageFilter;
import java.awt.Color;

/**
 * AlphaFilter is a simple ImageFilter which makes one color of an image 
 * (determined at the filters construction) transparent. Its main use is 
 * for making transparent GIFs or images which have a single color 
 * background. This background is simply made transparent (alpha value 
 * equals zero) thus createing the desired effect.
 *
 * @version 1.0 March 26, 1997
 * @author  John Kloss
 */

public class AlphaFilter extends RGBImageFilter {

  ////////////////////////////////////////
  // The color to be made transparent by
  // the AlphaFilter.

  Color alphaColor;

  /**
   * Constructor. This method creates an instance of AlphaFilter. The
   * parameter alphaColor indicates the color to be made transparent by
   * the filter. The constructor sets the RGBImageFilter value, 
   * canFilterIndexColorModel to true so that just the color table for
   * the image is filtered, not each pixel.
   *
   * @param alphaColor      The color to be made transparent by the
   *                        AlphaFilter.
   */
  public AlphaFilter (Color alphaColor) {
    this.alphaColor = alphaColor;
    canFilterIndexColorModel = true;
  }

  /**
   * Filter the image color table. If the value rgb matches the alphaColor
   * set during construction, a transparent color value is returned (zero).
   * Otherwise, the original color value is returned unchanged.
   *
   * @param rgb             A color value in the image's color table.
   * @return                A transparent color value or the original value.
   */
  public int filterRGB (int x, int y, int rgb) {

    ////////////////////////////////////////
    // Parse out the rgb color values

    int red   = (rgb >> 16) & 0xff;
    int green = (rgb >>  8) & 0xff;
    int blue  = (rgb      ) & 0xff;

    ////////////////////////////////////////
    // Compare the color created by these
    // values with alphaColor.
    //
    // Note: The alpha value for rgb is
    //       ignored.

    if (alphaColor.equals( new Color(red, green, blue)))
      return 0;
    else
      return rgb;
  }
}
