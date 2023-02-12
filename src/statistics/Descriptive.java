package statistics;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Different descriptive statistics.
 *
 * @author Pepe Gallardo
 */
public class Descriptive {
  static double sum(double[] data) {
    double sum = 0.0;
    for (var element : data) {
      sum += element;
    }
    return sum;
  }

  static double maximum(double[] data) {
    double max = Double.MIN_VALUE;
    for (var element : data) {
      if (element > max) {
        max = element;
      }
    }
    return max;
  }

  static double minimum(double[] data) {
    double min = Double.MAX_VALUE;
    for (var element : data) {
      if (element < min) {
        min = element;
      }
    }
    return min;
  }

  static double mean(double[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("mean: data cannot be empty");
    }
    double sum = 0.0;
    int len = 0;
    for (var element : data) {
      sum += element;
      len += 1;
    }
    return sum / len;
  }

  static double variance(double[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("variance: data cannot be empty");
    }
    double sum = 0.0;
    double sumSqr = 0.0;
    int len = 0;
    for (var element : data) {
      sum += element;
      sumSqr += element * element;
      len += 1;
    }
    double mean = sum / len;
    return (sumSqr - len * mean * mean) / (len - 1);
  }

  static double standardDeviation(double[] data) {
    return Math.sqrt(variance(data));
  }

  static double variancePopulation(double[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("variancePopulation: data cannot be empty");
    }
    double sum = 0;
    double sumSqr = 0.0;
    int len = 0;
    for (var element : data) {
      sum += element;
      sumSqr += element * element;
      len += 1;
    }
    double mean = sum / len;
    return (sumSqr - len * mean * mean) / len;
  }

  static double standardDeviationPopulation(double[] data) {
    return Math.sqrt(variancePopulation(data));
  }

  static double midRange(double[] data) {
    return (maximum(data) + minimum(data)) / 2.0;
  }

  static <T> T mode(Iterable<T> data) {
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

  private static class Selection<T extends Comparable<? super T>> {
    T[] data;

    Selection(T[] data) {
      this.data = Arrays.copyOf(data, data.length);
    }

    void swap(int i, int j) {
      var temp = data[i];
      data[i] = data[j];
      data[j] = temp;
    }

    T select(int k) {
      var length = data.length;
      var left = 0;
      var right = length - 1;

      var found = false;
      while (!found) {
        var leftPlus1 = left + 1;

        if (right <= leftPlus1) {
          // 1 or 2 elements
          if (right == leftPlus1 && data[left].compareTo(data[right]) > 0) {
            // 2 elements
            swap(left, right);
          }
          found = true;
        } else {
          var mid = (left + right) / 2;

          // Set median of left, mid, and right elements as pivot.
          // Force data(left) ≤ data(leftPlus1) and data(right) ≥ data(leftPlus1)
          swap(mid, leftPlus1);
          if (data[left].compareTo(data[right]) > 0) {
            swap(left, right);
          }

          if (data[leftPlus1].compareTo(data[right]) > 0) {
            swap(leftPlus1, right);
          }

          if (data[left].compareTo(data[leftPlus1]) > 0) {
            swap(left, leftPlus1);
          }

          var i = leftPlus1;
          var j = right;
          var pivot = data[leftPlus1];

          var partitioned = false;
          do {
            do {
              i += 1;
            } while (pivot.compareTo(data[i]) > 0);
            do {
              j -= 1;
            } while (data[j].compareTo(pivot) > 0);
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

  private static double linearInterpolation(Double[] data, double percentRank) {
    var selection = new Selection<>(data);
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

  public static double percentile(Double[] data, double percentRank) {
    if (data.length == 0) {
      throw new IllegalArgumentException("percentile: data cannot be empty");
    }
    if (percentRank < 0.0 || percentRank > 100.0) {
      throw new IllegalArgumentException("percentile: percentRank must be in [0,100]");
    }
    return linearInterpolation(data, percentRank);
  }

  public static double median(Double[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("median: data cannot be empty");
    }
    return linearInterpolation(data, 50);
  }

  static int sum(int[] data) {
    int sum = 0;
    for (var element : data) {
      sum += element;
    }
    return sum;
  }

  static int maximum(int[] data) {
    int max = Integer.MIN_VALUE;
    for (var element : data) {
      if (element > max) {
        max = element;
      }
    }
    return max;
  }

  static int minimum(int[] data) {
    int min = Integer.MAX_VALUE;
    for (var element : data) {
      if (element < min) {
        min = element;
      }
    }
    return min;
  }

  static double mean(int[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("mean: data cannot be empty");
    }
    int sum = 0;
    int len = 0;
    for (var element : data) {
      sum += element;
      len += 1;
    }
    return (double) sum / len;
  }

  static double variance(int[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("variance: data cannot be empty");
    }
    int sum = 0;
    int sumSqr = 0;
    int len = 0;
    for (var element : data) {
      sum += element;
      sumSqr += element * element;
      len += 1;
    }
    double mean = (double) sum / len;
    return (sumSqr - len * mean * mean) / (len - 1);
  }

  static double standardDeviation(int[] data) {
    return Math.sqrt(variance(data));
  }

  static double variancePopulation(int[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("variancePopulation: data cannot be empty");
    }
    int sum = 0;
    int sumSqr = 0;
    int len = 0;
    for (var element : data) {
      sum += element;
      sumSqr += element * element;
      len += 1;
    }
    double mean = (double) sum / len;
    return (sumSqr - len * mean * mean) / len;
  }

  static double standardDeviationPopulation(int[] data) {
    return Math.sqrt(variancePopulation(data));
  }

  static double midRange(int[] data) {
    return (maximum(data) + minimum(data)) / 2.0;
  }

  private static double linearInterpolation(Integer[] data, double percentRank) {
    var selection = new Selection<>(data);
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

  public static double percentile(Integer[] data, double percentRank) {
    if (data.length == 0) {
      throw new IllegalArgumentException("percentile: data cannot be empty");
    }
    if (percentRank < 0.0 || percentRank > 100.0) {
      throw new IllegalArgumentException("percentile: percentRank must be in [0,100]");
    }
    return linearInterpolation(data, percentRank);
  }

  public static double median(Integer[] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("median: data cannot be empty");
    }
    return linearInterpolation(data, 50);
  }
}


