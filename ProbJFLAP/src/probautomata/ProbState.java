package probautomata;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class represents a single state in an automaton. This class is intended
 * to act as nothing more than a simple placeholder.
 */

public class ProbState{
	/**
	 * Instantiates a new state.
	 * 
	 * @param id
	 *            the state id, used for generating
	 * @param automaton
	 *            the automaton this belongs to
	 */
	public ProbState(int id, ProbAutomaton automaton) {
		this.id = id;
		this.automaton = automaton;
	}

	public void setAutomaton(ProbAutomaton automaton) {
		this.automaton = automaton;
	}

	/**
	 * Returns the automaton that this state belongs to.
	 * 
	 * @return the automaton that this state belongs to
	 */
	public ProbAutomaton getAutomaton() {
		return automaton;
	}
	

	/**
	 * Returns the state ID for this state.
	 * 
	 * @return the ID of the state
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the ID for this state.
	 * 
	 * @param id
	 *            the new ID for this state.
	 */
	protected void setID(int id) {
		if (("q" + this.id).equals(name))
			name = null;
		this.id = id;
	}

	/**
	 * Returns a string representation of this object. The string representation
	 * contains the ID and the point of the state. If the ID is <CODE>5</CODE>
	 * and the point object is at <CODE>(50,80)</CODE>, then the string
	 * representation will be </CODE>"q_5 at (50,80)"</CODE>
	 */
	public String toString() {
		return "q_" + Integer.toString(getID()) + " label: "
				+ getLabel();
	}

	/**
	 * Sets the name for this state. A parameter of <CODE>null</CODE> will
	 * reset this to the default.
	 * 
	 * @param name
	 *            the new name for the state
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the simple "name" for this state. By default this will simply be
	 * "qd", where d is the ID number.
	 * 
	 * @return the name for this state
	 */
	public String getName() {
		if (name == null) {
			name = "q" + Integer.toString(getID());
		}
		return name;
	}

	/**
	 * Sets the "label" for a state, an optional description for the state.
	 * 
	 * @param label
	 *            the new descriptive label, or <CODE>null</CODE> if the user
	 *            wishes to specify that there is no label
	 */
	public void setLabel(String label) {
		this.label = label;
		if (label == null) {
			labels = new String[0];
		} else {
			StringTokenizer st = new StringTokenizer(label, "\n");
			ArrayList lines = new ArrayList();
			while (st.hasMoreTokens())
				lines.add(st.nextToken());
			labels = (String[]) lines.toArray(new String[0]);
		}
	}

	/**
	 * Returns the label for the state.
	 * 
	 * @return the descriptive label of the state, or <CODE>null</CODE> if
	 *         this state has no label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the label for the state, broken across newlines if there are
	 * newlines in it.
	 * 
	 * @return an array of all label elements, or an empty array if this state
	 *         has no labels
	 */
	public String[] getLabels() {
		return labels;
	}


	String internalName = null;

	/** The state ID. */
	int id;

	/** The name of the state. */
	String name = null;

	/** The automaton this state belongs to. */
	private ProbAutomaton automaton = null;

	/** The label for the state. */
	private String label;

	/** If there are multiple labels, return those. */
	private String[] labels = new String[0];

}
