package statistics;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Different descriptive statistics.
 *
 * @author Pepe Gallardo
 */
public class Descriptive {
  static double sum(Iterable<Double> data) {
    double sum = 0.0;
    for (var element : data) {
      sum += element;
    }
    return sum;
  }

  static double maximum(Iterable<Double> data) {
    double max = Double.MIN_VALUE;
    for (var element : data) {
      if (element > max) {
        max = element;
      }
    }
    return max;
  }

  static double minimum(Iterable<Double> data) {
    double min = Double.MAX_VALUE;
    for (var element : data) {
      if (element < min) {
        min = element;
      }
    }
    return min;
  }

  static double mean(Iterable<Double> data) {
    if (!data.iterator().hasNext()) {
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

  static double variance(Iterable<Double> data) {
    if (!data.iterator().hasNext()) {
      throw new IllegalArgumentException("variance: data cannot be empty");
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
    return (sumSqr - len * mean * mean) / (len - 1);
  }

  static double standardDeviation(Iterable<Double> data) {
    return Math.sqrt(variance(data));
  }

  static double variancePopulation(Iterable<Double> data) {
    if (!data.iterator().hasNext()) {
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

  static double standardDeviationPopulation(Iterable<Double> data) {
    return Math.sqrt(variancePopulation(data));
  }

  static double midRange(Iterable<Double> data) {
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

  private static class Selection {
    ArrayList<Double> arrayList;
    int length, k;

    Selection(Iterable<Double> data, int k) {
      this.k = k;
      this.arrayList = new ArrayList<>();
      for (var element : data) {
        arrayList.add(element);
      }
      this.length = arrayList.size();
    }

    void swap(int i, int j) {
      var temp = arrayList.get(i);
      arrayList.set(i, arrayList.get(j));
      arrayList.set(j, temp);
    }

    double run() {
      var left = 0;
      var right = length - 1;

      var found = false;
      while (!found) {
        var leftPlus1 = left + 1;

        if (right <= leftPlus1) {
          // 1 or 2 elements
          if (right == leftPlus1 && arrayList.get(left) > arrayList.get(right)) {
            // 2 elements
            swap(left, right);
          }
          found = true;
        } else {
          var mid = (left + right) / 2;

          // Set median of left, mid, and right elements as pivot.
          // Force data(left) ≤ data(leftPlus1) and data(right) ≥ data(leftPlus1)
          swap(mid, leftPlus1);
          if (arrayList.get(left) > arrayList.get(right)) {
            swap(left, right);
          }

          if (arrayList.get(leftPlus1) > arrayList.get(right)) {
            swap(leftPlus1, right);
          }

          if (arrayList.get(left) > arrayList.get(leftPlus1)) {
            swap(left, leftPlus1);
          }

          var i = leftPlus1;
          var j = right;
          var pivot = arrayList.get(leftPlus1);

          var partitioned = false;
          do {
            do {
              i += 1;
            } while (pivot > arrayList.get(i));
            do {
              j -= 1;
            } while (arrayList.get(j) > pivot);
            if (i <= j) {
              swap(i, j);
            } else {
              partitioned = true;
            }
          } while (!partitioned);

          arrayList.set(leftPlus1, arrayList.get(j));
          arrayList.set(j, pivot);

          if (j >= k) {
            right = j - 1;
          }
          if (j <= k) {
            left = i;
          }
        }
      }
      return arrayList.get(k);
    }
  }

  private static double linearInterpolation(Iterable<Double> data, double percentRank) {
    if (percentRank == 0) {
      return new Selection(data, 0).run();
    } else {
      var length = 0;
      for (var element : data) {
        length += 1;
      }
      if (percentRank == 100) {
        return new Selection(data, length - 1).run();
      } else {
        var rank = percentRank * (length - 1) / 100;
        var intPart = (int) rank;
        var fractPart = rank - intPart;

        var dataIntPart = new Selection(data, intPart).run();
        var dataIntPartNext = new Selection(data, intPart + 1).run();

        return dataIntPart + fractPart * (dataIntPartNext - dataIntPart);
      }
    }
  }

  public static double percentile(Iterable<Double> data, double percentRank) {
    if (!data.iterator().hasNext()) {
      throw new IllegalArgumentException("percentile: data cannot be empty");
    }
    if (percentRank < 0.0 || percentRank > 100.0) {
      throw new IllegalArgumentException("percentile: percentRank must be in [0,100]");
    }
    return linearInterpolation(data, percentRank);
  }

  public static double median(Iterable<Double> data) {
    if (!data.iterator().hasNext()) {
      throw new IllegalArgumentException("median: data cannot be empty");
    }
    return linearInterpolation(data, 50);
  }
}


