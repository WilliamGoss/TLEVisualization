import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class populateVersionNumber {
	
	private static String fileLocation = "data/VersionNames.txt";
	
	public static List<String> populateNumbers() throws IOException
	{
		List<String> versionNumbers = new ArrayList<String>();
		
		try(BufferedReader reader = new BufferedReader(new FileReader(fileLocation)))
		{
			String line;
			while((line = reader.readLine()) != null)
			{
				versionNumbers.add(line);
			}
		}
		
		return versionNumbers;
	}

}
