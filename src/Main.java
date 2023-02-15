import automata.CellularAutomaton;
import automata.CellularAutomatonParameters;
import automata.Statistics;
import automata.neighbourhood.MooreNeighbourhood;
import automata.pedestrian.PedestrianParameters;

import static automata.scenario.examples.RandomScenario.randomScenario;
import static automata.scenario.examples.Supermarket.supermarket;
import static statistics.Random.random;

/**
 * Main simulation class.
 *
 * @author Pepe Gallardo
 */
class Main {
  public static void main(String[] args) {
    random.setSeed();

    var scenario = random.bernoulli(0.5) ? randomScenario() : supermarket();

    var cellularAutomatonParameters =
        new CellularAutomatonParameters.Builder()
            .scenario(scenario)
            .secondsTimeLimit(60 * 10) // 10 minutes
            .neighbourhood(MooreNeighbourhood::of) // use Moore's Neighbourhood
            .pedestrianVelocity(1.3) // 1.3 m/s
            .GUITimeFactor(15) // x15 times faster
            .build();

    var automaton = new CellularAutomaton(cellularAutomatonParameters);

    // place pedestrians
    var pedestrianParameters =
        new PedestrianParameters.Builder()
            .fieldAttractionBias(random.nextDouble(0.75, 1.50))
            .crowdRepulsion(random.nextDouble(1.00, 1.50))
            .build();

    var numberOfPedestrians = random.nextInt(150, 300);
    automaton.addPedestriansUniformly(numberOfPedestrians, pedestrianParameters);

    automaton.runGUI(); // automaton.run() to run without GUI
    Statistics statistics = automaton.computeStatistics();
    System.out.println(statistics);
  }
}
