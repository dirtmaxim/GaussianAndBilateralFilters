package filter;

public class GaussianOriginalFunction implements GaussianFunction {
    @Override
    public float evaluateFunction(int x, int y, float sigma) {

        // Evaluate simple two dimensional Gaussian function.
        return (float) (1 / (2 * Math.PI * Math.pow(sigma, 2)) * Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(sigma, 2))));
    }
}