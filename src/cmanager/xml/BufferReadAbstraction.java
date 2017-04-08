package cmanager.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

class BufferReadAbstraction
{
    private final int LIMIT = 1024 * 1024 * 10;
    private char[] cbuf = new char[LIMIT];
    private BufferedReader br;

    public BufferReadAbstraction(InputStream is) throws UnsupportedEncodingException
    {
        br = new BufferedReader(new InputStreamReader(is, "UTF-8"), LIMIT);
    }

    public BufferReadAbstraction(String s) throws UnsupportedEncodingException
    {
        br = new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(s.getBytes("UTF-8")), "UTF-8"));
    }


    public char charAt(int index) throws IOException
    {
        br.mark(index + 1);
        br.read(cbuf, 0, index + 1);
        br.reset();

        return cbuf[index];
    }


    public boolean available() throws IOException
    {
        return br.ready();
    }

    public void deleteChar() throws IOException
    {
        br.skip(1);
    }


    public void deleteUntil(int end) throws IOException
    {
        br.skip(end);
    }


    public String substring(int start, int end) throws IOException
    {
        br.mark(end + 1);
        br.read(cbuf, 0, end + 1);
        br.reset();

        return new String(cbuf, start, end - start);
    }


    public int indexOf(String str) throws IOException
    {
        br.mark(LIMIT);
        int offset = 0;
        int size = 200;

        while (true)
        {
            if (offset + size > LIMIT)
            {
                br.reset();
                return -1;
            }
            int read = br.read(cbuf, offset, size);
            offset += read;
            size = size * 2;


            final int len = str.length();
            for (int j = 0; j < offset; j++)
                if (cbuf[j] == str.charAt(0))
                {
                    boolean match = true;
                    for (int i = 1; i < len && j + i < offset; i++)
                        if (cbuf[j + i] != str.charAt(i))
                            match = false;
                    if (match)
                    {
                        br.reset();
                        return j;
                    }
                }
        }
    }

    public StringBuilder toStringBuilder() throws IOException
    {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024 * 1024];
        int readChars;
        while ((readChars = br.read(buffer)) > 0)
        {
            sb.append(buffer, 0, readChars);
        }
        return sb;
    }


    public String getHead(int max) throws IOException
    {
        max = max < LIMIT - 1 ? max : LIMIT - 1;

        br.mark(max);
        max = br.read(cbuf, 0, max);
        br.reset();

        return new String(cbuf, 0, max);
    }
}