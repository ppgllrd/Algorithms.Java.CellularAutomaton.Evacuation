import gui.Canvas;
import gui.Frame;
import util.Random;

import java.awt.*;
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
    for (var cell : cells) {
      Arrays.fill(cell, Cell.Empty);
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

      var around = neighborhood(neighbour.row(), neighbour.column());
      // keep only empty neighbours
      around.removeIf(location -> agentsLocations[location.row()][location.column()] != Cell.Empty);
      if (around.isEmpty()) {
        // all neighbours of new cell are occupied or blocked
        repulsion *= 2.0;
      }
      var bias = -0.75;
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
        if (agentsLocations[i][j] == Cell.Occupied) {
          if (scenario.isExit(i, j)) {
            numberOfAgents--; // agent exits scenario
          } else {
            var optional = moveAgentAt(i, j);
            if (optional.isPresent()) {
              var location = optional.get();
              agentsNextLocations[location.row()][location.column()] = Cell.Occupied;
            } else {
              // don't move
              agentsNextLocations[i][j] = Cell.Occupied;
            }
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
      int pixelsPerCell = 8;
      var canvas = new Canvas(scenario.getColumns() * pixelsPerCell, scenario.getRows() * pixelsPerCell) {
        @Override
        public void paint(Graphics2D graphics2D, Canvas canvas) {
          CellularAutomata.this.paint(graphics2D, canvas);
        }
      };
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

  void paint(Graphics2D graphics2D, Canvas canvas) {
    var wc = canvas.getWidth();
    var hc = canvas.getHeight();

    var scale = Math.min(wc / scenario.getColumns(), hc / scenario.getRows());
    var diameter = scale;

    scenario.paint(graphics2D, canvas);

    graphics2D.setColor(Color.blue);
    for (int i = 0; i < scenario.getRows(); i++) {
      for (int j = 0; j < scenario.getColumns(); j++) {
        if (agentsLocations[i][j] == Cell.Occupied) {
          graphics2D.fillOval(j * scale, (scenario.getRows() - 1 - i) * scale, diameter, diameter);
        }
      }
    }
  }
}


