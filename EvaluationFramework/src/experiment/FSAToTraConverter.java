package experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import automata.State;
import automata.Transition;
import automata.fsa.FiniteStateAutomaton;

public class FSAToTraConverter {

	public static void fsaToTra(FiniteStateAutomaton fsa, String pathToOutputDir)
			throws FileNotFoundException{
		
		PrintWriter pw = new PrintWriter(new File(pathToOutputDir + "out.tra"));
		StringBuilder fsaData = new StringBuilder();
		
		int numberOfStates = fsa.getStates().length;
		int numberOfTransitions = fsa.getTransitions().length;
		
		fsaData.append(numberOfStates);
		fsaData.append(" ");
		fsaData.append(numberOfTransitions);
		fsaData.append("\n");
		
		for(State state: fsa.getStates()){
			for(Transition transition: fsa.getTransitionsFromState(state)){
				fsaData.append(transition.getFromState().getID());
				fsaData.append(" ");
				fsaData.append(transition.getToState().getID());
				fsaData.append(" ");
				fsaData.append(1); //probability is 1 because its not a PA 
				fsaData.append("\n");
			}
		}
		
		pw.write(fsaData.toString());
		pw.close();
		System.out.println("Done. Results are in the file " + pathToOutputDir + "out.tra");
		
	}
}
