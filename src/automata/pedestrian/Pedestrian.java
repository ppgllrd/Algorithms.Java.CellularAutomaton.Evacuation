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
  private static int nextIdentifier = 0;

  private final int identifier; // each pedestrian has a unique identifier
  private int row, column; // current location
  private int numberOfSteps; // number of steps taken
  private int exitTimeSteps; // time steps elapsed at exit time

  private final PedestrianParameters parameters;
  private final CellularAutomaton automaton;

  private record TentativeMovement(Location location, double desirability) implements Comparable<TentativeMovement> {
    @Override
    public int compareTo(TentativeMovement that) {
      return Double.compare(this.desirability, that.desirability);
    }
  }

  public Pedestrian(int row, int column, PedestrianParameters parameters, CellularAutomaton automaton) {
    this.identifier = nextIdentifier++;
    this.row = row;
    this.column = column;
    this.parameters = parameters;
    this.automaton = automaton;
    this.numberOfSteps = 0;
  }

  public int getIdentifier() {
    return identifier;
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  public Location getLocation() {
    return new Location(row, column);
  }

  public void moveTo(int row, int column) {
    this.row = row;
    this.column = column;
    this.numberOfSteps++;
  }

  public void moveTo(Location location) {
    moveTo(location.row(), location.column());
  }

  public int getNumberOfSteps() {
    return numberOfSteps;
  }

  public void setExitTimeSteps(int timeSteps) {
    this.exitTimeSteps = timeSteps;
  }

  public int getExitTimeSteps() {
    return exitTimeSteps;
  }

  private List<TentativeMovement> tentativeMovements() {
    var scenario = automaton.getScenario();
    var neighbours = automaton.neighbours(row, column);
    var movements = new ArrayList<TentativeMovement>(neighbours.size());

    for (var neighbour : neighbours) {
      if (automaton.isCellOccupied(neighbour)) {
        continue;
      }
      if (scenario.isBlocked(neighbour)) {
        continue;
      }
      double attraction = scenario.getStaticFloorField(neighbour);

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
    return movements;
  }

  public Optional<Location> chooseMovement() {
    var movements = tentativeMovements();
    if (movements.isEmpty()) {
      // cannot make a movement
      return Optional.empty();
    }

    // choose one movement according to discrete distribution of desirabilities
    var chosen = random.discrete(movements, TentativeMovement::desirability);
    return Optional.of(chosen.location);
  }

  public void paint(Canvas canvas, Color fillColor, Color outlineColor) {
    var graphics2D = canvas.graphics2D();
    graphics2D.setColor(fillColor);
    graphics2D.fillOval(column, row, 1, 1);
    graphics2D.setColor(outlineColor);
    graphics2D.drawOval(column, row, 1, 1);
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(identifier);
  }

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

  @Override
  public String toString() {
    var className = getClass().getSimpleName();
    StringJoiner sj = new StringJoiner(", ", className + "(", ")");
    sj.add(getLocation().toString());
    sj.add(Integer.toString(identifier));
    return sj.toString();
  }
}
