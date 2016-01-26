import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Program {

	public static void main(String args[]) throws FileNotFoundException, IOException
	{
		List<ResultObject> results = new ArrayList<ResultObject>();
		results = generateResults.parseResults("C:\\Users\\wgoss2\\Documents\\Mona\\TLE_Scenario_Checker\\cassandra-cassandra-1.1.12.txt");
	}
}
