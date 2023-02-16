package automata.floorField;

import automata.neighbourhood.Neighbourhood;
import automata.scenario.Scenario;

import java.util.PriorityQueue;
import java.util.function.Function;

/**
 * Class for representing a static floor field proportional to the shortest distance of each cell to its closest
 * exit. Neighbourhood relationship among cells in grid used to find shortest paths can be specified.
 *
 * @author Pepe Gallardo
 */
public class DijkstraStaticFloorField extends StaticFloorField {
  protected final Neighbourhood neighbourhood;

  public DijkstraStaticFloorField(Scenario scenario, Function<Scenario, Neighbourhood> buildNeighbourhood) {
    super(new double[scenario.getRows()][scenario.getColumns()], scenario);
    this.neighbourhood = buildNeighbourhood.apply(scenario);
  }

  public static DijkstraStaticFloorField of(Scenario scenario, Function<Scenario, Neighbourhood> buildNeighbourhood) {
    return new DijkstraStaticFloorField(scenario, buildNeighbourhood);
  }

  public void initialize() {
    record Node(int row, int column, double priority) implements Comparable<Node> {
      @Override
      public int compareTo(Node that) {
        return Double.compare(this.priority, that.priority);
      }
    }

    // Compute shortest distances to any exit from each node
    var priorityQueue = new PriorityQueue<Node>();

    // Initially distance to any exit is 0 and to any other non-blocked cell is Infinity
    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getColumns(); j++) {
        if (scenario.isExit(i, j)) {
          staticFloorField[i][j] = 0;
          priorityQueue.add(new Node(i, j, staticFloorField[i][j]));
        } else if (scenario.isBlocked(i, j)) {
          staticFloorField[i][j] = Double.MAX_VALUE;
        } else {
          staticFloorField[i][j] = Double.MAX_VALUE;
          // priorityQueue.add(new Node(i, j, staticFloorField[i][j]));
        }
      }
    }

    double maxDistance = 0; // will store distance for non-blocked cell that is furthest away from an exit

    while (!priorityQueue.isEmpty()) {
      var node = priorityQueue.poll();
      double nodeDistance = staticFloorField[node.row][node.column];
      if (node.priority == nodeDistance) {
        // This is first extraction of node from PQ, hence it corresponds to its optimal cost, which is already
        // recorded in staticFloorField.
        // Now that we know optimal cost for node, let's compute alternative costs to its neighbours and
        // update if they improve current ones
        for (var neighbour : neighbourhood.neighbours(node.row, node.column)) {
          if (!scenario.isBlocked(neighbour)) {
            var delta = Math.sqrt(Math.abs(neighbour.row() - node.row) + Math.abs(neighbour.column() - node.column));
            double newNeighbourDistance = nodeDistance + delta;
            if (newNeighbourDistance < staticFloorField[neighbour.row()][neighbour.column()]) {
              // Shorter distance to neighbour was found: update
              staticFloorField[neighbour.row()][neighbour.column()] = newNeighbourDistance;
              priorityQueue.add(new Node(neighbour.row(), neighbour.column(), newNeighbourDistance));
            }
          }
        }
        if (nodeDistance > maxDistance) {
          // A cell that is furthest away from an exit was found
          maxDistance = nodeDistance;
        }
      }
    }

    // Normalize so that the closer to an exit the larger the static field
    for (int i = 0; i < getRows(); i++) {
      for (int j = 0; j < getColumns(); j++) {
        if (!scenario.isBlocked(i, j)) {
          staticFloorField[i][j] = maxDistance - staticFloorField[i][j];
        }
      }
    }
  }
}
