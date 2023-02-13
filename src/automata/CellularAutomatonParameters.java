package automata;

import automata.neighbourhood.Neighbourhood;
import automata.neighbourhood.VonNeumannNeighbourhood;

/**
 * Class representing parameters for a cellular automaton.
 *
 * @param scenario       Static scenario where simulation takes place.
 * @param neighbourhood  Neighbourhood relationship used by automaton.
 * @param secondsPerTick Seconds of time elapsed for each tick of simulation.
 * @author Pepe Gallardo
 */
public record CellularAutomatonParameters(Scenario scenario, Neighbourhood neighbourhood, double secondsPerTick) {
  public interface WithScenarioBuilder {
    Builder neighbourhood(Neighbourhood neighbourhood);

    Builder secondsPerTick(double secondsPerTick);

    Builder pedestrianVelocity(double pedestrianVelocity);

    CellularAutomatonParameters build();
  }

  public final static class Builder implements WithScenarioBuilder {
    private Scenario scenario;
    private Neighbourhood neighbourhood;
    private double secondsPerTick = 0.4;

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
    public Builder secondsPerTick(double secondsPerTick) {
      this.secondsPerTick = secondsPerTick;
      return this;
    }

    @Override
    public Builder pedestrianVelocity(double pedestrianVelocity) {
      this.secondsPerTick = pedestrianVelocity * scenario.getCellDimension();
      return this;
    }

    public CellularAutomatonParameters build() {
      return new CellularAutomatonParameters(scenario, neighbourhood, secondsPerTick);
    }
  }
}
