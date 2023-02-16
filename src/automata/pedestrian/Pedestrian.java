package automata.pedestrian;

import automata.CellularAutomaton;
import geometry._2d.Location;
import gui.Canvas;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import static statistics.Random.random;

/**
 * A pedestrian in the simulation.
 *
 * @author Pepe Gallardo
 */
public class Pedestrian {
  /**
   * Class counter to generate unique identifiers for pedestrians.
   */
  protected static int nextIdentifier = 0;
  /**
   * Each pedestrian has a unique identifier.
   */
  protected final int identifier;
  /**
   * Row in scenario where pedestrian is currently located.
   */
  protected int row;
  /**
   * Column in scenario where pedestrian is currently located.
   */
  protected int column;
  /**
   * Number of steps currently taken by pedestrian.
   */
  protected int numberOfSteps;
  /**
   * Number of discrete time steps elapsed when pedestrian exited the scenario.
   */
  protected int exitTimeSteps;
  /**
   * Parameters describing this pedestrian.
   */
  protected final PedestrianParameters parameters;
  /**
   * Automaton where this pedestrian is running.
   */
  protected final CellularAutomaton automaton;

  /**
   * A tentative movement consists of a location (where we should move) and a desirability (the higher the
   * desirability the higher the willingness to move to such location). We do not use the term probability because
   * sum of all desirabilities do not have to be 1.
   *
   * @param location     where we should move.
   * @param desirability willing to move to such location
   */
  protected record TentativeMovement(Location location, double desirability) implements Comparable<TentativeMovement> {
    /**
     * Compares this tentative movement to another one according to their desirabilities.
     *
     * @param that another tentative movement.
     * @return less than 0 if {@code this.desirability < that.desirability},
     * greater than 0 if {@code this.desirability > that.desirability},
     * 0 if {@code this.desirability == that.desirability}.
     */
    @Override
    public int compareTo(TentativeMovement that) {
      return Double.compare(this.desirability, that.desirability);
    }
  }

  /**
   * Constructs a new pedestrian.
   *
   * @param row        row in scenario where pedestrian will be located.
   * @param column     column in scenario where pedestrian will be located.
   * @param parameters parameters describing new pedestrian.
   * @param automaton  automaton where this pedestrian evolves.
   */
  public Pedestrian(int row, int column, PedestrianParameters parameters, CellularAutomaton automaton) {
    this.identifier = nextIdentifier++;
    this.row = row;
    this.column = column;
    this.parameters = parameters;
    this.automaton = automaton;
    this.numberOfSteps = 0;
  }

  /**
   * Unique identifier corresponding to this pedestrian.
   *
   * @return unique identifier corresponding to this pedestrian.
   */
  public int getIdentifier() {
    return identifier;
  }

  /**
   * Row in scenario where this pedestrian is currently located.
   *
   * @return row in scenario where this pedestrian is currently located.
   */
  public int getRow() {
    return row;
  }

  /**
   * Column in scenario where this pedestrian is currently located.
   *
   * @return column in scenario where this pedestrian is currently located.
   */
  public int getColumn() {
    return column;
  }

  /**
   * Location in scenario where this pedestrian is currently located.
   *
   * @return location in scenario where this pedestrian is currently located.
   */
  public Location getLocation() {
    return new Location(row, column);
  }

  /**
   * Make pedestrian move to cell with coordinates {@code row} and {@code column}.
   *
   * @param row    vertical coordinate of destination cell.
   * @param column horizontal coordinate of destination cell.
   */
  public void moveTo(int row, int column) {
    this.row = row;
    this.column = column;
    this.numberOfSteps++;
  }

  /**
   * Make pedestrian move to cell with coordinates defined by {@code location}.
   *
   * @param location location of destination cell.
   */
  public void moveTo(Location location) {
    moveTo(location.row(), location.column());
  }

  /**
   * Number of steps currently taken by this pedestrian
   *
   * @return number of steps currently taken by this pedestrian
   */
  public int getNumberOfSteps() {
    return numberOfSteps;
  }

  /**
   * Record time (as number of discrete time steps) when pedestrian exited the scenario.
   *
   * @param timeSteps number of discrete time steps elapsed when pedestrian exited the scenario.
   */
  public void setExitTimeSteps(int timeSteps) {
    this.exitTimeSteps = timeSteps;
  }

  /**
   * Number of discrete time steps elapsed when pedestrian exited the scenario.
   *
   * @return number of discrete time steps elapsed when pedestrian exited the scenario.
   */
  public int getExitTimeSteps() {
    return exitTimeSteps;
  }

  /**
   * Computes transition desirabilities for reachable cells in the neighbourhood on this pedestrian. (the higher the
   * desirability the higher the willingness to move to such location). We do not use the term probability because
   * sum of all desirabilities do not have to be 1.
   *
   * @return List of tentative movements that this pedestrian can make, each one with associate desirability.
   */
  public List<TentativeMovement> computeTransitionDesirabilities() {
    var scenario = automaton.getScenario();
    var neighbours = automaton.neighbours(row, column);

    var movements = new ArrayList<TentativeMovement>(neighbours.size());
    for (var neighbour : neighbours) {
      if (automaton.isCellReachable(neighbour)) {
        double attraction = scenario.getStaticFloorField().getField(neighbour);

        // count reachable cells around new location
        var numberOfReachableCellsAround = 0;
        for (var around : automaton.neighbours(neighbour)) {
          if (automaton.isCellReachable(around)) {
            numberOfReachableCellsAround++;
            break;
          }
        }
        if (numberOfReachableCellsAround == 0) {
          // all neighbours of new cell are occupied or blocked
          attraction = attraction / parameters.crowdRepulsion();
        }
        var desirability = Math.exp(parameters.fieldAttractionBias() * attraction);
        movements.add(new TentativeMovement(neighbour, desirability));
      }
    }
    return movements;
  }

  /**
   * Choose randomly pedestrian's next move from those computed by {@code computeTransitionDesirabilities}.
   *
   * @return {@code Optional.empty} if no move is available or {@code Optional(m)} if move {@code m} was chosen.
   */
  public Optional<Location> chooseMovement() {
    var movements = computeTransitionDesirabilities();
    if (movements.isEmpty()) {
      // cannot make a movement
      return Optional.empty();
    }

    // choose one movement according to discrete distribution of desirabilities
    var chosen = random.discrete(movements, TentativeMovement::desirability);
    return Optional.of(chosen.location);
  }

  /**
   * Paints pedestrian in GUI representing the simulation.
   *
   * @param canvas       Graphical canvas where pedestrian should be drawn.
   * @param fillColor    fill color for drawing pedestrian.
   * @param outlineColor outline color for drawing of pedestrian.
   */
  public void paint(Canvas canvas, Color fillColor, Color outlineColor) {
    var graphics2D = canvas.graphics2D();
    graphics2D.setColor(fillColor);
    graphics2D.fillOval(column, row, 1, 1);
    graphics2D.setColor(outlineColor);
    graphics2D.drawOval(column, row, 1, 1);
  }

  /**
   * A hash code for this pedestrian.
   *
   * @return a hash code for this pedestrian.
   */
  @Override
  public int hashCode() {
    return Integer.hashCode(identifier);
  }

  /**
   * Checks whether this pedestrian is equal to another object.
   *
   * @param o another object to compare to this pedestrian.
   * @return {@code true} this pedestrian is equal to object {@code o}.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pedestrian that = (Pedestrian) o;
    return identifier == that.identifier;
  }

  /**
   * A textual representation of this pedestrian.
   *
   * @return a textual representation of this pedestrian.
   */
  @Override
  public String toString() {
    var className = getClass().getSimpleName();
    StringJoiner sj = new StringJoiner(", ", className + "(", ")");
    sj.add(getLocation().toString());
    sj.add(Integer.toString(identifier));
    return sj.toString();
  }
}
