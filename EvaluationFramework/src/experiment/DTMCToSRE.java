package experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;

import probautomata.ProbState;
import probautomata.ProbTransition;
import probautomata.dtmc.DTMCToStochasticRegularExpressionConverter;
import probautomata.dtmc.DiscreteTimeMarkovChain;

public class DTMCToSRE {
	
	public static String dtmcToSRE(DiscreteTimeMarkovChain dtmc){
		
		DTMCToStochasticRegularExpressionConverter.convertToSimpleAutomaton(dtmc);
		for(ProbState state: dtmc.getStates()){
			//don't weight the transitions from the initial state 
			if(state.equals(dtmc.getInitialState())) continue;
			dtmc.weightTransitionProbabilities(state);
		}
		DTMCToStochasticRegularExpressionConverter.convertToGTG(dtmc);
		
		String computedRE = DTMCToStochasticRegularExpressionConverter.getExpressionFromGTG(dtmc);
		return computedRE;
	}
	
	public static DiscreteTimeMarkovChain constructDTMCFromFile(File f){
		DiscreteTimeMarkovChain dtmc = new DiscreteTimeMarkovChain();
		int fromStateId, toStateId, numberOfStates;
		BigDecimal bd;
		double temp;
		String expr;
		String line;
		try{
			BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));

			line = reader.readLine();
			numberOfStates = Integer.parseInt(line.split(" ")[0]);
			
			if(numberOfStates > 500){
				reader.close();
				throw new Exception();
			}
			
			for(int i=0; i<numberOfStates; i++){
				dtmc.createStateWithId(i);
			}
			dtmc.setInitialState(dtmc.getStateWithID(0));
			dtmc.addFinalState(dtmc.getStateWithID(numberOfStates-1));
			
			while((line = reader.readLine()) != null){
				temp = Double.parseDouble(line.split(" ")[2]);
				bd = BigDecimal.valueOf(temp);
				expr = "s" + line.split(" ")[1];
				fromStateId = Integer.parseInt(line.split(" ")[0]);
				toStateId = Integer.parseInt(line.split(" ")[1]);
				
				dtmc.addTransition(new ProbTransition(dtmc.getStateWithID
						(fromStateId), dtmc.getStateWithID(toStateId), expr, bd));
			}

			reader.close();
		} catch (Exception e){
			dtmc = null;
		}
		return dtmc;
		
	}
	
}
