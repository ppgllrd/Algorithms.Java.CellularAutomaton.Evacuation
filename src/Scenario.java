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

  public Scenario(int rows, int columns, double cellDimension) {
    if (rows <= 0) {
      throw new IllegalArgumentException("Scenario: rows should be larger than 0.");
    }
    if (columns <= 0) {
      throw new IllegalArgumentException("Scenario: columns should be larger than 0.");
    }
    if (cellDimension <= 0) {
      throw new IllegalArgumentException("Scenario: cellDimension must be greater that 0");
    }
    this.rows = rows;
    this.columns = columns;
    this.cellDimension = cellDimension;

    this.boundingBox = new Rectangle(0, 0, columns, rows);

    this.grid = new Cell[rows][columns];
    for (Cell[] row : grid) {
      Arrays.fill(row, Cell.Empty);
    }

    exits = new HashSet<>();
    blocks = new HashSet<>();
  }

  public int getRows() {
    return rows;
  }

  public int getColumns() {
    return columns;
  }

  public void setExit(Rectangle rectangle) {
    if (!boundingBox.contains(rectangle)) {
      throw new IllegalArgumentException("setExit: exit is out of bounds of scenario");
    }
    exits.add(rectangle);
  }

  public void setBlock(Rectangle rectangle) {
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
    for (var exit : exits) {
      if (exit.intersects(location)) {
        return true;
      }
    }
    return false;
  }

  boolean isBlocked(int row, int column) {
    return grid[row][column] == Cell.Blocked;
  }

  void paint(Graphics2D graphics2D, Canvas canvas) {
    var wc = canvas.getWidth();
    var hc = canvas.getHeight();

    var scale = Math.min(wc / columns, hc / rows);
    var diameter = scale;

    graphics2D.setColor(Color.green);
    for (var exit : exits) {
      graphics2D.fillRect(exit.left() * scale, (rows - 1 - exit.top()) * scale, diameter * exit.width(), diameter * exit.height());
    }

    graphics2D.setColor(Color.red);
    for (var block : blocks) {
      graphics2D.fillRect(block.left() * scale, (rows - 1 - block.top()) * scale, diameter * block.width(), diameter * block.height());
    }
  }
}
