import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;


public class drawGraph {
	
	public static void draw(DirectedGraph<graphVizObject, DefaultEdge> graph)
	{
		StringBuilder graphViz = new StringBuilder();
		graphViz.append("digraph g { graph [ rankdir = \"LR\"];\nnode [ fontsize = \"16\" shape = \"record\" ];\n");
		graphViz.append("edge [];\n");
		
		
		
		graphViz.append("\n}");
	}

}
