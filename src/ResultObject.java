
public class ResultObject {
	
	public String suppliedClass;
	public int scenarioSelected;
	public String selectedClass;
	public boolean addedClass;
	public int version;
	
	public ResultObject(String suClass, int sSelected, String seClass, boolean addCheck, int ver)
	{
		suppliedClass = suClass;
		scenarioSelected = sSelected;
		selectedClass = seClass;
		addedClass = addCheck;
		version = ver;
	}

}
