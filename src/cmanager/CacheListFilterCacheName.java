package cmanager;


public class CacheListFilterCacheName extends CacheListFilterModel {

	private static final long serialVersionUID = -6582495781375197847L;
	
	private String filterString = "";

	public CacheListFilterCacheName() 
	{
		lblLinks2.setText("Cachename contains: ");
		runDoModelUpdateNow = new Runnable() {
			@Override
			public void run() 
			{
				filterString = textField.getText().toLowerCase();
			}
		};
		
		panel_2.setVisible(true);
	}
	
	@Override
	protected boolean isGood(Geocache g) {
		return g.getName().toLowerCase().contains(filterString);
	}

}