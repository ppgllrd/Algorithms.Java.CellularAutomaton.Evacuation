package automata;

/**
 * Class for collecting statistics after simulation.
 *
 * @param meanSteps            mean number of steps taken by evacuated pedestrians.
 * @param meanEvacuationTime   mean evacuation time for evacuated pedestrians.
 * @param medianSteps          median number of steps taken by evacuated pedestrians.
 * @param medianEvacuationTime median evacuation time for evacuated pedestrians.
 * @param numberOfEvacuees     number of pedestrians that could evacuate the scenario.
 * @param numberOfNonEvacuees  number of pedestrians that could not evacuate the scenario.
 * @author Pepe Gallardo
 */
public record Statistics(
    double meanSteps
    , double meanEvacuationTime
    , double medianSteps
    , double medianEvacuationTime
    , int numberOfEvacuees
    , int numberOfNonEvacuees) {
}
