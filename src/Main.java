import automata.AgentParameters;
import automata.CellularAutomata;
import automata.Scenario;
import geometry._2d.Rectangle;
import statistics.Random;


/**
 * Main simulation class.
 *
 * @author Pepe Gallardo
 */
class Main {
  private static boolean intersects(Iterable<Rectangle> iterable, Rectangle rectangle) {
    for (var element : iterable) {
      if (element.intersects(rectangle)) {
        return true;
      }
    }
    return false;
  }

  public static void main(String[] args) {
    var random = Random.getInstance();
    random.setSeed();

    int rows = 40, columns = 120;
    var scenario = new Scenario(rows, columns, 0.5);

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
    int numberOfBlocks = random.nextInt(20, 80);
    int numberOfBlocksPlaced = 0;
    while (numberOfBlocksPlaced < numberOfBlocks) {
      var width = 1 + random.nextInt(20);
      var height = 1 + random.nextInt(Math.max(1, rows / (2 * width)));

      var row = random.nextInt(0, 1 + rows - height);
      var column = random.nextInt(0, 1 + columns - width);

      var newBlock = new Rectangle(row, column, height, width);
      var border = new Rectangle(row - 2, column - 2, height + 4, width + 4);

      var shoulBePlaced = !intersects(scenario.exits(), border)
          && !intersects(scenario.blocks(), border);

      if (shoulBePlaced) {
        scenario.setBlock(newBlock);
        numberOfBlocksPlaced++;
      }
    }

    var automata = new CellularAutomata(scenario);

    // place agents
    var numberOfAgents = random.nextInt(300, 600);
    var numberOfAgentsPlaced = 0;
    while (numberOfAgentsPlaced < numberOfAgents) {
      var row = random.nextInt(rows);
      var column = random.nextInt(columns);
      var riskAttraction = -random.nextDouble(0.50, 1.00);
      var crowdRepulsion = random.nextDouble(1.00, 2.50);
      var parameters = new AgentParameters(riskAttraction, crowdRepulsion);

      if (automata.addAgent(row, column, parameters)) {
        numberOfAgentsPlaced++;
      }
    }

    automata.runGUI();
    // automata.run();
  }
}
