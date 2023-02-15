package automata.scenario.floorField;

import automata.scenario.Scenario;

/**
 * Class for representing a static floor field proportional to Manhattan distance of each cell to the closest exit.
 *
 * @author Pepe Gallardo
 */
public class ManhattanStaticFloorField extends StaticFloorField {
  public ManhattanStaticFloorField(Scenario scenario) {
    super(new int[scenario.getRows()][scenario.getColumns()], scenario);
  }

  public static ManhattanStaticFloorField of(Scenario scenario) {
    return new ManhattanStaticFloorField(scenario);
  }

  public void initialize() {
    // for each cell compute distance to the closest exit
    var maxDistance = Integer.MIN_VALUE;
    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getColumns(); j++) {
        staticFloorField[i][j] = Integer.MAX_VALUE;
        for (var exit : scenario.exits()) {
          int distance = exit.manhattanDistance(i, j);
          if (distance < staticFloorField[i][j]) {
            staticFloorField[i][j] = distance;
            if (distance > maxDistance) {
              maxDistance = distance;
            }
          }
        }
      }
    }

    // normalize, so that the closer to an exit the larger the static field
    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getColumns(); j++) {
        staticFloorField[i][j] = maxDistance - staticFloorField[i][j];
      }
    }
  }
}
