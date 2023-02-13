import automata.CellularAutomaton;
import automata.CellularAutomatonParameters;
import automata.Scenario;
import automata.Statistics;
import automata.neighbourhood.MooreNeighbourhood;
import automata.pedestrian.PedestrianParameters;
import geometry._2d.Rectangle;

import static statistics.Random.random;


/**
 * Main simulation class.
 *
 * @author Pepe Gallardo
 */
class Main {
  public static void main(String[] args) {
    random.setSeed();

    int rows = 40, columns = 120;
    var cellDimension = 0.4; // 0.4 meters
    var scenario = new Scenario(rows, columns, cellDimension);

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
    int numberOfBlocks = random.nextInt(20, 50);
    int numberOfBlocksPlaced = 0;
    while (numberOfBlocksPlaced < numberOfBlocks) {
      var width = 1 + random.nextInt(20);
      var height = 1 + random.nextInt(Math.max(1, rows / (2 * width)));

      var row = random.nextInt(0, 1 + rows - height);
      var column = random.nextInt(3, 1 + columns - width - 3);

      var newBlock = new Rectangle(row, column, height, width);
      var border = new Rectangle(row - 2, column - 2, height + 4, width + 4);

      var shoulBePlaced = !border.intersects(scenario.exits())
          && !border.intersects(scenario.blocks());

      if (shoulBePlaced) {
        scenario.setBlock(newBlock);
        numberOfBlocksPlaced++;
      }
    }

    var parameters =
        CellularAutomatonParameters.Builder
            .scenario(scenario)
            .secondsTimeLimit(60 * 10) // 10 minutes
            .neighbourhood(MooreNeighbourhood.of(scenario)) // use Moore's Neighbourhood
            .pedestrianVelocity(1.3) // 1.3 m/s
            .GUITimeFactor(25) // x25 times faster
            .build();

    var automaton = new CellularAutomaton(parameters);

    // place pedestrians
    var numberOfPedestrians = random.nextInt(300, 600);
    var fieldAttractionBias = random.nextDouble(0.75, 1.50);
    var crowdRepulsion = random.nextDouble(1.00, 1.50);
    var pedestrianParameters =
        PedestrianParameters.Builder()
            .fieldAttractionBias(fieldAttractionBias)
            .crowdRepulsion(crowdRepulsion)
            .build();
    automaton.addPedestriansUniformly(numberOfPedestrians, pedestrianParameters);

    automaton.runGUI();
    Statistics statistics = automaton.computeStatistics();
    System.out.println(statistics);
    // automaton.run();
  }
}
