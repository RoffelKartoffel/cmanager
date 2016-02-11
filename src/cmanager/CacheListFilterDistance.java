package cmanager;

public class CacheListFilterDistance extends CacheListFilterModel
{

	private Double maxDistance;
	private Location location;

	public CacheListFilterDistance() 
	{
		lblLinks2.setText("Maximum distance to location (km): ");
		runDoModelUpdateNow = new Runnable() {
			@Override
			public void run() 
			{
				maxDistance = new Double( textField.getText() );
			}
		};
		
		panel_2.setVisible(true);
	}
	
	public void setLocation(Location l){
		location = l;
	}
	
	@Override
	protected boolean isGood(Geocache g) 
	{
		if( location == null || maxDistance == null)
			return true;
		
		return g.getCoordinate().distanceSphere(location) < maxDistance;
	}
}
