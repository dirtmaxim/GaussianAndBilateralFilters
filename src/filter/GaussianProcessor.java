package filter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class GaussianProcessor {
    private float sigma;
    private BufferedImage bufferedImage;
    private int kernelSize;

    public GaussianProcessor(Image image, float sigma) {
        this.bufferedImage = SwingFXUtils.fromFXImage(image, null);
        this.sigma = sigma;

        // Using 3σ rule for filter size. Addition performing to kernelSize will be an odd number.
        this.kernelSize = (int) Math.floor(6 * sigma) + 1;
    }

    /**
     * Create Gaussian kernel using passed probability function.
     *
     * @param gaussianFunction Interface is used to call {@link GaussianFunction#evaluateFunction(float, float, float)}
     * method
     * @return Kernel of Gaussian convolution
     */
    private float[] createGaussianKernel(GaussianFunction gaussianFunction) {

        // We are using one dimension array because of java.awt.bufferedImage.Kernel takes Gaussian two dimension matrix as
        // one dimension array.
        float[] kernelMatrix = new float[kernelSize];
        float amount = 0;

        // To increase productivity, function fills kernel as one dimension matrix and
        // then apply it horizontally and vertically.
        for (int i = 0, j = 0; i < kernelSize; i++, j++) {
            kernelMatrix[i] = gaussianFunction.evaluateFunction(i, j, sigma);
            amount += kernelMatrix[i];
        }

        // Normalization.
        for (int i = 0; i < kernelSize; i++) {
            kernelMatrix[i] /= amount;
        }

        return kernelMatrix;
    }

    /**
     * Add border to original image as transparent pixels in order to decrease losses of image and allow
     * Gaussian filter to apply to edge pixels.
     *
     * @param bufferedImage Image needs to be prepared to convolution
     * @return Prepared image
     */
    private BufferedImage addBorder(BufferedImage bufferedImage) {
        BufferedImage borderedImage = new BufferedImage(bufferedImage.getWidth() + kernelSize - 1,
                bufferedImage.getHeight() + kernelSize - 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = borderedImage.createGraphics();
        graphics2D.drawImage(bufferedImage, (kernelSize - 1) / 2, (kernelSize - 1) / 2, null);
        graphics2D.dispose();
        return borderedImage;
    }

    /**
     * Truncate transparent areas of image.
     *
     * @param borderedImage Image needs to be truncated after convolution
     * @return Truncated image
     */
    private BufferedImage deleteBorder(BufferedImage borderedImage) {
        BufferedImage resultImage = new BufferedImage(bufferedImage.getWidth() - (kernelSize - 1) / 2,
                bufferedImage.getHeight() - (kernelSize - 1) / 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = resultImage.createGraphics();
        graphics2D.drawImage(borderedImage, -(kernelSize - 1) / 2, -(kernelSize - 1) / 2, null);
        graphics2D.dispose();
        return resultImage;
    }

    /**
     * Blur image using Gaussian kernel.
     *
     * @param gaussianFunction Interface is used to call {@link GaussianFunction#evaluateFunction(float, float, float)}
     * method
     * @return JavaFX Image
     */
    public Image blurImage(GaussianFunction gaussianFunction) {
        float[] kernelMatrix = createGaussianKernel(gaussianFunction);
        BufferedImageOp horizontalConvolve = new ConvolveOp(new Kernel(kernelSize, 1, kernelMatrix), ConvolveOp.EDGE_NO_OP, null);
        BufferedImageOp verticalConvolve = new ConvolveOp(new Kernel(1, kernelSize, kernelMatrix), ConvolveOp.EDGE_NO_OP, null);
        return SwingFXUtils.toFXImage(deleteBorder(verticalConvolve.filter(horizontalConvolve.filter(addBorder(bufferedImage), null), null)), null);
    }
}