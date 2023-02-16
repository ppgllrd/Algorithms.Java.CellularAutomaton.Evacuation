package automata.scenario.examples;

import automata.floorField.DijkstraStaticFloorFieldWithMooreNeighbourhood;
import automata.scenario.Scenario;
import geometry._2d.Rectangle;

import static statistics.Random.random;

/**
 * A class for generating random scenarios
 *
 * @author Pepe Gallardo
 */
public class RandomScenario {
  public static Scenario randomScenario() {
    int rows = 45, columns = 90;
    double cellDimension = 0.4; // 0.4 meters

    var scenario =
        new Scenario.Builder()
            .rows(rows)
            .columns(columns)
            .cellDimension(cellDimension)
            .floorField(DijkstraStaticFloorFieldWithMooreNeighbourhood::of)
            .build();

    // place exits
    if (random.bernoulli(0.9)) {
      scenario.setExit(new Rectangle(2, columns - 1, 5, 1));
    }
    if (random.bernoulli(0.9)) {
      scenario.setExit(new Rectangle(rows - 7, columns - 1, 5, 1));
    }
    if (random.bernoulli(0.9)) {
      scenario.setExit(new Rectangle(10, 0, 5, 1));
    }
    if (random.bernoulli(0.9)) {
      scenario.setExit(new Rectangle(rows - 15, 0, 5, 1));
    }
    if (random.bernoulli(0.5)) {
      scenario.setExit(new Rectangle(rows / 2, columns / 2, 2, 2));
    }

    // place blocks
    int numberOfBlocks = random.nextInt(50, 120);
    int numberOfBlocksPlaced = 0;
    int maxTries = numberOfBlocks * 3;
    while (numberOfBlocksPlaced < numberOfBlocks && maxTries > 0) {
      var width = random.bernoulli(0.5) ? 1 + random.nextInt(2) : 1 + random.nextInt(20);
      var height = 1 + random.nextInt(Math.max(1, rows / (2 * width)));

      var row = random.nextInt(0, 1 + rows - height);
      var column = random.nextInt(2, 1 + columns - width - 2);

      var newBlock = new Rectangle(row, column, height, width);
      // so that blocks are apart
      var border = new Rectangle(row - 2, column - 2, height + 4, width + 4);

      var shouldBePlaced = !border.intersectsAny(scenario.exits())
          && !border.intersectsAny(scenario.blocks());

      if (shouldBePlaced) {
        scenario.setBlock(newBlock);
        numberOfBlocksPlaced++;
      }
      maxTries -= 1;
    }

    return scenario;
  }
}
