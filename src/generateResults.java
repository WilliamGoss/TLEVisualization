import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class generateResults {
	
	//Goes through all of the versions of Cassandra output and creates the result objects
	//Give the path of the file, and it will generate all of the results
	public static List<ResultObject> parseResults(String pathLocation) throws FileNotFoundException, IOException
	{
		List<ResultObject> results = new ArrayList<ResultObject>();
		
		try(BufferedReader reader = new BufferedReader(new FileReader(pathLocation)))
		{
			String line;
			String className = "";
			boolean newClass = false;
			String previousLine = "";
			List<String> classResults = new ArrayList<String>();
			boolean addedClass = false;
			
			/* Special case for deleted scnarios
			 * If it selects Scenario 9 as the winner, the values are not printed
			 * so the line needs to be saved and the results need to be taken out.
			 */
			String rule38 = "";
			
			while((line = reader.readLine()) != null)
			{
				if (line.length() == 0) { continue; }
				
				if (line.charAt(0) == '*')
				{
					if (line.toLowerCase().contains("added")) addedClass = true;
					else addedClass = false;
				}
				
				//Check if a class name is selected
				//Pattern is "ClassName.java ---" ~ So if first letter is an alphabetic character and line contains ---
				//Every time this pattern appears, see if the class name is still the same to make sure
				//classes are paired to the correct results.
				if (Character.isAlphabetic(line.charAt(0)) && line.contains("---"))
				{
					String tempClass = null;
					String[] splitString = line.split(".java");
					tempClass = splitString[0];
					
					if (className.length() == 0)
					{ 
						className = tempClass; 
					}
					
					//Check if it is a new class.
					//If it is, set the class name to the new class and empty the class result array.
					if (className.length() != 0 && !(className.equals(tempClass))) { newClass = true; }
					
					if (newClass)
					{
						className = tempClass;
						classResults = new ArrayList<String>();
						newClass = false;
					}
				}
				
				//If a number is encountered, it's the result from the weighted scenario.
				//The line before this number, should contain the class it picked from the scenario.
				//If it is 0, an empty string is added.
				if (Character.isDigit(line.charAt(0)))
				{
					if (line.charAt(0) == '0')
					{
						classResults.add(" ");
					}
					else if (line.length() > 2)
					{
						if (Integer.parseInt(line.substring(0, 2)) == 684) continue;
					}
					else
					{
						classResults.add(previousLine);
					}
				}
				
				else if (line.charAt(0) == 'R' && line.substring(0, 7).equals("Rule 38") && !addedClass)
				{
					rule38 = line;
				}
				
				else if (line.charAt(0) == 't' && line.substring(0, 4).equals("test"))
				{
					int correctScenario = Integer.parseInt(line.substring(4));
					String resultingClass = null;
					
					if(addedClass)
					{
						if (correctScenario - 1 < 0) { resultingClass = "NONE"; }
						else { resultingClass = classResults.get(correctScenario - 1); }
					}
					else
					{
						if (correctScenario - 7 < 0) { resultingClass = "NONE"; }
						else if (correctScenario == 9)
						{
							String[] tempResult = rule38.split("\\[");
							String rule38Result = tempResult[1].replace(']', ' ').trim();
							if (rule38Result.length() == 0) { rule38Result = "NONE"; }
							resultingClass = rule38Result;
						}
						else 
						{ 
							resultingClass = classResults.get(correctScenario - 7); 
						}
					}
					
					//Create the result object which contains the class name, scenario it fits best with,
					//and the result of that scenario.
					//System.out.println(className + ".java is matched to Scenario " + correctScenario + " with the class " + resultingClass + ".");
					ResultObject newResult = new ResultObject(className + ".java", correctScenario, resultingClass, addedClass);
					results.add(newResult);
				}
				
				//Store the previous line for when the following value is a digit.
				previousLine = line;
			}
		}
		
		return results;
	}

}
