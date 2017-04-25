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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import automata.event.AutomataStateEvent;
import automata.event.AutomataStateListener;
import automata.event.AutomataTransitionEvent;
import automata.event.AutomataTransitionListener;



/**
 * The automata object is the root class for the representation of all forms of
 * automata, including FSA, PDA, and Turing machines. This object does NOT
 * simulate the behavior of any of those machines; it simply maintains a
 * structure that holds and maintains the data necessary to represent such a
 * machine.
 * 
 * @see automata.State
 * @see automata.Transition
 * 
 * @author Thomas Finley
 */

public class Automaton implements Serializable, Cloneable {
	/**
	 * Creates an instance of <CODE>Automaton</CODE>. The created instance
	 * has no states and no transitions.
	 */
	public Automaton() {
		states = new HashSet();
		transitions = new HashSet();
		finalStates = new HashSet();
		initialState = null;
	}

	/**
	 * Creates a clone of this automaton.
	 * 
	 * @return a clone of this automaton, or <CODE>null</CODE> if the clone
	 *         failed
	 */
	public Object clone() {
		Automaton a;
		// Try to create a new object.
		try {
			// I am a bad person for writing this hack.
//			if (this instanceof TuringMachine)
//				a = new TuringMachine(((TuringMachine) this).tapes());
//			else
				a = (Automaton) getClass().newInstance();
		} catch (Throwable e) {
			// Well golly, we're sure screwed now!
			System.err.println("Warning: clone of automaton failed!");
			return null;
		}

		
		
		// Copy over the states.
		HashMap map = new HashMap(); // Old states to new states.
		Iterator it = states.iterator();
		while (it.hasNext()) {
			State state = (State) it.next();
			State nstate = new State(state.getID(), a);
			nstate.setLabel(state.getLabel());
			nstate.setName(state.getName());
			map.put(state, nstate);
			a.addState(nstate);
            /*
             * If it is a Moore machine, set the state output.
             */
		}
		// Set special states.
		it = finalStates.iterator();
		while (it.hasNext()) {
			State state = (State) it.next();
			a.addFinalState((State) map.get(state));
		}
		a.setInitialState((State) map.get(getInitialState()));

		// Copy over the transitions.
		it = states.iterator();
		while (it.hasNext()) {
			State state = (State) it.next();
			Transition[] ts = getTransitionsFromState(state);
			State from = (State) map.get(state);
			for (int i = 0; i < ts.length; i++) {
				State to = (State) map.get(ts[i].getToState());
                Transition toBeAdded = (Transition) ts[i].clone(); //call clone instead of copy so that the gui stuff can get appropriately updated
                toBeAdded.setFromState(from);
                toBeAdded.setToState(to);
//				a.addTransition(ts[i].copy(from, to));
				a.addTransition(toBeAdded);
			}
		}

		// Should be done now!
		return a;
	}
	
	/**
	 * Turn a into b. This code is copied from the clone method and tweaked. If I am daring, I will remove it from clone and call this.
	 * 
	 * @param dest
	 * @param src
	 */
	public static void become(Automaton dest, Automaton src){
		
		dest.clear();
		// Copy over the states.
		HashMap map = new HashMap(); // Old states to new states.
		Iterator it = src.states.iterator();
		while (it.hasNext()) {
			State state = (State) it.next();
			State nstate = new State(state.getID(), dest);
			nstate.setLabel(state.getLabel());
			nstate.setName(state.getName());
			map.put(state, nstate);
			dest.addState(nstate);

		}
		// Set special states.
		it = src.finalStates.iterator();
		while (it.hasNext()) {
			State state = (State) it.next();
			dest.addFinalState((State) map.get(state));
		}
		dest.setInitialState((State) map.get(src.getInitialState()));

		// Copy over the transitions.
		it = src.states.iterator();
		while (it.hasNext()) {
			State state = (State) it.next();
			Transition[] ts = src.getTransitionsFromState(state);
			State from = (State) map.get(state);
			for (int i = 0; i < ts.length; i++) {
				State to = (State) map.get(ts[i].getToState());
                Transition toBeAdded = (Transition) ts[i].clone(); //call clone instead of copy so that the gui stuff can get appropriately updated
                toBeAdded.setFromState(from);
                toBeAdded.setToState(to);
//				dest.addTransition(ts[i].copy(from, to));
				dest.addTransition(toBeAdded);
			}
		}
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
	public Transition[] getTransitionsFromState(State from) {
		Transition[] toReturn = (Transition[]) transitionArrayFromStateMap
				.get(from);
		if (toReturn == null) {
			List list = (List) transitionFromStateMap.get(from);
			toReturn = (Transition[]) list.toArray(new Transition[0]);
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
	public Transition[] getTransitionsToState(State to) {
		Transition[] toReturn = (Transition[]) transitionArrayToStateMap
				.get(to);
		if (toReturn == null) {
			List list = (List) transitionToStateMap.get(to);
			toReturn = (Transition[]) list.toArray(new Transition[0]);
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
	public Transition[] getTransitionsFromStateToState(State from, State to) {
		Transition[] t = getTransitionsFromState(from);
		ArrayList list = new ArrayList();
		for (int i = 0; i < t.length; i++)
			if (t[i].getToState() == to)
				list.add(t[i]);
		return (Transition[]) list.toArray(new Transition[0]);
	}

	/**
	 * Retrieves all transitions.
	 * 
	 * @return an array containing all transitions for this automaton
	 */
	public Transition[] getTransitions() {
		if (cachedTransitions == null)
			cachedTransitions = (Transition[]) transitions
					.toArray(new Transition[0]);
		return cachedTransitions;
	}

	/**
	 * Adds a <CODE>Transition</CODE> to this automaton. This method may do
	 * nothing if the transition is already in the automaton.
	 * 
	 * @param trans
	 *            the transition object to add to the automaton
	 */
	public void addTransition(Transition trans) {
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

		distributeTransitionEvent(new AutomataTransitionEvent(this, trans,
				true, false));
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
	public void replaceTransition(Transition oldTrans, Transition newTrans) {
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
		distributeTransitionEvent(new AutomataTransitionEvent(this, newTrans,
				true, false));
	}

	/**
	 * Removes a <CODE>Transition</CODE> from this automaton.
	 * 
	 * @param trans
	 *            the transition object to remove from this automaton.
	 */
	public void removeTransition(Transition trans) {
		transitions.remove(trans);
		List l = (List) transitionFromStateMap.get(trans.getFromState());
		l.remove(trans);
		l = (List) transitionToStateMap.get(trans.getToState());
		l.remove(trans);
		// Remove cached arrays.
		transitionArrayFromStateMap.remove(trans.getFromState());
		transitionArrayToStateMap.remove(trans.getToState());
		cachedTransitions = null;

		distributeTransitionEvent(new AutomataTransitionEvent(this, trans,
				false, false));
	}

	
	


	
	/**
	 * Moves objects from Array to List
	 * 
	 * @param point
	 * @return
	 */
	public static List makeListFromArray(Object[] array) {
		List list = new ArrayList();
		for (int k = 0; k < array.length; k++) {
			list.add(array[k]);
		}
		return list;
	}

	/**
	 * Creates a state, inserts it in this automaton, and returns that state.
	 * The ID for the state is set appropriately.
	 * 
	 */
	public State createState() {
		int i = 0;
		while (getStateWithID(i) != null)
			i++;
		State state = new State(i, this);
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
	public final State createStateWithId(int i) {
		State state = new State(i, this);
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
	protected final void addState(State state) {
		states.add(state);
		transitionFromStateMap.put(state, new LinkedList());
		transitionToStateMap.put(state, new LinkedList());
		cachedStates = null;
		distributeStateEvent(new AutomataStateEvent(this, state, true, false,
				false));
	}

	/**
	 * Removes a state from the automaton. This will also remove all transitions
	 * associated with this state.
	 * 
	 * @param state
	 *            the state to remove
	 */
	public void removeState(State state) {
		Transition[] t = getTransitionsFromState(state);
		for (int i = 0; i < t.length; i++)
			removeTransition(t[i]);
		t = getTransitionsToState(state);
		for (int i = 0; i < t.length; i++)
			removeTransition(t[i]);
		distributeStateEvent(new AutomataStateEvent(this, state, false, false,
				false));
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
	public State setInitialState(State initialState) {
		State oldInitialState = this.initialState;
		this.initialState = initialState;
		distributeStateEvent(new AutomataStateEvent(this, initialState, false, false,
				true));
		return oldInitialState;
	}

	/**
	 * Returns the start state for this automaton.
	 * 
	 * @return the start state for this automaton
	 */
	public State getInitialState() {
		return this.initialState;
	}

	/**
	 * Returns an array that contains every state in this automaton. The array
	 * is gauranteed to be in order of ascending state IDs.
	 * 
	 * @return an array containing all the states in this automaton
	 */
	public State[] getStates() {
		if (cachedStates == null) {
			cachedStates = (State[]) states.toArray(new State[0]);
			Arrays.sort(cachedStates, new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((State) o1).getID() - ((State) o2).getID();
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
	public void addFinalState(State finalState) {
		cachedFinalStates = null;
		finalStates.add(finalState);
		distributeStateEvent(new AutomataStateEvent(this, finalState, false, false,
				true));
	}

	/**
	 * Removes a state from the set of final states. This will not remove a
	 * state from the list of states; it shall merely make it nonfinal.
	 * 
	 * @param state
	 *            the state to make not a final state
	 */
	public void removeFinalState(State state) {
		cachedFinalStates = null;
		finalStates.remove(state);
		distributeStateEvent(new AutomataStateEvent(this, state, false, false,
				true));
	}

	/**
	 * Returns an array that contains every state in this automaton that is a
	 * final state. The array is not necessarily gauranteed to be in any
	 * particular order.
	 * 
	 * @return an array containing all final states of this automaton
	 */
	public State[] getFinalStates() {
		if (cachedFinalStates == null)
			cachedFinalStates = (State[]) finalStates.toArray(new State[0]);
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
	public boolean isFinalState(State state) {
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
	public boolean isInitialState(State state) {
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
	public State getStateWithID(int id) {
		Iterator it = states.iterator();
		while (it.hasNext()) {
			State state = (State) it.next();
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
	public boolean isState(State state) {
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
		return automata.Transition.class;
	}

	/**
	 * Returns a string representation of this <CODE>Automaton</CODE>.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(super.toString());
		buffer.append('\n');
		State[] states = getStates();
		for (int s = 0; s < states.length; s++) {
			if (initialState == states[s])
				buffer.append("--> ");
			buffer.append(states[s]);
			if (isFinalState(states[s]))
				buffer.append(" **FINAL**");
			buffer.append('\n');
			Transition[] transitions = getTransitionsFromState(states[s]);
			for (int t = 0; t < transitions.length; t++) {
				buffer.append('\t');
				buffer.append(transitions[t]);
				buffer.append('\n');
			}
		}

		return buffer.toString();
	}

	/**
	 * Adds a <CODE>AutomataStateListener</CODE> to this automata.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addStateListener(AutomataStateListener listener) {
		stateListeners.add(listener);
	}

	/**
	 * Adds a <CODE>AutomataTransitionListener</CODE> to this automata.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addTransitionListener(AutomataTransitionListener listener) {
		transitionListeners.add(listener);
	}

	/**
	 * Gives an automata state change event to all state listeners.
	 * 
	 * @param event
	 *            the event to distribute
	 */
	void distributeStateEvent(AutomataStateEvent event) {
		Iterator it = stateListeners.iterator();
		while (it.hasNext()) {
			AutomataStateListener listener = (AutomataStateListener) it.next();
			listener.automataStateChange(event);
		}
	}



	/**
	 * Removes a <CODE>AutomataStateListener</CODE> from this automata.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeStateListener(AutomataStateListener listener) {
		stateListeners.remove(listener);
	}

	/**
	 * Removes a <CODE>AutomataTransitionListener</CODE> from this automata.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeTransitionListener(AutomataTransitionListener listener) {
		transitionListeners.remove(listener);
	}

	/**
	 * Gives an automata transition change event to all transition listeners.
	 * 
	 * @param event
	 *            the event to distribute
	 */
	void distributeTransitionEvent(AutomataTransitionEvent event) {
		Iterator it = transitionListeners.iterator();
		while (it.hasNext()) {
			AutomataTransitionListener listener = (AutomataTransitionListener) it
					.next();
			listener.automataTransitionChange(event);
		}
	}
	
	public void setFilePath(String name){
		fileName = name;
	}
	
	public String getFileName(){
		int last = fileName.lastIndexOf("\\");
		if(last == -1) last = fileName.lastIndexOf("/");
		
		return fileName.substring(last+1);
	}
	
	public String getFilePath(){
		int last = fileName.lastIndexOf("\\");
		if(last == -1) last = fileName.lastIndexOf("/");
		
		return fileName.substring(0, last+1);
	}

	// AUTOMATA SPECIFIC CRAP
	// This includes lots of stuff not strictly necessary for the
	// defintion of automata, but stuff that makes it at least
	// somewhat efficient in the process.
    private String fileName = "";   // Jinghui bug fixing.

	/** The collection of states in this automaton. */
	protected Set states;

	/** The cached array of states. */
	private State[] cachedStates = null;

	/** The cached array of transitions. */
	private Transition[] cachedTransitions = null;

	/** The cached array of final states. */
	private State[] cachedFinalStates = null;

	/**
	 * The collection of final states in this automaton. This is a subset of the
	 * "states" collection.
	 */
	protected Set finalStates;

	/** The initial state. */
	protected State initialState = null;

	/** The list of transitions in this automaton. */
	protected Set transitions;

	/**
	 * A mapping from states to a list holding transitions from those states.
	 */
	private HashMap transitionFromStateMap = new HashMap();

	/**
	 * A mapping from state to a list holding transitions to those states.
	 */
	private HashMap transitionToStateMap = new HashMap();

	/**
	 * A mapping from states to an array holding transitions from a state. This
	 * is a sort of cashing.
	 */
	private HashMap transitionArrayFromStateMap = new HashMap();

	/**
	 * A mapping from states to an array holding transitions from a state. This
	 * is a sort of cashing.
	 */
	private HashMap transitionArrayToStateMap = new HashMap();

//	/**
//	 * A mapping from the name of an automaton to the automaton. Used for
//	 * referencing the same automaton from multiple buliding blocks
//	 */
//	private HashMap blockMap = new HashMap();
	

	private ArrayList myNotes = new ArrayList();

	// LISTENER STUFF
	// Structures related to this object as something that generates
	// events, in particular as it pertains to the removal and
	// addition of states and transtions.
	private transient HashSet transitionListeners = new HashSet();

	private transient HashSet stateListeners = new HashSet();

	private transient HashSet noteListeners = new HashSet();
	
	/**
	 * Reset all non-transient data structures.
	 */
    protected void clear(){
    	
    	
    	
		
		
    	HashSet t = new HashSet(transitions);
		for (Object o:t)
			removeTransition((Transition)o);
		transitions = new HashSet();
		
		
		t = new HashSet(states);
		for (Object o:t)
			removeState((State)o);
		states = new HashSet();
		
		
		finalStates = new HashSet();
		
		
		initialState = null;
    
    
    	cachedStates = null;
    
    	 cachedTransitions = null;
    
    	 cachedFinalStates = null;
    
    	transitionFromStateMap = new HashMap();
    	transitionToStateMap = new HashMap();
    
    	transitionArrayFromStateMap = new HashMap();
    
    	transitionArrayToStateMap = new HashMap();
        
    }

}
