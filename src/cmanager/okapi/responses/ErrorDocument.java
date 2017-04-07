package cmanager.okapi.responses;

import java.util.ArrayList;

public class ErrorDocument
{
    private class Error
    {

        String developer_message = "hallo";
        ArrayList<String> reason_stack;
        int status;
        String parameter;
        String whats_wrong_about_it;
        String more_info;
    }

    private Error error;

    public String getDeveloper_message()
    {
        return error.developer_message;
    }
    public ArrayList<String> getReason_stack()
    {
        return error.reason_stack;
    }
    public int getStatus()
    {
        return error.status;
    }
    public String getParameter()
    {
        return error.parameter;
    }
    public String getWhats_wrong_about_it()
    {
        return error.whats_wrong_about_it;
    }
    public String getMore_info()
    {
        return error.more_info;
    }
}