package automata;

import automata.neighbourhood.Neighbourhood;
import automata.pedestrian.Pedestrian;
import automata.pedestrian.PedestrianFactory;
import automata.pedestrian.PedestrianParameters;
import automata.scenario.Scenario;
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
 * Cellular Automaton for simulating pedestrian evacuation.
 *
 * @author Pepe Gallardo
 */
public class CellularAutomaton {
  /**
   * Scenario where simulation takes place.
   */
  protected final Scenario scenario;
  /**
   * Parameters describing this automaton.
   */
  protected final CellularAutomatonParameters parameters;
  /**
   * Neighbourhood relationship used by this automaton.
   */
  protected final Neighbourhood neighbourhood;
  /**
   * {@code true} if cell is occupied by a pedestrian in current discrete state.
   */
  protected boolean[][] occupied;
  /**
   * {@code true} if cell will be occupied by a pedestrian in next discrete state.
   */
  protected boolean[][] occupiedNextState;
  /**
   * Factory for generating pedestrians for this automaton.
   */
  protected final PedestrianFactory pedestrianFactory;
  /**
   * List of pedestrians currently within the scenario.
   */
  protected final List<Pedestrian> inScenarioPedestrians;
  /**
   * List of pedestrians that have evacuated the scenario.
   */
  protected final List<Pedestrian> outOfScenarioPedestrians;
  /**
   * Number of discrete time steps elapsed since the start of the simulation.
   */
  protected int timeSteps;

  /**
   * Creates a new Cellular Automaton with provided parameters.
   *
   * @param parameters parameters describing this automaton.
   */
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

  /**
   * Number of rows in scenario where this automaton is running.
   *
   * @return number of rows in scenario where this automaton is running.
   */
  public int getRows() {
    return scenario.getRows();
  }

  /**
   * Number of columns in scenario where this automaton is running.
   *
   * @return number of columns in scenario where this automaton is running.
   */
  public int getColumns() {
    return scenario.getColumns();
  }

  /**
   * Adds a new pedestrian to this automaton.
   *
   * @param row        row of scenario where new pedestrian should be placed.
   * @param column     column of scenario where new pedestrian should be placed.
   * @param parameters parameters describing new pedestrian.
   * @return {@code true} if pedestrian could be created (location was neither blocked nor taken by another pedestrian).
   */
  public boolean addPedestrian(int row, int column, PedestrianParameters parameters) {
    assert row >= 0 && row < getRows() : "addPedestrian: invalid row";
    assert column >= 0 && row < getColumns() : "addPedestrian: invalid column";
    if (isCellReachable(row, column)) {
      var pedestrian = pedestrianFactory.getInstance(row, column, parameters);
      occupied[row][column] = true;
      inScenarioPedestrians.add(pedestrian);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Adds a new pedestrian to this automaton.
   *
   * @param location   location in scenario where new pedestrian should be placed.
   * @param parameters parameters describing new pedestrian.
   * @return {@code true} if pedestrian could be created (location was neither blocked nor taken by another pedestrian).
   */
  public boolean addPedestrian(Location location, PedestrianParameters parameters) {
    return addPedestrian(location.row(), location.column(), parameters);
  }

  /**
   * Adds a given number of new pedestrians located uniform randomly among free cells in automaton's scenario.
   *
   * @param numberOfPedestrians number of new pedestrian to add.
   * @param parameters          parameters describing new pedestrians.
   */
  public void addPedestriansUniformly(int numberOfPedestrians, PedestrianParameters parameters) {
    assert numberOfPedestrians >= 0 : "addPedestriansUniformly: number of pedestrian cannot be negative";
    var numberOfPedestriansPlaced = 0;
    while (numberOfPedestriansPlaced < numberOfPedestrians) {
      var row = random.nextInt(getRows());
      var column = random.nextInt(getColumns());

      if (addPedestrian(row, column, parameters)) {
        numberOfPedestriansPlaced++;
      }
    }
  }

  /**
   * Returns neighbours of a cell in this automaton (will depend on neighbourhood relationship).
   *
   * @param row    row of cell.
   * @param column column of cell.
   * @return neighbours a cell.
   */
  public List<Location> neighbours(int row, int column) {
    assert row >= 0 && row < getRows() : "neighbours: invalid row";
    assert column >= 0 && row < getColumns() : "neighbours: invalid column";
    return neighbourhood.neighbours(row, column);
  }

  /**
   * Returns neighbours of a cell in this automaton (will depend on neighbourhood relationship).
   *
   * @param location location of cell.
   * @return neighbours a cell.
   */
  public List<Location> neighbours(Location location) {
    return neighbours(location.row(), location.column());
  }

  /**
   * Checks whether a cell is occupied by some pedestrian.
   *
   * @param row    row of cell to check.
   * @param column column of cell to check.
   * @return {@code true} if cell is occupied by some pedestrian.
   */
  public boolean isCellOccupied(int row, int column) {
    assert row >= 0 && row < getRows() : "isCellOccupied: invalid row";
    assert column >= 0 && row < getColumns() : "isCellOccupied: invalid column";
    return occupied[row][column];
  }

  /**
   * Checks whether a cell is occupied by some pedestrian.
   *
   * @param location location of cell to check.
   * @return {@code true} if cell is occupied by some pedestrian.
   */
  public boolean isCellOccupied(Location location) {
    return isCellOccupied(location.row(), location.column());
  }

  /**
   * Checks whether a cell can be reached by some pedestrian (i.e. there is no pedestrian occupying the cell and the
   * cell is not blocked in the scenario).
   *
   * @param row    row of cell to check.
   * @param column column of cell to check.
   * @return {@code true} if cell can be reached by some pedestrian.
   */
  public boolean isCellReachable(int row, int column) {
    assert row >= 0 && row < getRows() : "isCellReachable: invalid row";
    assert column >= 0 && row < getColumns() : "isCellReachable: invalid column";
    return !occupied[row][column] && !scenario.isBlocked(row, column);
  }

  /**
   * Checks whether a cell can be reached by some pedestrian (i.e. there is no pedestrian occupying the cell and the
   * cell is not blocked in the scenario).
   *
   * @param location location of cell to check.
   * @return {@code true} if cell can be reached by some pedestrian.
   */
  public boolean isCellReachable(Location location) {
    return isCellReachable(location.row(), location.column());
  }

  /**
   * Checks whether some pedestrian has decided already to move to a cell in next discrete time step of simulation.
   *
   * @param row    row of cell to check.
   * @param column column of cell to check.
   * @return {@code true} if some pedestrian has decided already to move to cell in next discrete time step of
   * simulation.
   */
  public boolean willBeOccupied(int row, int column) {
    assert row >= 0 && row < getRows() : "willBeOccupied: invalid row";
    assert column >= 0 && row < getColumns() : "willBeOccupied: invalid column";
    return occupiedNextState[row][column];
  }

  /**
   * Checks whether some pedestrian has decided already to move to a cell in next discrete time step of simulation.
   *
   * @param location location of cell to check.
   * @return {@code true} if some pedestrian has decided already to move to cell in next discrete time step of
   * simulation.
   */
  public boolean willBeOccupied(Location location) {
    return willBeOccupied(location.row(), location.column());
  }

  /**
   * Scenario where automaton is running.
   *
   * @return scenario where automaton is running.
   */
  public Scenario getScenario() {
    return scenario;
  }

  /**
   * Runs one discrete time step for this automaton.
   */
  public void timeStep() {
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

  /**
   * Thread for running the simulation.
   */
  private class RunThread extends Thread {
    Canvas canvas;

    public RunThread(Canvas canvas) {
      this.canvas = canvas;
    }

    public void run() {
      scenario.getStaticFloorField().initialize();
      timeSteps = 0;
      var maximalTimeSteps = parameters.secondsTimeLimit() / parameters.secondsPerTimeStep();

      if (canvas != null) {
        // show initial configuration for 1.5 seconds
        canvas.update();
        try {
          Thread.sleep(1500);
        } catch (Exception ignored) {
        }
      }

      var millisBefore = System.currentTimeMillis();
      while (!inScenarioPedestrians.isEmpty() && timeSteps < maximalTimeSteps) {
        timeStep();
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
        // show final configuration
        canvas.update();
      }
    }
  }

  /**
   * Runs this automaton until end conditions are met.
   *
   * @param gui if this parameter is {@code true} the simulation is displayed in a GUI.
   */
  private void run(boolean gui) {
    Canvas canvas = null;
    if (gui) {
      canvas =
          new Canvas.Builder()
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

  /**
   * Runs this automaton until end conditions are met.
   */
  public void run() {
    run(false);
  }

  /**
   * Runs this automaton until end conditions are met and displays simulation in a GUI.
   */
  public void runGUI() {
    run(true);
  }

  /**
   * Computes some statistics regarding the execution of the simulation.
   *
   * @return statistics collected after running simulation.
   */
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
    int numberOfEvacuees = outOfScenarioPedestrians.size();
    int numberOfNonEvacuees = inScenarioPedestrians.size();
    return new Statistics(meanSteps, meanEvacuationTime
        , medianSteps, medianEvacuationTime
        , numberOfEvacuees, numberOfNonEvacuees);
  }

  private static final Color
      darkBlue = new Color(0, 71, 189),
      lightBlue = new Color(0, 120, 227);

  /**
   * Paints this automaton in GUI representing the simulation.
   *
   * @param canvas Graphical canvas where pedestrian should be drawn.
   */
  void paint(Canvas canvas) {
    scenario.paint(canvas);
    synchronized (inScenarioPedestrians) {
      for (var pedestrian : inScenarioPedestrians) {
        pedestrian.paint(canvas, lightBlue, darkBlue);
      }
    }
  }
}


