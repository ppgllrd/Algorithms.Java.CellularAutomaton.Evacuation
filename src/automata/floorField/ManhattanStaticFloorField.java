package automata.floorField;

import automata.scenario.Scenario;

/**
 * Class for representing a static floor field proportional to Manhattan distance of each cell to its closest exit.
 *
 * @author Pepe Gallardo
 */
public class ManhattanStaticFloorField extends StaticFloorField {
  public ManhattanStaticFloorField(Scenario scenario) {
    super(new double[scenario.getRows()][scenario.getColumns()], scenario);
  }

  public static ManhattanStaticFloorField of(Scenario scenario) {
    return new ManhattanStaticFloorField(scenario);
  }

  public void initialize() {
    // For each cell compute Manhattan distance to closest exit
    var maxDistance = Double.MIN_VALUE;
    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getColumns(); j++) {
        staticFloorField[i][j] = Double.MAX_VALUE;
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

    // Normalize so that the closer to an exit the larger the static field
    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getColumns(); j++) {
        staticFloorField[i][j] = maxDistance - staticFloorField[i][j];
      }
    }
  }
}
