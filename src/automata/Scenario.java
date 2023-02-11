package automata;

import geometry._2d.Location;
import geometry._2d.Rectangle;
import gui.Canvas;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for representing static scenario where simulation takes place.
 *
 * @author Pepe Gallardo
 */
public class Scenario {
  private final int rows, columns;
  private final double cellDimension;

  private final Rectangle boundingBox;
  private final Cell[][] grid;

  private final Set<Rectangle> exits, blocks;

  private final int[][] riskMatrix;

  public Scenario(int rows, int columns, double cellDimension) {
    if (rows <= 0) {
      throw new IllegalArgumentException("Scenario: rows should be larger than 0");
    }
    if (columns <= 0) {
      throw new IllegalArgumentException("Scenario: columns should be larger than 0");
    }
    if (cellDimension <= 0) {
      throw new IllegalArgumentException("automata.Scenario: cellDimension must be greater that 0");
    }
    this.rows = rows;
    this.columns = columns;
    this.cellDimension = cellDimension;

    this.boundingBox = new geometry._2d.Rectangle(0, 0, rows, columns);

    this.grid = new Cell[rows][columns];
    for (Cell[] row : grid) {
      Arrays.fill(row, Cell.Empty);
    }

    exits = new HashSet<>();
    blocks = new HashSet<>();

    this.riskMatrix = new int[rows][columns];
  }

  public void computeRisks() {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        int risk = Integer.MAX_VALUE;
        for (var exit : exits()) {
          int distance = exit.manhattanDistance(i, j);
          if (distance < risk) {
            risk = distance;
          }
        }
        riskMatrix[i][j] = risk;
      }
    }
  }

  public int risk(int row, int column) {
    return riskMatrix[row][column];
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

  public void setExit(geometry._2d.Rectangle rectangle) {
    if (!boundingBox.contains(rectangle)) {
      throw new IllegalArgumentException("setExit: exit is out of bounds of scenario");
    }
    exits.add(rectangle);
  }

  public void setBlock(geometry._2d.Rectangle rectangle) {
    if (!boundingBox.contains(rectangle)) {
      throw new IllegalArgumentException("setBlock: block is out of bounds of scenario");
    }
    blocks.add(rectangle);

    for (int i = rectangle.bottom(); i <= rectangle.top(); i++) {
      for (int j = rectangle.left(); j <= rectangle.right(); j++) {
        grid[i][j] = Cell.Blocked;
      }
    }
  }

  public Iterable<Rectangle> exits() {
    return exits;
  }

  boolean isExit(int row, int column) {
    for (var exit : exits) {
      if (exit.intersects(row, column)) {
        return true;
      }
    }
    return false;
  }

  boolean isExit(Location location) {
    return isExit(location.row(), location.column());
  }

  public Iterable<Rectangle> blocks() {
    return blocks;
  }

  boolean isBlocked(int row, int column) {
    return grid[row][column] == Cell.Blocked;
  }

  boolean isBlocked(Location location) {
    return isBlocked(location.row(), location.column());
  }

  private static final Color
      darkGreen = new Color(41, 175, 52),
      lightGreen = new Color(0, 201, 20),
      darkRed = new Color(142, 5, 0),
      lightRed = new Color(179, 6, 0);

  void paint(Canvas canvas) {
    for (var exit : exits) {
      exit.paint(canvas, lightGreen, darkGreen);
    }
    for (var block : blocks) {
      block.paint(canvas, lightRed, darkRed);
    }
  }
}
