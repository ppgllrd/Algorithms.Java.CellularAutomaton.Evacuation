package automata.neighbourhood;

import automata.Scenario;
import geometry._2d.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for representing Moore's neighbourhood in a cellular automata.
 *
 * @author Pepe Gallardo
 */
public class MooreNeighbourhood implements Neighbourhood {
  private final int rows, columns;

  public MooreNeighbourhood(int rows, int columns) {
    this.rows = rows;
    this.columns = columns;
  }

  public static Neighbourhood forScenario(Scenario scenario) {
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
