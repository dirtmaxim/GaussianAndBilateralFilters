package filter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * This filter uses Gaussian derivatives by x and y to perform edge detection. It allows to change sigma to compute
 * kernel, but if computing sigma is not necessary, it will be better to use Sobel operator as approximation of
 * Gaussian derivative.
 */
public class EdgeDetectionFilter {
    private float sigma;
    private BufferedImage bufferedImage;
    private float[] kernelMatrix;
    private int kernelSize;

    /**
     * You can set sigma, then you can see different edge detection result because of different size of kernel.
     *
     * @param image Image to filter
     * @param sigma Sigma parameter will be used in Gaussian derivative functions
     */
    public EdgeDetectionFilter(Image image, float sigma) {
        this.bufferedImage = SwingFXUtils.fromFXImage(image, null);
        this.sigma = sigma;

        // Filter has constant size because its features.
        this.kernelSize = (int) Math.floor(6 * sigma) + 1;
    }

    /**
     * It is used to compute Gaussian function derivative by X.
     *
     * @param x X parameter of Gaussian function derivative
     * @param y Y parameter of Gaussian function derivative
     * @param sigma Sigma parameter of Gaussian derivative function
     * @return Compute value by derivative function
     */
    private float gaussianDerivativeByXFunction(int x, int y, float sigma) {
        return (float) (-x / Math.pow(sigma, 2) * (1 / (2 * Math.PI * Math.pow(sigma, 2)) * Math.exp(-(Math.pow(x, 2) +
                Math.pow(y, 2)) / (2 * Math.pow(sigma, 2)))));
    }

    /**
     * It is used to compute Gaussian function derivative by Y.
     *
     * @param x X parameter of Gaussian function derivative
     * @param y Y parameter of Gaussian function derivative
     * @param sigma Sigma parameter of Gaussian derivative function
     * @return Compute value by derivative function
     */
    private float gaussianDerivativeByYFunction(int x, int y, float sigma) {
        return (float) (-y / Math.pow(sigma, 2) * (1 / (2 * Math.PI * Math.pow(sigma, 2)) * Math.exp(-(Math.pow(x, 2) +
                Math.pow(y, 2)) / (2 * Math.pow(sigma, 2)))));
    }

    /**
     * Creates kernel according to selected function.
     * @param derivativeType Type of function that will be used to compute kernel
     * @return Created kernel
     */
    private float[] createKernel(DerivativeType derivativeType) {

        // We are using one dimension array because of java.awt.bufferedImage.
        // Kernel takes Gaussian two dimension matrix as one dimension array.
        float[] kernelMatrix = new float[(int) Math.pow(kernelSize, 2)];
        int halfKernelSize = (int) Math.floor(kernelSize / 2);
        float amount = 0;

        for (int i = halfKernelSize; i > -halfKernelSize - 1; i--) {
            for (int j = halfKernelSize; j > -halfKernelSize - 1; j--) {
                int index = (halfKernelSize - i) * kernelSize + (halfKernelSize - j);
                if (derivativeType == DerivativeType.X) {
                    kernelMatrix[index] = gaussianDerivativeByXFunction(j, i, sigma);
                } else if (derivativeType == DerivativeType.Y) {
                    kernelMatrix[index] = gaussianDerivativeByYFunction(j, i, sigma);
                } else {

                    // If nothing was passed into function.
                    return null;
                }

                // Create normalization weight.
                amount += (kernelMatrix[index]) > 0 ? kernelMatrix[index] : 0;
            }
        }

        // Normalization.
        for (int i = 0; i < Math.pow(kernelSize, 2); i++) {
            kernelMatrix[i] /= amount;
        }

        return kernelMatrix;
    }

    /**
     * Detect edges on the image using Gaussian function derivative.
     *
     * @param derivativeType Type of function that will be used to compute kernel
     * @return Processed image
     */
    public Image filterImage(DerivativeType derivativeType) {
        kernelMatrix = createKernel(derivativeType);
        BufferedImageOp bufferedImageOp = new ConvolveOp(new Kernel(kernelSize, kernelSize, kernelMatrix), ConvolveOp.EDGE_ZERO_FILL, null);

        // Pipeline of transformation and convolution.
        return SwingFXUtils.toFXImage(bufferedImageOp.filter(ImageTool.toGrayscale(bufferedImage), null), null);
    }
}