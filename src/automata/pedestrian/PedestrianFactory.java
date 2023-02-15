package automata.pedestrian;

import automata.CellularAutomaton;
import geometry._2d.Location;

/**
 * A class for creating different pedestrians for an automaton.
 *
 * @author Pepe Gallardo
 */
public class PedestrianFactory {
  private final CellularAutomaton automaton;

  public PedestrianFactory(CellularAutomaton automaton) {
    this.automaton = automaton;
  }

  public Pedestrian getInstance(int row, int column, PedestrianParameters parameters) {
    assert row >= 0 && row < automaton.getRows() : "getInstance: invalid row";
    assert column >= 0 && row < automaton.getColumns() : "getInstance: invalid column";
    return new Pedestrian(row, column, parameters, automaton);
  }

  public Pedestrian getInstance(Location location, PedestrianParameters parameters) {
    return getInstance(location.row(), location.column(), parameters);
  }
}
