package experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import automata.fsa.FSAToRegularExpressionConverter;
import automata.fsa.FSATransition;
import automata.fsa.FiniteStateAutomaton;

public class FSAToRegex {

	public static String fsaToRegex(FiniteStateAutomaton fsa){
		
		FSAToRegularExpressionConverter.convertToSimpleAutomaton(fsa);
		FSAToRegularExpressionConverter.convertToGTG(fsa);
		String computedRE = FSAToRegularExpressionConverter.getExpressionFromGTG(fsa);
		return computedRE;
	}
	
	public static FiniteStateAutomaton constructFSAFromFile(File f){
		FiniteStateAutomaton fsa = new FiniteStateAutomaton();
		int fromStateId, toStateId, numberOfStates;
		String expr;
		String line;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));

			line = reader.readLine();
			numberOfStates = Integer.parseInt(line.split(" ")[0]);
			
			for(int i=0; i<numberOfStates; i++){
				fsa.createStateWithId(i);
			}
			fsa.setInitialState(fsa.getStateWithID(0));
			fsa.addFinalState(fsa.getStateWithID(numberOfStates-1));
			
			while((line = reader.readLine()) != null){
				expr = "s" + line.split(" ")[1];
				fromStateId = Integer.parseInt(line.split(" ")[0]);
				toStateId = Integer.parseInt(line.split(" ")[1]);
				
				fsa.addTransition(new FSATransition(fsa.getStateWithID
						(fromStateId), fsa.getStateWithID(toStateId), expr));
			}

			reader.close();
		} catch (Exception e){
			fsa = null;
		}
		return fsa;
	}
	
}
