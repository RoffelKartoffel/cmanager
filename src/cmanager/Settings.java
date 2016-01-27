package cmanager;

import java.util.prefs.Preferences;

public class Settings {
	
	private static Preferences prefs = Preferences.userRoot().node(Main.APP_NAME);
	
	public enum Key {
		HEAP_SIZE,
		
		GC_USERNAME,
		FILE_CHOOSER_LOAD_GPX,
		
		OKAPI_TOKEN,
		OKAPI_SECRET
	}
	
	public static String key(Key key) {
		switch(key)
		{
		case HEAP_SIZE:
			return "javaHeapSize";
		case GC_USERNAME:
			return "gcUsername";
		case FILE_CHOOSER_LOAD_GPX:
			return "fileChooserGpx";
		case OKAPI_TOKEN:
			return "OKAPIToken";
		case OKAPI_SECRET:
			return "OKAPISecret";
			
		default:
			return null;
		}
	}
	
	public static String defaultS(Key key){
		switch(key)
		{
		case GC_USERNAME:
		case FILE_CHOOSER_LOAD_GPX:
			return "";
		
			
		default:
			return null;
		}
	}
	
	public static void set(Key key, String val){
		prefs.put(key(key), val);
	}
	
	public static String getS(Key key){
		return prefs.get(key(key), defaultS(key));
	}

}
