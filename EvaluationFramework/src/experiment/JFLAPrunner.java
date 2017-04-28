package experiment;

import java.io.IOException;
import java.math.BigDecimal;

import automata.State;
import automata.fsa.FSATransition;
import automata.fsa.FiniteStateAutomaton;
import probautomata.ProbState;
import probautomata.ProbTransition;
import probautomata.dtmc.DiscreteTimeMarkovChain;
import test.Tester;



public class JFLAPrunner {

	public static void main(String[] args) throws IOException {
	
		FiniteStateAutomaton fsa = new FiniteStateAutomaton();
		State s0 = fsa.createStateWithId(0);
		State s1 = fsa.createStateWithId(1);
		State s2 = fsa.createStateWithId(2);
		State s3 = fsa.createStateWithId(3);
		
		fsa.setInitialState(s0);
		fsa.addFinalState(s3);
		
		FSATransition tr0 = new FSATransition(s0,s1,"a");
		FSATransition tr1 = new FSATransition(s0,s2,"b");
		FSATransition tr2 = new FSATransition(s2,s2,"a");
		FSATransition tr3 = new FSATransition(s2,s3,"b");
		FSATransition tr4 = new FSATransition(s1,s0,"a");
		FSATransition tr5 = new FSATransition(s1,s3,"b");
		
		fsa.addTransition(tr0);
		fsa.addTransition(tr1);
		fsa.addTransition(tr2);
		fsa.addTransition(tr3);
		fsa.addTransition(tr4);
		fsa.addTransition(tr5);
		
		System.out.println(FSAToRegex.fsaToRegex(fsa));
		
		/***********************************************************/
		
		DiscreteTimeMarkovChain dtmc = new DiscreteTimeMarkovChain();
		ProbState ps0 = dtmc.createStateWithId(0);
		ProbState ps1 = dtmc.createStateWithId(1);
		ProbState ps2 = dtmc.createStateWithId(2);
		ProbState ps3 = dtmc.createStateWithId(3);
		
		dtmc.setInitialState(ps0);
		dtmc.addFinalState(ps3);
		
		ProbTransition ptr0 = new ProbTransition(ps0,ps0,"a", new BigDecimal(0.7));
		ProbTransition ptr1 = new ProbTransition(ps0,ps1,"b", new BigDecimal(0.2));
		ProbTransition ptr2 = new ProbTransition(ps1,ps1,"b", new BigDecimal(0.3));
		ProbTransition ptr3 = new ProbTransition(ps1,ps0,"a", new BigDecimal(0.5));
		ProbTransition ptr4 = new ProbTransition(ps0,ps2,"c", new BigDecimal(0.1));
		ProbTransition ptr5 = new ProbTransition(ps2,ps2,"a", new BigDecimal(0.4));
		ProbTransition ptr6 = new ProbTransition(ps2,ps1,"b", new BigDecimal(0.2));
		ProbTransition ptr7 = new ProbTransition(ps2,ps3,"c", new BigDecimal(0.4));
		ProbTransition ptr8 = new ProbTransition(ps1,ps3,"c", new BigDecimal(0.2));
		
		dtmc.addTransition(ptr0);
		dtmc.addTransition(ptr1);
		dtmc.addTransition(ptr2);
		dtmc.addTransition(ptr3);
		dtmc.addTransition(ptr4);
		dtmc.addTransition(ptr5);
		dtmc.addTransition(ptr6);
		dtmc.addTransition(ptr7);
		dtmc.addTransition(ptr8);
		
		System.out.println(DTMCToSRE.dtmcToSRE(dtmc));
		

		Tester tester = new Tester();
		tester.testFSAToRegexAllModels();
		tester.testDTMCToSREAllModels();
	}
	
}
