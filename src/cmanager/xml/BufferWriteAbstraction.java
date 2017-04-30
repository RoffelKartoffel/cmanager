package cmanager.xml;

import java.io.BufferedWriter;
import java.io.IOException;

abstract class BufferWriteAbstraction
{
    public abstract BufferWriteAbstraction append(String s) throws IOException;
    public abstract String toString();

    public BufferWriteAbstraction append(BufferWriteAbstraction bwa) throws IOException
    {
        return append(bwa.toString());
    }


    public static class SB extends BufferWriteAbstraction
    {
        private StringBuilder sb = null;

        @SuppressWarnings("unused")
        private SB()
        {
        }

        public SB(StringBuilder sb)
        {
            this.sb = sb;
        }

        @Override
        public BufferWriteAbstraction append(String s)
        {
            sb.append(s);
            return this;
        }

        public String toString()
        {
            return sb.toString();
        }
    }

    public static class BW extends BufferWriteAbstraction
    {
        private BufferedWriter bw = null;

        @SuppressWarnings("unused")
        private BW()
        {
        }

        public BW(BufferedWriter bw)
        {
            this.bw = bw;
        }

        @Override
        public BufferWriteAbstraction append(String s) throws IOException
        {
            bw.write(s);
            return this;
        }

        @Override
        public String toString()
        {
            throw new IllegalAccessError();
        }

        public BufferedWriter getBW()
        {
            return bw;
        }
    }
}
