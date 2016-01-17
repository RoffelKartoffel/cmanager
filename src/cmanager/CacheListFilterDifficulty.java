package cmanager;


import javax.swing.JTextField;

public class CacheListFilterDifficulty extends CacheListFilterModel
{
	private static final long serialVersionUID = -6582495781375197847L;
	
	private Double minD = 1.0;
	private Double maxD = 5.0;
	
	public CacheListFilterDifficulty() 
	{	
		final JTextField txtLinks = super.getTxtLinks();
		txtLinks.setText(minD.toString());		
		final JTextField txtRechts = super.getTxtRechts();
		txtRechts.setText(maxD.toString());
		
		runDoModelUpdateNow = new Runnable() {
			@Override
			public void run() {
				try {
					minD = new Double(txtLinks.getText());
				}catch(Exception ex) {}
				
				try {
					maxD = new Double(txtRechts.getText());
				}catch(Exception ex) {}
			}
		};
		
		getLblLinks().setText("min Difficulty: ");
		getLblRechts().setText("max Difficulty: ");
		panel_1.setVisible(true);
	}
	
	@Override
	protected boolean isGood(Geocache g) {
		return g.getDifficulty() >= minD && g.getDifficulty() <= maxD;
	}
	
	
	
}
