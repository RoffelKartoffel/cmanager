package cmanager;


import java.io.Serializable;
import java.util.ArrayList;


public class Geocache implements Serializable, Comparable<String>
{	
	private static final long serialVersionUID = 6173771530979347662L;

	
	public static final TMap TYPE = new TMap();
	static {
		//  pretty name 	-- 	GC 			-- OC
		TYPE.add("Tradi", 		"Traditional Cache", "Traditional");
		TYPE.add("Drive-In", 	null, "Drive-In");
		TYPE.add("Moving", 		null, "Moving");
		TYPE.add("Other", 		null, "Other");
		TYPE.add("Math/Physics",null, "Math/Physics");
		TYPE.add("Multi", 		"Multi-cache", "Multi");
		TYPE.add("Mystery", 	"Unknown Cache", "Quiz");
		
		TYPE.add("Virtual", 	"Virtual Cache", "Virtual");
		TYPE.add("Webcam", 		"Webcam Cache", null);
		TYPE.add("Earth", 		"Earthcache", null);
		TYPE.add("Reverse", 	null, "Locationless (Reverse) Cache");
		
		TYPE.add("L.Box", 		"Letterbox Hybrid", null);
		TYPE.add("Event", 		"Event Cache", "Event");
		TYPE.add("Mega-Event", 	"Mega-Event Cache", null);
		TYPE.add("Giga-Event", 	"Giga-Event Cache", null);
		TYPE.add("CITO", 		"Cache In Trash Out Event", null);
		TYPE.add("Wherigo", 	"Wherigo Cache", null);
		TYPE.add("GPS AE", 		"GPS Adventures Exhibit", null);	
	}

	public String getTypeAsNice(){
		return TYPE.get(type, 0);
	}
	public String getTypeAsGC(){
		String gc = TYPE.get(type, 1);
		return gc != null ? gc : TYPE.get(type, 2);
	}
	private static int stringToType(String s){
		return TYPE.getLC(s);
	}
	public static int getTradiType(){
		return TYPE.get("Tradi");
	}
	public static int getMultiType(){
		return TYPE.get("Multi");
	}
	public static int getMysteryType(){
		return TYPE.get("Mystery");
	}
	
	
	
	public static final TMap CONTAINER = new TMap();
	static {
		CONTAINER.add("None");
		CONTAINER.add("Nano");
		CONTAINER.add("Micro");
		CONTAINER.add("Small");
		CONTAINER.add("Regular");
		CONTAINER.add("Large");
		CONTAINER.add("Xlarge");
		CONTAINER.add("Other");
		CONTAINER.add("Virtual");
		CONTAINER.add("Not chosen", "not_chosen");
	}
	public String getContainerAsGC(){
		if( container == null )
			return null;
		String s = CONTAINER.get(container, 0);
		return s;
	}
	public void setContainer(String container) {
		if( container == null )
			return;
		container = container.toLowerCase();
		this.container = CONTAINER.getLC(container);
	}
	

	
	private String code;
	private String name;
	private Coordinate coordinate;
	private Double difficulty;
	private Double terrain;
	private int type;
	private Integer container;
	private String owner = null;
	private String code_gc = null;	// linked cache on gc
	private String listing = null;
	private String listing_short = null;
	private String hint = null;
	
	private Boolean archived = null;
	private Boolean available = null;
	private Integer id = null; 	// required by "Garmin etrex 10"
	
	private Boolean gcPremium = null;
	private Integer favPoints = null;

	private ArrayList<GeocacheAttribute> attributes = new ArrayList<>();
	private ArrayList<GeocacheLog> logs = new ArrayList<GeocacheLog>();
	private ArrayList<Waypoint> wps = new ArrayList<Waypoint>();
	
	
	// public Geocache(String code, String name, Coordinate coordinate, Double difficulty, Double terrain, Type type) throws NullPointerException
	public Geocache(String code, String name, Coordinate coordinate, Double difficulty, Double terrain, String type) throws NullPointerException
	{
		if( code == null || name == null ||coordinate == null || difficulty == null || terrain == null || type == null )
			throw new NullPointerException();
		
		this.code = code;
		this.name = name;
		this.coordinate = coordinate;
		this.difficulty = difficulty;
		this.terrain = terrain;
		this.type = stringToType(type);
	}

	public String toString(){
		return difficulty.toString() + "/" + terrain.toString() +" " + code + " ("+ getTypeAsNice() + ") -- " + coordinate.toString() + " -- "  + name;
	}
	
	public double claculateSimilarity(Geocache g)
	{
		if( ( this.code_gc != null && this.code_gc.equals(g.code) ) 
				|| (g.code_gc != null && g.code_gc.equals(this.code) ) )
					return 1;
		
		double dividend = 0;
		double divisor = 0;
		
		divisor++;
		if( name.equals( g.getName() ) )
			dividend++; 
		
		divisor++;
		if( coordinate.distanceSphere( g.getCoordinate()) < 0.001 )
			dividend++;

		divisor++;
		if( Double.compare(difficulty, g.getDifficulty()) == 0 )
			dividend++;
		
		divisor++;
		if( Double.compare(terrain, g.getTerrain()) == 0 )
			dividend++;

		divisor++;
		if( type == g.getType() )
			dividend++;
		
		if( owner != null )
		{
			divisor++;
			if( owner.equals(g.getOwner()) )
				dividend++;
		}
		
		if( container != null )
		{
			divisor++;
			if( container == g.getContainer() )
				dividend++;
		}
		
		if( available != null && archived != null )
		{
			divisor++;
			if( statusAsString().equals(g.statusAsString()) )
				dividend++;
		}
		
		return dividend / divisor;
	}
	
	public boolean isSimilar(Geocache g)
	{
		return claculateSimilarity(g) >= 0.8;
	}
	
	public boolean hasVolatileStart()
	{
		return this.type == getMysteryType();
	}
	
	public void update(Geocache g){
		update(g, true, true);
	}
	
	public void update(Geocache g, boolean override, boolean copyLogs)
	{
		if( !code.equals(g.code) )
			return;
		
		if( override )
		{
			name = ObjectHelper.getBest(name, g.name);
			coordinate = ObjectHelper.getBest(coordinate, g.coordinate);
			difficulty = ObjectHelper.getBest(this.getDifficulty(), g.getDifficulty());
			terrain = ObjectHelper.getBest(terrain, g.terrain);
			type = g.type;
			container = ObjectHelper.getBest(container, g.container);
			owner = ObjectHelper.getBest(owner, g.owner);
			code_gc = ObjectHelper.getBest(code_gc, g.code_gc);
			setListing( ObjectHelper.getBest(getListing(), g.getListing()) );
			listing_short = ObjectHelper.getBest(listing_short, g.listing_short);
			hint = ObjectHelper.getBest(hint, g.hint);
			archived = ObjectHelper.getBest(archived, g.archived);
			available = ObjectHelper.getBest(available, g.available);
			
			attributes = ObjectHelper.getBest(attributes, g.attributes);
		}
		if( copyLogs )
		{
			for(GeocacheLog newLog : g.logs)
			{
				boolean match = false;
				for(GeocacheLog oldLog : logs )
					if( newLog.equals( oldLog ) )
					{
						match = true;
						break;
					}
				if( !match )
					logs.add(newLog);
			}
		}
	}
	
	public String getURL(){
		if( isGC() )
			return "https://www.geocaching.com/geocache/" + code;
		if( isOC() )
			return "https://www.opencaching.de/" + code;
		
		return null;
	}
	
	public String statusAsString()
	{
		if( archived == null || available == null )
			return null;
			
		if( archived )
			return "archived";
		if( available )
			return "available";
		return "disabled";
	}
	
	public boolean isOC(){
		return code.substring(0, 2).toUpperCase().equals("OC");
	}
	
	public boolean isGC(){
		return code.substring(0, 2).toUpperCase().equals("GC");
	}
	
    public void add(GeocacheLog gl) {
        logs.add(gl);
    }
        
	public void add(ArrayList<GeocacheLog> logs) {
            for(GeocacheLog gl: logs )
                this.logs.add(gl);
    }
    
    public ArrayList<GeocacheLog> getLogs(){
        return logs;
    }
    
    public void add(Waypoint wp){
    	wp.setParent(code);
    	wps.add(wp);
    }
    
    public void addWaypoints(ArrayList<GeocacheAttribute> attributes)
    {
    	for(GeocacheAttribute a : attributes)
    		this.addAttribute(a);
    }
    
    public ArrayList<GeocacheAttribute> getAttributes(){
    	return attributes;
    }
    
    public void addAttribute(GeocacheAttribute a){
    	attributes.add(a);
    }
    
    
    public ArrayList<Waypoint> getWaypoints(){
    	return wps;
    }
    
    public void setId(Integer id){
    	this.id = id;
    }
    
    public Integer getId(){
    	return id;
    }
    
	public Integer getFavPoints() {
		return favPoints;
	}
	public void setFavPoints(Integer favPoints) {
		this.favPoints = favPoints;
	}
    
	public Boolean getGcPremium() {
		return gcPremium;
	}
	public void setGcPremium(Boolean gcPremium) {
		this.gcPremium = gcPremium;
	}
    
    public void setArchived(Boolean archived){
    	this.archived = archived;
    }
    
    public Boolean getArchived(){
    	return archived;
    }
    
    public void setAvailable(Boolean available){
    	this.available = available;
    }
    
    public Boolean getAvailable(){
    	return available;
    }

    public void setHint(String hint){
    	this.hint = hint;
    }
    
    public String getHint(){
    	return hint;
    }
        
	public void setCodeGC(String gc){
		code_gc = gc;
	}
	
	public String getCodeGC(){
		return code_gc;
	}
	
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public Double getDifficulty() {
		return difficulty;
	}

	public Double getTerrain() {
		return terrain;
	}

	public int getType() {
		return type;
	}

	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner){
		this.owner = owner;
	}

	public String getListing() {
		return listing;
	}

	public void setListing(String listing) {
		this.listing = listing;
	}
	
	public int getContainer() {
		return container;
	}
	public String getListing_short() {
		return listing_short;
	}
	public void setListing_short(String listing_short) {
		this.listing_short = listing_short;
	}
	@Override
	public int compareTo(String s) {
		return code.compareTo( s );
	}
}
