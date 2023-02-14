package statistics;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * Class providing a global random generator by using Singleton Design Pattern.
 *
 * @author Pepe Gallardo.
 */
public class Random implements RandomGenerator {
  /**
   * Any class can access the global random generator using this object.
   */
  public final static Random random = new Random();

  private final static RandomGeneratorFactory<RandomGenerator> factory =
      RandomGeneratorFactory.of("Xoshiro256PlusPlus");
  private static RandomGenerator generator = factory.create();

  /**
   * Any class can access the global random generator using this method.
   */
  public static Random getInstance() {
    return random;
  }

  @Override
  public long nextLong() {
    return generator.nextLong();
  }

  /**
   * Initializes the random generator with an arbitrary seed.
   */
  public void setSeed() {
    generator = factory.create();
  }

  /**
   * Initializes the random generator with provided seed.
   *
   * @param seed seed for initializing random generator.
   */
  public void setSeed(long seed) {
    generator = factory.create(seed);
  }

  /**
   * Returns {@code true} with probability {@code successProbability} or {@code false} with probability 1 - {@code
   * successProbability}.
   *
   * @param successProbability probability of choosing {@code true}.
   * @return {@code true} with probability {@code successProbability} or {@code false} with probability 1 - {@code
   * successProbability}.
   */
  public boolean bernoulli(double successProbability) {
    if (successProbability < 0.0 || successProbability > 1.0) {
      throw new IllegalArgumentException("bernoulli: probability " + successProbability + "must be in [0.0, 1.0]");
    }
    return nextDouble() < successProbability;
  }

  /**
   * Chooses randomly one element from collection with probability proportional to its desirability.
   *
   * @param collection   collection of elements to choose from.
   * @param desirability function returning desirability of an element.
   * @param <T>          type of elements
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

  private static <T> void swap(List<T> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
  }

  /**
   * Randomly shuffles the order of elements in a list.
   *
   * @param list list to shuffle.
   */
  public void shuffle(List<?> list) {
    int size = list.size();
    for (int i = size; i > 1; i--) {
      swap(list, i - 1, nextInt(i));
    }
  }
}
