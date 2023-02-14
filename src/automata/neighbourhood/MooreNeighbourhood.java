package automata.neighbourhood;

import automata.scenario.Scenario;
import geometry._2d.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for representing Moore's neighbourhood in a cellular automaton.
 *
 * @author Pepe Gallardo
 */
public class MooreNeighbourhood implements Neighbourhood {
  private final int rows, columns;

  /**
   * Creates a Moore neighbourhood for a scenario.
   *
   * @param rows    number of rows in scenario.
   * @param columns number of columns in scenario.
   */
  public MooreNeighbourhood(int rows, int columns) {
    this.rows = rows;
    this.columns = columns;
  }

  /**
   * Creates a Moore neighbourhood for given scenario.
   *
   * @param scenario scenario in which neighbourhood is described.
   * @return a Von Neumann neighbourhood for given scenario.
   */
  public static Neighbourhood of(Scenario scenario) {
    return new MooreNeighbourhood(scenario.getRows(), scenario.getColumns());
  }

  @Override
  public List<Location> neighbours(int row, int column) {
    var neighbours = new ArrayList<Location>(8);
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
    // northeast
    if (row < rows - 1 && column < columns - 1) {
      neighbours.add(new Location(row + 1, column + 1));
    }
    // southeast
    if (row > 0 && column < columns - 1) {
      neighbours.add(new Location(row - 1, column + 1));
    }
    // northwest
    if (row < rows - 1 && column > 0) {
      neighbours.add(new Location(row + 1, column - 1));
    }
    // southwest
    if (row > 0 && column > 0) {
      neighbours.add(new Location(row - 1, column - 1));
    }
    return neighbours;
  }
}
