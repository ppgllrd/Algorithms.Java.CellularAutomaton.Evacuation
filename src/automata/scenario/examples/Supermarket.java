package automata.scenario.examples;

import automata.scenario.Scenario;
import geometry._2d.Rectangle;

/**
 * Scenario corresponding to a supermarket
 *
 * @author Pepe Gallardo
 */
public class Supermarket extends Scenario {
  public Supermarket() {
    super(45, 55, 0.5); // 45 rows x 55 columns x 0.5 m per cell

    // top corner
    setBlock(new Rectangle(39, 0, 6, 2));

    // seafood
    setBlock(new Rectangle(39, 6, 4, 3));

    // meat
    setBlock(new Rectangle(39, 12, 4, 2));
    setBlock(new Rectangle(39, 16, 4, 2));
    setBlock(new Rectangle(39, 22, 4, 2));
    setBlock(new Rectangle(39, 26, 4, 2));

    // dairy
    setBlock(new Rectangle(39, 36, 4, 2));
    setBlock(new Rectangle(39, 40, 4, 2));

    // wine
    setBlock(new Rectangle(41, 48, 1, 5));
    setBlock(new Rectangle(38, 48, 1, 5));
    setBlock(new Rectangle(35, 48, 1, 5));

    // bakery
    setBlock(new Rectangle(32, 48, 1, 2));
    setBlock(new Rectangle(32, 51, 1, 2));

    setBlock(new Rectangle(28, 48, 1, 2));
    setBlock(new Rectangle(28, 51, 1, 2));

    setBlock(new Rectangle(24, 48, 1, 2));
    setBlock(new Rectangle(24, 51, 1, 2));

    // deli
    setBlock(new Rectangle(20, 48, 1, 5));
    setBlock(new Rectangle(15, 48, 1, 5));
    setBlock(new Rectangle(10, 48, 1, 5));

    // grocery
    setBlock(new Rectangle(24, 12, 13, 2));
    setBlock(new Rectangle(24, 17, 13, 2));
    setBlock(new Rectangle(24, 22, 13, 2));
    setBlock(new Rectangle(24, 27, 13, 2));
    setBlock(new Rectangle(24, 32, 13, 2));
    setBlock(new Rectangle(24, 37, 13, 2));
    setBlock(new Rectangle(24, 42, 13, 2));

    // frozen
    setBlock(new Rectangle(9, 12, 12, 2));
    setBlock(new Rectangle(9, 17, 12, 2));
    setBlock(new Rectangle(9, 22, 12, 2));
    setBlock(new Rectangle(9, 27, 12, 2));
    setBlock(new Rectangle(9, 32, 12, 2));
    setBlock(new Rectangle(9, 37, 12, 2));
    setBlock(new Rectangle(9, 42, 12, 2));

    // bulk
    setBlock(new Rectangle(34, 8, 3, 2));
    setBlock(new Rectangle(29, 8, 3, 2));

    setBlock(new Rectangle(35, 3, 1, 3));
    setBlock(new Rectangle(31, 3, 1, 3));

    // produce
    setBlock(new Rectangle(25, 3, 1, 5));
    setBlock(new Rectangle(21, 3, 1, 5));

    setBlock(new Rectangle(17, 3, 1, 2));
    setBlock(new Rectangle(17, 6, 1, 2));

    setBlock(new Rectangle(13, 3, 1, 2));
    setBlock(new Rectangle(13, 6, 1, 2));

    setBlock(new Rectangle(9, 3, 1, 2));
    setBlock(new Rectangle(9, 6, 1, 2));

    // florist
    setBlock(new Rectangle(4, 3, 1, 4));

    // south
    setBlock(new Rectangle(3, 24, 3, 1));
    setBlock(new Rectangle(3, 27, 3, 1));
    setBlock(new Rectangle(3, 30, 3, 1));
    setBlock(new Rectangle(3, 33, 3, 1));
    setBlock(new Rectangle(3, 36, 3, 1));
    setBlock(new Rectangle(3, 39, 3, 1));
    setBlock(new Rectangle(3, 42, 3, 1));

    // exit
    setExit(new Rectangle(0, 21, 1, 8));
  }
}
