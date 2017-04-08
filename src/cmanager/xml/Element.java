package cmanager.xml;

import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;

public class Element
{
    private String elementName = null;
    private String body = null;
    private ArrayList<Element> children = new ArrayList<Element>();
    private ArrayList<XMLAttribute> attributes = new ArrayList<XMLAttribute>();


    public Element()
    {
    }

    public Element(String name)
    {
        setName(name);
    }

    public Element(String name, String body)
    {
        setName(name);
        setBody(body);
    }

    public Element(String name, Double body)
    {
        setName(name);
        setBody(body != null ? body.toString() : null);
    }

    public Element(String name, Integer body)
    {
        setName(name);
        setBody(body != null ? body.toString() : null);
    }

    public Element(String name, Boolean body)
    {
        setName(name);
        setBody(body != null ? body.toString() : null);
    }


    public void setName(String name)
    {
        elementName = name;
    }

    public String getName()
    {
        return elementName;
    }

    public boolean is(String name)
    {
        return this.elementName.equals(name);
    }


    public boolean attrIs(String attr, String is)
    {
        for (XMLAttribute a : attributes)
            if (a.is(attr, is))
                return true;
        return false;
    }

    public void add(Element child)
    {
        children.add(child);
    }

    public ArrayList<Element> getChildren()
    {
        return children;
    }

    public Element getChild(String name)
    {
        for (Element e : children)
            if (e.is(name))
                return e;
        return null;
    }


    public void add(XMLAttribute attr)
    {
        attributes.add(attr);
    }

    public ArrayList<XMLAttribute> getAttributes()
    {
        return attributes;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getUnescapedBody()
    {
        return body == null ? null : StringEscapeUtils.unescapeXml(body);
    }

    public Double getBodyD()
    {
        return new Double(body);
    }

    public Integer getBodyI()
    {
        return new Integer(body);
    }

    public Boolean getBodyB()
    {
        return new Boolean(body);
    }


    public static class XMLAttribute
    {
        private String name;
        private String value = null;

        public XMLAttribute(String name)
        {
            this.name = name;
        }

        public XMLAttribute(String name, String value)
        {
            this.name = name;
            this.value = value;
        }

        public XMLAttribute(String name, Double value)
        {
            this.name = name;
            this.value = value != null ? value.toString() : null;
        }

        public XMLAttribute(String name, Boolean value)
        {
            this.name = name;
            this.value = value == null ? null : value == true ? "True" : "False";
        }

        public XMLAttribute(String name, Integer value)
        {
            this.name = name;
            this.value = value != null ? value.toString() : null;
        }

        public String getName()
        {
            return name;
        }

        public boolean is(String name)
        {
            return this.name.equals(name);
        }

        public boolean is(String name, String value)
        {
            return this.name.equals(name) && this.value.equals(value);
        }


        public void setValue(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        public Double getValueD()
        {
            return new Double(value);
        }

        public Integer getValueI()
        {
            return new Integer(value);
        }
    }
}
