package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import transitiongraph.TransitionGraph;
import transitiongraph.TransitiongraphPackage;

public abstract class TestFramework {
	
	public static String pathToMainModels = "../ExperimentalData/testdata/emf/task-main";
	public static String pathToExtension2Models = "../ExperimentalData/testdata/emf/task-extension2";
	public static String pathToPositive = "testdata/acceptedWords";
	public static String pathToNegative = "testdata/notAcceptedWords";
	
	public void testFSAToRegexAllModels() throws FileNotFoundException{
		
		PrintWriter pw = new PrintWriter(new File("testresult/result_fsa2regex.csv"));
		File[] modelFiles = new File(pathToMainModels).listFiles();
		String result;
		StringBuilder resultData = new StringBuilder();
		String transformationType = "fsa2regex";
		
		resultData.append("modelname,");
		resultData.append("size of the regular expression,");
		resultData.append("time to transform (ms),");
		resultData.append("correctly accepted words,");
		resultData.append("correctly not accepted words");
		resultData.append("\n");
		
		for(File modelFile : modelFiles){
			result = testModelToRegex(modelFile.getName(), transformationType);
			resultData.append(result);
		}
		
		pw.write(resultData.toString());
        pw.close();
        System.out.println("done!");
	}
	
	public void testDTMCToSREAllModels() throws FileNotFoundException{
		
		PrintWriter pw = new PrintWriter(new File("testresult/result_dtmc2sre.csv"));
		File[] modelFiles = new File(pathToExtension2Models).listFiles();
		String result;
		StringBuilder resultData = new StringBuilder();
		String transformationType = "dtmc2sre";
		
		resultData.append("modelname,");
		resultData.append("size of the regular expression,");
		resultData.append("time to transform (ms),");
		resultData.append("correctly accepted words,");
		resultData.append("correctly not accepted words");
		resultData.append("\n");
		
		for(File modelFile : modelFiles){
			result = testModelToRegex(modelFile.getName(), transformationType);
			resultData.append(result);
		}
		
		pw.write(resultData.toString());
        pw.close();
        System.out.println("done!");
	}
	
	public String testModelToRegex(String modelFileName, String transformationType) throws FileNotFoundException{
		String modelName;
		String regex = "";
		int regexSize;
		String acceptedWordsFile, notAcceptedWordsFile;
		StringBuilder resultData = new StringBuilder();
		TransitionGraph tg;
		long timeToTransform;
		
		if(!modelFileName.matches(".+\\.xmi")) return "";
		
		modelName = modelFileName.replaceAll("\\.xmi", "");
		tg = getTransitionGraphFromXMI(modelName);
		
		timeToTransform = System.currentTimeMillis();
		if(transformationType.equals("fsa2regex")) regex = FSAToRegex(tg);
		else if(transformationType.equals("dtmc2sre")) regex = DTMCToSRE(tg);
		timeToTransform = System.currentTimeMillis() - timeToTransform;
		
		regexSize = getRegexSize(regex);
		
		acceptedWordsFile = pathToPositive + "/" + modelName + "-positive.data";
		notAcceptedWordsFile = pathToNegative + "/" + modelName + "-negative.data";
		
		resultData.append(modelName + ",");
		resultData.append(regexSize + ",");
		resultData.append(timeToTransform + ",");
		resultData.append(testWords(regex,acceptedWordsFile,true) + ",");
		resultData.append(testWords(regex,notAcceptedWordsFile,false));
		resultData.append("\n");
		
		System.out.println(modelName + " done.");
		return resultData.toString();
	}
	
	/*
	 * implement this method
	 */
	public abstract String FSAToRegex(TransitionGraph fsa);
	
	/*
	 * implement this method
	 */
	public abstract String DTMCToSRE(TransitionGraph dtmc);
	
	public TransitionGraph getTransitionGraphFromXMI(String modelName){
		
		TransitiongraphPackage.eINSTANCE.eClass();
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("xmi", new XMIResourceFactoryImpl());
        
        ResourceSet resSet = new ResourceSetImpl();
        
        Resource resource = resSet.getResource(URI
                .createURI("../ExperimentalData/testdata/emf/task-main/" + modelName + ".xmi"), true);
        
        TransitionGraph tg = (TransitionGraph) resource.getContents().get(0);
        
        return tg;
	}
	
	private int getRegexSize(String regex){
		int size = 0;
		
		for(int i=0;i<regex.length();i++){
			if(regex.charAt(i) == 's') size++;
		}
		
		return size;
	}
	
	private String formatRegex(String regex){
		regex = regex.replace('+', '|');	//java uses '|' as the or symbol
		regex = regex.replaceAll("\\[.*?\\]", "");	//remove probability
		regex = regex.replaceAll(":", "");	//':' is concatenation
		return regex;
	}
	
	private String testWords(String regex, String acceptedWordsFile, boolean accept){
		int totalWords = 0;
		int passed = 0;
		String word = "";
		BufferedReader reader;
		
		regex = formatRegex(regex);
		try{
			reader = new BufferedReader(new FileReader(acceptedWordsFile));
			while((word = reader.readLine()) != null){
				totalWords++;
				if(word.matches(regex) == accept) passed++;
			}
			reader.close();
		} catch (Exception e){
			
		}
		return passed + "/" + totalWords;
	}

}
