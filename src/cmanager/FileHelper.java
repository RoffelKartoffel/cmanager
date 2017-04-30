package cmanager;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileHelper
{
    public static <T extends Serializable> T deserialize(InputStream is)
        throws IOException, ClassNotFoundException
    {
        ObjectInputStream in = new ObjectInputStream(is);
        @SuppressWarnings("unchecked")
        T e = (T)in.readObject();
        in.close();
        return e;
    }

    public static <T extends Serializable> T deserializeFromFile(String path)
        throws IOException, ClassNotFoundException
    {
        FileInputStream fileIn = new FileInputStream(path);
        T e = deserialize(fileIn);
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
        FileOutputStream fileOut = new FileOutputStream(path);
        serialize(s, fileOut);
        fileOut.close();
    }


    public static void processFiles(String path, FileHelper.InputAction ia) throws Throwable
    {
        String lcPath = path.toLowerCase();
        if (lcPath.endsWith(".zip"))
            processZipFile(path, ia);
        else if (lcPath.endsWith(".gz"))
            processGZipFile(path, ia);
        else
            processFile(path, ia);
    }

    private static void processGZipFile(String path, FileHelper.InputAction ia) throws Throwable
    {
        processGZipFile(new FileInputStream(path), ia);
    }

    private static void processGZipFile(InputStream is, FileHelper.InputAction ia) throws Throwable
    {
        // get the gzip file content
        GZIPInputStream gzis = new GZIPInputStream(is);
        ia.process(gzis);
        gzis.close();
    }

    private static void processZipFile(String path, FileHelper.InputAction ia) throws Throwable
    {
        processZipFile(new FileInputStream(path), ia);
    }

    private static void processZipFile(InputStream is, FileHelper.InputAction ia) throws Throwable
    {

        // get the zip file content
        ZipInputStream zis = new ZipInputStream(is);
        // get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();

        while (ze != null)
        {
            String fileName = ze.getName();
            if (fileName.toLowerCase().endsWith(".zip"))
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

    private static void processFile(String path, FileHelper.InputAction ia) throws Throwable
    {
        ia.process(new FileInputStream(path));
    }

    public static OutputStream openFileWrite(String path) throws IOException
    {
        return new FileOutputStream(path);
    }

    public static void closeFileWrite(BufferedWriter bwr) throws IOException
    {
        bwr.flush();
        bwr.close();
    }

    public static String getFileExtension(String fileName)
    {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0)
        {
            extension = fileName.substring(i + 1);
        }
        return extension.toLowerCase();
    }

    public abstract static class InputAction
    {
        public abstract void process(InputStream is) throws Throwable;
    }
}
