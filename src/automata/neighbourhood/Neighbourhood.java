package automata.neighbourhood;

import geometry._2d.Location;

import java.util.List;

/**
 * Class for representing a neighbourhood relationship in a cellular automata.
 *
 * @author Pepe Gallardo
 */
public interface Neighbourhood {
  List<Location> neighbours(int row, int column);

  default List<Location> neighbours(Location location) {
    return neighbours(location.row(), location.column());
  }
}
