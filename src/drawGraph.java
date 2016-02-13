import java.util.HashMap;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;


public class drawGraph {
	
	public static void draw(DirectedGraph<graphVizObject, DefaultEdge> graph)
	{
		StringBuilder graphViz = new StringBuilder();
		graphViz.append("digraph g { graph [ rankdir = \"LR\"];\nnode [ fontsize = \"16\" shape = \"record\" ];\n");
		graphViz.append("edge [];\n");
		
		//Dictionary of created nodes.
		HashMap<String, String> graphToNode = new HashMap<String, String>();
		for(graphVizObject vizObj: graph.vertexSet())
		{
			String nodeNum = "node" + vizObj.nodeID;
			graphToNode.put(vizObj.toString(), nodeNum);
			graphViz.append(vizObj.draw());
		}
		
		int idCount = 0;
		for(DefaultEdge edge: graph.edgeSet())
		{
			String[] edgeParts = edge.toString().split(":");
			String nodeOne = graphToNode.get(edgeParts[0].substring(1, edgeParts[0].length()).trim());
			String nodeTwo = graphToNode.get(edgeParts[1].substring(1, edgeParts[0].length()-1).trim());

			graphViz.append("\"" + nodeOne + "\":f0 -> \"" + nodeTwo + "\":f0\n");
			graphViz.append("[\nid = " + idCount + "\n];\n");
			idCount++;
		}
		
		graphViz.append("\n}");
		
		System.out.println(graphViz);
	}

}
