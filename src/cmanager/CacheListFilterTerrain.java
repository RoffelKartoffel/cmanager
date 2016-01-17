package cmanager;


import javax.swing.JTextField;

public class CacheListFilterTerrain extends CacheListFilterModel
{
	private static final long serialVersionUID = -6582495781375197847L;
	
	private Double minTerrain = 1.0;
	private Double maxTerrain = 5.0;
	
	public CacheListFilterTerrain() 
	{	
		final JTextField txtLinks = super.getTxtLinks();
		txtLinks.setText(minTerrain.toString());
		final JTextField txtRechts = super.getTxtRechts();
		txtRechts.setText(maxTerrain.toString());
		
		runDoModelUpdateNow = new Runnable() {
			@Override
			public void run() {
				try {
					minTerrain = new Double(txtLinks.getText());
				}catch(Exception ex) {}
				try {
					maxTerrain = new Double(txtRechts.getText());
				}catch(Exception ex) {}
			}
		};
		
		getLblLinks().setText("min Terrain: ");
		getLblRechts().setText("max Terrain: ");
		panel_1.setVisible(true);
	}
	
	@Override
	protected boolean isGood(Geocache g) {
		return g.getTerrain() >= minTerrain && g.getTerrain() <= maxTerrain;
	}
	
	
	
}
