package experiment;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import transitiongraph.State;
import transitiongraph.Transition;
import transitiongraph.TransitionGraph;
import transitiongraph.TransitiongraphFactory;
import transitiongraph.TransitiongraphPackage;

public class TransitionGraphUniformer {
	
	public static void uniformTG(TransitionGraph tg){
		
		if(getNumberOfInitialStates(tg) > 1){
			getSingleInitialState(tg);
		}
		if(getNumberOfFinalStates(tg) > 1){
			getSingleFinalState(tg);
		}
	}
	
	private static void getSingleInitialState(TransitionGraph tg){
		
		EList<State> states = tg.getStates();
		EList<Transition> transitions = tg.getTransitions();
		State newInitial = TransitiongraphFactory.eINSTANCE.createState();
		
		newInitial.setId(getHighestId(tg) + 1);
		newInitial.setIsFinal(false);
		newInitial.setIsInitial(true);
		for(State state: states){
			if(state.isIsInitial()){
				state.setIsInitial(false);
				Transition t = TransitiongraphFactory.eINSTANCE.createTransition();
				t.setLabel("");
				t.setProbability(1.0d);
				t.setSource(newInitial);
				t.setTarget(state);
				transitions.add(t);
			}
		}
		states.add(newInitial);
	}
	
	private static void getSingleFinalState(TransitionGraph tg){
		
		EList<State> states = tg.getStates();
		State newFinal = TransitiongraphFactory.eINSTANCE.createState();
		EList<Transition> transitions = tg.getTransitions();
		
		newFinal.setId(getHighestId(tg) + 1);
		newFinal.setIsFinal(true);
		newFinal.setIsInitial(false);
		for(State state: states){
			if(state.isIsFinal()){
				state.setIsFinal(false);
				Transition t = TransitiongraphFactory.eINSTANCE.createTransition();
				t.setLabel("");
				t.setProbability(1.0d);
				t.setSource(state);
				t.setTarget(newFinal);
				transitions.add(t);
			}
		}
		states.add(newFinal);
	}
	
	/*
	 * get the highest id off all states
	 */
	private static int getHighestId(TransitionGraph tg){
		
		int maxId=0;
		EList<State> states = tg.getStates();
		for(State state: states){
			if(state.getId() > maxId){
				maxId = state.getId();
			}
		}
		return maxId;
	}
	
	private static int getNumberOfInitialStates(TransitionGraph tg){
		
		int numberOfInitialStates = 0;
		EList<State> states = tg.getStates();
		for(State state: states){
			if(state.isIsInitial()){
				numberOfInitialStates++;
			}
		}
		return numberOfInitialStates;
	}
	
	private static int getNumberOfFinalStates(TransitionGraph tg){
		
		int numberOfFinalStates = 0;
		EList<State> states = tg.getStates();
		for(State state: states){
			if(state.isIsFinal()){
				numberOfFinalStates++;
			}
		}
		return numberOfFinalStates;
	}
	
	public static TransitionGraph getTransitionGraphFromXMI(String uri){
		
		//String uri = "../ExperimentalData/testdata/emf/task-extension1/" + modelName + ".xmi";
		
		TransitiongraphPackage.eINSTANCE.eClass();
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("xmi", new XMIResourceFactoryImpl());
        
        ResourceSet resSet = new ResourceSetImpl();
        
        Resource resource = resSet.getResource(URI
                .createURI(uri), true);
        
        TransitionGraph tg = (TransitionGraph) resource.getContents().get(0);
        
        return tg;
	}
	
	/*
	 * store transitiongraph in an xmi file 
	 */
	public static void TGtoXMI(TransitionGraph tg, String outputDirectory){
		
		ResourceSet resSet = new ResourceSetImpl();
		Resource resource = resSet.createResource(URI.createURI(outputDirectory + File.separator
				+ "out.xmi"));
		resource.getContents().add(tg);
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
