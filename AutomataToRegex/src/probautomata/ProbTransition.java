package probautomata;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * An <CODE>FSATransition</CODE> is a <CODE>Transition</CODE> object with an
 * additional field for the label, which determines if the machine should move
 * on this transition.
 * 
 * @see automata.fsa.FiniteStateAutomaton
 */

public class ProbTransition{
	/**
	 * Instantiates a new <CODE>ProbTransition</CODE> object.
	 * 
	 * @param from
	 *            the state this transition comes from
	 * @param to
	 *            the state this transition goes to
	 * @param probabLabel
	 *            the string for the transition and its probability
	 *            e.g. a[0.86]
	 */
	public ProbTransition(ProbState from, ProbState to, String label, BigDecimal probability) {
		this.from = from;
		this.to = to;
		setLabel(label);
		setProbability(probability);
	}

	/**
	 * Produces a copy of this transition with new from and to states.
	 * 
	 * @param from
	 *            the new from state
	 * @param to
	 *            the new to state
	 * @return a copy of this transition with the new states
	 */
	public ProbTransition copy(ProbState from, ProbState to) {
		return new ProbTransition(from, to, myLabel, myProbability);
	}

	/**
	 * Returns the state this transition eminates from.
	 * 
	 * @return the state this transition eminates from
	 */
	public ProbState getFromState() {
		return this.from;
	}

	/**
	 * Returns the state this transition travels to.
	 * 
	 * @return the state this transition travels to
	 */
	public ProbState getToState() {
		return this.to;
	}
	/**
	 * Sets the state the transition starts at.
	 * @param newFrom the state the transition starts at
	 */
	public void setFromState(ProbState newFrom) {
		this.from = newFrom;
	}

	/**
	 * Sets the state the transition goes to.
	 * @param newTo the state the transition goes to
	 */
	public void setToState(ProbState newTo) {
		this.to = newTo;
	}
	
	/**
	 * Returns the automaton this transition is over.
	 * 
	 * @return the automaton this transition is over
	 */
	public ProbAutomaton getAutomaton() {
		return this.from.getAutomaton();
	}
	
	/**
	 * Returns the label for this transition.
	 */
	public String getLabel() {
		return myLabel;
	}

	/**
	 * Sets the label for this transition.
	 * 
	 * @param label
	 *            the new label for this transition
	 * @throws IllegalArgumentException
	 *             if the label contains any "bad" characters, i.e., not
	 *             alphanumeric
	 */
	protected void setLabel(String label) {
		myLabel = label;
	}
	
	/**
	 * @return the probability for this transition
	 */
	public BigDecimal getProbability() {
		return myProbability;
	}
	
	/**
	 * sets the probability for this transition
	 * 
	 * this method is used when the probability of the transition is different from what is encoded
	 * encoded in the label e.g. using relative probabilities see 
	 * weightTransitionProbabilities(ProbState state) in the ProbAutomaton class
	 */
	protected void setProbability(BigDecimal prob) {
		myProbability = prob.setScale(10, RoundingMode.HALF_EVEN);
	}
	
	/**
	 * Returns the description for this transition.
	 * 
	 * @return the description, in this case, simply the label
	 */
	public String getDescription() {
		return getLabel();
	}

	/**
	 * Returns a string representation of this object. This is the same as the
	 * string representation for a regular transition object, with the label
	 * tacked on.
	 * 
	 * @see automata.Transition#toString
	 * @return a string representation of this object
	 */
	public String toString() {
		return super.toString() + ": \"" + getLabel() + "\"";
	}

	/**
	 * Returns if this transition equals another object.
	 * 
	 * @param object
	 *            the object to test against
	 * @return <CODE>true</CODE> if the two are equal, <CODE>false</CODE>
	 *         otherwise
	 */
	public boolean equals(Object object) {
		try {
			ProbTransition t = (ProbTransition) object;
			return super.equals(t) && myLabel.equals(t.myLabel) && myProbability.equals(t.myProbability);
		} catch (ClassCastException e) {
			return false;
		}
	}

	/**
	 * Returns the hash code for this transition.
	 * 
	 * @return the hash code for this transition
	 */
	public int hashCode() {
		return super.hashCode() ^ myLabel.hashCode();
	}

	protected ProbState from,to;
	
	/**
	 * The label for this transition, which is intended to be used as the
	 * precondition that a string must satisfy before the machine continues.
	 */
	protected String myLabel = "";
	
	/**
	 * The probability for this transition
	 */
	protected BigDecimal myProbability = new BigDecimal(0);
	
}

