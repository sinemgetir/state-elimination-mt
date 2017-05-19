package tra2emf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import transitiongraph.State;
import transitiongraph.Transition;
import transitiongraph.TransitionGraph;
import transitiongraph.TransitiongraphFactory;

public class Tra2EmfConverter {

	private int[] initialIds;
	private int[] finalIds;

	private Map<Integer, State> id2state;
	private int stateCounter;

	private TransitionGraph tg;

	public TransitionGraph convert(File traFile, String kind, boolean uniform) {

		this.id2state = new HashMap<Integer, State>();
		this.stateCounter = 0;

		this.tg = TransitiongraphFactory.eINSTANCE.createTransitionGraph();

		// creates transitions (lazyly creates states)
		try {
			int lineCounter = 0;

			FileReader fileReader = new FileReader(traFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.trim().equals("")){
					break;
				}
				
				lineCounter++;

				if (uniform) {
					// Ignore first line
					if (lineCounter == 1) {
						continue;
					}
				} else {
					// First two lines give us start and final state IDs
					if (lineCounter == 1) {
						this.initialIds = getIds(line);
						continue;
					}
					if (lineCounter == 2) {
						this.finalIds = getIds(line);
						continue;
					}
				}

				int srcId = getSrcId(line);
				int tgtId = getTgtId(line);
				String label = getLabel(line);
				double probability = getProbability(line);

				Transition t = TransitiongraphFactory.eINSTANCE.createTransition();
				t.setSource(getState(srcId));
				t.setTarget(getState(tgtId));
				t.setLabel(label);
				if (kind.equals(Tra2Emf.FSA)) {
					t.setProbability(1.0d);
				} else {
					t.setProbability(probability);
				}

				tg.getTransitions().add(t);
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// set start and initial states
		if (uniform) {
			initialIds = new int[] { 0 };
			finalIds = new int[] { stateCounter - 1 };
		}
		for (int i = 0; i < initialIds.length; i++) {
			id2state.get(initialIds[i]).setIsInitial(true);
		}
		for (int i = 0; i < finalIds.length; i++) {
			id2state.get(finalIds[i]).setIsFinal(true);
		}

		return tg;
	}

	private State getState(int id) {
		if (id2state.containsKey(id)) {
			return id2state.get(id);
		}

		State s = TransitiongraphFactory.eINSTANCE.createState();
		s.setId(id);
		s.setIsInitial(false);
		s.setIsFinal(false);

		this.tg.getStates().add(s);
		this.id2state.put(id, s);
		this.stateCounter++;

		return s;
	}

	private static int[] getIds(String line) {
		String[] fragments = line.split(" ");
		int[] res = new int[fragments.length];
		for (int i = 0; i < fragments.length; i++) {
			res[i] = Integer.parseInt(fragments[i]);
		}

		return res;
	}

	private static int getSrcId(String line) {
		String[] fragments = line.split(" ");
		return Integer.parseInt(fragments[0]);
	}

	private static int getTgtId(String line) {
		String[] fragments = line.split(" ");
		return Integer.parseInt(fragments[1]);
	}

	private static double getProbability(String line) {
		String[] fragments = line.split(" ");
		return Double.parseDouble(fragments[2]);
	}

	private static String getLabel(String line) {
		String[] fragments = line.split(" ");
		return "s" + fragments[1];
	}

}
