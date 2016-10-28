package bl;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ProtoArgs implements Serializable
{
    private final Hashtable args;

    public ProtoArgs()
    {
        args = new Hashtable();
    }

    public Vector getArgNames()
    {
        Vector result = new Vector();

        for (Enumeration e = args.keys(); e.hasMoreElements(); )
        {
            result.add(e.nextElement());
        }

        return result;
    }

    public void removeAllArgs()
    {
        args.clear();
    }

    public String getArgValue(String argName)
    {
        return (String)args.get(argName);
    }

    public void setArgValue(String argName, String argValue)
    {
        args.put(argName,argValue);
    }

    public void copyFrom(ProtoArgs protoArgs)
    {
        args.clear();

        Vector argNames = protoArgs.getArgNames();
        for (int x = 0; x < argNames.size(); x++)
        {
            String argName = (String)argNames.get(x);
            setArgValue(argName,protoArgs.getArgValue(argName));
        }
    }
}
