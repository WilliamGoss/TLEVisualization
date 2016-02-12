import java.util.ArrayList;
import java.util.List;


public class graphVizObject {
	
	public String className;
	public String action;
	public int version;
	public String feature;
	public boolean isClass;
	public int nodeID;
	public boolean addCheck;
	
	public List<String> children;
	
	public graphVizObject(boolean classCheck, String cName, String cAction, int cVersion, String cFeature, int nID, boolean isAdded)
	{
		//Oval or Square
		isClass = classCheck;
		
		//Information for inside a class
		className = cName;
		action = cAction;
		version = cVersion;
		feature = cFeature;
		
		//Check if the class is a deleted class, because deleted classes need dashed lines
		addCheck = isAdded;
		
		//Useful node information
		nodeID = nID;
		children = new ArrayList<String>();
	}
	
	//This is for ovals ONLY!
	public graphVizObject(boolean classCheck, String cAction, int nID)
	{
		isClass = classCheck;
		action = cAction;
		nodeID = nID;
	}
	
	//Add a child to this node, which will be used to draw later on.
	public void addChild(String nodeChild)
	{
		children.add(nodeChild);
	}
	
	//Add an action to the object.
	public void addAction(String nodeAction)
	{
		this.action = this.action + "\n" + nodeAction;
	}
	
	//Call this method on itself to draw the node.
	public StringBuilder draw()
	{
		StringBuilder graph = new StringBuilder();
		
		
		
		return graph;
	}

}
