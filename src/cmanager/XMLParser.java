package cmanager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;

import cmanager.XMLElement.XMLAttribute;

public class XMLParser 
{
	public static XMLElement parse(String element) 
			throws MalFormedException, IOException
	{
		return parse(new BufferAbstraction(element));
	}
	
	public static XMLElement parse(StringBuilder element) 
			throws MalFormedException, IOException
	{
		return parse(new BufferAbstraction(element));
	}
	
	public static XMLElement parse(InputStream is) 
			throws MalFormedException, IOException
	{
		return parse(new BufferAbstraction(is));
	}
	
	
	private static XMLElement parse(BufferAbstraction element) 
			throws MalFormedException, IOException
	{
		XMLElement root = new XMLElement();
		do
		{
			if( element.substring(0, 5).equals("<?xml") )
			{
				int index = element.indexOf("?>");
				element.deleteUntil(index +2);
			}
			parse(element, root);
			removeDelimiter(element);
		}
		while( element.available() );
		
		return root;
	}
	
	private static void parse(BufferAbstraction element, XMLElement root) 
			throws MalFormedException, IOException
	{
		removeDelimiter(element);
		if( element.charAt(0) != '<' )
			throw new MalFormedException();
		if( element.charAt(1) == '/' )
			return;
		
		XMLElement ele = new XMLElement();
		root.getChildren().add(ele);
		
		int nameEnd = endOfName(element);
		String elementName = element.substring(1, nameEnd);
		element.deleteUntil(nameEnd);
		ele.setName(elementName);
		
		// parse attributes
		removeDelimiter(element);
		while( element.charAt(0) != '>' )
		{
			removeDelimiter(element);
			
			// catch ?> endings
			// catch /> endings
			if( (element.charAt(0) == '?' && element.charAt(1) == '>')
					|| (element.charAt(0) == '/' && element.charAt(1) == '>') )
			{
				element.deleteChar();
				element.deleteChar();
				
				parse(element, root);
				return;		// end of <? .. ?> 
			}
			
			int index = element.indexOf("=");
			String attrName = element.substring(0, index);
			element.deleteUntil(index+1);
			
			String attrVal = null;
			if( element.charAt(0) == '"' )
			{
				element.deleteChar();
				index = element.indexOf("\"");
				attrVal = element.substring(0, index);
				element.deleteUntil(index+1);
			}
			else 
				throw new MalFormedException();
			
			XMLAttribute attr = new XMLAttribute(attrName);
			attr.setValue( StringEscapeUtils.unescapeXml(attrVal) );
			ele.getAttributes().add(attr);
		}
		element.deleteChar();
		
		while(true)
		{
			int startOfName = element.indexOf("<");
			if( startOfName == -1 )
			{
				StringBuilder element_tmp = element.toStringBuilder();
				trim(element_tmp);
				if(element_tmp.length() == 0 )
					return;
				else
					throw new MalFormedException();
			}
			
			StringBuilder body = new StringBuilder( element.substring(0, startOfName) );
			trim(body);	
			ele.setBody( body.toString() );
			
			element.deleteUntil(startOfName);
			
			if( element.charAt(1) == '/' )
			{
				element.deleteChar();
				element.deleteChar();
				
				if( !element.substring(0, elementName.length() +1 ).equals(elementName + ">" ) )
					throw new MalFormedException();
				element.deleteUntil(elementName.length() +1);
				
				break;
			}
			else
			{
				parse(element, ele);
			}
		}
	}
	
	
	static void trim(StringBuilder sb)
	{	
		removeDelimiter(sb);
		
		while( sb.length() > 0 && isDelimiter( sb.charAt( sb.length() -1 ) ) )
			sb.deleteCharAt( sb.length() -1 );

	}
	
	static int endOfName(BufferAbstraction sb) throws IOException
	{
		int i = 0;
		while( !isDelimiter(sb.charAt(i)) && sb.charAt(i) != '>' 
				&& !( sb.charAt(i) == '?' && sb.charAt(i+1) == '>' ) )
			i++;
		return i;
	}
	
	static boolean isDelimiter(char c)
	{
		if( c == ' ' || c == '\n' || c == '\t' || c == '\r' )
			return true;
		else
			return false;
	}
	
	static void removeDelimiter(BufferAbstraction sb) throws IOException
	{
		while( sb.available() && isDelimiter( sb.charAt(0) ) )
			sb.deleteChar();
	}
	
	static void removeDelimiter(StringBuilder sb)
	{
		while( sb.length() > 0 && isDelimiter( sb.charAt(0) ) )
			sb.deleteCharAt(0);
	}
	
	
	public static void xmlToBuffer(XMLElement root, BufferWriteAbstraction bwa) throws Throwable
	{
		shrinkXMLTree(root);
		
		bwa.append("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n");
		for(XMLElement child : root.getChildren())
			xmlToBuffer(child, bwa, 0);
	}
	
	private static void shrinkXMLTree(final XMLElement e) throws Throwable
	{
		if( e.getChildren().size() <  100 )
		{
			for(XMLElement child : e.getChildren())
				shrinkXMLTree(child);
		}
		else
		{
			int cores = Runtime.getRuntime().availableProcessors();
			ThreadStore ts = new ThreadStore();
			for(int c=0; c<=cores; c++)
			{
				int listSize = e.getChildren().size();
				int perProcess = listSize / cores;
				final int start = perProcess*c;
				final int end = perProcess*(c+1) < listSize ? perProcess*(c+1) : listSize;
				
				Thread t = new Thread(new Runnable() {
					public void run() 
					{
						try 
						{
							for(int i= start; i<end; i++)
							{
								shrinkXMLTree(e.getChildren().get(i));
							}
						}catch(Throwable ex){
							Thread t = Thread.currentThread();
						    t.getUncaughtExceptionHandler().uncaughtException(t, ex);
						}
					}
				});
				ts.add(t);
				t.start();
			}
			ts.join();
		}
		
		
		Iterator<XMLElement> it = e.getChildren().iterator();
		while(it.hasNext())
		{
			XMLElement child = it.next();
			if( child.getUnescapedBody() == null && child.getAttributes().size() == 0 && child.getChildren().size() == 0 )
				it.remove();
		}
	}
	
	private static void xmlToBuffer(final XMLElement e, final BufferWriteAbstraction bwa, final int level) throws Throwable
	{
		String name = e.getName();
		
		appendSpaces(bwa, level);
		bwa.append("<").append(name);
		for(XMLAttribute a : e.getAttributes() )
		{
			if( a.getValue() != null )
			{
				bwa.append(" ").append(a.getName()).append("=\"");
				bwa.append( StringEscapeUtils.escapeXml11( a.getValue() ) ).append("\"");
			}
		}
		
		if( e.getUnescapedBody() == null && e.getChildren().size() == 0 )
			bwa.append(" />\n");
		else
		{
			bwa.append(">");
			if( e.getChildren().size() != 0 )
				bwa.append("\n");
			if( e.getChildren().size() > 200 )
			{
				//
				// use multiple threads, if there are many children e.g. the children of "gpx"
				//
				int cores = Runtime.getRuntime().availableProcessors();
				ThreadStore ts = new ThreadStore();
				for(int c=0; c<=cores; c++)
				{
					int listSize = e.getChildren().size();
					int perProcess = listSize / cores;
					final int start = perProcess*c;
					final int end = perProcess*(c+1) < listSize ? perProcess*(c+1) : listSize;
					
					Thread t = new Thread(new Runnable(){
						public void run() 
						{
							try {
								BufferWriteAbstraction.SB bwa_thread = 
										new BufferWriteAbstraction.SB(new StringBuilder());
								for(int i=start; i<end; i++)
								{
									XMLElement child = e.getChildren().get(i);
									xmlToBuffer(child, bwa_thread, level+1);
									
									// Flush each n elements
									if( i % 100 == 0 )
									{
										synchronized (bwa) {
											bwa.append(bwa_thread);
										}
										bwa_thread = new BufferWriteAbstraction.SB(new StringBuilder());
									}
								}
								
								synchronized (bwa) {
									bwa.append(bwa_thread);
								}
							}catch(Throwable ex){
								Thread t = Thread.currentThread();
							    t.getUncaughtExceptionHandler().uncaughtException(t, ex);
							}
						}
					});
					ts.add(t);
					t.start();
				}
				
				ts.join();
				
				//
				//
				//
			}
			else
			{
				for( XMLElement child : e.getChildren() )
					xmlToBuffer(child, bwa, level+1);
			}
			if( e.getUnescapedBody() != null )
				bwa.append( StringEscapeUtils.escapeXml11( e.getUnescapedBody() ) );
			else
				appendSpaces(bwa, level);
			bwa.append("</").append(name).append(">\n");
		}		
	}
	
	private static void appendSpaces(BufferWriteAbstraction bwa, int faktor) throws IOException
	{
		for(int i=0; i<faktor*2; i++)
			bwa.append(" ");
	}
	
	
	
	
	
	
	
	
	
		
	private static class BufferAbstraction 
	{
		private final int LIMIT = 1024*512;
		private char[] cbuf = new char[LIMIT]; 
		private BufferedReader br; 
	
		
		public BufferAbstraction(StringBuilder sb) throws UnsupportedEncodingException{
			this(sb.toString());
		}
		
		public BufferAbstraction(InputStream is) throws UnsupportedEncodingException
		{
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"), LIMIT*2);
		}
		
		public BufferAbstraction(String s) throws UnsupportedEncodingException{
			br = new BufferedReader(new InputStreamReader( 
					new ByteArrayInputStream( s.getBytes("UTF-8")) , "UTF-8"));
		}
		
		

		public char charAt(int index) throws IOException{
			br.mark(index+1);
			br.read(cbuf, 0, index+1);
			br.reset();
			
			return cbuf[index];
		}
		
		
		public boolean available() throws IOException
		{
			return br.ready();
		}
		
		public void deleteChar() throws IOException{			
			br.skip(1);
		}

		
		public void deleteUntil(int end) throws IOException{
			br.skip(end);
		}
		
		
		public String substring(int start, int end) throws IOException{
			br.mark(end+1);
			br.read(cbuf, 0, end+1);
			br.reset();
			
			return new String(cbuf, start, end-start);
		}


		
		 public int indexOf(String str) throws IOException{
			br.mark(LIMIT);
			int offset = 0;
			int size = 200;
			
			while(true)
			{
				if( offset + size > LIMIT )
				{
					br.reset();
					return -1;
				}
				int read = br.read(cbuf, offset, size);
				offset += read;
				size = size*2;
				
				
				final int len = str.length();
				for(int j=0; j<offset; j++)
					if( cbuf[j] == str.charAt(0) )
					{
						boolean match = true;
						for(int i=1; i<len && j+i < offset; i++)
							if( cbuf[j+i] != str.charAt(i) )
								match = false;
						if(match)
						{
							br.reset();
							return j;
						}
					}
			}
		}
		
		public StringBuilder toStringBuilder() throws IOException {
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[1024 * 1024];
			int readChars;
			while ((readChars = br.read(buffer)) > 0) {
				sb.append(buffer, 0, readChars);
		    }
			return sb;
		}
		
	}
	
	
//	private static class BufferAbstraction {
//		
//		private StringBuilder sb;
//		private int offset = 0;
//		
//		public BufferAbstraction(StringBuilder sb){
//			this.sb = sb;
//		}
//		
//		public BufferAbstraction(String s){
//			sb = new StringBuilder(s);
//		}
//		
//		
////		private void sync(){
////			sb.delete(0, offset);
////			offset = 0;
////		}
//		
//		
//		public char charAt(int index){
//			return sb.charAt(index +offset);
//		}
//		
//		public int length(){
//			int l = sb.length() - offset;
//			return l >= 0 ? l : 0;
//		}
//		
//		public void deleteChar(){
//			offset++;
////			sync();
//		}
//		
//		public void deleteCharAt(int index){
//			sb.deleteCharAt(index+offset);
////			sync();
//		}
//		
//		public void deleteUntil(int end){
//			offset += end;
////			sync();
//		}
//		
//		
//		public String substring(int start, int end){
//			return sb.substring(start +offset, end +offset);
//		}
//
//		public int indexOf(String str){
//			int index = sb.indexOf(str, offset);
//			return index != -1 ? index - offset : -1;
//		}
//		
//	}

}
