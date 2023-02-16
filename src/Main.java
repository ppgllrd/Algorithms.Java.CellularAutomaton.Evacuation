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

    var scenario = random.bernoulli(0.75) ? randomScenario() : supermarket();

    var cellularAutomatonParameters =
        new CellularAutomatonParameters.Builder()
            .scenario(scenario) // use this scenario
            .secondsTimeLimit(60 * 10) // 10 minutes is time limit for simulation
            .neighbourhood(MooreNeighbourhood::of) // use Moore's Neighbourhood for automaton
            .pedestrianVelocity(1.3) // a pedestrian walks at 1.3 m/s
            .GUITimeFactor(8) // perform GUI animation x8 times faster than real time
            .build();

    var automaton = new CellularAutomaton(cellularAutomatonParameters);

    // place pedestrians
    var pedestrianParameters =
        new PedestrianParameters.Builder()
            .fieldAttractionBias(random.nextDouble(0.85, 2.50))
            .crowdRepulsion(random.nextDouble(1.00, 1.50))
            .build();

    var numberOfPedestrians = random.nextInt(150, 600);
    automaton.addPedestriansUniformly(numberOfPedestrians, pedestrianParameters);

    automaton.runGUI(); // automaton.run() to run without GUI
    Statistics statistics = automaton.computeStatistics();
    System.out.println(statistics);
  }
}
