package automata;

import automata.neighbourhood.Neighbourhood;
import automata.pedestrian.Pedestrian;
import automata.pedestrian.PedestrianFactory;
import automata.pedestrian.PedestrianParameters;
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
 * Basic Cellular Automaton for simulating pedestrian evacuation.
 *
 * @author Pepe Gallardo
 */
public class CellularAutomaton {
  private final Scenario scenario;
  private final CellularAutomatonParameters parameters;
  private final Neighbourhood neighbourhood;
  private boolean[][] occupied, occupiedNextState;

  private final PedestrianFactory pedestrianFactory;
  private final List<Pedestrian> inScenarioPedestrians, outOfScenarioPedestrians;

  private int timeSteps;

  public CellularAutomaton(CellularAutomatonParameters parameters) {
    this.parameters = parameters;
    this.scenario = parameters.scenario();
    this.neighbourhood = parameters.neighbourhood();
    this.occupied = new boolean[scenario.getRows()][scenario.getColumns()];
    clearCells(occupied);
    this.occupiedNextState = new boolean[scenario.getRows()][scenario.getColumns()];
    this.pedestrianFactory = new PedestrianFactory(this);

    this.inScenarioPedestrians = Collections.synchronizedList(new ArrayList<>());
    this.outOfScenarioPedestrians = new ArrayList<>();
    this.timeSteps = 0;
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

  public boolean addPedestrian(int row, int column, PedestrianParameters parameters) {
    if (isCellReachable(row, column)) {
      var pedestrian = pedestrianFactory.getInstance(row, column, parameters);
      occupied[row][column] = true;
      inScenarioPedestrians.add(pedestrian);
      return true;
    } else {
      return false;
    }
  }

  public boolean addPedestrian(Location location, PedestrianParameters parameters) {
    return addPedestrian(location.row(), location.column(), parameters);
  }

  public void addPedestriansUniformly(int numberOfPedestrians, PedestrianParameters parameters) {
    var numberOfPedestriansPlaced = 0;
    while (numberOfPedestriansPlaced < numberOfPedestrians) {
      var row = random.nextInt(getRows());
      var column = random.nextInt(getColumns());

      if (addPedestrian(row, column, parameters)) {
        numberOfPedestriansPlaced++;
      }
    }
  }

  public List<Location> neighbours(int row, int column) {
    return neighbourhood.neighbours(row, column);
  }

  public List<Location> neighbours(Location location) {
    return neighbours(location.row(), location.column());
  }

  public boolean isCellOccupied(int row, int column) {
    return occupied[row][column];
  }

  public boolean isCellOccupied(Location location) {
    return isCellOccupied(location.row(), location.column());
  }

  public boolean isCellReachable(int row, int column) {
    return !occupied[row][column] && !scenario.isBlocked(row, column);
  }

  public boolean isCellReachable(Location location) {
    return isCellReachable(location.row(), location.column());
  }

  public boolean willBeOccupied(int row, int column) {
    return occupiedNextState[row][column];
  }

  public boolean willBeOccupied(Location location) {
    return willBeOccupied(location.row(), location.column());
  }

  public Scenario getScenario() {
    return scenario;
  }

  public void tick() {
    // clear new state
    clearCells(occupiedNextState);

    // move each pedestrian
    synchronized (inScenarioPedestrians) {
      // in order to process pedestrians in random order
      random.shuffle(inScenarioPedestrians);

      var pedestriansIterator = inScenarioPedestrians.iterator();
      while (pedestriansIterator.hasNext()) {
        var pedestrian = pedestriansIterator.next();
        int row = pedestrian.getRow();
        int column = pedestrian.getColumn();

        if (scenario.isExit(row, column)) {
          // pedestrian exits scenario
          pedestrian.setExitTimeSteps(timeSteps);
          outOfScenarioPedestrians.add(pedestrian);
          pedestriansIterator.remove();
        } else {
          pedestrian.chooseMovement().ifPresentOrElse(
              location -> {
                if (willBeOccupied(location)) {
                  // new location already taken by another pedestrian. Don't move
                  occupiedNextState[row][column] = true;
                } else {
                  // move to new location
                  occupiedNextState[location.row()][location.column()] = true;
                  pedestrian.moveTo(location);
                }
              },
              // no new location to consider. Don't move
              () -> occupiedNextState[row][column] = true
          );
        }
      }
    }
    // make next state current one
    var temp = occupied;
    occupied = occupiedNextState;
    occupiedNextState = temp;

    timeSteps++;
  }

  private class RunThread extends Thread {
    Canvas canvas;

    public RunThread(Canvas canvas) {
      this.canvas = canvas;
    }

    public void run() {
      scenario.computeStaticFloorField();

      timeSteps = 0;
      var maximalTimeSteps = parameters.secondsTimeLimit() / parameters.secondsPerTimeStep();

      var millisBefore = System.currentTimeMillis();
      while (!inScenarioPedestrians.isEmpty() && timeSteps < maximalTimeSteps) {
        tick();
        if (canvas != null) {
          canvas.update();
          var elapsedMillis = (System.currentTimeMillis() - millisBefore);
          try {
            // wait some milliseconds to synchronize animation
            Thread.sleep(((int) (parameters.secondsPerTimeStep() * 1000) - elapsedMillis) / parameters.GUITimeFactor());
            millisBefore = System.currentTimeMillis();
          } catch (Exception ignored) {
          }
        }
      }
      if (canvas != null) {
        canvas.update();
      }
    }
  }

  private void run(boolean gui) {
    Canvas canvas = null;
    if (gui) {
      canvas =
          Canvas.Builder()
              .rows(scenario.getRows())
              .columns(scenario.getColumns())
              .pixelsPerCell(10)
              .paint(CellularAutomaton.this::paint)
              .build();

      var frame = new Frame(canvas);
    }
    var thread = new RunThread(canvas);
    thread.start();
    try {
      thread.join(); // wait for thread to complete
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
    int numberOfPedestrians = outOfScenarioPedestrians.size();
    int[] steps = new int[numberOfPedestrians];
    double[] evacuationTimes = new double[numberOfPedestrians];

    int i = 0;
    for (var pedestrian : outOfScenarioPedestrians) {
      steps[i] = pedestrian.getNumberOfSteps();
      evacuationTimes[i] = pedestrian.getExitTimeSteps() * parameters.secondsPerTimeStep();
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
    synchronized (inScenarioPedestrians) {
      for (var pedestrian : inScenarioPedestrians) {
        pedestrian.paint(canvas, lightBlue, darkBlue);
      }
    }
  }
}


