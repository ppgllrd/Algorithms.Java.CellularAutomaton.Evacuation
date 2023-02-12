package automata;

import geometry._2d.Location;
import gui.Canvas;
import gui.Frame;
import statistics.Random;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Basic Cellular Automata for simulating pedestrian evacuation.
 *
 * @author Pepe Gallardo
 */
public class CellularAutomata {
  private final static Random random = Random.getInstance();

  private final Scenario scenario;
  private boolean[][] agentAt, agentAtNext;

  private final AgentFactory agentFactory;
  private final List<Agent> inScenarioAgents, outOfScenarioAgents;

  private int numberOfTicks;

  public CellularAutomata(Scenario scenario) {
    this.scenario = scenario;
    this.agentAt = new boolean[scenario.getRows()][scenario.getColumns()];
    clearCells(agentAt);
    this.agentAtNext = new boolean[scenario.getRows()][scenario.getColumns()];
    this.agentFactory = new AgentFactory(this);

    this.inScenarioAgents = Collections.synchronizedList(new ArrayList<>());
    this.outOfScenarioAgents = new ArrayList<>();
    this.numberOfTicks = 0;
  }

  private void clearCells(boolean[][] cells) {
    for (var row : cells) {
      Arrays.fill(row, false);
    }
  }

  public boolean addAgent(int row, int column, AgentParameters parameters) {
    if (isCellEmpty(row, column)) {
      var agent = agentFactory.newAgent(row, column, parameters);
      agentAt[row][column] = true;
      inScenarioAgents.add(agent);
      return true;
    } else {
      return false;
    }
  }

  public boolean addAgent(Location location, AgentParameters parameters) {
    return addAgent(location.row(), location.column(), parameters);
  }

  public List<Location> neighborhood(int i, int j) {
    var neighbours = new ArrayList<Location>(4);
    // north
    if (i < scenario.getRows() - 1) {
      neighbours.add(new Location(i + 1, j));
    }
    // south
    if (i > 0) {
      neighbours.add(new Location(i - 1, j));
    }
    // east
    if (j < scenario.getColumns() - 1) {
      neighbours.add(new Location(i, j + 1));
    }
    // west
    if (j > 0) {
      neighbours.add(new Location(i, j - 1));
    }
    return neighbours;
  }

  public boolean isCellEmpty(int i, int j) {
    return !agentAt[i][j] && !scenario.isBlocked(i, j);
  }

  public boolean isCellEmpty(Location location) {
    return isCellEmpty(location.row(), location.column());
  }

  private boolean isCellEmptyAndWillBeEmpty(int i, int j) {
    return isCellEmpty(i, j) && !agentAtNext[i][j]; // not really a cellular automata as we use next state
  }

  public List<Location> emptyNeighborhood(int i, int j) {
    var neighbours = neighborhood(i, j);
    neighbours.removeIf(location -> !isCellEmptyAndWillBeEmpty(location.row(), location.column()));
    return neighbours;
  }

  public Scenario getScenario() {
    return scenario;
  }

  public void tick() {
    // clear new state
    clearCells(agentAtNext);

    // move each agent
    synchronized (inScenarioAgents) {
      var agentsIterator = inScenarioAgents.iterator();
      while (agentsIterator.hasNext()) {
        var agent = agentsIterator.next();
        int row = agent.getRow();
        int column = agent.getColumn();

        if (scenario.isExit(row, column)) {
          // agent exits scenario
          agent.setExitTime(numberOfTicks);
          outOfScenarioAgents.add(agent);
          agentsIterator.remove();
        } else {
          agent.randomMove().ifPresentOrElse(
              // move to new location
              location -> {
                agentAtNext[location.row()][location.column()] = true;
                agent.moveTo(location);
              },
              // no new location. Don't move
              () -> agentAtNext[row][column] = true
          );
        }
      }
    }
    // make nextCells new state
    var temp = agentAt;
    agentAt = agentAtNext;
    agentAtNext = temp;

    numberOfTicks++;
  }

  private class RunThread extends Thread {
    Canvas canvas;

    public RunThread(Canvas canvas) {
      this.canvas = canvas;
    }

    public void run() {
      scenario.computeRisks();
      numberOfTicks = 0;
      while (!inScenarioAgents.isEmpty()) {
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

  private void run(boolean gui) {
    if (gui) {
      var canvas = new Canvas.Builder()
          .setRows(scenario.getRows())
          .setColumns(scenario.getColumns())
          .setPixelsPerCell(10)
          .setPaint(CellularAutomata.this::paint)
          .build();

      var frame = new Frame(canvas);
      new RunThread(canvas).start();
    } else {
      new RunThread(null).start();
    }
  }

  public void run() {
    run(false);
  }

  public void runGUI() {
    run(true);
  }

  private static final Color
      darkBlue = new Color(0, 71, 189),
      lightBlue = new Color(0, 120, 227);

  void paint(Canvas canvas) {
    scenario.paint(canvas);
    synchronized (inScenarioAgents) {
      for (var agent : inScenarioAgents) {
        agent.paint(canvas, lightBlue, darkBlue);
      }
    }
  }
}


