package cmanager.xml;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;

import cmanager.MalFormedException;
import cmanager.ThreadStore;
import cmanager.xml.Element.XMLAttribute;

public class Parser
{
    public static Element parse(String element) throws MalFormedException, IOException
    {
        return parse(new BufferReadAbstraction(element), null);
    }

    public static Element parse(InputStream is, XMLParserCallbackI callback)
        throws MalFormedException, IOException
    {
        return parse(new BufferReadAbstraction(is), callback);
    }


    private static Element parse(BufferReadAbstraction element, XMLParserCallbackI callback)
        throws MalFormedException, IOException
    {
        Element root = new Element();
        do
        {
            removeDelimiter(element);
            if (element.substring(0, 5).equals("<?xml"))
            {
                int index = element.indexOf("?>");
                element.deleteUntil(index + 2);
            }
            removeDelimiter(element);
            if (element.substring(0, 9).equals("<!DOCTYPE"))
            {
                int index = element.indexOf(">");
                element.deleteUntil(index + 1);
            }
            parse(element, root, callback);
            removeDelimiter(element);
        } while (element.available());

        return root;
    }

    private static void parse(BufferReadAbstraction element, Element root,
                              XMLParserCallbackI callback) throws MalFormedException, IOException
    {
        removeDelimiter(element);
        if (element.charAt(0) != '<')
            throw new MalFormedException();
        if (element.charAt(1) == '/')
            return;

        Element ele = new Element();

        int nameEnd = endOfName(element);
        String elementName = element.substring(1, nameEnd);
        element.deleteUntil(nameEnd);
        ele.setName(elementName);

        // parse attributes
        removeDelimiter(element);
        while (element.charAt(0) != '>')
        {
            removeDelimiter(element);

            // catch /> endings
            if ((element.charAt(0) == '/' && element.charAt(1) == '>'))
            {
                element.deleteChar();
                element.deleteChar();

                parse(element, root, callback);

                if (callback != null && !callback.elementLocatedCorrectly(ele, root))
                    throw new MalFormedException();

                if (callback == null || !callback.elementFinished(ele))
                    root.getChildren().add(ele);
                return;
            }

            // Tag is not closed => an attribute is following
            int index = element.indexOf("=");
            String attrName = element.substring(0, index);
            element.deleteUntil(index + 1);

            String attrVal = null;
            char marking = element.charAt(0);
            if (marking == '"' || marking == '\'')
            {
                element.deleteChar();
                index = element.indexOf(String.valueOf(marking));
                attrVal = element.substring(0, index);
                element.deleteUntil(index + 1);
            }
            else
                throw new MalFormedException();

            XMLAttribute attr = new XMLAttribute(attrName);
            attr.setValue(StringEscapeUtils.unescapeXml(attrVal));
            ele.getAttributes().add(attr);
        }
        element.deleteChar();

        while (true)
        {
            int startOfName = element.indexOf("<");
            if (startOfName == -1)
            {
                StringBuilder element_tmp = element.toStringBuilder();
                trim(element_tmp);
                if (element_tmp.length() == 0)
                    break;
                else
                    throw new MalFormedException();
            }

            StringBuilder body = new StringBuilder(element.substring(0, startOfName));
            trim(body);
            ele.setBody(body.toString());

            element.deleteUntil(startOfName);

            if (element.charAt(1) == '/')
            {
                element.deleteChar();
                element.deleteChar();

                if (!element.substring(0, elementName.length() + 1).equals(elementName + ">"))
                    throw new MalFormedException();
                element.deleteUntil(elementName.length() + 1);

                break;
            }
            else
            {
                parse(element, ele, callback);
            }
        }

        if (callback != null && !callback.elementLocatedCorrectly(ele, root))
            throw new MalFormedException();

        if (callback == null || !callback.elementFinished(ele))
            root.getChildren().add(ele);
    }


    static void trim(StringBuilder sb)
    {
        removeDelimiter(sb);

        while (sb.length() > 0 && isDelimiter(sb.charAt(sb.length() - 1)))
            sb.deleteCharAt(sb.length() - 1);
    }

    static int endOfName(BufferReadAbstraction sb) throws IOException
    {
        int i = 0;
        while (!isDelimiter(sb.charAt(i)) && sb.charAt(i) != '>' &&
               !(sb.charAt(i) == '?' && sb.charAt(i + 1) == '>'))
            i++;
        return i;
    }

    static boolean isDelimiter(char c)
    {
        if (c == ' ' || c == '\n' || c == '\t' || c == '\r')
            return true;
        else
            return false;
    }

    static void removeDelimiter(BufferReadAbstraction sb) throws IOException
    {
        while (sb.available() && isDelimiter(sb.charAt(0)))
            sb.deleteChar();
    }

    static void removeDelimiter(StringBuilder sb)
    {
        while (sb.length() > 0 && isDelimiter(sb.charAt(0)))
            sb.deleteCharAt(0);
    }


    public static void xmlToBuffer(Element root, OutputStream os) throws Throwable
    {
        shrinkXMLTree(root);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        BufferWriteAbstraction bwa = new BufferWriteAbstraction.BW(bw);

        bwa.append("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>\n");
        for (Element child : root.getChildren())
            xmlToBuffer(child, bwa, 0);

        bw.flush();
    }

    private static void shrinkXMLTree(final Element e) throws Throwable
    {
        if (e.getChildren().size() < 100)
        {
            for (Element child : e.getChildren())
                shrinkXMLTree(child);
        }
        else
        {
            int listSize = e.getChildren().size();
            ThreadStore ts = new ThreadStore();
            int cores = ts.getCores(listSize);
            int perProcess = listSize / cores;

            for (int c = 0; c < cores; c++)
            {
                final int start = perProcess * c;

                int tmp = perProcess * (c + 1) < listSize ? perProcess * (c + 1) : listSize;
                if (c == cores - 1)
                    tmp = listSize;
                final int end = tmp;

                ts.addAndRun(new Thread(new Runnable() {
                    public void run()
                    {
                        try
                        {
                            for (int i = start; i < end; i++)
                            {
                                shrinkXMLTree(e.getChildren().get(i));
                            }
                        }
                        catch (Throwable ex)
                        {
                            Thread t = Thread.currentThread();
                            t.getUncaughtExceptionHandler().uncaughtException(t, ex);
                        }
                    }
                }));
            }
            ts.joinAndThrow();
        }


        Iterator<Element> it = e.getChildren().iterator();
        while (it.hasNext())
        {
            Element child = it.next();
            if (child.getUnescapedBody() == null && child.getAttributes().size() == 0 &&
                child.getChildren().size() == 0)
                it.remove();
        }
    }

    private static void xmlToBuffer(final Element e, final BufferWriteAbstraction bwa,
                                    final int level) throws Throwable
    {
        String name = e.getName();

        appendSpaces(bwa, level);
        bwa.append("<").append(name);
        for (XMLAttribute a : e.getAttributes())
        {
            if (a.getValue() != null)
            {
                bwa.append(" ").append(a.getName()).append("=\"");
                bwa.append(StringEscapeUtils.escapeXml11(a.getValue())).append("\"");
            }
        }

        if (e.getUnescapedBody() == null && e.getChildren().size() == 0)
            bwa.append(" />\n");
        else
        {
            bwa.append(">");
            if (e.getChildren().size() != 0)
                bwa.append("\n");
            if (e.getChildren().size() > 200)
            {
                //
                // use multiple threads, if there are many children e.g. the
                // children of "gpx"
                //
                int listSize = e.getChildren().size();
                ThreadStore ts = new ThreadStore();
                int cores = ts.getCores(listSize);
                int perProcess = listSize / cores;

                for (int c = 0; c < cores; c++)
                {
                    final int start = perProcess * c;

                    int tmp = perProcess * (c + 1) < listSize ? perProcess * (c + 1) : listSize;
                    if (c == cores - 1)
                        tmp = listSize;
                    final int end = tmp;

                    ts.addAndRun(new Thread(new Runnable() {
                        public void run()
                        {
                            try
                            {
                                BufferWriteAbstraction.SB bwa_thread =
                                    new BufferWriteAbstraction.SB(new StringBuilder());
                                for (int i = start; i < end; i++)
                                {
                                    Element child = e.getChildren().get(i);
                                    xmlToBuffer(child, bwa_thread, level + 1);

                                    // Flush each n elements
                                    if (i % 100 == 0)
                                    {
                                        synchronized (bwa)
                                        {
                                            bwa.append(bwa_thread);
                                        }
                                        bwa_thread =
                                            new BufferWriteAbstraction.SB(new StringBuilder());
                                    }
                                }

                                synchronized (bwa)
                                {
                                    bwa.append(bwa_thread);
                                }
                            }
                            catch (Throwable ex)
                            {
                                Thread t = Thread.currentThread();
                                t.getUncaughtExceptionHandler().uncaughtException(t, ex);
                            }
                        }
                    }));
                }
                ts.joinAndThrow();

                //
                //
                //
            }
            else
            {
                for (Element child : e.getChildren())
                    xmlToBuffer(child, bwa, level + 1);
            }
            if (e.getUnescapedBody() != null)
                bwa.append(StringEscapeUtils.escapeXml11(e.getUnescapedBody()));
            else
                appendSpaces(bwa, level);
            bwa.append("</").append(name).append(">\n");
        }
    }

    private static void appendSpaces(BufferWriteAbstraction bwa, int faktor) throws IOException
    {
        for (int i = 0; i < faktor * 2; i++)
            bwa.append(" ");
    }

    ///////////////////////////////
    // XMLParserCallbackI
    ///////////////////////////////

    public static interface XMLParserCallbackI {
        public boolean elementLocatedCorrectly(Element element, Element parent);
        public boolean elementFinished(Element element);
    }
}
