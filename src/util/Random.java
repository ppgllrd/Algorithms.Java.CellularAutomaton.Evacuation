package util;

/**
 * Class providing a global random generator by using Singleton Design Pattern.
 *
 * @author Pepe Gallardo.
 */
public class Random extends java.util.Random {
  private static Random instance;

  private Random() { }

  public static Random getInstance() {
    if (instance == null) {
      instance = new Random();
    }
    return instance;
  }
}
