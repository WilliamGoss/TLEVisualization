import java.util.ArrayList;
import java.util.List;


public class graphVizObject {
	
	public String className;
	public String action;
	public String version;
	public String feature;
	public boolean isClass;
	public int nodeID;
	
	public List<String> children;
	
	public graphVizObject(boolean classCheck, String cName, String cAction, String cVersion, String cFeature, int nID)
	{
		//Oval or Square
		isClass = classCheck;
		
		//Information for inside a class
		className = cName;
		action = cAction;
		version = cVersion;
		feature = cFeature;
		
		//Useful node information
		nodeID = nID;
		children = new ArrayList<String>();
	}
	
	//Add a child to this node, which will be used to draw later on.
	public void addChild(String nodeChild)
	{
		children.add(nodeChild);
	}
	
	//Call this method on itself to draw the node.
	public StringBuilder draw()
	{
		StringBuilder graph = new StringBuilder();
		
		
		
		return graph;
	}

}
