import java.util.ArrayList;
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
		
		if (relevantResults.size() == 11) 
		{ 
			System.out.println(className); 
			for(ResultObject rs: relevantResults)
			{
				System.out.println("Scenario: " + rs.scenarioSelected);
				System.out.println("Added/Deleted Class: " + rs.suppliedClass + " && Resulting Class: " + rs.selectedClass);
				System.out.println(rs.version);
				System.out.println(rs.addedClass);
			}
		}
		
	}
	
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
