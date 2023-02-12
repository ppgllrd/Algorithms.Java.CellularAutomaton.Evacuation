package statistics;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Different descriptive statistics.
 *
 * @author Pepe Gallardo
 */
public class Descriptive {
  public static double sum(double[] data) {
    double sum = 0.0;
    for (var element : data) {
      sum += element;
    }
    return sum;
  }

  public static double maximum(double[] data) {
    double max = Double.MIN_VALUE;
    for (var element : data) {
      if (element > max) {
        max = element;
      }
    }
    return max;
  }

  public static double minimum(double[] data) {
    double min = Double.MAX_VALUE;
    for (var element : data) {
      if (element < min) {
        min = element;
      }
    }
    return min;
  }

  public static double mean(double[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("mean: data cannot be empty");
    }
    double sum = 0.0;
    for (var element : data) {
      sum += element;
    }
    return sum / data.length;
  }

  public static double variance(double[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("variance: data cannot be empty");
    }
    double sum = 0.0;
    double sumSqr = 0.0;
    for (var element : data) {
      sum += element;
      sumSqr += element * element;
    }
    var len = data.length;
    double mean = sum / len;
    return (sumSqr - len * mean * mean) / (len - 1);
  }

  public static double standardDeviation(double[] data) {
    return Math.sqrt(variance(data));
  }

  public static double variancePopulation(double[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("variancePopulation: data cannot be empty");
    }
    double sum = 0;
    double sumSqr = 0.0;
    for (var element : data) {
      sum += element;
      sumSqr += element * element;
    }
    var len = data.length;
    double mean = sum / len;
    return (sumSqr - len * mean * mean) / len;
  }

  public static double standardDeviationPopulation(double[] data) {
    return Math.sqrt(variancePopulation(data));
  }

  public static double midRange(double[] data) {
    return (maximum(data) + minimum(data)) / 2.0;
  }

  public static <T> T mode(Iterable<T> data) {
    if (!data.iterator().hasNext()) {
      throw new IllegalArgumentException("mode: data cannot be empty");
    }

    var counters = new HashMap<T, Integer>();
    for (var element : data) {
      Integer count = counters.get(element);
      counters.put(element, count == 0 ? 1 : count + 1);
    }

    var iterator = data.iterator();
    var maxCounter = -1;
    T mode = null;
    for (var entry : counters.entrySet()) {
      var count = entry.getValue();
      if (count > maxCounter) {
        mode = entry.getKey();
        maxCounter = count;
      }
    }
    return mode;
  }

  private static class Selection {
    double[] data;

    Selection(double[] data) {
      this.data = Arrays.copyOf(data, data.length);
    }

    Selection(int[] data) {
      this.data = new double[data.length];
      for (int i = 0; i < data.length; i++) {
        this.data[i] = data[i];
      }
    }

    void swap(int i, int j) {
      var temp = data[i];
      data[i] = data[j];
      data[j] = temp;
    }

    double select(int k) {
      var length = data.length;
      var left = 0;
      var right = length - 1;

      var found = false;
      while (!found) {
        var leftPlus1 = left + 1;

        if (right <= leftPlus1) {
          // 1 or 2 elements
          if (right == leftPlus1 && data[left] > data[right]) {
            // 2 elements
            swap(left, right);
          }
          found = true;
        } else {
          var mid = (left + right) / 2;

          // Set median of left, mid, and right elements as pivot.
          // Force data(left) ≤ data(leftPlus1) and data(right) ≥ data(leftPlus1)
          swap(mid, leftPlus1);
          if (data[left] > data[right]) {
            swap(left, right);
          }

          if (data[leftPlus1] > data[right]) {
            swap(leftPlus1, right);
          }

          if (data[left] > data[leftPlus1]) {
            swap(left, leftPlus1);
          }

          var i = leftPlus1;
          var j = right;
          var pivot = data[leftPlus1];

          var partitioned = false;
          do {
            do {
              i += 1;
            } while (pivot > data[i]);
            do {
              j -= 1;
            } while (data[j] > pivot);
            if (i <= j) {
              swap(i, j);
            } else {
              partitioned = true;
            }
          } while (!partitioned);

          data[leftPlus1] = data[j];
          data[j] = pivot;

          if (j >= k) {
            right = j - 1;
          }
          if (j <= k) {
            left = i;
          }
        }
      }
      return data[k];
    }
  }

  private static double linearInterpolation(double[] data, double percentRank) {
    var selection = new Selection(data);
    if (percentRank == 0) {
      return selection.select(0);
    } else if (percentRank == 100) {
      return selection.select(data.length - 1);
    } else {
      var rank = percentRank * (data.length - 1) / 100;
      var intPart = (int) rank;
      var fractPart = rank - intPart;

      var dataIntPart = selection.select(intPart);
      var dataIntPartNext = selection.select(intPart + 1);

      return dataIntPart + fractPart * (dataIntPartNext - dataIntPart);
    }
  }

  public static double percentile(double[] data, double percentRank) {
    if (data.length == 0) {
      throw new IllegalArgumentException("percentile: data cannot be empty");
    }
    if (percentRank < 0.0 || percentRank > 100.0) {
      throw new IllegalArgumentException("percentile: percentRank must be in [0,100]");
    }
    return linearInterpolation(data, percentRank);
  }

  public static double median(double[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("median: data cannot be empty");
    }
    return linearInterpolation(data, 50);
  }

  public static int sum(int[] data) {
    int sum = 0;
    for (var element : data) {
      sum += element;
    }
    return sum;
  }

  public static int maximum(int[] data) {
    int max = Integer.MIN_VALUE;
    for (var element : data) {
      if (element > max) {
        max = element;
      }
    }
    return max;
  }

  public static int minimum(int[] data) {
    int min = Integer.MAX_VALUE;
    for (var element : data) {
      if (element < min) {
        min = element;
      }
    }
    return min;
  }

  public static double mean(int[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("mean: data cannot be empty");
    }
    int sum = 0;
    for (var element : data) {
      sum += element;
    }
    return (double) sum / data.length;
  }

  public static double variance(int[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("variance: data cannot be empty");
    }
    int sum = 0;
    int sumSqr = 0;
    for (var element : data) {
      sum += element;
      sumSqr += element * element;
    }
    int len = data.length;
    double mean = (double) sum / len;
    return (sumSqr - len * mean * mean) / (len - 1);
  }

  public static double standardDeviation(int[] data) {
    return Math.sqrt(variance(data));
  }

  public static double variancePopulation(int[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("variancePopulation: data cannot be empty");
    }
    int sum = 0;
    int sumSqr = 0;
    for (var element : data) {
      sum += element;
      sumSqr += element * element;
    }
    var len = data.length;
    double mean = (double) sum / len;
    return (sumSqr - len * mean * mean) / len;
  }

  public static double standardDeviationPopulation(int[] data) {
    return Math.sqrt(variancePopulation(data));
  }

  public static double midRange(int[] data) {
    return (maximum(data) + minimum(data)) / 2.0;
  }

  private static double linearInterpolation(int[] data, double percentRank) {
    var selection = new Selection(data);
    if (percentRank == 0) {
      return selection.select(0);
    } else if (percentRank == 100) {
      return selection.select(data.length - 1);
    } else {
      var rank = percentRank * (data.length - 1) / 100;
      var intPart = (int) rank;
      var fractPart = rank - intPart;

      var dataIntPart = selection.select(intPart);
      var dataIntPartNext = selection.select(intPart + 1);

      return dataIntPart + fractPart * (dataIntPartNext - dataIntPart);
    }
  }

  public static double percentile(int[] data, double percentRank) {
    if (data.length == 0) {
      throw new IllegalArgumentException("percentile: data cannot be empty");
    }
    if (percentRank < 0.0 || percentRank > 100.0) {
      throw new IllegalArgumentException("percentile: percentRank must be in [0,100]");
    }
    return linearInterpolation(data, percentRank);
  }

  public static double median(int[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("median: data cannot be empty");
    }
    return linearInterpolation(data, 50);
  }
}


