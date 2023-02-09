public class Random {
  private final java.util.Random random;
  private static Random instance;

  private Random() {
    random = new java.util.Random();
  }

  public static Random getInstance() {
    if (instance == null) {
      instance = new Random();
    }
    return instance;
  }

  public void setSeed(long seed) {
    random.setSeed(seed);
  }

  public int nextInt() {
    return random.nextInt();
  }

  public int nextInt(int n) {
    return random.nextInt(n);
  }

  public double nextDouble() {
    return random.nextDouble();
  }

  public double nextDouble(double n) {
    return random.nextDouble(n);
  }
}
