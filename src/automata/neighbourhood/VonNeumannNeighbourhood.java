package automata.neighbourhood;

import automata.scenario.Scenario;
import geometry._2d.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for representing Von Neumann's neighbourhood in a cellular automaton.
 *
 * @author Pepe Gallardo
 */
public class VonNeumannNeighbourhood implements Neighbourhood {
  private final int rows, columns;

  /**
   * Creates a Von Neumann neighbourhood for a scenario.
   *
   * @param rows    number of rows in scenario.
   * @param columns number of columns in scenario.
   */
  public VonNeumannNeighbourhood(int rows, int columns) {
    this.rows = rows;
    this.columns = columns;
  }

  /**
   * Creates a Von Neumann neighbourhood for given scenario.
   *
   * @param scenario scenario in which neighbourhood is described.
   * @return a Von Neumann neighbourhood for given scenario.
   */
  public static Neighbourhood of(Scenario scenario) {
    return new VonNeumannNeighbourhood(scenario.getRows(), scenario.getColumns());
  }

  @Override
  public List<Location> neighbours(int row, int column) {
    var neighbours = new ArrayList<Location>(4);
    // north
    if (row < rows - 1) {
      neighbours.add(new Location(row + 1, column));
    }
    // south
    if (row > 0) {
      neighbours.add(new Location(row - 1, column));
    }
    // east
    if (column < columns - 1) {
      neighbours.add(new Location(row, column + 1));
    }
    // west
    if (column > 0) {
      neighbours.add(new Location(row, column - 1));
    }
    return neighbours;
  }
}
