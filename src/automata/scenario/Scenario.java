package automata.scenario;

import automata.floorField.FloorField;
import automata.floorField.ManhattanStaticFloorField;
import geometry._2d.Location;
import geometry._2d.Rectangle;
import gui.Canvas;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Class for representing static scenario where simulation takes place.
 *
 * @author Pepe Gallardo
 */
public class Scenario {
  /**
   * Number of rows in scenario.
   */
  protected final int rows;
  /**
   * Number of columns in scenario.
   */
  protected final int columns;
  /**
   * Cells are squared. Dimension (in meters) of side of a grid cell in scenario.
   */
  protected final double cellDimension;
  /**
   * A rectangle comprising whole scenario.
   */
  protected final Rectangle boundingBox;

  /**
   * A cell in scenario can either be blocked, clear or it can be an exit.
   */
  protected enum CellStatus {Blocked, Clear, Exit}

  /**
   * Grid of cells comprising this scenario.
   */
  protected final CellStatus[][] cell;
  /**
   * Exits are rectangular. Set comprising rectangles corresponding to all exits.
   */
  protected final Set<Rectangle> exits;
  /**
   * Blocked regions are rectangular. Set comprising rectangles corresponding to all blocked regions.
   */
  protected final Set<Rectangle> blocks;
  /**
   * Static floor field corresponding to this scenario.
   */
  protected final FloorField staticFloorField;

  /**
   * Constructs a new scenario.
   *
   * @param rows                  number of rows of scenario.
   * @param columns               number of columns of scenario.
   * @param cellDimension         dimension (in meters) of side of a grid cell in scenario.
   * @param buildStaticFloorField a function taking this scenario and returning its corresponding static floor field.
   */
  public Scenario(int rows, int columns, double cellDimension, Function<Scenario, FloorField> buildStaticFloorField) {
    if (rows <= 0) {
      throw new IllegalArgumentException("Scenario: rows should be larger than 0");
    }
    if (columns <= 0) {
      throw new IllegalArgumentException("Scenario: columns should be larger than 0");
    }
    if (cellDimension <= 0) {
      throw new IllegalArgumentException("Scenario: cellDimension must be greater that 0");
    }
    this.rows = rows;
    this.columns = columns;
    this.cellDimension = cellDimension;

    this.boundingBox = new Rectangle(0, 0, rows, columns);

    this.cell = new CellStatus[rows][columns];
    for (CellStatus[] row : cell) {
      Arrays.fill(row, CellStatus.Clear);
    }

    exits = new HashSet<>();
    blocks = new HashSet<>();

    this.staticFloorField = buildStaticFloorField.apply(this);
  }

  /**
   * Gets static floor field of this scenario.
   *
   * @return static floor field of this scenario.
   */
  public FloorField getStaticFloorField() {
    return staticFloorField;
  }

  /**
   * Gets number of rows in this scenario.
   *
   * @return number of rows in this scenario.
   */
  public int getRows() {
    return rows;
  }

  /**
   * Gets number of columns in this scenario.
   *
   * @return number of columns in this scenario.
   */
  public int getColumns() {
    return columns;
  }

  /**
   * Cells in the scenario are squared. Gets dimension (in meters) of side of a grid cell in scenario.
   *
   * @return dimension (in meters) of side of a grid cell in scenario.
   */
  public double getCellDimension() {
    return cellDimension;
  }

  /**
   * Sets a region defined by provided rectangle as blocked in scenario.
   *
   * @param rectangle rectangle corresponding to blocked region.
   */
  public void setBlock(Rectangle rectangle) {
    if (!boundingBox.contains(rectangle)) {
      throw new IllegalArgumentException("setBlock: block is out of bounds of scenario");
    }
    blocks.add(rectangle);

    for (int i = rectangle.bottom(); i <= rectangle.top(); i++) {
      for (int j = rectangle.left(); j <= rectangle.right(); j++) {
        cell[i][j] = CellStatus.Blocked;
      }
    }
  }

  /**
   * An iterable over all rectangles corresponding to blocked regions in scenario.
   *
   * @return iterable over all rectangles corresponding to blocked regions in scenario.
   */
  public Iterable<Rectangle> blocks() {
    return blocks;
  }

  /**
   * Checks whether a grid cell is blocked in this scenario.
   *
   * @param row    vertical coordinate of cell.
   * @param column horizontal coordinate of cell.
   * @return {@code true} if grid cell is blocked in this scenario.
   */
  public boolean isBlocked(int row, int column) {
    return cell[row][column] == CellStatus.Blocked;
  }

  /**
   * Checks whether a grid cell is blocked in this scenario.
   *
   * @param location location of cell.
   * @return {@code true} if grid cell is blocked in this scenario.
   */
  public boolean isBlocked(Location location) {
    return isBlocked(location.row(), location.column());
  }

  /**
   * Sets a region defined by provided rectangle as an exit in scenario.
   *
   * @param rectangle rectangle corresponding to exit.
   */
  public void setExit(Rectangle rectangle) {
    if (!boundingBox.contains(rectangle)) {
      throw new IllegalArgumentException("setExit: exit is out of bounds of scenario");
    }
    exits.add(rectangle);

    for (int i = rectangle.bottom(); i <= rectangle.top(); i++) {
      for (int j = rectangle.left(); j <= rectangle.right(); j++) {
        cell[i][j] = CellStatus.Exit;
      }
    }
  }

  /**
   * An iterable over all rectangles corresponding to exits in scenario.
   *
   * @return iterable over all rectangles corresponding to exits in scenario.
   */
  public Iterable<Rectangle> exits() {
    return exits;
  }

  /**
   * Checks whether a grid cell is an exit in this scenario.
   *
   * @param row    vertical coordinate of cell.
   * @param column horizontal coordinate of cell.
   * @return {@code true} if grid cell is an exit in this scenario.
   */
  public boolean isExit(int row, int column) {
    return cell[row][column] == CellStatus.Exit;
  }

  /**
   * Checks whether a grid cell is an exit in this scenario.
   *
   * @param location location of cell.
   * @return {@code true} if grid cell is an exit in this scenario.
   */
  public boolean isExit(Location location) {
    return isExit(location.row(), location.column());
  }

  private static final Color
      darkGreen = new Color(41, 175, 52),
      lightGreen = new Color(0, 201, 20),
      darkRed = new Color(142, 5, 0),
      lightRed = new Color(179, 6, 0);

  /**
   * Paints scenario in GUI representing the simulation.
   *
   * @param canvas Graphical canvas where scenario should be drawn.
   */
  public void paint(Canvas canvas) {
    for (var exit : exits) {
      exit.paint(canvas, lightGreen, darkGreen);
    }
    for (var block : blocks) {
      block.paint(canvas, lightRed, darkRed);
    }
  }

  /**
   * Class for building a scenario by providing its parameters.
   */
  public static final class Builder {
    private int rows = 10;
    private int columns = 10;
    private double cellDimension = 0.5;
    private Function<Scenario, FloorField> buildFloorField = ManhattanStaticFloorField::of;

    public Builder() {
    }

    public Builder rows(int rows) {
      this.rows = rows;
      return this;
    }

    public Builder columns(int columns) {
      this.columns = columns;
      return this;
    }

    public Builder cellDimension(double cellDimension) {
      this.cellDimension = cellDimension;
      return this;
    }

    public Builder floorField(Function<Scenario, FloorField> buildFloorField) {
      this.buildFloorField = buildFloorField;
      return this;
    }

    public Scenario build() {
      return new Scenario(rows, columns, cellDimension, buildFloorField);
    }
  }
}
