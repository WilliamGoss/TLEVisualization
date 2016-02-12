import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;


public class Program {

	public static void main(String args[]) throws FileNotFoundException, IOException
	{
		
		//Get all of the versions of software
		List<String> softwareVersions = new ArrayList<String>();
		softwareVersions = populateVersionNumber.populateNumbers();
		
		//A set to hold the versions and result for each.
		LinkedHashMap<String, List<ResultObject>> versionResults = new LinkedHashMap<String, List<ResultObject>>();
		
		int verCount = 0;
		for(String versionID: softwareVersions)
		{
			List<ResultObject> results = new ArrayList<ResultObject>();
			String fileLoc = "../TLE_Scenario_Checker/" + versionID + ".txt";
			
			//Need to isolate the version id from the string
			String[] versionSplit = versionID.split("-");
			String version =  "";
			if (versionSplit.length > 3) { version = versionSplit[2] + "-" + versionSplit[3]; }
			else version = versionSplit[2];
			
			results = generateResults.parseResults(fileLoc, verCount);
			
			versionResults.put(version, results);
			verCount++;
		}
		
		//Get all of the added and deleted classes.
		HashSet<String> classNames = getUniqueClasses.getClasses(versionResults);
		
		for(String cName: classNames)
		{
			graphVizCreation.create(cName, versionResults);
		}
		
		
		
		/*
		//versionResults contains a map of all the version numbers and their results
		//Now it needs to be used in creating graphviz outputs!
		for(String versID: softwareVersions)
		{
			String[] versionSplit = versID.split("-");
			String version =  "";
			if (versionSplit.length > 3) { version = versionSplit[2] + "-" + versionSplit[3]; }
			else version = versionSplit[2];
			
			for(ResultObject rso: versionResults.get(version))
			{
				//System.out.println("Supplied Class: " + rso.suppliedClass);
				//System.out.println("Result: " + rso.selectedClass);
				//System.out.println("Scenario Selected: " + rso.scenarioSelected);
				//if (rso.suppliedClass.contains("AllowAllAuthenticator.java") || rso.selectedClass.contains("AllowAllAuthenticator.java"))
				if (rso.suppliedClass.contains("RefCountedMemory.java") || rso.selectedClass.contains("RefCountedMemory.java"))
				{
					System.out.println("It showed up again!");
					System.out.println(version);
					System.out.println("Scenario: " + rso.scenarioSelected);
					System.out.println("Added/Deleted Class: " + rso.suppliedClass + " && Resulting Class: " + rso.selectedClass);
					System.out.println(rso.addedClass);
				}
			}
		}
		*/
		
	}
}
