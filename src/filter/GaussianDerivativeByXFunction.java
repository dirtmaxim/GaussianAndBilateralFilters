package filter;

public class GaussianDerivativeByXFunction implements GaussianFunction {
    @Override
    public float evaluateFunction(int x, int y, float sigma) {
        return (float) (-x / Math.pow(sigma, 2)) * new GaussianOriginalFunction().evaluateFunction(x, y, sigma);
    }
}
