package automata;

import geometry._2d.Location;
import gui.Canvas;
import statistics.Random;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * An agent in the simulation.
 *
 * @author Pepe Gallardo
 */
public class Agent {
  private static int nextIdentifier = 0;
  private final static Random random = Random.getInstance();

  private final int identifier; // each agent has a unique identifier
  private int row, column; // current location
  private int numberOfSteps; // number of steps taken
  private int exitTime; // ticks elapsed at exit time

  private final AgentParameters parameters;
  private final CellularAutomata automata;

  private record Movement(Location location, double desirability) implements Comparable<Movement> {
    @Override
    public int compareTo(Movement that) {
      return Double.compare(this.desirability, that.desirability);
    }
  }

  public Agent(int row, int column, AgentParameters parameters, CellularAutomata automata) {
    this.identifier = nextIdentifier++;
    this.row = row;
    this.column = column;
    this.parameters = parameters;
    this.automata = automata;
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

  public void setExitTime(int ticks) {
    this.exitTime = ticks;
  }

  public int getExitTime() {
    return exitTime;
  }

  private List<Movement> possibleMovements() {
    var neighborhood = automata.emptyNeighborhood(row, column);
    var movements = new ArrayList<Movement>(neighborhood.size());
    Scenario scenario = automata.getScenario();
    for (var neighbour : neighborhood) {
      double repulsion = scenario.risk(neighbour.row(), neighbour.column());

      // count empty cells around new location
      var numberOfEmptyCellsAround = 0;
      for (var around : automata.emptyNeighborhood(neighbour.row(), neighbour.column())) {
        if (automata.isCellEmpty(around)) {
          numberOfEmptyCellsAround++;
        }
      }
      if (numberOfEmptyCellsAround == 0) {
        // all neighbours of new cell are occupied or blocked
        repulsion *= parameters.crowdRepulsion();
      }
      var desirability = Math.exp(parameters.riskBias() * repulsion);
      movements.add(new Movement(neighbour, desirability));
    }
    return movements;
  }

  public Optional<Location> randomMove() {
    var movements = possibleMovements();
    if (movements.isEmpty()) {
      return Optional.empty();
    }

    // choose one movement according to discrete distribution of desirabilities
    var chosen = random.discrete(movements, Movement::desirability);
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
    Agent that = (Agent) o;
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
