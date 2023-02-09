import gui.Canvas;
import gui.Frame;
import util.Random;


import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Basic Cellular Automata for simulating pedestrian evacuation.
 *
 * @author Pepe Gallardo
 */
public class CellularAutomata {
  private final int rows;
  private final int columns;
  private final double cellDimension;
  private final int[][] riskMatrix;

  private record Location(int row, int column) { }
  private record Movement(Location location, double desirability) implements Comparable<Movement> {
    @Override
    public int compareTo(Movement that) {
      return Double.compare(this.desirability, that.desirability);
    }
  }

  private enum Cell {Empty, Occupied, Blocked}
  private Cell[][] cells;
  private Cell[][] nextCells;

  private final Set<Location> exits;

  private final Random random;

  private int numberOfAgents;
  private int ticks;

  public CellularAutomata(int rows, int columns, double cellDimension) {
    if (rows <= 0) {
      throw new IllegalArgumentException("rows must be greater that 0");
    }
    if (columns <= 0) {
      throw new IllegalArgumentException("columns must be greater that 0");
    }
    if (cellDimension <= 0) {
      throw new IllegalArgumentException("cellDimension must be greater that 0");
    }
    this.rows = rows;
    this.columns = columns;
    this.cellDimension = cellDimension;
    this.cells = new Cell[rows][columns];
    this.nextCells = new Cell[rows][columns];
    this.exits = new HashSet<>();
    this.riskMatrix = new int[rows][columns];
    this.random = Random.getInstance();
    clearCells();
  }

  public void clearCells() {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        cells[i][j] = Cell.Empty;
      }
    }
  }

  public void blockCellAt(int i, int j) {
    if (i < 0 || i >= rows) {
      throw new IllegalArgumentException("block(). Wrong row coordinate.");
    }
    if (j < 0 || j >= columns) {
      throw new IllegalArgumentException("block(). Wrong column coordinate.");
    }
    cells[i][j] = Cell.Blocked;
  }

  public void setExitAt(int i, int j) {
    if (i < 0 || i >= rows) {
      throw new IllegalArgumentException("setExitAt(). Wrong row coordinate.");
    }
    if (j < 0 || j >= columns) {
      throw new IllegalArgumentException("setExitAt(). Wrong column coordinate.");
    }
    exits.add(new Location(i, j));
  }

  private void computeRiskMatrix() {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        int risk = Integer.MAX_VALUE;
        for (var exit : exits) {
          int distance = Math.abs(i - exit.row) + Math.abs(j - exit.column);
          if (distance < risk) {
            risk = distance;
          }
        }
        riskMatrix[i][j] = risk;
      }
    }
  }

  private List<Location> neighborhood(int i, int j) {
    var neighbours = new ArrayList<Location>(4);

    // north
    if (i < rows - 1 && cells[i + 1][j] == Cell.Empty && nextCells[i + 1][j] == Cell.Empty) {
      neighbours.add(new Location(i + 1, j));
    }

    // south
    if (i > 0 && cells[i - 1][j] == Cell.Empty && nextCells[i - 1][j] == Cell.Empty) {
      neighbours.add(new Location(i - 1, j));
    }

    // east
    if (j < columns - 1 && cells[i][j + 1] == Cell.Empty && nextCells[i][j + 1] == Cell.Empty) {
      neighbours.add(new Location(i, j + 1));
    }

    // west
    if (j > 0 && cells[i][j - 1] == Cell.Empty && nextCells[i][j - 1] == Cell.Empty) {
      neighbours.add(new Location(i, j - 1));
    }

    return neighbours;
  }

  private List<Movement> possibleMovements(int i, int j) {
    var neighborhood = neighborhood(i, j);
    var movements = new ArrayList<Movement>(neighborhood.size());
    for (var neighbour : neighborhood) {
      double repulsion = riskMatrix[neighbour.row][neighbour.column];

      var around = neighborhood(neighbour.row, neighbour.column);
      // keep only empty neighbours
      around.removeIf(location -> cells[location.row][location.column] != Cell.Empty);
      if (around.isEmpty()) {
        // all neighbours of new cell are occupied or blocked
        repulsion *= 1.75;
      }
      var bias = -0.35;
      var desirability = Math.exp(bias * repulsion);
      movements.add(new Movement(neighbour, desirability));
    }
    return movements;
  }

  private Optional<Location> moveAgentAt(int i, int j) {
    var movements = possibleMovements(i, j);
    if (movements.isEmpty()) {
      return Optional.empty();
    }
    // choose one according to discrete distribution of desirabilities
    double sum = 0.0;
    for (var move : movements) {
      sum += move.desirability;
    }

    var choose = random.nextDouble(sum);
    sum = 0.0;
    for (var move : movements) {
      sum += move.desirability;
      if (sum > choose) {
        return Optional.of(move.location);
      }
    }

    // not reached
    return Optional.empty();
  }

  private void placeRandomAgents(int numberOfAgentsToPlace) {
    numberOfAgents = 0;
    while (numberOfAgents < numberOfAgentsToPlace) {
      int i = random.nextInt(rows);
      int j = random.nextInt(columns);
      if (cells[i][j] == Cell.Empty) {
        cells[i][j] = Cell.Occupied;
        numberOfAgents++;
      }
    }
  }

  private boolean isExit(Location location) {
    return exits.contains(location);
  }

  public void tick() {
    // copy Blocked cells to new state
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        if (cells[i][j] == Cell.Blocked) {
          nextCells[i][j] = Cell.Blocked;
        } else {
          nextCells[i][j] = Cell.Empty;
        }
      }
    }

    // move each agent
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        if (cells[i][j] == Cell.Occupied) {
          if (isExit(new Location(i, j))) {
            numberOfAgents--; // agent exits scenario
          } else {
            var optional = moveAgentAt(i, j);
            if (optional.isPresent()) {
              var location = optional.get();
              nextCells[location.row][location.column] = Cell.Occupied;
            } else {
              // don't move
              nextCells[i][j] = Cell.Occupied;
            }
          }
        }
      }
    }
    // make nextCells new state
    var temp = cells;
    cells = nextCells;
    nextCells = temp;

    ticks++;
  }

  private class RunThread extends Thread {
    Canvas canvas;
    int numberOfAgents;

    public RunThread(Canvas canvas, int numberOfAgents) {
      this.canvas = canvas;
      this.numberOfAgents = numberOfAgents;
    }

    public void run() {
      computeRiskMatrix();
      placeRandomAgents(numberOfAgents);
      ticks = 0;
      while (CellularAutomata.this.numberOfAgents > 0) {
        tick();
        if (canvas != null) {
          try {
            Thread.sleep(15); // wait some milliseconds
          } catch (Exception ignored) {
          }
          canvas.repaint();
        }
      }
      System.out.println(ticks);
    }
  }

  private void run(int numberOfAgents, boolean gui) {
    if (gui) {
      int pixelsPerCell = 8;
      var canvas = new Canvas(columns * pixelsPerCell, rows * pixelsPerCell) {
        @Override
        public void paint(Graphics graphics, Canvas canvas) {
          CellularAutomata.this.paint((Graphics2D) graphics, canvas);
        }
      };
      var frame = new Frame(canvas);
      frame.setVisible(true);
      new RunThread(canvas, numberOfAgents).start();
    } else {
      new RunThread(null, numberOfAgents).start();
    }
  }

  public void run(int numberOfAgents) {
    run(numberOfAgents, false);
  }

  public void run(long seed, int numberOfAgents) {
    random.setSeed(seed);
    run(numberOfAgents);
  }

  public void runGUI(int numberOfAgents) {
    run(numberOfAgents, true);
  }

  public void runGUI(long seed, int numberOfAgents) {
    random.setSeed(seed);
    runGUI(numberOfAgents);
  }

  void paint(Graphics2D graphics2D, Canvas canvas) {
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    var wc = canvas.getWidth();
    var hc = canvas.getHeight();
    var dimc = Math.min(wc, hc);

    var w = columns;
    var h = rows;
    var dim = Math.max(w, h);

    var sc = wc / columns;

    var diameter = sc;

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        if (cells[i][j] == Cell.Occupied) {
          graphics2D.setColor(Color.blue);
          graphics2D.fillOval(j * sc, (rows - 1 - i) * sc, diameter, diameter);
        } else if (cells[i][j] == Cell.Empty) {
          graphics2D.setColor(Color.LIGHT_GRAY);
          graphics2D.fillOval(j * sc, (rows - 1 - i) * sc, diameter, diameter);
        } else {
          graphics2D.setColor(Color.red);
          graphics2D.fillRect(j * sc, (rows - 1 - i) * sc, diameter, diameter);
        }
      }
    }

    graphics2D.setColor(Color.green);
    for (var location : exits) {
      graphics2D.fillRect(location.column * sc, (rows - 1 - location.row) * sc, diameter, diameter);
    }
  }
}


