package automata.scenario.examples;

import automata.floorField.DijkstraStaticFloorFieldWithMooreNeighbourhood;
import automata.scenario.Scenario;
import geometry._2d.Rectangle;


/**
 * Scenario corresponding to a supermarket
 *
 * @author Pepe Gallardo
 */
public class Supermarket {
  public static Scenario supermarket() {
    int rows = 45, columns = 55;
    double cellDimension = 0.5;

    var scenario =
        new Scenario.Builder()
            .rows(rows)
            .columns(columns)
            .cellDimension(cellDimension)
            .floorField(DijkstraStaticFloorFieldWithMooreNeighbourhood::of)
            .build();

    // top corner
    scenario.setBlock(new Rectangle(39, 0, 6, 2));

    // seafood
    scenario.setBlock(new Rectangle(39, 6, 4, 3));

    // meat
    scenario.setBlock(new Rectangle(39, 12, 4, 2));
    scenario.setBlock(new Rectangle(39, 16, 4, 2));
    scenario.setBlock(new Rectangle(39, 22, 4, 2));
    scenario.setBlock(new Rectangle(39, 26, 4, 2));

    // dairy
    scenario.setBlock(new Rectangle(39, 36, 4, 2));
    scenario.setBlock(new Rectangle(39, 40, 4, 2));

    // wine
    scenario.setBlock(new Rectangle(41, 48, 1, 5));
    scenario.setBlock(new Rectangle(38, 48, 1, 5));
    scenario.setBlock(new Rectangle(35, 48, 1, 5));

    // bakery
    scenario.setBlock(new Rectangle(32, 48, 1, 2));
    scenario.setBlock(new Rectangle(32, 51, 1, 2));

    scenario.setBlock(new Rectangle(28, 48, 1, 2));
    scenario.setBlock(new Rectangle(28, 51, 1, 2));

    scenario.setBlock(new Rectangle(24, 48, 1, 2));
    scenario.setBlock(new Rectangle(24, 51, 1, 2));

    // deli
    scenario.setBlock(new Rectangle(20, 48, 1, 5));
    scenario.setBlock(new Rectangle(15, 48, 1, 5));
    scenario.setBlock(new Rectangle(10, 48, 1, 5));

    // grocery
    scenario.setBlock(new Rectangle(24, 12, 13, 2));
    scenario.setBlock(new Rectangle(24, 17, 13, 2));
    scenario.setBlock(new Rectangle(24, 22, 13, 2));
    scenario.setBlock(new Rectangle(24, 27, 13, 2));
    scenario.setBlock(new Rectangle(24, 32, 13, 2));
    scenario.setBlock(new Rectangle(24, 37, 13, 2));
    scenario.setBlock(new Rectangle(24, 42, 13, 2));

    // frozen
    scenario.setBlock(new Rectangle(9, 12, 12, 2));
    scenario.setBlock(new Rectangle(9, 17, 12, 2));
    scenario.setBlock(new Rectangle(9, 22, 12, 2));
    scenario.setBlock(new Rectangle(9, 27, 12, 2));
    scenario.setBlock(new Rectangle(9, 32, 12, 2));
    scenario.setBlock(new Rectangle(9, 37, 12, 2));
    scenario.setBlock(new Rectangle(9, 42, 12, 2));

    // bulk
    scenario.setBlock(new Rectangle(34, 8, 3, 2));
    scenario.setBlock(new Rectangle(29, 8, 3, 2));

    scenario.setBlock(new Rectangle(35, 3, 1, 3));
    scenario.setBlock(new Rectangle(31, 3, 1, 3));

    // produce
    scenario.setBlock(new Rectangle(25, 3, 1, 5));
    scenario.setBlock(new Rectangle(21, 3, 1, 5));

    scenario.setBlock(new Rectangle(17, 3, 1, 2));
    scenario.setBlock(new Rectangle(17, 6, 1, 2));

    scenario.setBlock(new Rectangle(13, 3, 1, 2));
    scenario.setBlock(new Rectangle(13, 6, 1, 2));

    scenario.setBlock(new Rectangle(9, 3, 1, 2));
    scenario.setBlock(new Rectangle(9, 6, 1, 2));

    // florist
    scenario.setBlock(new Rectangle(4, 3, 1, 4));

    // south
    scenario.setBlock(new Rectangle(3, 24, 3, 1));
    scenario.setBlock(new Rectangle(3, 27, 3, 1));
    scenario.setBlock(new Rectangle(3, 30, 3, 1));
    scenario.setBlock(new Rectangle(3, 33, 3, 1));
    scenario.setBlock(new Rectangle(3, 36, 3, 1));
    scenario.setBlock(new Rectangle(3, 39, 3, 1));
    scenario.setBlock(new Rectangle(3, 42, 3, 1));

    // exit
    scenario.setExit(new Rectangle(0, 21, 1, 8));

    return scenario;
  }
}
