package tra2emf;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import transitiongraph.TransitionGraph;

public class Tra2Emf {

	public static final String PA = "PA";
	public static final String FSA = "FSA";

	static {
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("xmi", new XMIResourceFactoryImpl());
	}

	private static File[] getFilesFromFolder(String folderPath) {
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();

		return listOfFiles;
	}

	public static void main(String[] args) {
		String inputDirectory = null;
		String outputDirectory = null;
		String kind = null;
		boolean uniform = true;

		try {
			inputDirectory = args[0];
			outputDirectory = args[1];
			kind = args[2];
			uniform = Boolean.parseBoolean(args[3]);
		} catch (Exception e1) {
			System.err.println("Arguments should be: <input-dir> <output-dir> {\"FSA\"|\"PA\"} {\"true\"|\"false\"}}");
			e1.printStackTrace();
		}

		assert (kind.equals(FSA) || kind.equals(PA));

		File[] filesInFolder = getFilesFromFolder(inputDirectory);
		for (int i = 0; i < filesInFolder.length; i++) {
			assert (filesInFolder[i].getName().endsWith("tra"));
			System.out.println("Converting " + filesInFolder[i]);
			Tra2EmfConverter converter = new Tra2EmfConverter();
			TransitionGraph tg = converter.convert(filesInFolder[i], kind, uniform);

			// We just use EMF serialization to store the tg in an XMI file
			ResourceSet resSet = new ResourceSetImpl();
			Resource resource = resSet.createResource(URI.createURI(outputDirectory + File.separator
					+ filesInFolder[i].getName().replace(".tra", ".xmi")));
			resource.getContents().add(tg);
			try {
				resource.save(Collections.EMPTY_MAP);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
