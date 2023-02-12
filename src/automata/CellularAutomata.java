package automata;

import automata.neighbourhood.Neighbourhood;
import geometry._2d.Location;
import gui.Canvas;
import gui.Frame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static statistics.Descriptive.mean;
import static statistics.Descriptive.median;
import static statistics.Random.random;

/**
 * Basic Cellular Automata for simulating pedestrian evacuation.
 *
 * @author Pepe Gallardo
 */
public class CellularAutomata {
  private final Scenario scenario;
  private final AutomataParameters parameters;
  private final Neighbourhood neighbourhood;
  private boolean[][] agentAt, agentAtNext;

  private final AgentFactory agentFactory;
  private final List<Agent> inScenarioAgents, outOfScenarioAgents;

  private int numberOfTicks;

  public CellularAutomata(AutomataParameters parameters) {
    this.parameters = parameters;
    this.scenario = parameters.scenario();
    this.neighbourhood = parameters.neighbourhood();
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

  public int getRows() {
    return scenario.getRows();
  }

  public int getColumns() {
    return scenario.getColumns();
  }

  public boolean addAgent(int row, int column, AgentParameters parameters) {
    if (isCellReachable(row, column)) {
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

  public List<Location> neighbours(int row, int column) {
    return neighbourhood.neighbours(row, column);
  }

  public List<Location> neighbours(Location location) {
    return neighbours(location.row(), location.column());
  }

  public boolean isCellOccupied(int row, int column) {
    return agentAt[row][column];
  }

  public boolean isCellOccupied(Location location) {
    return isCellOccupied(location.row(), location.column());
  }

  public boolean isCellReachable(int row, int column) {
    return !agentAt[row][column] && !scenario.isBlocked(row, column);
  }

  public boolean isCellReachable(Location location) {
    return isCellReachable(location.row(), location.column());
  }

  public boolean willBeOccupied(int row, int column) {
    return agentAtNext[row][column];
  }

  public boolean willBeOccupied(Location location) {
    return willBeOccupied(location.row(), location.column());
  }

  public Scenario getScenario() {
    return scenario;
  }

  public void tick() {
    // clear new state
    clearCells(agentAtNext);

    // move each agent
    synchronized (inScenarioAgents) {
      // in order to process agents in random order
      random.shuffle(inScenarioAgents);

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
          agent.chooseMovement().ifPresentOrElse(
              location -> {
                if (willBeOccupied(location)) {
                  // new location already taken by another agent. Don't move
                  agentAtNext[row][column] = true;
                } else {
                  // move to new location
                  agentAtNext[location.row()][location.column()] = true;
                  agent.moveTo(location);
                }
              },
              // no new location to consider. Don't move
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
      scenario.computeStaticFloorField();
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
    Canvas canvas = null;
    if (gui) {
      canvas = new Canvas.Builder()
          .setRows(scenario.getRows())
          .setColumns(scenario.getColumns())
          .setPixelsPerCell(10)
          .setPaint(CellularAutomata.this::paint)
          .build();

      var frame = new Frame(canvas);
    }
    var thread = new RunThread(canvas);
    thread.start();
    try {
      thread.join();
    } catch (InterruptedException e) {
      System.out.println("Interrupted!");
    }
  }

  public void run() {
    run(false);
  }

  public void runGUI() {
    run(true);
  }

  public Statistics computeStatistics() {
    int numberOfAgents = outOfScenarioAgents.size();
    int[] steps = new int[numberOfAgents];
    double[] evacuationTimes = new double[numberOfAgents];

    int i = 0;
    for (var agent : outOfScenarioAgents) {
      steps[i] = agent.getNumberOfSteps();
      evacuationTimes[i] = agent.getExitTime() * parameters.secondsPerTick();
      i += 1;
    }
    double meanSteps = mean(steps);
    double meanEvacuationTime = mean(evacuationTimes);
    double medianSteps = median(steps);
    double medianEvacuationTime = median(evacuationTimes);
    return new Statistics(meanSteps, meanEvacuationTime, medianSteps, medianEvacuationTime);
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


