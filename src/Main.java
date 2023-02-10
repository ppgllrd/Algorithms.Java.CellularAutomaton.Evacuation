import util.Random;

import java.util.HashSet;
import java.util.Set;

/**
 * Main simulation class.
 *
 * @author Pepe Gallardo
 */
class Main {
  public static void main(String[] args) {
    var random = Random.getInstance();

    int rows = 40, columns = 120;

    var scenario = new Scenario(rows, columns, 0.5);

    var exits = new java.util.HashSet<>(Set.of(new Rectangle(columns - 1, 2, 1, 5), new Rectangle(columns - 1,
        rows - 7, 1, 5), new Rectangle(0, 10, 1, 5), new Rectangle(0, rows - 15, 1, 5)));

    if (random.bernoulli(0.5)) {
      exits.add(new Rectangle(columns / 2, rows / 2, 2, 2));
    }

    for (var exit : exits) {
      scenario.setExit(exit);
    }

    int numberOfBlocks = random.nextInt(20, 80);
    var blocks = new HashSet<Rectangle>();

    while (numberOfBlocks > 0) {
      var width = 1 + random.nextInt(20);
      var height = 1 + random.nextInt(Math.max(1, rows / (2 * width)));

      var row = random.nextInt(0, 1 + rows - height);
      var column = random.nextInt(0, 1 + columns - width);

      var newBlock = new Rectangle(column, row, width, height);
      var border = new Rectangle(column - 2, row - 2, width + 4, height + 4);

      var place = true;
      for (var exit : exits) {
        if (exit.intersects(border)) {
          place = false;
          break;
        }
      }
      if (place) {
        for (var block : blocks) {
          if (block.intersects(border)) {
            place = false;
            break;
          }
        }
      }

      if (place) {
        scenario.setBlock(newBlock);
        blocks.add(newBlock);
        numberOfBlocks--;
      }
    }

    var cellularAutomata = new CellularAutomata(scenario);

    var numberOfAgents = random.nextInt(250, 500);
    cellularAutomata.runGUI(numberOfAgents);
    // cellularAutomata.run(numberOfAgents);
  }
}
