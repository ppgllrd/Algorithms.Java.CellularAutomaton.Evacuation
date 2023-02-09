class Main {
  public static void main(String[] args) {
    var cellularAutomata = new CellularAutomata(20, 100, 0.5);
    cellularAutomata.clearCells();

    cellularAutomata.setExitAt(10, 99);
    cellularAutomata.setExitAt(19, 99);

    cellularAutomata.setExitAt(10, 0);
    cellularAutomata.setExitAt(16, 0);

    cellularAutomata.blockCellAt(8, 80);
    cellularAutomata.blockCellAt(9, 80);
    cellularAutomata.blockCellAt(10, 80);
    cellularAutomata.blockCellAt(11, 80);
    cellularAutomata.blockCellAt(12, 80);

    cellularAutomata.blockCellAt(1, 90);
    cellularAutomata.blockCellAt(2, 90);
    cellularAutomata.blockCellAt(3, 90);
    cellularAutomata.blockCellAt(4, 90);
    cellularAutomata.blockCellAt(5, 90);

    cellularAutomata.blockCellAt(18, 90);
    cellularAutomata.blockCellAt(17, 90);
    cellularAutomata.blockCellAt(16, 90);
    cellularAutomata.blockCellAt(15, 90);
    cellularAutomata.blockCellAt(14, 90);

    cellularAutomata.blockCellAt(7, 10);
    cellularAutomata.blockCellAt(8, 10);
    cellularAutomata.blockCellAt(9, 10);
    cellularAutomata.blockCellAt(10, 10);
    cellularAutomata.blockCellAt(11, 10);
    cellularAutomata.blockCellAt(12, 10);
    cellularAutomata.blockCellAt(13, 10);
    cellularAutomata.blockCellAt(14, 10);
    cellularAutomata.blockCellAt(15, 10);
    cellularAutomata.blockCellAt(16, 10);
    // cellularAutomata.blockCellAt(17, 10);
    cellularAutomata.blockCellAt(18, 10);
    cellularAutomata.blockCellAt(19, 10);

    cellularAutomata.run(500);
  }
}
