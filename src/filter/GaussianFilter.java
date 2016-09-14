package filter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class GaussianFilter {
    private float sigma;
    private BufferedImage bufferedImage;
    private float[] kernelMatrix;
    private int kernelSize;

    public GaussianFilter(Image image, float sigma) {
        this.bufferedImage = SwingFXUtils.fromFXImage(image, null);
        this.sigma = sigma;

        // Using 3σ rule for filter size. Addition performing to kernelSize will be an odd number.
        this.kernelSize = (int) Math.floor(6 * sigma) + 1;
    }

    /**
     * One dimensional Gaussian function used for separable kernel.
     *
     * @param x Current coordinate on kernel
     * @return Value that follows the law of normal distribution
     */
    private float gaussianFunction(int x) {
        return (float) (1 / (sigma * Math.sqrt(2 * Math.PI)) * Math.exp(-Math.pow(x, 2) / (2 * Math.pow(sigma, 2))));
    }

    /**
     * Create Gaussian kernel using probability density function.
     *
     * @return Separable kernel of Gaussian convolution
     */
    private float[] createGaussianKernel() {

        // We are using one dimension array because of java.awt.bufferedImage.Kernel takes Gaussian two dimension matrix as
        // one dimension array.
        float[] kernelMatrix = new float[kernelSize];
        int halfKernelSize = (int) Math.floor(kernelSize / 2);
        float amount = 0;

        // To increase productivity, function fills kernel as one dimension matrix and
        // then apply it horizontally and vertically.
        for (int i = -halfKernelSize; i < halfKernelSize + 1; i++) {
            kernelMatrix[i + halfKernelSize] = gaussianFunction(i);
            amount += kernelMatrix[i + halfKernelSize];
        }

        // Normalization.
        for (int i = 0; i < kernelSize; i++) {
            kernelMatrix[i] /= amount;
        }

        return kernelMatrix;
    }

    /**
     * Blur image using Gaussian kernel.
     *
     * method
     * @return JavaFX Image
     */
    public Image filterImage() {
        kernelMatrix = createGaussianKernel();
        BufferedImageOp horizontalConvolve = new ConvolveOp(new Kernel(kernelSize, 1, kernelMatrix), ConvolveOp.EDGE_NO_OP, null);
        BufferedImageOp verticalConvolve = new ConvolveOp(new Kernel(1, kernelSize, kernelMatrix), ConvolveOp.EDGE_NO_OP, null);

        // Pipeline of transformation and convolution.
        return SwingFXUtils.toFXImage(ImageTool.deleteAlphaChannel(ImageTool.deleteBorder(verticalConvolve.filter(horizontalConvolve.
                filter(ImageTool.addBorder(bufferedImage, kernelSize), null), null), kernelSize)), null);
    }
}