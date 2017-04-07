package cmanager.network;

public class UnexpectedStatusCode extends Exception
{
    private static final long serialVersionUID = -1132973286480626832L;

    private int statusCode;
    private String body;

    public UnexpectedStatusCode(int statusCode, String body)
    {
        super("Unexpected status code " + new Integer(statusCode).toString());
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public boolean is400BadRequest()
    {
        return statusCode == 400;
    }

    public String getBody()
    {
        return body;
    }
}
