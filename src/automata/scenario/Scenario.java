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
  private final int rows, columns;
  private final double cellDimension;

  private final Rectangle boundingBox;

  private enum CellStatus {Blocked, Clear, Exit}

  private final CellStatus[][] cell;

  private final Set<Rectangle> exits, blocks;

  private final FloorField staticFloorField;

  public Scenario(int rows, int columns, double cellDimension, Function<Scenario, FloorField> buildFloorField) {
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

    this.staticFloorField = buildFloorField.apply(this);
  }

  public FloorField getStaticFloorField() {
    return staticFloorField;
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public double getCellDimension() {
    return cellDimension;
  }

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

  public Iterable<Rectangle> blocks() {
    return blocks;
  }

  public boolean isBlocked(int row, int column) {
    return cell[row][column] == CellStatus.Blocked;
  }

  public boolean isBlocked(Location location) {
    return isBlocked(location.row(), location.column());
  }

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

  public Iterable<Rectangle> exits() {
    return exits;
  }

  public boolean isExit(int row, int column) {
    return cell[row][column] == CellStatus.Exit;
  }

  public boolean isExit(Location location) {
    return isExit(location.row(), location.column());
  }

  private static final Color
      darkGreen = new Color(41, 175, 52),
      lightGreen = new Color(0, 201, 20),
      darkRed = new Color(142, 5, 0),
      lightRed = new Color(179, 6, 0);

  public void paint(Canvas canvas) {
    for (var exit : exits) {
      exit.paint(canvas, lightGreen, darkGreen);
    }
    for (var block : blocks) {
      block.paint(canvas, lightRed, darkRed);
    }
  }

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
