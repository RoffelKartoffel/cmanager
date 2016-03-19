package cmanager;

public class Updates 
{
	private static Boolean updateAvailable = null;
	private static String newVersion = null; 
	
	public static synchronized boolean updateAvailable_block()
	{
		if( updateAvailable == null )
		{
			try
			{
				String url = "https://github.com/RoffelKartoffel/cmanager/releases.atom";
				String http = HTTP.get( url );
				
				XMLElement root = XMLParser.parse(http);

				XMLElement child = root.getChild("feed").getChild("entry").getChild("title");
				newVersion = child.getUnescapedBody();
				
				updateAvailable = !newVersion.equals(Version.VERSION);
			}
			catch(Throwable t){
				// Errors might be due to missing internet connection.
				updateAvailable = false;
			}
		}
		
		return updateAvailable;
	}
	
	public static String getNewVersion(){
		return newVersion;
	}
}
