package probautomata;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import automata.IncompatibleTransitionException;
import probautomata.dtmc.DTMCToStochasticRegularExpressionConverter;


public class ProbAutomaton{
	
	/**
	 * Creates an instance of <CODE>Automaton</CODE>. The created instance
	 * has no states and no transitions.
	 */
	public ProbAutomaton() {
		states = new HashSet();
		transitions = new HashSet();
		finalStates = new HashSet();
		initialState = null;
	}
	
	/**
	 * Retrieves all transitions that eminate from a state.
	 * 
	 * @param from
	 *            the <CODE>State</CODE> from which returned transitions
	 *            should come from
	 * @return an array of the <CODE>Transition</CODE> objects emanating from
	 *         this state
	 */
	public ProbTransition[] getTransitionsFromState(ProbState from) {
		ProbTransition[] toReturn = (ProbTransition[]) transitionArrayFromStateMap
				.get(from);
		if (toReturn == null) {
			List list = (List) transitionFromStateMap.get(from);
			toReturn = (ProbTransition[]) list.toArray(new ProbTransition[0]);
			transitionArrayFromStateMap.put(from, toReturn);
		}
		return toReturn;
	}
	
	/**
	 * Retrieves all transitions that travel from a state.
	 * 
	 * @param to
	 *            the <CODE>State</CODE> to which all returned transitions
	 *            should go to
	 * @return an array of all <CODE>Transition</CODE> objects going to the
	 *         State
	 */
	public ProbTransition[] getTransitionsToState(ProbState to) {
		ProbTransition[] toReturn = (ProbTransition[]) transitionArrayToStateMap
				.get(to);
		if (toReturn == null) {
			List list = (List) transitionToStateMap.get(to);
			toReturn = (ProbTransition[]) list.toArray(new ProbTransition[0]);
			transitionArrayToStateMap.put(to, toReturn);
		}
		return toReturn;
	}
	
	/**
	 * Retrieves all transitions going from one given state to another given
	 * state.
	 * 
	 * @param from
	 *            the state all returned transitions should come from
	 * @param to
	 *            the state all returned transitions should go to
	 * @return an array of all transitions coming from <CODE>from</CODE> and
	 *         going to <CODE>to</CODE>
	 */
	public ProbTransition[] getTransitionsFromStateToState(ProbState from, ProbState to) {
		ProbTransition[] t = getTransitionsFromState(from);
		ArrayList list = new ArrayList();
		for (int i = 0; i < t.length; i++)
			if (t[i].getToState() == to)
				list.add(t[i]);
		return (ProbTransition[]) list.toArray(new ProbTransition[0]);
	}
	
	/**
	 * Retrieves all transitions.
	 * 
	 * @return an array containing all transitions for this automaton
	 */
	public ProbTransition[] getTransitions() {
		if (cachedTransitions == null)
			cachedTransitions = (ProbTransition[]) transitions
					.toArray(new ProbTransition[0]);
		return cachedTransitions;
	}
	
	/**
	 * Adds a <CODE>Transition</CODE> to this automaton. This method may do
	 * nothing if the transition is already in the automaton.
	 * 
	 * @param trans
	 *            the transition object to add to the automaton
	 */
	public void addTransition(ProbTransition trans) {
		if (!getTransitionClass().isInstance(trans) || trans == null) {
			throw (new IncompatibleTransitionException());
		}
		if (transitions.contains(trans))
			return;
        if(trans.getToState() == null || trans.getFromState() == null) return;
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

	}
	
	/**
	 * Replaces a <CODE>Transition</CODE> in this automaton with another
	 * transition with the same from and to states. This method will delete the
	 * old if the transition is already in the automaton.
	 * 
	 * @param oldTrans
	 *            the transition object to add to the automaton
	 * @param newTrans
	 *            the transition object to add to the automaton
	 */
	public void replaceTransition(ProbTransition oldTrans, ProbTransition newTrans) {
		if (!getTransitionClass().isInstance(newTrans)) {
			throw new IncompatibleTransitionException();
		}
		if (oldTrans.equals(newTrans)) {
			return;
		}
		if (transitions.contains(newTrans)) {
			removeTransition(oldTrans);
			return;
		}
		if (!transitions.remove(oldTrans)) {
			throw new IllegalArgumentException(
					"Replacing transition that not already in the automaton!");
		}
		transitions.add(newTrans);
		List list = (List) transitionFromStateMap.get(oldTrans.getFromState());
		list.set(list.indexOf(oldTrans), newTrans);
		list = (List) transitionToStateMap.get(oldTrans.getToState());
		list.set(list.indexOf(oldTrans), newTrans);
		transitionArrayFromStateMap.remove(oldTrans.getFromState());
		transitionArrayToStateMap.remove(oldTrans.getToState());
		cachedTransitions = null;
	}
	
	/**
	 * Removes a <CODE>Transition</CODE> from this automaton.
	 * 
	 * @param trans
	 *            the transition object to remove from this automaton.
	 */
	public void removeTransition(ProbTransition trans) {
		transitions.remove(trans);
		List l = (List) transitionFromStateMap.get(trans.getFromState());
		l.remove(trans);
		l = (List) transitionToStateMap.get(trans.getToState());
		l.remove(trans);
		// Remove cached arrays.
		transitionArrayFromStateMap.remove(trans.getFromState());
		transitionArrayToStateMap.remove(trans.getToState());
		cachedTransitions = null;

	}
	
	/**
	 * Creates a state, inserts it in this automaton, and returns that state.
	 * The ID for the state is set appropriately.
	 * 
	 * @param point
	 *            the point to put the state at
	 */
	public ProbState createState() {
		int i = 0;
		while (getStateWithID(i) != null)
			i++;
		ProbState state = new ProbState(i, this);
		addState(state);
		return state;
	}
	
	/**
	 * Creates a state, inserts it in this automaton, and returns that state.
	 * The ID for the state is set appropriately.
	 * 
	 * @param point
	 *            the point to put the state at
	 */
	public final ProbState createStateWithId(int i) {
		ProbState state = new ProbState(i, this);
		addState(state);
		return state;
	}
	
	/**
	 * Adds a new state to this automata. Clients should use the <CODE>createState</CODE>
	 * method instead.
	 * 
	 * @param state
	 *            the state to add
	 */
	protected final void addState(ProbState state) {
		states.add(state);
		transitionFromStateMap.put(state, new LinkedList());
		transitionToStateMap.put(state, new LinkedList());
		cachedStates = null;
	}
	
	/**
	 * Removes a state from the automaton. This will also remove all transitions
	 * associated with this state.
	 * 
	 * @param state
	 *            the state to remove
	 */
	public void removeState(ProbState state) {
		ProbTransition[] t = getTransitionsFromState(state);
		for (int i = 0; i < t.length; i++)
			removeTransition(t[i]);
		t = getTransitionsToState(state);
		for (int i = 0; i < t.length; i++)
			removeTransition(t[i]);

		states.remove(state);
		finalStates.remove(state);
		if (state == initialState)
			initialState = null;

		transitionFromStateMap.remove(state);
		transitionToStateMap.remove(state);

		transitionArrayFromStateMap.remove(state);
		transitionArrayToStateMap.remove(state);

		cachedStates = null;
	}
	
	/**
	 * Sets the new initial state to <CODE>initialState</CODE> and returns
	 * what used to be the initial state, or <CODE>null</CODE> if there was no
	 * initial state. The state specified should already exist in the automata.
	 * 
	 * @param initialState
	 *            the new initial state
	 * @return the old initial state, or <CODE>null</CODE> if there was no
	 *         initial state
	 */
	public ProbState setInitialState(ProbState initialState) {
		ProbState oldInitialState = this.initialState;
		this.initialState = initialState;
		return oldInitialState;
	}
	
	/**
	 * Returns the start state for this automaton.
	 * 
	 * @return the start state for this automaton
	 */
	public ProbState getInitialState() {
		return this.initialState;
	}

	/**
	 * Returns an array that contains every state in this automaton. The array
	 * is gauranteed to be in order of ascending state IDs.
	 * 
	 * @return an array containing all the states in this automaton
	 */
	public ProbState[] getStates() {
		if (cachedStates == null) {
			cachedStates = (ProbState[]) states.toArray(new ProbState[0]);
			Arrays.sort(cachedStates, new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((ProbState) o1).getID() - ((ProbState) o2).getID();
				}

				public boolean equals(Object o) {
					return this == o;
				}
			});
		}
		return cachedStates;
	}
	
	/**
	 * Adds a single final state to the set of final states. Note that the
	 * general automaton can have an unlimited number of final states, and
	 * should have at least one. The state that is added should already be one
	 * of the existing states.
	 * 
	 * @param finalState
	 *            a new final state to add to the collection of final states
	 */
	public void addFinalState(ProbState finalState) {
		cachedFinalStates = null;
		finalStates.add(finalState);
	}
	
	/**
	 * Removes a state from the set of final states. This will not remove a
	 * state from the list of states; it shall merely make it nonfinal.
	 * 
	 * @param state
	 *            the state to make not a final state
	 */
	public void removeFinalState(ProbState state) {
		cachedFinalStates = null;
		finalStates.remove(state);
	}

	/**
	 * Returns an array that contains every state in this automaton that is a
	 * final state. The array is not necessarily gauranteed to be in any
	 * particular order.
	 * 
	 * @return an array containing all final states of this automaton
	 */
	public ProbState[] getFinalStates() {
		if (cachedFinalStates == null)
			cachedFinalStates = (ProbState[]) finalStates.toArray(new ProbState[0]);
		return cachedFinalStates;
	}

	/**
	 * Determines if the state passed in is in the set of final states.
	 * 
	 * @param state
	 *            the state to determine if is final
	 * @return <CODE>true</CODE> if the state is a final state in this
	 *         automaton, <CODE>false</CODE> if it is not
	 */
	public boolean isFinalState(ProbState state) {
		return finalStates.contains(state);
	}

	/**
	 * Determines if the state passed in is the initial states.
	 * Added for JFLAP 6.3
	 * @param state
	 *            the state to determine if is final
	 * @return <CODE>true</CODE> if the state is a final state in this
	 *         automaton, <CODE>false</CODE> if it is not
	 */
	public boolean isInitialState(ProbState state) {
		return (state.equals(initialState));
	}
	
	/**
	 * Returns the <CODE>State</CODE> in this automaton with this ID.
	 * 
	 * @param id
	 *            the ID to look for
	 * @return the instance of <CODE>State</CODE> in this automaton with this
	 *         ID, or <CODE>null</CODE> if no such state exists
	 */
	public ProbState getStateWithID(int id) {
		Iterator it = states.iterator();
		while (it.hasNext()) {
			ProbState state = (ProbState) it.next();
			if (state.getID() == id)
				return state;
		}
		return null;
	}

	/**
	 * Tells if the passed in object is indeed a state in this automaton.
	 * 
	 * @param state
	 *            the state to check for membership in the automaton
	 * @return <CODE>true</CODE> if this state is in the automaton, <CODE>false</CODE>otherwise
	 */
	public boolean isState(ProbState state) {
		return states.contains(state);
	}

	/**
	 * Returns the particular class that added transition objects should be a
	 * part of. Subclasses may wish to override in case they want to restrict
	 * the type of transitions their automaton will respect. By default this
	 * method simply returns the class object for the abstract class <CODE>automata.Transition</CODE>.
	 * 
	 * @see #addTransition
	 * @see automata.Transition
	 * @return the <CODE>Class</CODE> object that all added transitions should
	 *         derive from
	 */
	protected Class getTransitionClass() {
		return probautomata.ProbTransition.class;
	}

	/**
	 * Returns a string representation of this <CODE>Automaton</CODE>.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append('\n');
		ProbState[] states = getStates();
		for (int s = 0; s < states.length; s++) {
			if (initialState == states[s])
				buffer.append("--> ");
			buffer.append(states[s]);
			if (isFinalState(states[s]))
				buffer.append(" **FINAL**");
			buffer.append('\n');
			ProbTransition[] transitions = getTransitionsFromState(states[s]);
			for (int t = 0; t < transitions.length; t++) {
				buffer.append('\t');
				buffer.append(transitions[t]);
				buffer.append('\n');
			}
		}

		return buffer.toString();
	}
	
	/**
	 * calculate the relative probabilities by excluding the transition that goes to the state itself
	 * 
	 * @param state
	 * 			whose outgoing transitions will be weighted
	 */
	public void weightTransitionProbabilities(ProbState state){
		ProbTransition[] transitions = getTransitionsFromState(state);
		BigDecimal hundredPercent = new BigDecimal(0);
		BigDecimal temp;
		
		for(ProbTransition transition: transitions){
			if(transition.getFromState().equals(transition.getToState())) continue;
			if(transition.getLabel().equals(DTMCToStochasticRegularExpressionConverter.LAMBDA) ||
					transition.getLabel().equals(DTMCToStochasticRegularExpressionConverter.EMPTY)) continue;
			temp = transition.getProbability().multiply(new BigDecimal(100));
			hundredPercent = hundredPercent.add(temp);
		}
		hundredPercent = hundredPercent.setScale(2, RoundingMode.HALF_EVEN);
		
		for(ProbTransition transition: transitions){
			if(transition.getFromState().equals(transition.getToState())) continue;
			if(transition.getLabel().equals(DTMCToStochasticRegularExpressionConverter.LAMBDA) ||
					transition.getLabel().equals(DTMCToStochasticRegularExpressionConverter.EMPTY)) continue;
			temp = transition.getProbability().multiply(new BigDecimal(100));
			transition.setProbability(temp.divide(hundredPercent,2 , RoundingMode.HALF_EVEN));
		}
	}
	
	
	
	/** The collection of states in this automaton. */
	protected Set states;

	/** The cached array of states. */
	protected ProbState[] cachedStates = null;

	/** The cached array of transitions. */
	protected ProbTransition[] cachedTransitions = null;

	/** The cached array of final states. */
	protected ProbState[] cachedFinalStates = null;

	/**
	 * The collection of final states in this automaton. This is a subset of the
	 * "states" collection.
	 */
	protected Set finalStates;

	/** The initial state. */
	protected ProbState initialState = null;

	/** The list of transitions in this automaton. */
	protected Set transitions;

	/**
	 * A mapping from states to a list holding transitions from those states.
	 */
	protected HashMap transitionFromStateMap = new HashMap();

	/**
	 * A mapping from state to a list holding transitions to those states.
	 */
	protected HashMap transitionToStateMap = new HashMap();

	/**
	 * A mapping from states to an array holding transitions from a state. This
	 * is a sort of cashing.
	 */
	protected HashMap transitionArrayFromStateMap = new HashMap();

	/**
	 * A mapping from states to an array holding transitions from a state. This
	 * is a sort of cashing.
	 */
	protected HashMap transitionArrayToStateMap = new HashMap();
}
