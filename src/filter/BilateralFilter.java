package filter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;

/**
 * This class implements bilateral filter given by formula:
 * ∑ e^(-(distanceA^2 + distanceB^2) / 2 * σDistance^2) * e^(√((intensityCenter - intensityPosition)^2) / 2 * σIntensity^2) * intensityPosition
 * -----------------------------------------------------------------------------------------------------------------------------------------
 * ∑ e^(-(distanceA^2 + distanceB^2) / 2 * σDistance^2) * e^(√((intensityCenter - intensityPosition)^2) / 2 * σIntensity^2).
 */
public class BilateralFilter {
    private float distanceSigma;
    private float intensitySigma;
    private BufferedImage bufferedImage;
    private int kernelSize;

    // It is filled by computing Gaussian function depend on kernelSize.
    private float gaussianKernelMatrix[][];

    // It is filled by computing intensity part of bilateral function.
    private float intensityVector[];

    public BilateralFilter(Image image, float distanceSigma, float intensitySigma) {
        this.bufferedImage = SwingFXUtils.fromFXImage(image, null);
        this.distanceSigma = distanceSigma;
        this.intensitySigma = intensitySigma;

        // Using 3σ rule for filter size. Addition performing to kernelSize will be an odd number.
        this.kernelSize = (int) Math.floor(6 * distanceSigma) + 1;
        createGaussianKernel();
        createIntensityVector();
    }

    /**
     * Gaussian part of bilateral filter. It will be calculated in full formula.
     *
     * @param x Gaussian parameter X
     * @param y Gaussian parameter Y
     * @return Gaussian value
     */
    private float gaussianFunction(int x, int y) {
        return (float) Math.exp(-(x * x + y * y) / (2 * distanceSigma * distanceSigma));
    }

    /**
     * Calculate Gaussian kernel for kernelSize range.
     */
    private void createGaussianKernel() {
        gaussianKernelMatrix = new float[kernelSize][kernelSize];
        int halfKernelSize = (int) Math.floor(kernelSize / 2);

        for (int i = -halfKernelSize; i < halfKernelSize + 1; i++) {
            for (int j = -halfKernelSize; j < halfKernelSize + 1; j++) {
                gaussianKernelMatrix[i + halfKernelSize][j + halfKernelSize] = gaussianFunction(i, j);
            }
        }
    }


    /**
     * Calculate intensity vector for performance reason.
     * Test shows that this approach gives fourfold performance improvement.
     */
    private void createIntensityVector() {

        // It needs to increase performance. Compute intensity difference for each possible value.
        // There are 442 values since filter measure intensity difference as
        // √((R2 - R1)^2 + (G2 - G1)^2 + (B2 - B1)^2). So, maximal value is √(255^2 + 255^2 + 255^2). That is 442.
        intensityVector = new float[442];

        for (int i = 0; i < intensityVector.length; i++) {
            intensityVector[i] = (float) Math.exp(-((i) / (2 * intensitySigma * intensitySigma)));
        }
    }

    /**
     * Fast way to compute intensity difference. But it is not as precise as transfer to CIELAB color space.
     *
     * @param firstColor Subtrahend
     * @param secondColor Subtractor
     * @return Difference between intensity of colors
     */
    private int getIntensityDifference(int firstColor, int secondColor) {
        int rDifference = (secondColor >> 16 & 0xFF) - (firstColor >> 16) & 0xFF;
        int gDifference = (secondColor >> 8 & 0xFF) - (firstColor >> 8) & 0xFF;
        int bDifference = (secondColor & 0xFF) - (firstColor & 0xFF);

        // Calculate intensity difference. It is used in not Gaussian part of bilateral filter formula.
        return (int) (Math.sqrt(rDifference * rDifference + gDifference * gDifference +
                bDifference * bDifference));
    }

    /**
     * Filter image using bilateral function.
     *
     * @return Processed image
     */
    public Image filterImage() {
        bufferedImage = ImageTool.addBorder(bufferedImage, kernelSize);
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                float numeratorSumR = 0;
                float numeratorSumG = 0;
                float numeratorSumB = 0;
                float denominatorSum = 0;

                // It needs to calculate number of pixel that fits to kernel.
                int halfKernelSize = (int) Math.floor(kernelSize / 2);
                int kernelCenterIntensity = bufferedImage.getRGB(x, y);

                // Go around the kernel if it is allowed by border of image.
                for (int i = x - halfKernelSize; i < x + halfKernelSize; i++) {
                    for (int j = y - halfKernelSize; j < y + halfKernelSize; j++) {
                        if (i >= 0 && j >= 0 && i < bufferedImage.getWidth() && j < bufferedImage.getHeight()) {
                            float kernelPositionWeight;
                            int kernelPositionIntensity = bufferedImage.getRGB(i, j);

                            // Translate kernel image coordinates into local gaussianKernelMatrix coordinates and
                            // calculate weight of current kernel position.
                            kernelPositionWeight = gaussianKernelMatrix[x - i + halfKernelSize][y - j + halfKernelSize] *
                                    intensityVector[getIntensityDifference(kernelCenterIntensity, kernelPositionIntensity)];

                            // Process each color component separately.
                            // It is necessary to make filter work with color images.
                            numeratorSumR += kernelPositionWeight * ((kernelPositionIntensity >> 16) & 0xFF);
                            numeratorSumG += kernelPositionWeight * ((kernelPositionIntensity >> 8) & 0xFF);
                            numeratorSumB += kernelPositionWeight * (kernelPositionIntensity & 0xFF);
                            denominatorSum += kernelPositionWeight;
                        }
                    }
                }

                // Normalization by division and combination bit color value from separate components
                // into compound 32-bits value, delete an alpha channel. Then set new value.
                bufferedImage.setRGB(x, y, 0xFF000000 | (((int) (numeratorSumR / denominatorSum) & 0xFF) << 16) |
                        (((int) (numeratorSumG / denominatorSum) & 0xFF) << 8) |
                        ((int) (numeratorSumB / denominatorSum) & 0xFF));
            }
        }

        return SwingFXUtils.toFXImage(ImageTool.deleteBorder(bufferedImage, kernelSize), null);
    }
}