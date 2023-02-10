package util;

import java.util.Collection;
import java.util.function.Function;

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

  /**
   * Returns {@code true} with probability {@code p} or {@code false} with probability 1 - {@code p}.
   *
   * @param p
   * @return
   */
  public boolean bernoulli(double p) {
    if (p < 0.0 || p > 1.0) {
      throw new IllegalArgumentException("bernoulli: probability " + p + "must be in [0.0, 1.0]");
    }
    return nextDouble() < p;
  }

  /**
   * Chooses one element from collection randomly with probability proportional to its desirability.
   *
   * @param collection   collection of elements to choose from.
   * @param desirability function returning desirability of an element.
   * @param <T>          typecof elements
   * @return one random element from collection chosen with probability proportional to its desirability.
   */
  public <T> T discrete(Collection<T> collection, Function<T, Double> desirability) {
    // choose one according to discrete distribution of desirabilities
    double sum = 0.0;
    for (var element : collection) {
      sum += desirability.apply(element);
    }

    if (sum <= 0) {
      throw new IllegalArgumentException("discrete: sum of desirabilities must be larger than 0");
    }

    var choose = nextDouble(sum);
    sum = 0.0;
    for (var element : collection) {
      sum += desirability.apply(element);
      if (sum > choose) {
        return element;
      }
    }
    // not reached
    assert false;
    return null;
  }
}
