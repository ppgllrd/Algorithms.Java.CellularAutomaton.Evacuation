package automata;

import automata.neighbourhood.Neighbourhood;

/**
 * Class representing parameters for a cellular automata.
 *
 * @param scenario       Static scenario where simulation takes place.
 * @param neighbourhood  Neighbourhood relationship used by automata.
 * @param secondsPerTick Seconds of time elapsed for each tick of simulation.
 * @author Pepe Gallardo
 */
public record AutomataParameters(Scenario scenario, Neighbourhood neighbourhood, double secondsPerTick) {
}
