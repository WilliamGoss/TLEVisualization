import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


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
		
		//Dictionary will contain the relationship between classes and their nodes
		//The key will be the class name and the value will be the node.
		HashMap<String, String> classToNode = new HashMap<String, String>();
		
		int nodeCount = 0;
		int idCount = 0;
		
		for(ResultObject result: results)
		{
			String nodeValue = "";
			switch(result.scenarioSelected)
			{
			case 5: {
				//TODO: Check for multiple values
				boolean oldNode = false;
				if (classToNode.containsKey(result.selectedClass)) { oldNode = true; }
				else { nodeValue = "node" + nodeCount; nodeCount++; classToNode.put(result.selectedClass, nodeValue); }
				
				//Always a single value since it's the added or deleted class.
				if (classToNode.containsKey(result.suppliedClass)) { nodeValue = classToNode.get(result.suppliedClass); }
				else { nodeValue = "node" + nodeCount; nodeCount++; classToNode.put(result.suppliedClass, nodeValue); }
				
				//Always draw from selectedClass to suppliedClass (Left to Right)
				if (!oldNode)
				{
					graph.append("\"" + classToNode.get(result.selectedClass) + "\" [\n");
					graph.append("label = \"<f0> " + result.selectedClass + " |<f1> Scenario " + result.scenarioSelected + " |<f2> " + result.version + "\"\n");
					graph.append("shape = \"record\"\n");
					graph.append("color = \"black\"\n];\n");
				}
				
				graph.append("\"" + classToNode.get(result.suppliedClass) + "\" [\n");
				graph.append("label = \"<f0> " + result.suppliedClass + " |<f1> Scenario " + result.suppliedClass + " |<f2> " + result.version + "\"\n");
				graph.append("shape = \"record\"\n");
				graph.append("color = \"green\"\n];\n");
				
				graph.append("\"" + classToNode.get(result.selectedClass) + "\":f0 -> \"" + classToNode.get(result.suppliedClass) + "\":f0 [\n");
				graph.append("id = " + idCount + "\n");
				idCount++;
				graph.append("];");
			}	
			case 6: 
				//Set<String> individualClasses = new HashSet<String>();
				//if (javaCount(result.selectedClass) > 1) { individualClasses = splitClasses(result.selectedClass); }
				//TODO: Check for multiple values
				if (classToNode.containsKey(result.selectedClass)) { nodeValue = classToNode.get(result.selectedClass); }
				else { nodeValue = "node" + nodeCount; nodeCount++; classToNode.put(result.selectedClass, nodeValue); }
				
				//Always a single value since it's the added or deleted class.
				if (classToNode.containsKey(result.suppliedClass)) { nodeValue = classToNode.get(result.suppliedClass); }
				else { nodeValue = "node" + nodeCount; nodeCount++; classToNode.put(result.suppliedClass, nodeValue); }
				
				//Always draw from selectedClass to suppliedClass (Left to Right)
				graph.append("\"" + classToNode.get(result.selectedClass) + "\" [\n");
				graph.append("label = \"<f0> " + result.selectedClass + " |<f1> Scenario " + result.scenarioSelected + " |<f2> " + result.version + "\"\n");
				graph.append("shape = \"record\"\n");
				graph.append("color = \"black\"\n];\n");
				
				graph.append("\"" + classToNode.get(result.suppliedClass) + "\" [\n");
				graph.append("label = \"<f0> " + result.suppliedClass + " |<f1> Scenario " + result.suppliedClass + " |<f2> " + result.version + "\"\n");
				graph.append("shape = \"record\"\n");
				graph.append("color = \"green\"\n];\n");
				
				graph.append("\"" + classToNode.get(result.selectedClass) + "\":f0 -> \"" + classToNode.get(result.suppliedClass) + "\":f0 [\n");
				graph.append("id = " + idCount + "\n");
				idCount++;
				graph.append("];");
			}
		}
		
		graph.append("\n}");
		
		System.out.println(graph.toString());
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

}
