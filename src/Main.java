import automata.CellularAutomaton;
import automata.CellularAutomatonParameters;
import automata.Statistics;
import automata.neighbourhood.MooreNeighbourhood;
import automata.pedestrian.PedestrianParameters;
import automata.scenario.Scenario;
import automata.scenario.examples.Supermarket;

import static statistics.Random.random;

/**
 * Main simulation class.
 *
 * @author Pepe Gallardo
 */
class Main {
  public static void main(String[] args) {
    random.setSeed();

    Scenario scenario;
    if (random.bernoulli(0.5)) {
      int rows = 40, columns = 80;
      var cellDimension = 0.4; // 0.4 meters
      scenario = new automata.scenario.examples.RandomScenario(rows, columns, cellDimension);

    } else {
      scenario = new Supermarket();
    }

    var cellularAutomatonParameters =
        CellularAutomatonParameters.Builder
            .scenario(scenario)
            .secondsTimeLimit(60 * 10) // 10 minutes
            .neighbourhood(MooreNeighbourhood.of(scenario)) // use Moore's Neighbourhood
            .pedestrianVelocity(1.3) // 1.3 m/s
            .GUITimeFactor(15) // x15 times faster
            .build();

    var automaton = new CellularAutomaton(cellularAutomatonParameters);

    // place pedestrians
    var numberOfPedestrians = random.nextInt(150, 300);
    var fieldAttractionBias = random.nextDouble(0.75, 1.50);
    var crowdRepulsion = random.nextDouble(1.00, 1.50);
    var pedestrianParameters =
        PedestrianParameters.Builder()
            .fieldAttractionBias(fieldAttractionBias)
            .crowdRepulsion(crowdRepulsion)
            .build();
    automaton.addPedestriansUniformly(numberOfPedestrians, pedestrianParameters);

    automaton.runGUI(); // automaton.run() to run without GUI
    Statistics statistics = automaton.computeStatistics();
    System.out.println(statistics);
  }
}
