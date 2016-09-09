package filter;

public class GaussianDerivativeByYFunction implements GaussianFunction {
    @Override
    public float evaluateFunction(int x, int y, float sigma) {
        return (float) (-y / Math.pow(sigma, 2)) * new GaussianOriginalFunction().evaluateFunction(x, y, sigma);
    }
}