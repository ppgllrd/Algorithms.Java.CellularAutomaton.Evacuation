package automata;

import geometry._2d.Location;

/**
 * A class for creating different agents for a scenario.
 *
 * @author Pepe Gallardo
 */
public class AgentFactory {
  private final CellularAutomaton automaton;

  public AgentFactory(CellularAutomaton automaton) {
    this.automaton = automaton;
  }

  public Agent newAgent(int row, int column, AgentParameters parameters) {
    return new Agent(row, column, parameters, automaton);
  }

  public Agent newAgent(Location location, AgentParameters parameters) {
    return newAgent(location.row(), location.column(), parameters);
  }
}
