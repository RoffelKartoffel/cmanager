package cmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileHelper 
{
	public static <T extends Serializable> T deserialize(InputStream is, Class<T> type) throws IOException, ClassNotFoundException 
	{
        ObjectInputStream in = new ObjectInputStream(is);
        @SuppressWarnings("unchecked")
        T e = (T) in.readObject();
        in.close();
        return e;
	}
	
	public static <T extends Serializable> T deserializeFromFile(String path, Class<T> type) throws IOException, ClassNotFoundException 
	{
		FileInputStream fileIn = new FileInputStream(path);
        T e = deserialize(fileIn, type);
        fileIn.close();
        return e;
	}
	
	public static void serialize(Serializable s, OutputStream os) throws IOException
	{
		ObjectOutputStream out = new ObjectOutputStream(os);
		out.writeObject(s);
		out.close();
	}
	
	public static void serializeToFile(Serializable s, String path) throws IOException
	{
		FileOutputStream fileOut =
		         new FileOutputStream(path);
		serialize(s, fileOut);
		fileOut.close();
	}
	
	
	public static void processFiles(String path, FileHelper.InputAction ia) throws Throwable
	{
		if( path.toLowerCase().endsWith(".zip") )
			processZipFile(path, ia);
		else
			processFile(path, ia);
	}
	
	
//	public static StringBuilder readFileToSB(String path) throws IOException
//	{
//		StringBuilder sb = new StringBuilder( );
//		
//		if( path.toLowerCase().endsWith(".zip") )
//			readZipFileToSB(sb, path);
//		else
//			readTextFileToSB(sb, path);
//		
//		return sb;
//	}
//	
	
	private static void processZipFile(String path, FileHelper.InputAction ia) throws Throwable
	{
		processZipFile( new FileInputStream(path), ia );
	}
	
	private static void processZipFile(InputStream is, FileHelper.InputAction ia) throws Throwable
	{
		
		//get the zip file content
    	ZipInputStream zis = new ZipInputStream(is);
    	//get the zipped file list entry
    	ZipEntry ze = zis.getNextEntry();
    	
    	while(ze!=null)
    	{
    	   	String fileName = ze.getName();
			if( fileName.toLowerCase().endsWith(".zip") )
				processZipFile(zis, ia);
			else
			{
				ia.process(zis);
			}
				
			ze = zis.getNextEntry();
     	}
    	zis.closeEntry();
//    	zis.close();	// crashes on recursion
    	
	}
	
	
//	private static void readZipFileToSB(StringBuilder sb, String path) throws IOException
//	{
//		readZipFileToSB(sb, new FileInputStream(path));
//	}
	
//	private static void readZipFileToSB(StringBuilder sb, InputStream is) throws IOException
//	{
//		//get the zip file content
//    	ZipInputStream zis = new ZipInputStream(is);
//    	//get the zipped file list entry
//    	ZipEntry ze = zis.getNextEntry();
//    	
//    	byte[] buffer = new byte[1024*1024*10];
//    	while(ze!=null)
//    	{
//    	   	String fileName = ze.getName();
//			if( fileName.toLowerCase().endsWith(".zip") )
//				readZipFileToSB(sb, zis);
//			else
//			{
//				int len;
//				ByteArrayOutputStream out = new ByteArrayOutputStream();
//				while ((len = zis.read(buffer)) > 0) {
//					out.write(buffer, 0, len);
//				}
//				sb.append( out.toString("UTF-8") );
//			}
//				
//			ze = zis.getNextEntry();
//     	}
//    	zis.closeEntry();
////    	zis.close();	// crashes on recursion
//	}
	
	private static void processFile(String path, FileHelper.InputAction ia) throws Throwable
	{
		ia.process( new FileInputStream(path) );
	}
	
//	private static void readTextFileToSB(StringBuilder sb, String path) throws IOException
//	{
//		BufferedReader in = new BufferedReader(
//				   new InputStreamReader(new FileInputStream(path), "UTF8"));
//
//		char[] buffer = new char[1024 * 1024 * 10];
//		int readChars;
//		while ((readChars = in.read(buffer)) > 0) {
//			sb.append(buffer, 0, readChars);
//	    }
//		in.close();
//	}
	
//	public static void writeToFile(String path, String s) throws IOException
//	{
//		BufferedWriter bwr = new BufferedWriter(
//				new OutputStreamWriter(new FileOutputStream(path),"UTF-8"));
//        bwr.write(s);
//        bwr.flush();
//        bwr.close();
//	}
	
	public static OutputStream openFileWrite(String path) throws IOException
	{
//		BufferedWriter bwr = new BufferedWriter(
//				new OutputStreamWriter(new FileOutputStream(path),"UTF-8"));
        
		return new FileOutputStream(path);
	}
	
	public static void closeFileWrite(BufferedWriter bwr) throws IOException
	{
		bwr.flush();
        bwr.close();
	}
	
	public static String getFileExtension(String fileName){
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
		    extension = fileName.substring(i+1);
		}
		return extension.toLowerCase();
	}
	
	public abstract static class InputAction
	{
		public abstract void process(InputStream is) throws Throwable;
	}
}
