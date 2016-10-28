package net;

import bl.Log;
import bl.ProtoArgs;
import bl.ProtoHandler;
import bl.ProtoState;
import java.io.*;
import java.time.Duration;
import java.util.Vector;

public class PH_SMTP implements ProtoHandler
{

    private final String protoName;
    private final ProtoArgs defaultArgs;

    public PH_SMTP()
    {
        protoName = "SMTP";

        defaultArgs = new ProtoArgs();
        defaultArgs.setArgValue("Port", "25");
    }

    @Override
    public ProtoState execute(String hostName, ProtoArgs protoArgs)
            throws InterruptedException
    {
        ProtoState result = ProtoState.DOWN;

        Log.log("SMTP: starting for " + hostName);

        try {
            int port;

            try {
                port = Integer.decode(protoArgs.getArgValue("Port"));
            } catch (NumberFormatException e) {
                port = 80;
            }

            NetStreamConnection nsc = new NetStreamConnection();

            try {
                if (nsc.open(hostName, port)) {
                    PrintWriter out = nsc.getWriter();
                    BufferedReader in = nsc.getReader();

                    String s;

                    s = in.readLine();
                    if ((s != null)
                            && (s.length() > 0)
                            && (s.contains("220"))) {
                        out.println("EHLO\n");
                        s = in.readLine();

                        if ((s != null)
                                && (s.length() > 0)
                                && (s.contains("250"))) {
                            out.println("QUIT\n");
                            result = ProtoState.upWithPing(Duration.ZERO);
                        }
                    }

                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
            } finally {
                nsc.close();
            }
        } catch (IOException | InterruptedException e) {
            Log.log("SMTP: exception (" + hostName + "): " + e);
        }

        Log.log("SMTP: finished for " + hostName + " with " + result.toString());
        return result;
    }

    @Override
    public String getProtoName()
    {
        return protoName;
    }

    @Override
    public Vector validateArgs(ProtoArgs protoArgs)
    {
        return null;
    }

    @Override
    public ProtoArgs getDefaultArgs()
    {
        return defaultArgs;
    }
}
