package automata;

import automata.neighbourhood.Neighbourhood;
import automata.neighbourhood.VonNeumannNeighbourhood;
import automata.scenario.Scenario;

import java.util.function.Function;

/**
 * Class representing parameters for a cellular automaton.
 *
 * @param scenario           Static scenario where simulation takes place.
 * @param neighbourhood      Neighbourhood relationship used by automaton.
 * @param secondsTimeLimit   Time limit of simulation.
 * @param secondsPerTimeStep Seconds of time elapsed for each tick of simulation.
 * @param GUITimeFactor      Acceleration for rendering animation wrt real time.
 * @author Pepe Gallardo
 */
public record CellularAutomatonParameters(
    Scenario scenario
    , Neighbourhood neighbourhood
    , double secondsTimeLimit
    , double secondsPerTimeStep
    , int GUITimeFactor
) {

  public static final class Builder {
    public BuilderWithScenario scenario(Scenario scenario) {
      BuilderWithScenario builder = new BuilderWithScenario();
      builder.scenario = scenario;
      return builder;
    }
  }

  public static final class BuilderWithScenario {
    private Scenario scenario;

    private BuilderWithScenario() {
    }

    public BuilderWithScenarioWithTimeLimit secondsTimeLimit(double secondsTimeLimit) {
      BuilderWithScenarioWithTimeLimit builder = new BuilderWithScenarioWithTimeLimit(this);
      builder.secondsTimeLimit = secondsTimeLimit;
      return builder;
    }
  }

  public static final class BuilderWithScenarioWithTimeLimit {
    private final Scenario scenario;
    private double secondsTimeLimit;
    private Neighbourhood neighbourhood;
    private double secondsPerTick;
    private int GUITimeFactor;

    private BuilderWithScenarioWithTimeLimit(BuilderWithScenario builder) {
      this.scenario = builder.scenario;
      this.neighbourhood = VonNeumannNeighbourhood.of(scenario); // default neighbourhood
      this.secondsPerTick = 0.4; // default is 0.4 secs per tick
      this.GUITimeFactor = 20; // default GUI time is x20 faster
    }

    public BuilderWithScenarioWithTimeLimit neighbourhood(Function<Scenario, Neighbourhood> buildNeighbourhood) {
      this.neighbourhood = buildNeighbourhood.apply(scenario);
      return this;
    }

    public BuilderWithScenarioWithTimeLimit secondsPerTick(double secondsPerTick) {
      this.secondsPerTick = secondsPerTick;
      return this;
    }

    public BuilderWithScenarioWithTimeLimit pedestrianVelocity(double pedestrianVelocity) {
      this.secondsPerTick = pedestrianVelocity * scenario.getCellDimension();
      return this;
    }

    public BuilderWithScenarioWithTimeLimit GUITimeFactor(int GUITimeFactor) {
      this.GUITimeFactor = GUITimeFactor;
      return this;
    }

    public CellularAutomatonParameters build() {
      return new CellularAutomatonParameters(scenario, neighbourhood, secondsTimeLimit, secondsPerTick, GUITimeFactor);
    }
  }
}
