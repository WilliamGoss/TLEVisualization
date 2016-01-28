import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Program {

	public static void main(String args[]) throws FileNotFoundException, IOException
	{
		//Get all of the versions of software
		List<String> softwareVersions = new ArrayList<String>();
		softwareVersions = populateVersionNumber.populateNumbers();
		
		for(String versionID: softwareVersions)
		{
			List<ResultObject> results = new ArrayList<ResultObject>();
			String fileLoc = "../TLE_Scenario_Checker/" + versionID + ".txt";
			//results = generateResults.parseResults("C:\\Users\\wgoss2\\Documents\\Mona\\TLE_Scenario_Checker\\cassandra-cassandra-1.1.12.txt");
			results = generateResults.parseResults(fileLoc);
		}
	}
}
