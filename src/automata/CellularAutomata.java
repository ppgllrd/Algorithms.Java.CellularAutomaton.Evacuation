package automata;

import geometry._2d.Location;
import gui.Canvas;
import gui.Frame;
import util.Random;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Basic Cellular Automata for simulating pedestrian evacuation.
 *
 * @author Pepe Gallardo
 */
public class CellularAutomata {
  private final Scenario scenario;
  private final int[][] riskMatrix;

  private record Movement(Location location, double desirability) implements Comparable<Movement> {
    @Override
    public int compareTo(Movement that) {
      return Double.compare(this.desirability, that.desirability);
    }
  }

  private Cell[][] agentsLocations, agentsNextLocations;

  private final Random random;

  private int numberOfAgents;
  private int numberOfTicks;

  public CellularAutomata(Scenario scenario) {
    this.scenario = scenario;
    this.agentsLocations = new Cell[scenario.getRows()][scenario.getColumns()];
    clearCells(agentsLocations);
    this.agentsNextLocations = new Cell[scenario.getRows()][scenario.getColumns()];
    this.riskMatrix = new int[scenario.getRows()][scenario.getColumns()];
    this.random = Random.getInstance();
  }

  private void clearCells(Cell[][] cells) {
    for (var row : cells) {
      Arrays.fill(row, Cell.Empty);
    }
  }

  private void computeRiskMatrix() {
    for (int i = 0; i < scenario.getRows(); i++) {
      for (int j = 0; j < scenario.getColumns(); j++) {
        int risk = Integer.MAX_VALUE;
        for (var exit : scenario.exits()) {
          int distance = exit.manhattanDistance(i, j);
          if (distance < risk) {
            risk = distance;
          }
        }
        riskMatrix[i][j] = risk;
      }
    }
  }

  private boolean isEmpty(int i, int j) {
    return agentsLocations[i][j] == Cell.Empty && !scenario.isBlocked(i, j);
  }

  private boolean isAndWillBeEmpty(int i, int j) {
    return isEmpty(i, j) && agentsNextLocations[i][j] == Cell.Empty; // not really a cellular automata as we use next state
  }

  private List<Location> neighborhood(int i, int j) {
    var neighbours = new ArrayList<Location>(4);
    // north
    if (i < scenario.getRows() - 1 && isAndWillBeEmpty(i + 1, j)) {
      neighbours.add(new Location(i + 1, j));
    }
    // south
    if (i > 0 && isAndWillBeEmpty(i - 1, j)) {
      neighbours.add(new Location(i - 1, j));
    }
    // east
    if (j < scenario.getColumns() - 1 && isAndWillBeEmpty(i, j + 1)) {
      neighbours.add(new Location(i, j + 1));
    }
    // west
    if (j > 0 && isAndWillBeEmpty(i, j - 1)) {
      neighbours.add(new Location(i, j - 1));
    }
    return neighbours;
  }

  private List<Movement> possibleMovements(int i, int j) {
    var neighborhood = neighborhood(i, j);
    var movements = new ArrayList<Movement>(neighborhood.size());
    for (var neighbour : neighborhood) {
      double repulsion = riskMatrix[neighbour.row()][neighbour.column()];

      // count empty cells around new location
      var numberOfEmptyCellsAround = 0;
      for (var around : neighborhood(neighbour.row(), neighbour.column())) {
        if (isEmpty(around.row(), around.column())) {
          numberOfEmptyCellsAround++;
        }
      }
      if (numberOfEmptyCellsAround == 0) {
        // all neighbours of new cell are occupied or blocked
        repulsion *= 1.30;
      }
      var bias = -0.6;
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

    // choose one movement according to discrete distribution of desirabilities
    var chosen = random.discrete(movements, Movement::desirability);
    return Optional.of(chosen.location);
  }

  private void placeRandomAgents(int numberOfAgentsToPlace) {
    numberOfAgents = 0;
    while (numberOfAgents < numberOfAgentsToPlace) {
      int i = random.nextInt(scenario.getRows());
      int j = random.nextInt(scenario.getColumns());
      if (isEmpty(i, j)) {
        agentsLocations[i][j] = Cell.Occupied;
        numberOfAgents++;
      }
    }
  }

  public void tick() {
    // clear new state
    clearCells(agentsNextLocations);

    // move each agent
    for (int i = 0; i < scenario.getRows(); i++) {
      for (int j = 0; j < scenario.getColumns(); j++) {
        int row = i, column = j;
        if (agentsLocations[row][column] == Cell.Occupied) {
          if (scenario.isExit(row, column)) {
            numberOfAgents--; // agent exits scenario
          } else {
            moveAgentAt(row, column).ifPresentOrElse(
                // move to new location
                location -> agentsNextLocations[location.row()][location.column()] = Cell.Occupied,
                // no new location. Don't move
                () -> agentsNextLocations[row][column] = Cell.Occupied
            );
          }
        }
      }
    }

    // make nextCells new state
    var temp = agentsLocations;
    agentsLocations = agentsNextLocations;
    agentsNextLocations = temp;

    numberOfTicks++;
  }

  private class RunThread extends Thread {
    Canvas canvas;

    public RunThread(Canvas canvas) {
      this.canvas = canvas;
    }

    public void run() {
      numberOfTicks = 0;
      while (numberOfAgents > 0) {
        tick();
        if (canvas != null) {
          canvas.update();
          try {
            Thread.sleep(50); // wait some milliseconds
          } catch (Exception ignored) {
          }
        }
      }
      if (canvas != null) {
        canvas.update();
      }
      System.out.println(numberOfTicks);
    }
  }

  private void run(int numberOfAgents, boolean gui) {
    if (gui) {
      var canvas = new Canvas.Builder()
          .setRows(scenario.getRows())
          .setColumns(scenario.getColumns())
          .setPixelsPerCell(10)
          .setPaint(CellularAutomata.this::paint)
          .build();

      var frame = new Frame(canvas);
      computeRiskMatrix();
      placeRandomAgents(numberOfAgents);
      new RunThread(canvas).start();
    } else {
      new RunThread(null).start();
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

  private static final Color
      darkBlue = new Color(0, 71, 189),
      lightBlue = new Color(0, 120, 227);
  private static final Ellipse2D ellipse2D = new Ellipse2D.Double(0, 0, 0, 0);

  void paint(Canvas canvas) {
    scenario.paint(canvas);

    var diameter = 1;
    var graphics2D = canvas.graphics2D();

    for (int i = 0; i < scenario.getRows(); i++) {
      for (int j = 0; j < scenario.getColumns(); j++) {
        if (agentsLocations[i][j] == Cell.Occupied) {
          ellipse2D.setFrame(j, i, diameter, diameter);
          graphics2D.setColor(darkBlue);
          graphics2D.fill(ellipse2D);
          graphics2D.setColor(lightBlue);
          graphics2D.fill(ellipse2D);
        }
      }
    }
  }
}


