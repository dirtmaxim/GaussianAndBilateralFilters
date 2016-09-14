package filter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageTool {
    /**
     * Add border to original image as transparent pixels in order to decrease
     * losses of convolution performing.
     *
     * @param bufferedImage Image needs to be prepared to convolution
     * @param kernelSize Border will be added according to size of a kernel
     * @return Prepared image
     */
    public static BufferedImage addBorder(BufferedImage bufferedImage, int kernelSize) {
        BufferedImage borderedImage = new BufferedImage(bufferedImage.getWidth() + kernelSize - 1,
                bufferedImage.getHeight() + kernelSize - 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = borderedImage.createGraphics();
        graphics2D.drawImage(bufferedImage, (kernelSize - 1) / 2, (kernelSize - 1) / 2, null);
        graphics2D.dispose();
        return borderedImage;
    }

    /**
     * Truncate transparent areas of image.
     *
     * @param borderedImage Image needs to be truncated after convolution
     * @param kernelSize Border will be truncated according to size of a kernel
     * @return Truncated image
     */
    public static BufferedImage deleteBorder(BufferedImage borderedImage, int kernelSize) {
        BufferedImage resultImage = new BufferedImage(borderedImage.getWidth() - (kernelSize - 1),
                borderedImage.getHeight() - (kernelSize - 1), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resultImage.createGraphics();
        graphics2D.drawImage(borderedImage, -(kernelSize - 1) / 2, -(kernelSize - 1) / 2, null);
        graphics2D.dispose();
        return resultImage;
    }

    /**
     * It translates RGBA image to grayscale image.
     *
     * @return Grayscaled image
     */
    public static BufferedImage toGrayscale(BufferedImage bufferedImage) {
        BufferedImage processedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int colorRGB = bufferedImage.getRGB(x, y);
                int colorR = (colorRGB >> 16) & 0xFF;
                int colorG = (colorRGB >> 8) & 0xFF;
                int colorB = colorRGB & 0xFF;

                // Formula to get intensity of color from RGB components.
                int intensity = (int) (0.3 * colorR + 0.59 * colorG + 0.11 * colorB);
                processedImage.setRGB(x, y, 0xFF000000 | ((intensity & 0xFF) << 16) |
                        ((intensity & 0xFF) << 8) |
                        (intensity & 0xFF));
            }
        }
        return processedImage;
    }

    /**
     * It deletes alpha channel from RGBA image.
     *
     * @param imageRGBA Image that contains alpha channel to be deleted
     * @return Image without alpha channel
     */
    public static BufferedImage deleteAlphaChannel(BufferedImage imageRGBA) {
        BufferedImage imageRGB = new BufferedImage(imageRGBA.getWidth(), imageRGBA.getHeight(), BufferedImage.OPAQUE);
        Graphics2D graphics = imageRGB.createGraphics();
        graphics.drawImage(imageRGBA, 0, 0, null);
        graphics.dispose();
        return imageRGB;
    }
}
