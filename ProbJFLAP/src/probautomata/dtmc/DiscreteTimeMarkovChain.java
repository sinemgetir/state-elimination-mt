package probautomata.dtmc;

import probautomata.ProbAutomaton;
import probautomata.ProbTransition;

/**
 * This subclass of <CODE>Automaton</CODE> is specifically for a definition of
 * a Discrete Time Markov Chain.
 */

public class DiscreteTimeMarkovChain extends ProbAutomaton {
	/**
	 * Creates a finite state automaton with no states and no transitions.
	 */
	public DiscreteTimeMarkovChain() {
		super();
	}

	/**
	 * Returns the class of <CODE>Transition</CODE> this automaton must
	 * accept.
	 * 
	 * @return the <CODE>Class</CODE> object for <CODE>automata.fsa.FSATransition</CODE>
	 */
	protected Class getTransitionClass() {
		return probautomata.ProbTransition.class;
	}
	
	/**
	 * Adds a <CODE>Transition</CODE> to this automaton. This method may do
	 * nothing if the transition is already in the automaton.
	 * 
	 * @param trans
	 *            the transition object to add to the automaton
	 */
	/*public void addTransition(ProbTransition trans) {
		if (!getTransitionClass().isInstance(trans) || trans == null) {
			throw (new IncompatibleTransitionException());
		}
		if(this.hasTransition(trans)) return;
        if(trans.getToState() == null || trans.getFromState() == null) return;
        
        if(this.hasTransition(trans)) return;
        
		transitions.add(trans);
        if(transitionFromStateMap == null) transitionFromStateMap = new HashMap();
		List list = (List) transitionFromStateMap.get(trans.getFromState());
		list.add(trans);
        if(transitionToStateMap == null) transitionToStateMap = new HashMap();
		list = (List)transitionToStateMap.get(trans.getToState()) ;
		list.add(trans);
		transitionArrayFromStateMap.remove(trans.getFromState());
		transitionArrayToStateMap.remove(trans.getToState());
		cachedTransitions = null;

	}*/
	
	/**
	 * Checks whether the automaton already has a Transition between
	 * the two states
	 * 
	 * @param trans
	 *            a transition between two states
	 * 
	 * @return true if there already exists a transition between the two states
	 */
	public boolean hasTransition(ProbTransition trans){
		ProbTransition[] transitions = getTransitions();
		for (int i = 0; i < transitions.length; i++)
			if (transitions[i].getFromState() == trans.getFromState() && 
					transitions[i].getToState() == trans.getToState()){
				return true;
			}
		return false;
	}
}
