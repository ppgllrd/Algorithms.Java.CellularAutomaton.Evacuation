package automata.floorField;

import automata.neighbourhood.MooreNeighbourhood;
import automata.scenario.Scenario;

/**
 * Class for representing a static floor field proportional to the shortest distance of each cell to its closest
 * exit. Uses Moore's neighbourhood among cells in grid in order to find shortest paths to exits.
 *
 * @author Pepe Gallardo
 */
public class DijkstraStaticFloorFieldWithMooreNeighbourhood extends DijkstraStaticFloorField {
  public DijkstraStaticFloorFieldWithMooreNeighbourhood(Scenario scenario) {
    super(scenario, MooreNeighbourhood::of);
  }

  public static DijkstraStaticFloorFieldWithMooreNeighbourhood of(Scenario scenario) {
    return new DijkstraStaticFloorFieldWithMooreNeighbourhood(scenario);
  }
}
