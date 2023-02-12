package automata;

/**
 * Class for collecting statistics after simulation.
 *
 * @param meanSteps            mean number of steps taken by evacuated agents.
 * @param meanEvacuationTime   mean evacuation time for evacuated agents.
 * @param medianSteps          median number of steps taken by evacuated agents.
 * @param medianEvacuationTime median evacuation time for evacuated agents.
 * @author Pepe Gallardo
 */
public record Statistics(double meanSteps, double meanEvacuationTime, double medianSteps, double medianEvacuationTime) {
}
