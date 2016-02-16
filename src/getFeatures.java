import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class getFeatures {
	
	public static String get(String version, String className) throws FileNotFoundException, IOException
	{
		String features = "";
		String fileLocation = "../TLE_Scenario_Checker/old-cassandra-cassandra-" + version + "/RequirementClass2-V2.txt";
		String line;
		try(BufferedReader reader = new BufferedReader(new FileReader(fileLocation)))
		{
			while((line = reader.readLine()) != null)
			{
				if (line.length() > 0) 
				{
					String[] result = line.split(":");
					if (result[1].equals(className))
					{
						if (features.length() == 0) features = result[0];
						else features = features + "," + result[0];
					}
				}
			}
		}
		
		if (features.split(",").length > 2) { String trimFeatures = features.substring(0, features.length()-1); features = trimFeatures; }
		
		return features;
	}

}
