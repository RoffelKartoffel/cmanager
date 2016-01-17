package cmanager;

public class GeocacheAttribute 
{
	private int ID;
	private int inc;
	private String desc;
	
	public GeocacheAttribute(int ID, int inc, String desc)
	{
		if( desc == null )
			throw new IllegalArgumentException();
		
		this.ID = ID;
		this.inc = inc;
		this.desc = desc;
	}

	public int getID() {
		return ID;
	}

	public int getInc() {
		return inc;
	}

	public String getDesc() {
		return desc;
	}
}
