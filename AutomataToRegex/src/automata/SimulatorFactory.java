/*
 *  JFLAP - Formal Languages and Automata Package
 * 
 * 
 *  Susan H. Rodger
 *  Computer Science Department
 *  Duke University
 *  August 27, 2009

 *  Copyright (c) 2002-2009
 *  All rights reserved.

 *  JFLAP is open source software. Please see the LICENSE for terms.
 *
 */





package automata;

/**
 * This simulator factory returns the simulator for the type of automaton passed
 * in.
 * 
 * @author Thomas Finley
 */

public class SimulatorFactory {
	/**
	 * Returns the automaton simulator for this type of automaton.
	 * 
	 * @param automaton
	 *            the automaton to get the simulator for
	 * @return the appropriate automaton simulator for this automaton, or <CODE>null</CODE>
	 *         if there is no automaton simulator known for this type of
	 *         automaton
	 */
	public static AutomatonSimulator getSimulator(Automaton automaton) {
		if (automaton instanceof automata.fsa.FiniteStateAutomaton)
			return new automata.fsa.FSAStepWithClosureSimulator(automaton);

		return null;
	}
}
