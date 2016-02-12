import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.*;


public class graphVizCreation {
	
	public static void create(String cn, LinkedHashMap<String, List<ResultObject>> versionResult)
	{
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
		{ 
			/*
			for(ResultObject result: relevantResults)
			{
				System.out.println("Supplied Class: " + result.suppliedClass);
				System.out.println("Result: " + result.selectedClass);
				System.out.println("Scenario Selected: " + result.scenarioSelected);
				System.out.println("Version: " + result.version);
			}
			*/
			drawGraph(className, relevantResults);
		}
		
	}
	
	/*
	 * The result objects should have been inserted by Version Number.
	 * Thus, we can iterate through them and create the graph from left to right.
	 */
	public static void drawGraph(String className, List<ResultObject> results)
	{
		StringBuilder graph = new StringBuilder();
		graph.append("digraph g { graph [ rankdir = \"LR\"];\nnode [ fontsize = \"16\" shape = \"record\" ];\n");
		graph.append("edge [];\n");
		
		DirectedGraph<graphVizObject, DefaultEdge> nodeGraph = new DefaultDirectedGraph<graphVizObject, DefaultEdge>(DefaultEdge.class);
		
		//Dictionary will contain the relationship between classes and their nodes
		//The key will be the class name and the value will be the node, version.
		HashMap<String, Tuple> classToNode = new HashMap<String, Tuple>();
		
		int nodeCount = 0;
		int idCount = 0;
		
		for(ResultObject result: results)
		{
			switch(result.scenarioSelected)
			{
			case 1:
			{
				break;
			}
			case 2:
			{
				break;
			}
			case 3:
			{
				break;
			}
			case 4:
			{
				break;
			}
			case 5: 
			{				
				//graph.append("\"" + result.suppliedClass + "\" [\n");
				//graph.append("label = \"<f0> " + result.suppliedClass + " |<f1> Scenario " + result.suppliedClass + " |<f2> " + result.version + "\"\n");
				//graph.append("shape = \"record\"\n");
				//graph.append("color = \"green\"\n];\n");
				
				//graph.append("\"" + classToNode.get(result.selectedClass) + "\":f0 -> \"" + classToNode.get(result.suppliedClass) + "\":f0 [\n");
				//graph.append("id = " + idCount + "\n");
				//idCount++;
				//graph.append("];");
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
					gOne = new graphVizObject(true, removeJava(result.selectedClass), "Original class", result.version, "F3", nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gOne);
					classToNode.put(removeJava(result.selectedClass), nTup);
				}
				
				//Create the oval graphviz object	
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
					gThree = new graphVizObject(true, removeJava(result.suppliedClass), "Extracted superclass", result.version, "F3", nodeCount, true);
					nodeCount++;
					Tuple nTup = new Tuple(result.version, gThree);
					classToNode.put(removeJava(result.selectedClass), nTup);
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
					gFour = new graphVizObject(true, removeJava(result.selectedClass), "Remnant class", result.version, "F3", nodeCount, true);
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
				break;
			}
			case 8:
			{
				break;
			}
			case 9:
			{
				break;
			}
			}
		}
		
		graph.append("\n}");
		
		System.out.println(nodeGraph.toString());
		
		//System.out.println(graph.toString());
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

}
