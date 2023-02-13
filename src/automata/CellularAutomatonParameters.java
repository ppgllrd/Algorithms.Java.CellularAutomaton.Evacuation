package automata;

import automata.neighbourhood.Neighbourhood;
import automata.neighbourhood.VonNeumannNeighbourhood;

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
  public interface WithScenarioBuilder {
    WithTimeLimitBuilder secondsTimeLimit(double secondsTimeLimit);
  }

  public interface WithTimeLimitBuilder {
    Builder neighbourhood(Neighbourhood neighbourhood);

    Builder secondsPerTick(double secondsPerTick);

    Builder pedestrianVelocity(double pedestrianVelocity);

    Builder GUITimeFactor(int GUITimeFactor);

    CellularAutomatonParameters build();
  }

  public final static class Builder implements WithScenarioBuilder, WithTimeLimitBuilder {
    private Scenario scenario;
    private Neighbourhood neighbourhood;
    private double secondsTimeLimit = 100;
    private double secondsPerTick = 0.4;
    private int GUITimeFactor = 20; // x20 times faster

    private Builder() {
    }

    public static WithScenarioBuilder scenario(Scenario scenario) {
      Builder builder = new Builder();
      builder.scenario = scenario;
      builder.neighbourhood = VonNeumannNeighbourhood.of(scenario);
      return builder;
    }

    @Override
    public Builder neighbourhood(Neighbourhood neighbourhood) {
      this.neighbourhood = neighbourhood;
      return this;
    }

    @Override
    public Builder secondsTimeLimit(double secondsTimeLimit) {
      this.secondsTimeLimit = secondsTimeLimit;
      return this;
    }

    @Override
    public Builder secondsPerTick(double secondsPerTick) {
      this.secondsPerTick = secondsPerTick;
      return this;
    }

    @Override
    public Builder pedestrianVelocity(double pedestrianVelocity) {
      this.secondsPerTick = pedestrianVelocity * scenario.getCellDimension();
      return this;
    }

    @Override
    public Builder GUITimeFactor(int GUITimeFactor) {
      this.GUITimeFactor = GUITimeFactor;
      return this;
    }

    public CellularAutomatonParameters build() {
      return new CellularAutomatonParameters(scenario, neighbourhood, secondsTimeLimit, secondsPerTick, GUITimeFactor);
    }
  }
}
