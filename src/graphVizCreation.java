import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.*;


public class graphVizCreation {
	
	private static List<String> softwareVersions;
	
	public static void create(String cn, LinkedHashMap<String, List<ResultObject>> versionResult) throws IOException
	{
		//Declare softwareVersions to avoid nullpointer.
		softwareVersions = new ArrayList<String>();
		
		String className = cn;
		List<ResultObject> relevantResults = new ArrayList<ResultObject>();
		
		//Get the relevant results that contain the added/deleted class
		Set<String> versionNumber = versionResult.keySet();
		for(String version: versionNumber)
		{
			for(ResultObject result: versionResult.get(version))
			{
				//Check if the added or deleted class
				if (result.suppliedClass.equals(className)) { relevantResults.add(result); }
				//Check if the result from TLE contains the class
				//Some might have multiple results
				else if (result.selectedClass.contains(",")) 
				{ 
					Set<String> classes = splitClassesOnComma(result.selectedClass);
					for(String _class: classes)
					{
						if (javaCount(_class) > 1)
						{
							Set<String> individualClasses = splitClasses(_class);
							if (individualClasses.contains(className)) { relevantResults.add(result); }
						}
						else
						{
							if (classes.contains(className)) { relevantResults.add(result); }
						}
					}
				}
				//Some might be two classes as the result
				else if (javaCount(result.selectedClass) > 1)
				{
					Set<String> individualClasses = splitClasses(result.selectedClass);
					if (individualClasses.contains(className)) { relevantResults.add(result); }
				}
				//Some might be single
				else if (result.selectedClass.equals(className)) { relevantResults.add(result); }
			}
		}
		
		if (className.equals("Memory.java")) 
		//if (className.equals("UseStatement.java"))
		//if (className.equals("Migration.java"))
		//if (className.equals("CommitLogAllocator.java"))
		//if (className.equals("Message.java"))
		{
			/*
			for(ResultObject res: relevantResults)
			{
				System.out.println("Supplied Class: " + res.suppliedClass);
				System.out.println("Result: " + res.selectedClass);
				System.out.println("Scenario: " + res.scenarioSelected);
				System.out.println("Version: " + getVersion(res.version-1));
			}
			*/
			drawGraph(className, relevantResults);
		}
		
		//System.out.println(className);
		//drawGraph(className, relevantResults);
	}
	
	/*
	 * The result objects should have been inserted by Version Number.
	 * Thus, we can iterate through them and create the graph from left to right.
	 */
	public static void drawGraph(String className, List<ResultObject> results) throws IOException
	{		
		DirectedGraph<graphVizObject, DefaultEdge> nodeGraph = new DefaultDirectedGraph<graphVizObject, DefaultEdge>(DefaultEdge.class);
		
		//Dictionary will contain the relationship between classes and their nodes
		//The key will be the class name and the value will be the node, version.
		HashMap<String, Tuple> classToNode = new HashMap<String, Tuple>();
		
		int nodeCount = 0;
		
		for(ResultObject result: results)
		{
			switch(result.scenarioSelected)
			{
			case 1:
			{
				//Create the oval that shows a create function action.
				graphVizObject gOne = new graphVizObject(false, "Create\nnew\nfunction", nodeCount);
				nodeCount++;
				
				nodeGraph.addVertex(gOne);
				
				//Now we need to connect the oval to the added class
				//But first, we need to see if the class was added by another scenario
				//within the same version.
				graphVizObject gTwo = null;
				boolean oldNode = false;
				if (classToNode.containsKey(removeJava(result.suppliedClass)))
				{
					if (classToNode.get(removeJava(result.suppliedClass)).version == result.version)
					{
						gTwo = classToNode.get(removeJava(result.suppliedClass)).gVO;
						gTwo.addAction("Added class");
						Tuple updatedNode = new Tuple(result.version, gTwo);
						classToNode.put(removeJava(result.suppliedClass), updatedNode);
						oldNode = true;
					}
				}
				else
				{
					String feat = getFeatures.get(getVersion(result.version), removeJava(result.suppliedClass));
					if (feat.length() == 0) { feat = result.selectedClass; }
					else { feat = feat + "," + result.selectedClass; }
					gTwo = new graphVizObject(true, removeJava(result.suppliedClass), "Added class", getVersion(result.version), feat, nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gTwo);
					classToNode.put(removeJava(result.suppliedClass), nTup);
				}
				
				//Create an edge from the oval to the result
				if (!oldNode) { nodeGraph.addVertex(gTwo); }
				nodeGraph.addEdge(gOne, gTwo);
				
				break;
			}
			case 2:
			{
				//Check if the resulting class already exists from an older version.
				//If it doesn't, it will need to be created as an original class.
				graphVizObject gOne = null;
				boolean oldNode = false;
				if (classToNode.containsKey(removeJava(result.selectedClass)))
				{
					if (classToNode.get(removeJava(result.selectedClass)).version <= result.version)
					{
						gOne = classToNode.get(removeJava(result.selectedClass)).gVO;
						oldNode = true;
					}
				}
				else
				{
					gOne = new graphVizObject(true, removeJava(result.selectedClass), "Original class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.selectedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gOne);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
				
				//Create the oval that shows an extract class action.
				graphVizObject gTwo = new graphVizObject(false, "Extract\nclass", nodeCount);
				nodeCount++;
	
				//Create an edge from the gOne node to the gTwo node.
				if (!oldNode) { nodeGraph.addVertex(gOne); }
				nodeGraph.addVertex(gTwo);
				nodeGraph.addEdge(gOne, gTwo);
				
				//Now we need to connect the oval to the added class
				//But first, we need to see if the class was added by another scenario
				//within the same version.
				graphVizObject gThree = null;
				oldNode = false;
				if (classToNode.containsKey(removeJava(result.suppliedClass)))
				{
					if (classToNode.get(removeJava(result.suppliedClass)).version == result.version)
					{
						gThree = classToNode.get(removeJava(result.suppliedClass)).gVO;
						gThree.addAction("Extracted class");
						Tuple updatedNode = new Tuple(result.version, gThree);
						classToNode.put(removeJava(result.suppliedClass), updatedNode);
						oldNode = true;
					}
					else
					{
						gThree = new graphVizObject(true, removeJava(result.suppliedClass), "Extracted class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.suppliedClass)), nodeCount, true);
						nodeCount++;
						Tuple nTup = new Tuple(result.version, gThree);
						classToNode.put(removeJava(result.suppliedClass), nTup);
					}
				}
				else
				{
					gThree = new graphVizObject(true, removeJava(result.suppliedClass), "Extracted class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.suppliedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gThree);
					classToNode.put(removeJava(result.suppliedClass), nTup);
				}
				
				//Create an edge from the oval to the result
				if (!oldNode) { nodeGraph.addVertex(gThree); }
				nodeGraph.addEdge(gTwo, gThree);
				
				break;
			}
			case 3:
			{
				Set<String> classes = splitClasses(result.selectedClass);
				String[] classList = classes.toArray(new String[classes.size()]);
				//Since the result involves two classes, the initial step must be repeated twice.
				graphVizObject gOne = null;
				boolean oldNode = false;
				if (classToNode.containsKey(result.selectedClass))
				{
					if (classToNode.get(result.selectedClass).version < result.version)
					{
						gOne = classToNode.get(result.selectedClass).gVO;
						oldNode = true;
					}
				}
				else
				{
					gOne = new graphVizObject(true, classList[0], "Original class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.selectedClass)), nodeCount, false);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gOne);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
					
				if (!oldNode) { nodeGraph.addVertex(gOne); }
				
				//Second class
				graphVizObject gTwo = null;
				oldNode = false;
				if (classToNode.containsKey(result.selectedClass))
				{
					if (classToNode.get(result.selectedClass).version <= result.version)
					{
						gTwo = classToNode.get(result.selectedClass).gVO;
						oldNode = true;
					}
				}
				else
				{
					gTwo = new graphVizObject(true, classList[1], "Original class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.selectedClass)), nodeCount, false);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gTwo);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
					
				if (!oldNode) { nodeGraph.addVertex(gTwo); }
				
				//Create the oval graphviz object to show a merge action.
				graphVizObject gThree = new graphVizObject(false, "Merge classes", nodeCount);
				nodeCount++;
				
				nodeGraph.addVertex(gThree);
				nodeGraph.addEdge(gOne, gThree);
				nodeGraph.addEdge(gTwo, gThree);
				
				//Merged into class
				graphVizObject gFour = null;
				oldNode = false;
				if (classToNode.containsKey(removeJava(result.suppliedClass)))
				{
					if (classToNode.get(removeJava(result.suppliedClass)).version == result.version + 1)
					{
						gFour = classToNode.get(removeJava(result.suppliedClass)).gVO;
						gFour.addAction("Additional methods");
						Tuple updatedNode = new Tuple(result.version, gFour);
						classToNode.put(removeJava(result.suppliedClass), updatedNode);
						oldNode = true;
					}
					else
					{
						gFour = new graphVizObject(true, removeJava(result.suppliedClass), "Merged class", getVersion(result.version), getFeatures.get(getVersion(result.version), classList[1]), nodeCount, true);
						nodeCount++;
						Tuple nTup = new Tuple(result.version, gFour);
						classToNode.put(classList[1], nTup);
					}
				}
				else
				{
					gFour = new graphVizObject(true, removeJava(result.suppliedClass), "Merged class", getVersion(result.version), getFeatures.get(getVersion(result.version), classList[1]), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gFour);
					classToNode.put(removeJava(result.suppliedClass), nTup);
				}
				
				if (!oldNode) { nodeGraph.addVertex(gFour); }
				nodeGraph.addEdge(gThree, gFour);
				
				break;
			}
			case 4:
			{
				//Check if the resulting class already exists from an older version.
				//If it doesn't, it will need to be created as an original class.
				graphVizObject gOne = null;
				boolean oldNode = false;
				if (classToNode.containsKey(removeJava(result.selectedClass)))
				{
					if (classToNode.get(removeJava(result.selectedClass)).version <= result.version)
					{
						gOne = classToNode.get(removeJava(result.selectedClass)).gVO;
						oldNode = true;
					}
				}
				else
				{
					gOne = new graphVizObject(true, removeJava(result.selectedClass), "Original class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.selectedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gOne);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
				
				//Create the oval that shows a promote method action.
				graphVizObject gTwo = new graphVizObject(false, "Promote method", nodeCount);
				nodeCount++;
	
				//Create an edge from the gOne node to the gTwo node.
				if (!oldNode) { nodeGraph.addVertex(gOne); }
				nodeGraph.addVertex(gTwo);
				nodeGraph.addEdge(gOne, gTwo);
				
				//Now we need to connect the oval to the added class
				//But first, we need to see if the class was added by another scenario
				//within the same version.
				graphVizObject gThree = null;
				oldNode = false;
				if (classToNode.containsKey(removeJava(result.suppliedClass)))
				{
					if (classToNode.get(removeJava(result.suppliedClass)).version == result.version)
					{
						gThree = classToNode.get(removeJava(result.suppliedClass)).gVO;
						gThree.addAction("Including promoted method");
						Tuple updatedNode = new Tuple(result.version, gThree);
						classToNode.put(removeJava(result.suppliedClass), updatedNode);
						oldNode = true;
					}
				}
				else
				{
					gThree = new graphVizObject(true, removeJava(result.suppliedClass), "Including promoted method", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.suppliedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gThree);
					classToNode.put(removeJava(result.suppliedClass), nTup);
				}
				
				//Create an edge from the oval to the result
				if (!oldNode) { nodeGraph.addVertex(gThree); }
				nodeGraph.addEdge(gTwo, gThree);
				
				break;
			}
			case 5: 
			{		
				//Check if the resulting class already exists from an older version.
				//If it doesn't, it will need to be created as an original class.
				graphVizObject gOne = null;
				boolean oldNode = false;
				if (classToNode.containsKey(removeJava(result.selectedClass)))
				{
					if (classToNode.get(removeJava(result.selectedClass)).version < result.version)
					{
						gOne = classToNode.get(removeJava(result.selectedClass)).gVO;
						oldNode = true;
					}
				}
				else
				{
					gOne = new graphVizObject(true, removeJava(result.selectedClass), "Original class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.selectedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gOne);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
				
				//Create the oval that shows an extend class action.
				graphVizObject gTwo = new graphVizObject(false, "Extend\nsuperclass", nodeCount);
				nodeCount++;
	
				//System.out.println(result.suppliedClass);
				//System.out.println(gOne);
				//Create an edge from the gOne node to the gTwo node.
				if (!oldNode) { nodeGraph.addVertex(gOne); }
				nodeGraph.addVertex(gTwo);
				nodeGraph.addEdge(gOne, gTwo);
				
				//Now we need to connect the oval to the added class
				//But first, we need to see if the class was added by another scenario
				//within the same version.
				graphVizObject gThree = null;
				oldNode = false;
				if (classToNode.containsKey(removeJava(result.suppliedClass)))
				{
					if (classToNode.get(removeJava(result.suppliedClass)).version == result.version)
					{
						gThree = classToNode.get(removeJava(result.suppliedClass)).gVO;
						gThree.addAction("Subclass of " + removeJava(result.suppliedClass));
						Tuple updatedNode = new Tuple(result.version, gThree);
						classToNode.put(removeJava(result.suppliedClass), updatedNode);
						oldNode = true;
					}
				}
				else
				{
					gThree = new graphVizObject(true, removeJava(result.suppliedClass), "Subclass of " + removeJava(result.selectedClass), getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.suppliedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gThree);
					classToNode.put(removeJava(result.suppliedClass), nTup);
				}
				
				if (!oldNode) { nodeGraph.addVertex(gThree); }
				nodeGraph.addEdge(gTwo, gThree);
				
				break;
			}	
			case 6: 
			{
				//Always draw from selectedClass to suppliedClass (Left to Right)
				
				//Check if the suppliedClass already exists in the node dictionary.
				//If it does, and the versions are equal or less than, just use that node.
				//If it doesn't exist, the node needs to be created.
				graphVizObject gOne = null;
				boolean oldNode = false;
				if (classToNode.containsKey(result.selectedClass))
				{
					if (classToNode.get(result.selectedClass).version < result.version)
					{
						//classToNode.get(result.selectedClass).gVO.addAction("Original class");
						gOne = classToNode.get(result.selectedClass).gVO;
						oldNode = true;
					}
				}
				else
				{
					gOne = new graphVizObject(true, removeJava(result.selectedClass), "Original class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.selectedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gOne);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
				
				//Create the oval graphviz object to show an extracting superclass action.
				graphVizObject gTwo = new graphVizObject(false, "Extracting\nsuperclass", nodeCount);
				nodeCount++;
	
				//Create an edge from the gOne node to the gTwo node.
				if (!oldNode) { nodeGraph.addVertex(gOne); }
				nodeGraph.addVertex(gTwo);
				nodeGraph.addEdge(gOne, gTwo);
				
				//There will be two resulting classes
				//They might be created in this version by another scenario,
				//but if they're old, create new ones.
				graphVizObject gThree = null;
				oldNode = false;
				if (classToNode.containsKey(result.suppliedClass))
				{
					if (classToNode.get(result.suppliedClass).version == result.version)
					{
						gThree = classToNode.get(result.suppliedClass).gVO;
						gThree.addAction("Extracted superclass");
						Tuple updatedNode = new Tuple(result.version, gThree);
						classToNode.put(result.suppliedClass, updatedNode);
						oldNode = true;
					}
				}
				else
				{
					gThree = new graphVizObject(true, removeJava(result.suppliedClass), "Extracted superclass", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.suppliedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gThree);
					classToNode.put(removeJava(result.suppliedClass), nTup);
				}
				
				//Create the edge from the previously created oval to this node
				if (!oldNode) { nodeGraph.addVertex(gThree); }
				nodeGraph.addEdge(gTwo, gThree);
				
				graphVizObject gFour = null;
				oldNode = false;
				//If originalNode is true, we know we need to create a new node.
				if (classToNode.containsKey(result.selectedClass)) {
					Tuple nodeCheck = classToNode.get(result.selectedClass);
					if (!nodeCheck.gVO.action.contains("Original class"))
					{
						gFour = nodeCheck.gVO;
						gFour.addAction("Remnant class");
						Tuple updatedNode = new Tuple(result.version, gFour);
						classToNode.put(result.selectedClass, updatedNode);
						oldNode = true;
					}
				}
				//If originalNode isn't new, then their might already be a node
				//from this version we can use.
				else
				{
					gFour = new graphVizObject(true, removeJava(result.selectedClass), "Remnant class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.selectedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gFour);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
				
				if (!oldNode) { nodeGraph.addVertex(gFour); }
				nodeGraph.addEdge(gTwo, gFour);
				break;
			}
			case 7:
			{
				//Check if the resulting class already exists from an older version.
				//If it doesn't, it will need to be created as an original class.
				graphVizObject gOne = null;
				boolean oldNode = false;
				if (classToNode.containsKey(removeJava(result.selectedClass)))
				{
					if (classToNode.get(removeJava(result.selectedClass)).version < result.version)
					{
						gOne = classToNode.get(removeJava(result.selectedClass)).gVO;
						oldNode = true;
					}
				}
				else
				{
					gOne = new graphVizObject(true, removeJava(result.selectedClass), "Original class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.selectedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gOne);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
				
				//Create the oval that shows an obsolete function action.
				graphVizObject gTwo = new graphVizObject(false, "Obsolete function", nodeCount);
				nodeCount++;
	
				//Create an edge from the gOne node to the gTwo node.
				if (!oldNode) { nodeGraph.addVertex(gOne); }
				nodeGraph.addVertex(gTwo);
				nodeGraph.addEdge(gOne, gTwo);
				
				//Now we need to connect the oval to the added class
				//But first, we need to see if the class was added by another scenario
				//within the same version.
				graphVizObject gThree = null;
				gThree = new graphVizObject(true, removeJava(result.suppliedClass), "Deleted", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.suppliedClass)), nodeCount, false);
				nodeCount++;
				Tuple nTup = new Tuple(result.version, gThree);
				classToNode.put(removeJava(result.suppliedClass), nTup);
				
				//Create an edge from the oval to the result
				nodeGraph.addVertex(gThree);
				nodeGraph.addEdge(gTwo, gThree);
				
				break;
			}
			case 8:
			{
				//Check if the resulting class already exists from an older version.
				//If it doesn't, it will need to be created as an original class.
				graphVizObject gOne = null;
				boolean oldNode = false;
				if (classToNode.containsKey(removeJava(result.suppliedClass)))
				{
					if (classToNode.get(removeJava(result.suppliedClass)).version <= result.version)
					{
						gOne = classToNode.get(removeJava(result.suppliedClass)).gVO;
						oldNode = true;
					}
				}
				else
				{
					gOne = new graphVizObject(true, removeJava(result.suppliedClass), "Original class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.suppliedClass)), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gOne);
					classToNode.put(removeJava(result.suppliedClass), nTup);
				}
				
				//Create the oval that shows a reallocate methods action.
				graphVizObject gTwo = new graphVizObject(false, "Reallocate\nmethods", nodeCount);
				nodeCount++;
	
				//Create an edge from the gOne node to the gTwo node.
				if (!oldNode) { nodeGraph.addVertex(gOne); }
				nodeGraph.addVertex(gTwo);
				nodeGraph.addEdge(gOne, gTwo);
				
				Set<String> classes = splitClasses(result.selectedClass);
				String[] classList = classes.toArray(new String[classes.size()]);
				//Now we need to connect the oval to the modified classes
				//But first, we need to see if the class was added by another scenario
				//within the same version.
				graphVizObject gThree = null;
				oldNode = false;
				if (classToNode.containsKey(classList[0]))
				{
					if (classToNode.get(classList[0]).version == result.version)
					{
						gThree = classToNode.get(classList[0]).gVO;
						gThree.addAction("Additional methods");
						Tuple updatedNode = new Tuple(result.version, gThree);
						classToNode.put(classList[0], updatedNode);
						oldNode = true;
					}
					else
					{
						gThree = new graphVizObject(true, classList[0], "Additional methods", getVersion(result.version), getFeatures.get(getVersion(result.version), classList[0]), nodeCount, true);
						nodeCount++;
						Tuple nTup = new Tuple(result.version, gThree);
						classToNode.put(classList[0], nTup);
					}
				}
				else
				{
					gThree = new graphVizObject(true, classList[0], "Additional methods", getVersion(result.version), getFeatures.get(getVersion(result.version), classList[0]), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gThree);
					classToNode.put(classList[0], nTup);
				}

				if (!oldNode) { nodeGraph.addVertex(gThree); }
				nodeGraph.addEdge(gTwo, gThree);
				
				graphVizObject gFour = null;
				oldNode = false;
				if (classToNode.containsKey(classList[1]))
				{
					if (classToNode.get(classList[1]).version == result.version)
					{
						gFour = classToNode.get(classList[1]).gVO;
						gFour.addAction("Additional methods");
						Tuple updatedNode = new Tuple(result.version, gFour);
						classToNode.put(classList[1], updatedNode);
						oldNode = true;
					}
					else
					{
						gFour = new graphVizObject(true, classList[1], "Additional methods", getVersion(result.version), getFeatures.get(getVersion(result.version), classList[1]), nodeCount, true);
						nodeCount++;
						Tuple nTup = new Tuple(result.version, gFour);
						classToNode.put(classList[1], nTup);
					}
				}
				else
				{
					gFour = new graphVizObject(true, classList[1], "Additional methods", getVersion(result.version), getFeatures.get(getVersion(result.version), classList[1]), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gFour);
					classToNode.put(classList[1], nTup);
				}
				
				if (!oldNode) { nodeGraph.addVertex(gFour); }
				nodeGraph.addEdge(gTwo, gFour);
				
				//Now we need to create the deleted class
				graphVizObject gFive = new graphVizObject(true, result.suppliedClass, "Deleted", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.suppliedClass)), nodeCount, false);
				nodeCount++;
				Tuple nTup = new Tuple(result.version, gFive);
				classToNode.put(result.suppliedClass, nTup);
				
				nodeGraph.addVertex(gFive);
				nodeGraph.addEdge(gTwo, gFive);				
				
				break;
			}
			case 9:
			{
				//Some classes have no result.
				if (result.selectedClass.equals("NONE")) break;
				
				Set<String> classes = splitClasses(result.selectedClass);
				String[] classList = classes.toArray(new String[classes.size()]);
				//Since the result involves two classes, the initial step must be repeated twice.
				graphVizObject gOne = null;
				boolean oldNode = false;
				if (classToNode.containsKey(result.selectedClass))
				{
					if (classToNode.get(result.selectedClass).version < result.version)
					{
						gOne = classToNode.get(result.selectedClass).gVO;
						oldNode = true;
					}
				}
				else
				{
					gOne = new graphVizObject(true, classList[0], "Original class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.selectedClass)), nodeCount, false);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gOne);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
					
				if (!oldNode) { nodeGraph.addVertex(gOne); }
				
				//Second class
				graphVizObject gTwo = null;
				oldNode = false;
				if (classToNode.containsKey(result.selectedClass))
				{
					if (classToNode.get(result.selectedClass).version <= result.version)
					{
						gTwo = classToNode.get(result.selectedClass).gVO;
						oldNode = true;
					}
				}
				else
				{
					gTwo = new graphVizObject(true, classList[1], "Original class", getVersion(result.version), getFeatures.get(getVersion(result.version), removeJava(result.selectedClass)), nodeCount, false);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gTwo);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
					
				if (!oldNode) { nodeGraph.addVertex(gTwo); }
				
				//Create the oval graphviz object to show a merge action.
				graphVizObject gThree = new graphVizObject(false, "Merge classes", nodeCount);
				nodeCount++;
				
				nodeGraph.addVertex(gThree);
				nodeGraph.addEdge(gOne, gThree);
				nodeGraph.addEdge(gTwo, gThree);
				
				//Merged into class
				graphVizObject gFour = null;
				oldNode = false;
				if (classToNode.containsKey(removeJava(result.suppliedClass)))
				{
					if (classToNode.get(removeJava(result.suppliedClass)).version == result.version + 1)
					{
						gFour = classToNode.get(removeJava(result.suppliedClass)).gVO;
						gFour.addAction("Additional methods");
						Tuple updatedNode = new Tuple(result.version, gFour);
						classToNode.put(removeJava(result.suppliedClass), updatedNode);
						oldNode = true;
					}
					else
					{
						gFour = new graphVizObject(true, removeJava(result.suppliedClass), "Merged class", getVersion(result.version), getFeatures.get(getVersion(result.version), classList[1]), nodeCount, true);
						nodeCount++;
						Tuple nTup = new Tuple(result.version, gFour);
						classToNode.put(classList[1], nTup);
					}
				}
				else
				{
					gFour = new graphVizObject(true, removeJava(result.suppliedClass), "Merged class", getVersion(result.version), getFeatures.get(getVersion(result.version), classList[1]), nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gFour);
					classToNode.put(removeJava(result.suppliedClass), nTup);
				}
				
				if (!oldNode) { nodeGraph.addVertex(gFour); }
				nodeGraph.addEdge(gThree, gFour);
				
				break;
			}
			}
		}
		
		drawGraph.draw(nodeGraph);
	}
	
	
	
	
	
	
	/*
	 * Various useful methods below.
	*/
	
	private static Set<String> splitClassesOnComma(String doubleResult)
	{
		Set<String> classes = new HashSet<String>();
		
		String[] splitClasses = doubleResult.split(",");
		for(String sClass: splitClasses)
		{
			classes.add(sClass.trim());
		}
		
		return classes;
	}
	
	private static Set<String> splitClasses(String doubleClass)
	{
		Set<String> classes = new HashSet<String>();
		
		String[] splitClasses = doubleClass.split(".java");
		for(String sClass: splitClasses)
		{
			String fixed = sClass + ".java";
			classes.add(fixed);
		}
		
		return classes;
	}
	
	private static int javaCount(String classResult)
	{
		int totalClasses = 0;
		String[] splitClass = classResult.split(".java");
		totalClasses = splitClass.length;
		return totalClasses;
	}
	
	private static String removeJava(String className)
	{
		String cleanClass = "";
		String[] classSplit = className.split(".java");
		cleanClass = classSplit[0];
		return cleanClass;
	}
	
	//The current bat file used to generate scenario results numbers everything one additional time.
	//If it is changed, you'll want to change the "ver" variable.
	private static String getVersion(int ver) throws IOException
	{
		if (softwareVersions.isEmpty())
		{
			List<String> sVersions = populateVersionNumber.populateNumbers();
			
			for(String bigVersion: sVersions)
			{
				String[] versionSplit = bigVersion.split("-");
				String version =  "";
				if (versionSplit.length > 3) { version = versionSplit[2] + "-" + versionSplit[3]; }
				else version = versionSplit[2];
				
				softwareVersions.add(version);
			}
		}
		
		return softwareVersions.get(ver);
		
	}

}
