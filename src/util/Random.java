package util;

/**
 * Class providing a global random generator by using Singleton Design Pattern.
 *
 * @author Pepe Gallardo.
 */
public class Random extends java.util.Random {
  private static Random instance;

  private Random() {
  }

  public static Random getInstance() {
    if (instance == null) {
      instance = new Random();
    }
    return instance;
  }

  public boolean bernoulli(double p) {
    if (p < 0.0 || p > 1.0) {
      throw new IllegalArgumentException("bernoulli(): probability must be between 0.0 and 1.0: " + p);
    }
    return nextDouble() < p;
  }
}
