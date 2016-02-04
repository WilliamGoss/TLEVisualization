import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;


/*
 * Instead of reading in the various classes in the added and deleted class,
 * this will get them from the results. This could be done with the files, but
 * using the ResultObjects is one less file that is needed to run this Visualization
 * tool.
 */
public class getUniqueClasses {

	public static HashSet<String> getClasses(LinkedHashMap<String, List<ResultObject>> results)
	{
		HashSet<String> classNames = new HashSet<String>();
		
		for(String versionNumber: results.keySet())
		{			
			for(ResultObject result: results.get(versionNumber))
			{
				classNames.add(result.suppliedClass);
			}
		}
		
		return classNames;
	}
}
