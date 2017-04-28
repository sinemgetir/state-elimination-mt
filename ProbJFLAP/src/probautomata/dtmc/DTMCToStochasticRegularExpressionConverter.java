package probautomata.dtmc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import probautomata.ProbAutomaton;
import probautomata.ProbState;
import probautomata.ProbTransition;
import regular.Discretizer;

/**
 * The DTMC to stochastic regular expression converter
 */

public class DTMCToStochasticRegularExpressionConverter {
	/**
	 * Creates an instance of <CODE>FSAToRegularExpressionConverter</CODE>.
	 */
	private DTMCToStochasticRegularExpressionConverter() {

	}

	/**
	 * Returns true if <CODE>automaton</CODE> can be converted to a regular
	 * expression (i.e. it has a unique initial and final state and it is a
	 * finite state automaton, and the initial state is not the final state).
	 * 
	 * @param automaton
	 *            the automaton to convert
	 * @return true if <CODE>automaton</CODE> can be converted to a regular
	 *         expression.
	 */
public static boolean isConvertable(ProbAutomaton automaton) {
		if (!(automaton instanceof DiscreteTimeMarkovChain))
			return false;
		ProbState[] finalStates = automaton.getFinalStates();
		if (finalStates.length != 1) {
			return false;
		}

		ProbState initialState = automaton.getInitialState();
		if (finalStates[0] == initialState) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true if there are more removable states in <CODE>automaton</CODE>.
	 * 
	 * @param automaton
	 *            the automaton
	 * @return true if there are more removable states in <CODE>automaton</CODE>.
	 */
public static boolean areRemovableStates(ProbAutomaton automaton) {
		ProbState[] states = automaton.getStates();
		for (int k = 0; k < states.length; k++) {
			if (isRemovable(states[k], automaton))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if <CODE>state</CODE> is a removable state (i.e. it is not
	 * the unique initial or final state).
	 * 
	 * @param state
	 *            the state to remove.
	 * @param automaton
	 *            the automaton.
	 * @return true if <CODE>state</CODE> is a removable state
	 */
public static boolean isRemovable(ProbState state, ProbAutomaton automaton) {
		ProbState[] finalStates = automaton.getFinalStates();
		ProbState finalState = finalStates[0];
		ProbState initialState = automaton.getInitialState();
		if (state == finalState || state == initialState)
			return false;
		return true;
	}

	/**
	 * Returns a Transition object that represents the transition between the
	 * states with ID's <CODE>p</CODE> and <CODE>q</CODE>, with <CODE>expression</CODE>
	 * as the transition label.
	 * 
	 * @param p
	 *            the ID of the from state.
	 * @param q
	 *            the ID of the to state.
	 * @param expression
	 *            the expression
	 * @param automaton
	 *            the automaton
	 * @return a Transition object that represents the transition between the
	 *         states with ID's <CODE>p</CODE> and <CODE>q</CODE>, with
	 *         <CODE>expression</CODE> as the transition label.
	 */
public static ProbTransition getTransitionForExpression(int p, int q,
			String expression, BigDecimal probability, ProbAutomaton automaton) {
		ProbState fromState = automaton.getStateWithID(p);
		ProbState toState = automaton.getStateWithID(q);
		ProbTransition transition = new ProbTransition(fromState, toState,
				expression, probability);
		return transition;
	}

	/**
	 * Returns the expression on the transition between <CODE>fromState</CODE>
	 * and <CODE>toState</CODE> in <CODE>automaton</CODE>.
	 * 
	 * @param fromState
	 *            the from state
	 * @param toState
	 *            the to state
	 * @param automaton
	 *            the automaton
	 * @return the expression on the transition between <CODE>fromState</CODE>
	 *         and <CODE>toState</CODE> in <CODE>automaton</CODE>.
	 */
public static String getExpressionBetweenStates(ProbState fromState, ProbState toState,
			ProbAutomaton automaton) {
		ProbTransition[] transitions = automaton.getTransitionsFromStateToState(
				fromState, toState);
		ProbTransition trans = (ProbTransition) transitions[0];
		return trans.getLabel();
	}

public static BigDecimal getProbabilityBetweenStates(ProbState fromState, ProbState toState,
		ProbAutomaton automaton) {
	ProbTransition[] transitions = automaton.getTransitionsFromStateToState(
			fromState, toState);
	ProbTransition trans = (ProbTransition) transitions[0];
	return trans.getProbability();
}

	/**
	 * Returns the expression obtained from evaluating the following equation:
	 * r(pq) = r(pq) + r(pk)r(kk)*r(kq), where p, q, and k represent the IDs of
	 * states in <CODE>automaton</CODE>.
	 * 
	 * @param p
	 *            the from state
	 * @param q
	 *            the to state
	 * @param k
	 *            the state being removed.
	 * @param automaton
	 *            the automaton.
	 * @return the expression obtained from evaluating the following equation:
	 *         r(pq) = r(pq) + r(pk)r(kk)*r(kq), where p, q, and k represent the
	 *         IDs of states in <CODE>automaton</CODE>.
	 */
public static String getExpression(int p, int q, int k, ProbAutomaton automaton) {
		ProbState fromState = automaton.getStateWithID(p);
		ProbState toState = automaton.getStateWithID(q);
		ProbState removeState = automaton.getStateWithID(k);

		String pq = getExpressionBetweenStates(fromState, toState, automaton);	
		String pk = getExpressionBetweenStates(fromState, removeState, automaton);
		String kk = getExpressionBetweenStates(removeState, removeState, automaton);
		String kq = getExpressionBetweenStates(removeState, toState, automaton);
		
		BigDecimal pqProb = getProbabilityBetweenStates(fromState, toState, automaton);
		BigDecimal pkProb = getProbabilityBetweenStates(fromState, removeState, automaton);
		BigDecimal kkProb = getProbabilityBetweenStates(removeState, removeState, automaton);
		BigDecimal kqProb = getProbabilityBetweenStates(removeState, toState, automaton);

		String temp1 = star(kk, kkProb);
		String temp2 = concatenate(temp1, kq);
		String temp3 = concatenate(pk, temp2);
		BigDecimal temp3Prob = pkProb.multiply(kqProb);
		String label = or(pq, pqProb, temp3, temp3Prob);
		return label;
	}

public static BigDecimal getProbability(int p, int q, int k, ProbAutomaton automaton) {
	ProbState fromState = automaton.getStateWithID(p);
	ProbState toState = automaton.getStateWithID(q);
	ProbState removeState = automaton.getStateWithID(k);
	
	BigDecimal pqProb = getProbabilityBetweenStates(fromState, toState, automaton);
	BigDecimal pkProb = getProbabilityBetweenStates(fromState, removeState, automaton);
	BigDecimal kkProb = getProbabilityBetweenStates(removeState, removeState, automaton);
	BigDecimal kqProb = getProbabilityBetweenStates(removeState, toState, automaton);

	BigDecimal temp3Prob = pkProb.multiply(kqProb);
	BigDecimal prob = temp3Prob.add(pqProb);
	
	return prob;
}

	/**
	 * Returns the expression that represents <CODE>r1</CODE> concatenated
	 * with <CODE>r2</CODE>. (essentialy just the two strings concatenated).
	 * 
	 * @param r1
	 *            the first part of the expression.
	 * @param r2
	 *            the second part of the expression.
	 * @return the expression that represents <CODE>r1</CODE> concatenated
	 *         with <CODE>r2</CODE>. (essentialy just the two strings
	 *         concatenated).
	 */
public static String concatenate(String r1, String r2) {
		if (r1.equals(EMPTY) || r2.equals(EMPTY))
			return EMPTY;
		else if (r1.equals(LAMBDA))
			return r2;
		else if (r2.equals(LAMBDA))
			return r1;
		
		if (Discretizer.or(r1).length > 1)
			r1 = addParen(r1);
		if (Discretizer.or(r2).length > 1)
			r2 = addParen(r2);
			
		return r1 + CONCAT + r2;
	}

	/**
	 * Returns the expression that represents <CODE>r1</CODE> kleene-starred.
	 * 
	 * @param r1
	 *            the expression being kleene-starred.
	 * @param probability
	 *            the probability for the kleene-star
	 * @return the expression that represents <CODE>r1</CODE> kleene-starred.
	 */
public static String star(String r1, BigDecimal r1Prob) {
		if (r1.equals(EMPTY) || r1.equals(LAMBDA))
			return LAMBDA;

		if (Discretizer.or(r1).length > 1 || Discretizer.cat(r1).length > 1) {
			r1 = addParen(r1);
		} else {
			if (r1.matches(".+\\*...."))
				return r1;
		}
		
		return r1 + 
				KLEENE_STAR + r1Prob.setScale(2, RoundingMode.HALF_EVEN);
	}

	/**
	 * Returns the string that represents <CODE>r1</CODE> or'ed with <CODE>r2</CODE>.
	 * 
	 * @param r1
	 *            the first expression
	 * @param prob1
	 *            the probability for the first expression
	 * @param r2
	 *            the second expression
	 * @param prob2
	 *            the probability for the second expression
	 * @return the string that represents <CODE>r1</CODE> or'ed with <CODE>r2</CODE>.
	 */
public static String or(String r1, BigDecimal r1Prob, String r2, BigDecimal r2Prob) {
		if (r1.equals(EMPTY))
			return r2;
		if (r2.equals(EMPTY))
			return r1;
		if (r1.equals(LAMBDA) && r2.equals(LAMBDA))
			return LAMBDA;
		
		r1 = cutProbability(r1);
		r2 = cutProbability(r2);
		
		if(r1.length() > 1 && Discretizer.or(r1).length == 1) r1 = addParen(r1);
		if(r2.length() > 1 && Discretizer.or(r2).length == 1) r2 = addParen(r2);
		
		String r1Weight = LEFT_BRACKET + r1Prob.multiply(new BigDecimal(1000))
			.setScale(0, RoundingMode.HALF_EVEN).toString() + RIGHT_BRACKET;
		String r2Weight = LEFT_BRACKET + r2Prob.multiply(new BigDecimal(1000))
			.setScale(0, RoundingMode.HALF_EVEN).toString() + RIGHT_BRACKET;
		
		if(Discretizer.or(r1).length > 1) r1Weight = "";
		if(Discretizer.or(r2).length > 1) r2Weight = "";
		
		return r1 + r1Weight  + OR + r2 + r2Weight;
	}

	/**
	 * Completely reconstructs <CODE>automaton</CODE>, removing all
	 * transitions and <CODE>state</CODE> and adding all transitions in <CODE>transitions</CODE>.
	 * 
	 * @param state
	 *            the state to remove.
	 * @param transitions
	 *            the transitions returned for removing <CODE>state</CODE>.
	 * @param automaton
	 *            the automaton.
	 */
public static void removeState(ProbState state, ProbTransition[] transitions,
			ProbAutomaton automaton) {
		ProbTransition[] oldTransitions = automaton.getTransitions();
		for (int k = 0; k < oldTransitions.length; k++) {
			automaton.removeTransition(oldTransitions[k]);
		}

		automaton.removeState(state);

		for (int i = 0; i < transitions.length; i++) {
			automaton.addTransition(transitions[i]);
		}
	}

	/**
	 * Returns a list of all transitions for <CODE>automaton</CODE> created by
	 * removing <CODE>state</CODE>.
	 * 
	 * @param state
	 *            the state to remove.
	 * @param automaton
	 *            the automaton.
	 * @return a list of all transitions for <CODE>automaton</CODE> created by
	 *         removing <CODE>state</CODE>.
	 */
public static ProbTransition[] getTransitionsForRemoveState(ProbState state,
			ProbAutomaton automaton) {
		if (!isRemovable(state, automaton))
			return null;
		ArrayList list = new ArrayList();
		int k = state.getID();
		ProbState[] states = automaton.getStates();
		for (int i = 0; i < states.length; i++) {
			int p = states[i].getID();
			if (p != k) {
				for (int j = 0; j < states.length; j++) {
					int q = states[j].getID();
					if (q != k) {
						String exp = getExpression(p, q, k, automaton);
						BigDecimal prob = getProbability(p, q, k, automaton);
						list.add(getTransitionForExpression(p, q, exp, prob,
								automaton));
					}
				}
			}
		}
		return (ProbTransition[]) list.toArray(new ProbTransition[0]);
	}

	/**
	 * Adds a new transition to <CODE>automaton</CODE> between <CODE>fromState</CODE>
	 * and </CODE>toState</CODE> on the symbol for the empty set.
	 * 
	 * @param fromState
	 *            the from state for the transition
	 * @param toState
	 *            the to state for the transition
	 * @param automaton
	 *            the automaton.
	 * @return the <CODE>FSATransition</CODE> that was created
	 */
public static ProbTransition addTransitionOnEmptySet(ProbState fromState,
			ProbState toState, ProbAutomaton automaton) {
		ProbTransition t = new ProbTransition(fromState, toState, EMPTY, new BigDecimal(0));
		automaton.addTransition(t);
		return t;
	}

	/**
	 * Removes all transitions in <CODE>transitions</CODE> from <CODE>automaton</CODE>,
	 * replacing them with a single transition in <CODE>automaton</CODE>
	 * between <CODE>fromState</CODE> and <CODE>toState</CODE> labeled with
	 * a regular expression that represents the labels of all the removed
	 * transitions Or'ed together (e.g. a + (b*c) + (d+e)).
	 * 
	 * @param fromState
	 *            the from state for <CODE>transitions</CODE> and for the
	 *            newly created transition.
	 * @param toState
	 *            the to state for <CODE>transitions</CODE> and for the newly
	 *            created transition.
	 * @param transitions
	 *            the transitions being removed and combined into a single
	 *            transition
	 * @param automaton
	 *            the automaton
	 * @return the transition that replaced all of these
	 */
/*public static ProbTransition combineToSingleTransition(ProbState fromState,
			ProbState toState, ProbTransition[] transitions, ProbAutomaton automaton) {
		String label = ((ProbTransition) transitions[0]).getDescription();
		automaton.removeTransition(transitions[0]);
		for (int i = 1; i < transitions.length; i++) {
			label = or(label, ((ProbTransition) transitions[i]).getDescription());
			automaton.removeTransition(transitions[i]);
		}
		ProbTransition t = new ProbTransition(fromState, toState, label);
		automaton.addTransition(t);
		return t;
	}*/

	/**
	 * Makes all final states in <CODE>automaton</CODE> non-final.
	 * In a dtmc all final states have a single transition to themselves with
	 * probability 1, so we change their destination to the new final state
	 * 
	 * @param automaton
	 *            the automaton
	 */
public static void getSingleFinalState(ProbAutomaton automaton) {
		
		ProbState newFinalState = automaton.createState();
		ProbTransition transition;
		
		for(ProbState finalState: automaton.getFinalStates()){
			transition = automaton.getTransitionsFromState(finalState)[0];
			transition.setToState(newFinalState);
			automaton.removeFinalState(finalState);
		}
		automaton.addFinalState(newFinalState);
		automaton.addTransition(new ProbTransition(newFinalState,newFinalState,LAMBDA,new BigDecimal(1)));
	}

	/**
	 * Converts <CODE>automaton</CODE> to an equivalent automaton with a
	 * single transition between all combinations of states. (if there are
	 * currently more than one transition between two states, it combines them
	 * into a single transition by or'ing the labels of all the transitions. If
	 * there is no transition between two states, it creates a transition and
	 * labels it with the empty set character (EMPTY).
	 * 
	 * @param automaton
	 *            the automaton.
	 */
public static void convertToSimpleAutomaton(ProbAutomaton automaton) {
		if (!isConvertable(automaton))
			getSingleFinalState(automaton);
		ProbState[] states = automaton.getStates();
		for (int k = 0; k < states.length; k++) {
			for (int j = 0; j < states.length; j++) {
				ProbTransition[] transitions = automaton
						.getTransitionsFromStateToState(states[k], states[j]);
				if (transitions.length == 0) {
					addTransitionOnEmptySet(states[k], states[j], automaton);
				}
				/*if (transitions.length > 1) {
					combineToSingleTransition(states[k], states[j],
							transitions, automaton);
				}*/
			}
		}
	}

	/**
	 * Converts <CODE>automaton</CODE> into a generalized transition graph
	 * with only two states, a unique initial state, and a unique final state.
	 * 
	 * @param automaton
	 *            the automaton.
	 */
public static void convertToGTG(ProbAutomaton automaton) {
		ProbState[] finalStates = automaton.getFinalStates();
		ProbState finalState = finalStates[0];
		ProbState initialState = automaton.getInitialState();
		ProbState[] states = automaton.getStates();
		for (int k = 0; k < states.length; k++) {
			ProbState state = states[k];
			if (state != finalState && state != initialState) {
				ProbTransition[] transitions = getTransitionsForRemoveState(state,
						automaton);
				removeState(state, transitions, automaton);
			}
		}
	}

	/**
	 * Returns a string of <CODE>word</CODE> surrounded by parentheses. i.e. (<word>),
	 * unless it is unnecessary.
	 * 
	 * @param word
	 *            the word.
	 * @return a string of <CODE>word</CODE> surrounded by parentheses.
	 */
public static String addParen(String word) {
		return LEFT_PAREN + word + RIGHT_PAREN;
	}

	/**
	 * Returns a non-unicoded version of <CODE>word</CODE> for debug purposes.
	 * 
	 * @param word
	 *            the expression to output
	 * @return a non-unicoded version of <CODE>word</CODE> for debug purposes.
	 */
public static String getExp(String word) {
		if (word.equals(LAMBDA))
			return "lambda";
		else if (word.equals(EMPTY))
			return "empty";
		return word;
	}

	/**
	 * Returns the expression for the values of ii, ij, jj, and ji determined
	 * from the GTG with a unique initial and final state.
	 * 
	 * @param ii
	 *            the expression on the loop off the initial state
	 * @param ij
	 *            the expression on the arc from the initial state to the final
	 *            state.
	 * @param jj
	 *            the expression on the loop off the final state.
	 * @param ji
	 *            the expression on the arc from the final state to the initial
	 *            state.
	 * @return the expression for the values of ii, ij, jj, and ji determined
	 *         from the GTG with a unique initial and final state.
	 */
public static String getFinalExpression(String ii, BigDecimal iiProb, String ij, BigDecimal ijProb,
								String jj, BigDecimal jjProb, String ji, BigDecimal jiProb) {
		String temp = concatenate(star(ii,iiProb), concatenate(ij, concatenate(
				star(jj,jjProb), ji)));
		
		BigDecimal tempProb = ijProb.multiply(jiProb); 
		
		String temp2 = concatenate(star(ii,iiProb), concatenate(ij, star(jj,jjProb)));
		String expression = concatenate(star(temp,tempProb), temp2);
		return expression;
	}

	/**
	 * Returns the expression on the loop off the initial state of <CODE>automaton</CODE>.
	 * 
	 * @param automaton
	 *            a generalized transition graph with only two states, a unique
	 *            initial and final state.
	 * @return the expression on the loop off the initial state of <CODE>automaton</CODE>.
	 */
public static String getII(ProbAutomaton automaton) {
		ProbState initialState = automaton.getInitialState();
		return getExpressionBetweenStates(initialState, initialState, automaton);
	}

public static BigDecimal getIIProbability(ProbAutomaton automaton) {
	ProbState initialState = automaton.getInitialState();
	return getProbabilityBetweenStates(initialState, initialState, automaton);
}

	/**
	 * Returns the expression on the arc from the initial state to the final
	 * state of <CODE>automaton</CODE>.
	 * 
	 * @param automaton
	 *            a generalized transition graph with only two states, a unique
	 *            initial and final state.
	 * @return the expression on the arc from the initial state to the final
	 *         state of <CODE>automaton</CODE>.
	 */
public static String getIJ(ProbAutomaton automaton) {
		ProbState initialState = automaton.getInitialState();
		ProbState[] finalStates = automaton.getFinalStates();
		ProbState finalState = finalStates[0];
		return getExpressionBetweenStates(initialState, finalState, automaton);
	}

public static BigDecimal getIJProbability(ProbAutomaton automaton) {
	ProbState initialState = automaton.getInitialState();
	ProbState[] finalStates = automaton.getFinalStates();
	ProbState finalState = finalStates[0];
	return getProbabilityBetweenStates(initialState, finalState, automaton);
}

	/**
	 * Returns the expression on the loop off the final state of <CODE>automaton</CODE>
	 * 
	 * @param automaton
	 *            a generalized transition graph with only two states, a unique
	 *            initial and final state.
	 * @return the expression on the loop off the final state of <CODE>automaton</CODE>
	 */
public static String getJJ(ProbAutomaton automaton) {
		ProbState[] finalStates = automaton.getFinalStates();
		ProbState finalState = finalStates[0];
		return getExpressionBetweenStates(finalState, finalState, automaton);
	}

public static BigDecimal getJJProbability(ProbAutomaton automaton) {
	ProbState[] finalStates = automaton.getFinalStates();
	ProbState finalState = finalStates[0];
	return getProbabilityBetweenStates(finalState, finalState, automaton);
}

	/**
	 * Returns the expression on the arc from the final state to the initial
	 * state of <CODE>automaton</CODE>
	 * 
	 * @param automaton
	 *            a generalized transition graph with only two states, a unique
	 *            initial and final state.
	 * @return the expression on the arc from the final state to the initial
	 *         state of <CODE>automaton</CODE>
	 */
public static String getJI(ProbAutomaton automaton) {
		ProbState initialState = automaton.getInitialState();
		ProbState[] finalStates = automaton.getFinalStates();
		ProbState finalState = finalStates[0];
		return getExpressionBetweenStates(finalState, initialState, automaton);
	}

public static BigDecimal getJIProbability(ProbAutomaton automaton) {
	ProbState initialState = automaton.getInitialState();
	ProbState[] finalStates = automaton.getFinalStates();
	ProbState finalState = finalStates[0];
	return getProbabilityBetweenStates(finalState, initialState, automaton);
}

	/**
	 * Returns the expression for the generalized transition graph <CODE>automaton</CODE>
	 * with two states, a unique initial and unique final state. Evaluates to
	 * the expression r = (r(ii)*r(ij)r(jj)*r(ji))*r(ii)*r(ij)r(jj)*. where
	 * r(ij) represents the expression on the transition between state i (the
	 * initial state) and state j (the final state)
	 * 
	 * @param automaton
	 *            the generalized transition graph with two states (a unique
	 *            initial and final state).
	 * @return the expression for the generalized transition graph <CODE>automaton</CODE>
	 *         with two states, a unique initial and unique final state
	 */
public static String getExpressionFromGTG(ProbAutomaton automaton) {
		String ii = getII(automaton);
		BigDecimal iiProb = getIIProbability(automaton);
		String ij = getIJ(automaton);
		BigDecimal ijProb = getIJProbability(automaton);
		String jj = getJJ(automaton);
		BigDecimal jjProb = getJJProbability(automaton);
		String ji = getJI(automaton);
		BigDecimal jiProb = getJIProbability(automaton);

		return getFinalExpression(ii, iiProb, ij, ijProb, jj, jjProb, ji, jiProb);
	}

	/**
	 * Returns the regular expression that represents <CODE>automaton</CODE>.
	 * 
	 * @param automaton
	 *            the automaton
	 * @return the regular expression that represents <CODE>automaton</CODE>.
	 */
public static String convertToRegularExpression(ProbAutomaton probAutomaton) {
		if (!isConvertable(probAutomaton))
			return null;
		convertToGTG(probAutomaton);
		return getExpressionFromGTG(probAutomaton);
	}

	/**
	 * 
	 * @param expr
	 * 			the expression and its probability
	 * @return the expression without the probability
	 */			
public static String cutProbability(String probExpr){
	if(probExpr.length() > 6){
		if(probExpr.substring(probExpr.length()-7).matches("\\*\\[.\\...\\]"))
			return probExpr;
		else if(probExpr.substring(probExpr.length()-6).matches("\\[.\\...\\]"))
			return probExpr.substring(0, probExpr.length() - 6); //6 chars get cut: [x,yz] 
	}
	return probExpr;
}

	/**
	 * 
	 * @param probExpr
	 * @return true if the expression is a kleene expression
	 */
public static boolean isKleene(String probExpr){
	if(probExpr.length() > 6){
		if(probExpr.substring(probExpr.length()-7).matches("\\*\\[.\\...\\]"))
			return true;
	}
	return false;
}


	/* the string for the empty set. */
	public static final String EMPTY = "\u00F8";

	/* the string for lambda. */
	//public static final String LAMBDA_DISPLAY = Universe.curProfile.getEmptyString();

	public static final String LAMBDA = "";

	/* the string for the kleene star. */
	public static final String KLEENE_STAR = "*";

	/* the string for the or symbol. */
	public static final String OR = "+";
	
	/* the string for the concatenation */
	public static final String CONCAT = ":";
	
	/* dot */
	public static final String DOT = ".";
	
	/** right bracket */
	public static final String RIGHT_BRACKET = "]";
	
	/** left bracket */
	public static final String LEFT_BRACKET = "[";

	/** right paren. */
	public static final String RIGHT_PAREN = ")";

	/** left paren. */
	public static final String LEFT_PAREN = "(";
}