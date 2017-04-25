package test;

import java.util.Random;

import org.eclipse.emf.common.util.EList;

import transitiongraph.State;
import transitiongraph.Transition;
import transitiongraph.TransitionGraph;

public class Tester extends TestFramework{
	
	public String FSAToRegex(TransitionGraph fsa){
		return "";
	}
	
	public String DTMCToSRE(TransitionGraph dtmc){
		return "";
	}
	
	public String getAcceptedWordFromAutomaton(TransitionGraph tg, int maxLength){
		String word = "";
		String longestAcceptingWord = null;
		EList<State> states = tg.getStates();
		EList<Transition> transitions;
		Random rand = new Random();
		Transition nextTransition;
		int count = 0;
		State currentState = null;
		
		for(State s: states){
			if(s.isIsInitial()){
				currentState = s;
				break;
			}
		}
		
		while(count < maxLength){
			transitions = currentState.getOutgoing();
			nextTransition = transitions.get(rand.nextInt(transitions.size()));
			word = word + nextTransition.getLabel();
			currentState = nextTransition.getTarget();
			
			if(currentState.isIsFinal()) longestAcceptingWord = word;
			
			count++;
		}
		
		return longestAcceptingWord;
	}
	
	public String getNotAcceptedWordFromAutomaton(TransitionGraph tg, int maxLength){
		String word = "";
		String longestNotAcceptingWord = null;
		EList<State> states = tg.getStates();
		EList<Transition> transitions;
		Random rand = new Random();
		Transition nextTransition;
		int count = 0;
		State currentState = null;
		
		for(State s: states){
			if(s.isIsInitial()){
				currentState = s;
				break;
			}
		}
		
		while(count < maxLength){
			transitions = currentState.getOutgoing();
			nextTransition = transitions.get(rand.nextInt(transitions.size()));
			word = word + nextTransition.getLabel();
			currentState = nextTransition.getTarget();
			
			if(!currentState.isIsFinal()) longestNotAcceptingWord = word;
			
			count++;
		}
		
		return longestNotAcceptingWord;
	}
	
}
