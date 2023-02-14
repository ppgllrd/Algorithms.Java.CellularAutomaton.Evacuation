package automata.neighbourhood;

import geometry._2d.Location;

import java.util.List;

/**
 * Class for representing a neighbourhood relationship in a cellular automaton.
 *
 * @author Pepe Gallardo
 */
public interface Neighbourhood {
  /**
   * Returns neighbourhood of a cell.
   *
   * @param row    vertical coordinate of cell.
   * @param column horizontal  coordinate of cell.
   * @return locations of all cells in neighborhood of cell.
   */
  List<Location> neighbours(int row, int column);

  /**
   * Returns neighbourhood of a cell.
   *
   * @param location location of cell.
   * @return locations of all cells in neighborhood of cell.
   */
  default List<Location> neighbours(Location location) {
    return neighbours(location.row(), location.column());
  }
}
