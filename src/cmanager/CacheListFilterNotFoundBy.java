package cmanager;

import java.util.ArrayList;

public class CacheListFilterNotFoundBy extends CacheListFilterModel
{
	private static final long serialVersionUID = 5585453135104325357L;
	
	private ArrayList<String> usernames = new ArrayList<>();

	public CacheListFilterNotFoundBy()
	{
		lblLinks2.setText("Not Found by: ");
		runDoModelUpdateNow = new Runnable() {
			@Override
			public void run() 
			{
				String input = textField.getText();
				String[] parts = input.split(",");
				usernames = new ArrayList<>();
				for( String part : parts )
					usernames.add( part.trim().toLowerCase() );
			}
		};
		
		panel_2.setVisible(true);
	}
	
	@Override
	protected boolean isGood(Geocache g) 
	{
		for(GeocacheLog log : g.getLogs())
			for(String username : usernames )
				if( log.isFoundLog() && log.isAuthor(username) )
					return false;
		return true;
	}

}
