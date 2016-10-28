package net;

import java.io.*;
import java.net.*;

public class NetStreamConnection
{
    private Socket sock = null;
    private PrintWriter out = null;
    private InputStreamReader isr = null;
    private BufferedReader in = null;

    public boolean open(String host, int port)
    {
        boolean result = false;

        try
        {
            sock = new Socket(host,port);
            out = new PrintWriter(sock.getOutputStream(),true);
            isr = new InputStreamReader(sock.getInputStream());
            in = new BufferedReader(isr);
            result = true;
        }

        catch (Exception e)
        {
        }

        return result;
    }

    public PrintWriter getWriter()
    {
        return out;
    }

    public BufferedReader getReader()
    {
        return in;
    }

    public void close()
    {
        try
        {
            if (sock != null)
            {
                sock.shutdownInput();
                sock.shutdownOutput();
                sock.close();
                sock = null;
            }

            if (out != null)
            {
                out.close();
                out = null;
            }

            if (in != null)
            {
                in.close();
                in = null;
            }

            if (isr != null)
            {
                isr.close();
                isr = null;
            }
        }

        catch (Exception e)
        {
        }
    }
}
