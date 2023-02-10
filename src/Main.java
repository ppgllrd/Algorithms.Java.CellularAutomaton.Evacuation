import automata.CellularAutomata;
import automata.Scenario;
import geometry._2d.Rectangle;
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

    var exits = new java.util.HashSet<>(
        Set.of(new Rectangle(2, columns - 1, 5, 1),
            new Rectangle(rows - 7, columns - 1, 5, 1),
            new Rectangle(10, 0, 5, 1),
            new Rectangle(rows - 15, 0, 5, 1)));

    if (random.bernoulli(0.5)) {
      exits.add(new Rectangle(rows / 2, columns / 2, 2, 2));
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

      var newBlock = new Rectangle(row, column, height, width);
      var border = new Rectangle(row - 2, column - 2, height + 4, width + 4);

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
