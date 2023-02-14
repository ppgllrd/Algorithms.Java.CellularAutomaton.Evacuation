package automata.scenario.examples;

import automata.scenario.Scenario;
import geometry._2d.Rectangle;

import static statistics.Random.random;

/**
 * A class for generating random scenarios
 *
 * @author Pepe Gallardo
 */
public class RandomScenario extends Scenario {
  public RandomScenario(int rows, int columns, double cellDimension) {
    super(rows, columns, cellDimension);

    // place exits
    if (random.bernoulli(0.9)) {
      setExit(new Rectangle(2, columns - 1, 5, 1));
    }
    if (random.bernoulli(0.9)) {
      setExit(new Rectangle(rows - 7, columns - 1, 5, 1));
    }
    if (random.bernoulli(0.9)) {
      setExit(new Rectangle(10, 0, 5, 1));
    }
    if (random.bernoulli(0.9)) {
      setExit(new Rectangle(rows - 15, 0, 5, 1));
    }
    if (random.bernoulli(0.5)) {
      setExit(new Rectangle(rows / 2, columns / 2, 2, 2));
    }

    // place blocks
    int numberOfBlocks = random.nextInt(20, 50);
    int numberOfBlocksPlaced = 0;
    while (numberOfBlocksPlaced < numberOfBlocks) {
      var width = 1 + random.nextInt(20);
      var height = 1 + random.nextInt(Math.max(1, rows / (2 * width)));

      var row = random.nextInt(0, 1 + rows - height);
      var column = random.nextInt(3, 1 + columns - width - 3);

      var newBlock = new Rectangle(row, column, height, width);
      // so that blocks are apart
      var border = new Rectangle(row - 2, column - 2, height + 4, width + 4);

      var shoulBePlaced = !border.intersectsAny(exits())
          && !border.intersectsAny(blocks());

      if (shoulBePlaced) {
        setBlock(newBlock);
        numberOfBlocksPlaced++;
      }
    }
  }
}
