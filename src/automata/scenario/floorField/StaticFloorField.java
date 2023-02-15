package automata.scenario.floorField;

import automata.scenario.Scenario;
import geometry._2d.Location;

/**
 * Class for representing a static floor field stored in a matrix of fields.
 *
 * @author Pepe Gallardo
 */
public abstract class StaticFloorField implements FloorField {
  protected final int[][] staticFloorField;
  protected final Scenario scenario;

  protected StaticFloorField(int[][] staticFloorField, Scenario scenario) {
    this.staticFloorField = staticFloorField;
    this.scenario = scenario;
  }

  public abstract void initialize();

  @Override
  public int getRows() {
    return scenario.getRows();
  }

  @Override
  public int getColumns() {
    return scenario.getColumns();
  }

  @Override
  public int getField(int row, int column) {
    assert (row >= 0 && row < getRows()) : "getField: invalid row";
    assert (column >= 0 && column < getColumns()) : "getField: invalid column";
    return staticFloorField[row][column];
  }

  @Override
  public int getField(Location location) {
    return getField(location.row(), location.column());
  }
}
