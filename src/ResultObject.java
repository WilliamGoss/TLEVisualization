
public class ResultObject {
	
	public String suppliedClass;
	public int scenarioSelected;
	public String selectedClass;
	public boolean addedClass;
	public String version;
	
	public ResultObject(String suClass, int sSelected, String seClass, boolean addCheck, String ver)
	{
		suppliedClass = suClass;
		scenarioSelected = sSelected;
		selectedClass = seClass;
		addedClass = addCheck;
		version = ver;
		
	}

}
