package cmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import cmanager.XMLElement.XMLAttribute;

public class GPX 
{
	public static void xmlToCachlistWithFree(
			StringBuilder sb,
			ArrayList<Geocache> gList,
			ArrayList<Waypoint> wList) throws Throwable
	{
		XMLElement root = XMLParser.parse( sb );
		xmlToCachlistWithFree(root, gList, wList);
	}
	
	public static void fileToXmlToCachlist(
			InputStream is,
			ArrayList<Geocache> gList,
			ArrayList<Waypoint> wList) throws Throwable
	{		
		XMLElement root = XMLParser.parse( is );
		xmlToCachlistWithFree(root, gList, wList);
	}
	
	
	private static void xmlToCachlistWithFree(
			XMLElement root, 
			final ArrayList<Geocache> gList,
			final ArrayList<Waypoint> wList) throws Throwable
	{ 
		for(XMLElement e : root.getChildren())
			if( e.is("gpx") )
			{
				final XMLElement gpx = e;
				
				// single threaded version
				
//				for(XMLElement ee : gpx.getChildren())
//				{
//					if( ee.is("wpt") )
//					{
//						Geocache g = null;
//						Waypoint w = null;
//						
//						try{
//							w = toWaypoint(ee);
//							g = toCache(ee);
//						}
//						catch(NullPointerException ex){
//							ExceptionPanel.display(ex);
//						}
//						
//						if( g != null )
//						{
//							gList.add( g );
//						}
//						else if( w != null )
//						{
//							wList.add(w);
//						}
//						
//					}
//				}
				
				//
				// multi threaded version
				//	
				
				int cores = Runtime.getRuntime().availableProcessors();
				ThreadStore ts = new ThreadStore();
				for(int c=0; c<=cores; c++)
				{
					int listSize = gpx.getChildren().size();
					int perProcess = listSize / cores;
					final int start = perProcess*c;
					final int end = perProcess*(c+1) < listSize ? perProcess*(c+1) : listSize;
					
					Thread t = new Thread(new Runnable() {
						public void run() 
						{
							ArrayList<Geocache> gList_thread = new ArrayList<>();
							ArrayList<Waypoint> wList_thread = new ArrayList<>();
							
							for(int i=start; i<end; i++)
							{
								XMLElement ee = gpx.getChildren().get(i);
								if( ee.is("wpt") )
								{
									Geocache g = null;
									Waypoint w = null;
									
									try{
										w = toWaypoint(ee);
										g = toCache(ee);
									}
									catch(NullPointerException ex){
										ExceptionPanel.display(ex);
									}
									
									
									if( g != null )
									{
										gList_thread.add( g );
									}
									else if( w != null )
									{
										wList_thread.add(w);
									}
								}
								// free memory
								gpx.getChildren().set(i, null);
							}
							
							synchronized (gList) {
								gList.addAll(gList_thread);
							}
							synchronized (wList) {
								wList.addAll(wList_thread);
							}
						}
					});
					ts.add(t);
					t.start();
				}
				ts.join();
				
			}
				
				
			
	}
	
	private static Waypoint toWaypoint(XMLElement wpt)
	{
		Coordinate coordinate = null;
		String code = null;
		String description = null;
		String symbol = null;
		String type = null;
		String parent = null;
		String date = null;
		
		
		double lat = 0.0, lon = 0.0;
		for(XMLAttribute a : wpt.getAttributes())
		{
			if( a.is("lat") )
				lat = a.getValueD();
			else if( a.is("lon") )
				lon = a.getValueD();
		}
		coordinate = new Coordinate(lat, lon);
		
		for(XMLElement e : wpt.getChildren())
		{
			if( e.is("name") )
				code = e.getUnescapedBody();
			else if( e.is("desc") )
				description = e.getUnescapedBody();
			else if( e.is("sym") )
				symbol = e.getUnescapedBody();
			else if( e.is("type") )
				type = e.getUnescapedBody();
			else if( e.is("time") )
				date = e.getUnescapedBody();
			
			else if( e.is("gsak:wptExtension") )
				for(XMLElement ee : e.getChildren())
					if( ee.is("gsak:Parent") )
						parent = ee.getUnescapedBody();
		}
		
		Waypoint waypoint = new Waypoint(coordinate, code, description, symbol, type, parent); 
		waypoint.setDate(date);
		return waypoint;
	}
	
	
	private static Geocache toCache(XMLElement wpt)
	{
		String code = null;
		String urlName = null; 
		String cacheName = null;
		Coordinate coordinate = null;
		Double difficulty = null; 
		Double terrain = null; 
		String type = null;
		String owner = null;
		String container = null;
		String listing = null;
		String listingShort = null;
		String hint = null;
		Integer id = null;
		Boolean archived = null;
		Boolean available = null;
		Boolean gcPremium = null;
		Integer favPoints = null;
		
		ArrayList<GeocacheAttribute> attributes = new ArrayList<>();
        ArrayList<GeocacheLog> logs = new ArrayList<>();
		
		double lat = 0.0, lon = 0.0;
		for(XMLAttribute a : wpt.getAttributes())
		{
			if( a.is("lat") )
				lat = a.getValueD();
			else if( a.is("lon") )
				lon = a.getValueD();
		}
		coordinate = new Coordinate(lat, lon);
		
		
		boolean groundspeak_cache = false;
		for(XMLElement e : wpt.getChildren())
		{
			if( e.is("name") )
				code = e.getUnescapedBody();
			else if( e.is("urlname") )
				urlName = e.getUnescapedBody();
			
			else if( e.is("groundspeak:cache") )
			{
				groundspeak_cache = true;
				
				for(XMLAttribute a : e.getAttributes() )
				{
					if( a.is("id") )
					{
						try{
							id = new Integer( a.getValue() );
						}
						catch(Exception ex){
						}
					}
					if( a.is("archived") )
						archived = new Boolean( a.getValue() );
					else if( a.is("available") )
						available = new Boolean( a.getValue() );
				}
				
				for(XMLElement ee : e.getChildren())
				{
					if( ee.is("groundspeak:name") )
						cacheName = ee.getUnescapedBody();
					else if( ee.is("groundspeak:difficulty") )
						difficulty = ee.getBodyD();
					else if( ee.is("groundspeak:terrain") )
						terrain = ee.getBodyD();
					else if( ee.is("groundspeak:type") )	
						type = ee.getUnescapedBody();
					else if( ee.is("groundspeak:owner") )
						owner = ee.getUnescapedBody();
					else if( ee.is("groundspeak:container") )
						container = ee.getUnescapedBody();
					else if( ee.is("groundspeak:long_description") )
						listing = ee.getUnescapedBody();
					else if( ee.is("groundspeak:short_description") )
						listingShort = ee.getUnescapedBody();
					else if( ee.is("groundspeak:encoded_hints") )
						hint = ee.getUnescapedBody();
					else if( ee.is("groundspeak:logs") )
					{
                        for( XMLElement eee : ee.getChildren() )
                            if( eee.is("groundspeak:log") )
                            {
                                // skip geotoad info log
                                if( eee.attrIs("id", "-2"))
                                    continue;
                                
                                String ltype = null;
                                String author = null;
                                String text = null;
                                String date = null;

                                for( XMLElement eeee : eee.getChildren() )
                                {
                                    if( eeee.is("groundspeak:date") )
                                        date = eeee.getUnescapedBody();
                                    else if( eeee.is("groundspeak:type") )
                                        ltype = eeee.getUnescapedBody();
                                    else if( eeee.is("groundspeak:finder") )
                                        author = eeee.getUnescapedBody();
                                    else if( eeee.is("groundspeak:text") )
                                        text = eeee.getUnescapedBody();
                                }

                                if( ltype != null && ltype.equals("Other") )
                                	continue;
                                
                                try {
                                    GeocacheLog gl = new GeocacheLog(ltype, author, text, date);    
                                    logs.add(gl);
                                } catch (NullPointerException | IllegalArgumentException ex) {
                                        ExceptionPanel.display(ex);
                                }
                            }
					}
                    else if( ee.is("groundspeak:attributes") )
                        for( XMLElement eee : ee.getChildren() )
                        	if( eee.is("groundspeak:attribute") )
                            {
                        		Integer ID = null;
                        		Integer inc = null;
                        		String desc = null;
                        		
                        		for(XMLAttribute a : eee.getAttributes() )
                        			if( a.is("id") )
                        				ID = a.getValueI();
                        			else if( a.is("inc") )
                        				inc = a.getValueI();
                        		desc = eee.getUnescapedBody();
                        		
                        		try {
                                    GeocacheAttribute attr = new GeocacheAttribute(ID, inc, desc);
                                    attributes.add(attr);
                                } catch (NullPointerException | IllegalArgumentException ex) {
                                        ExceptionPanel.display(ex);
                                }
                            }
						
				}
			}
			else if( e.is("gsak:wptExtension") )
			{
				for(XMLElement ee : e.getChildren())
					if( ee.is("gsak:IsPremium") )
						gcPremium = ee.getBodyB();
					else if( ee.is("gsak:FavPoints") )
						favPoints = ee.getBodyI();
			}
		}
		
		if(!groundspeak_cache)
			return null;
		
		if( container != null && container.equals("unknown") )
			container = null;
		
		Geocache g = new Geocache(
				code, 
				cacheName != null ? cacheName : urlName, 
				coordinate, 
				difficulty, 
				terrain, 
				type);
		g.setOwner(owner);
		g.setContainer(container);
		g.setListing(listing);
		g.setListing_short(listingShort);
		g.setHint(hint);
		g.setId(id);
		g.setArchived(archived);
		g.setAvailable(available);
		g.setGcPremium(gcPremium);
		g.setFavPoints(favPoints);
		g.addWaypoints(attributes);
        g.add(logs);
        
		return g;
	}
	
	public static void cachlistToBuffer(
			ArrayList<Geocache> list, 
			String name, 
			OutputStream os) throws Throwable
	{

		ZipOutputStream zos = new ZipOutputStream(os);
//		zos.setLevel(9);
		zos.setLevel(7);
		
		if( FileHelper.getFileExtension(name).equals("zip") )
			name = name.substring(0, name.length()-4);

		Integer number = 0;
		int offset = 0;
		final int CACHES_PER_GPX = 1000;
		do
		{
			ArrayList<Geocache> partial = new ArrayList<>();
			
			for(int i=0; i<CACHES_PER_GPX && i+offset < list.size(); i++)
				partial.add(list.get(i+offset));
			offset += CACHES_PER_GPX;
			number += 1;
			
			String partialName = list.size() <= CACHES_PER_GPX ? name : name+"-"+number.toString();
			partialName += ".gpx";
			zos.putNextEntry(new ZipEntry(partialName)); 
			
			XMLElement root = cachlistToXML(partial, name);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
			XMLParser.xmlToBuffer(root, new BufferWriteAbstraction.BW( bw ));
			bw.flush();
			
			zos.closeEntry();
		}	
		while(offset < list.size());
		
		
		zos.close();
	}
	
	
	public static XMLElement cachlistToXML(final ArrayList<Geocache> list, String name)
	{
		XMLElement root = new XMLElement();
		
		final XMLElement gpx = new XMLElement("gpx");
		gpx.add(new XMLAttribute("version", "1.0"));
		gpx.add(new XMLAttribute("creator", Main.APP_NAME));
		gpx.add(new XMLAttribute("xsi:schemaLocation", "http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd http://www.groundspeak.com/cache/1/0/1 http://www.groundspeak.com/cache/1/0/1/cache.xsd http://www.gsak.net/xmlv1/6 http://www.gsak.net/xmlv1/6/gsak.xsd"));
		gpx.add(new XMLAttribute("xmlns", "http://www.topografix.com/GPX/1/0"));
		gpx.add(new XMLAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
		gpx.add(new XMLAttribute("xmlns:groundspeak", "http://www.groundspeak.com/cache/1/0/1"));
		gpx.add(new XMLAttribute("xmlns:gsak", "http://www.gsak.net/xmlv1/6"));
		gpx.add(new XMLAttribute("xmlns:cgeo", "http://www.cgeo.org/wptext/1/0"));
		root.add(gpx);
		
		gpx.add(new XMLElement("name", name));
		gpx.add(new XMLElement("desc", "Geocache file generated by " + Main.APP_NAME + " " + Main.VERSION));
		gpx.add(new XMLElement("author", Main.APP_NAME));
		
		DateTime dt = new DateTime();
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		String strDate = fmt.print(dt);
		gpx.add(new XMLElement("time", strDate));
		
		
//		for(Geocache g : list)
//		{
//			gpx.add( cacheToXML(g) );
//			for( Waypoint w : g.getWaypoints() )
//				gpx.add( waypointToXML(w) );
//		}
		
		for(Geocache g : list)
		{
			gpx.add( cacheToXML(g) );
			for( Waypoint w : g.getWaypoints() )
				gpx.add( waypointToXML(w) );
		}
		
		return root;
	}
	
	private static XMLElement waypointToXML(Waypoint w)
	{
		XMLElement wpt = new XMLElement("wpt");
		wpt.add(new XMLAttribute("lat", w.getCoordinate().getLat()));
		wpt.add(new XMLAttribute("lon", w.getCoordinate().getLon()));
		
		wpt.add( new XMLElement("time", w.getDateStrISO8601()) );
		wpt.add( new XMLElement("name", w.getCode()) );
		wpt.add( new XMLElement("desc", w.getDescription()) );
		wpt.add( new XMLElement("sym", w.getSymbol()) );
		wpt.add( new XMLElement("type", w.getType()) );
			
		XMLElement gsakExtension = new XMLElement("gsak:wptExtension");
		wpt.add(gsakExtension);
		gsakExtension.add( new XMLElement("gsak:Parent", w.getParent()));
		
		return wpt;
	}
	
	
	private static XMLElement cacheToXML(Geocache g)
	{
		XMLElement wpt = new XMLElement("wpt");
		wpt.add(new XMLAttribute("lat", g.getCoordinate().getLat()));
		wpt.add(new XMLAttribute("lon", g.getCoordinate().getLon()));
		
		wpt.add( new XMLElement("name", g.getCode()) );
		wpt.add( new XMLElement("urlname", g.getName()) );
		
		XMLElement gspkCache = new XMLElement("groundspeak:cache");
		gspkCache.add( new XMLAttribute("id", g.getId()) );
		gspkCache.add( new XMLAttribute("available", g.getAvailable()) );
		gspkCache.add( new XMLAttribute("archived", g.getArchived()) );
		wpt.add(gspkCache);	
		
		
		XMLElement gspkAttrs = new XMLElement("groundspeak:attributes");
		gspkCache.add(gspkAttrs);
		for(GeocacheAttribute attr : g.getAttributes() )
		{
			XMLElement gspkAttr = new XMLElement("groundspeak:attribute");
			gspkAttr.add(new XMLAttribute("id", attr.getID()));
			gspkAttr.add(new XMLAttribute("inc", attr.getInc()));
			gspkAttr.setBody(attr.getDesc());
			gspkAttrs.add(gspkAttr);
		}
		
		gspkCache.add( new XMLElement("groundspeak:name", g.getName()));
		gspkCache.add( new XMLElement("groundspeak:difficulty", g.getDifficulty()));
		gspkCache.add( new XMLElement("groundspeak:terrain", g.getTerrain()));
		gspkCache.add( new XMLElement("groundspeak:type", g.getTypeAsGC()));
		gspkCache.add( new XMLElement("groundspeak:owner", g.getOwner()));
		gspkCache.add( new XMLElement("groundspeak:container", g.getContainerAsGC()));
		gspkCache.add( new XMLElement("groundspeak:long_description", g.getListing()));
		gspkCache.add( new XMLElement("groundspeak:short_description", g.getListing_short()));
		gspkCache.add( new XMLElement("groundspeak:encoded_hints", g.getHint()));
		
		if( g.getLogs().size() > 0 )
		{
			XMLElement gspkLogs = new XMLElement("groundspeak:logs");
			gspkCache.add(gspkLogs);
			
			for( GeocacheLog log : g.getLogs() )
			{
				XMLElement gspkLog = new XMLElement("groundspeak:log");
				gspkLogs.add(gspkLog);
				
				gspkLog.add( new XMLElement("groundspeak:date", log.getDateStrISO8601()) );
				gspkLog.add( new XMLElement("groundspeak:type", log.getTypeStr()) );
				gspkLog.add( new XMLElement("groundspeak:finder", log.getAuthor()) );
				gspkLog.add( new XMLElement("groundspeak:text", log.getText()) );
			}
		}

		XMLElement gsakExtension = new XMLElement("gsak:wptExtension");
		gsakExtension.add( new XMLElement("gsak:IsPremium", g.getGcPremium() ));
		gsakExtension.add( new XMLElement("gsak:FavPoints", g.getFavPoints() ));
		wpt.add(gsakExtension);
		
		return wpt;
	}

}
