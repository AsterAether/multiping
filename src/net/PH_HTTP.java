package net;

import bl.Log;
import bl.ProtoArgs;
import bl.ProtoHandler;
import bl.ProtoState;
import java.io.*;
import java.time.Duration;
import java.util.Vector;

public class PH_HTTP implements ProtoHandler
{

    private final String protoName;
    private final ProtoArgs defaultArgs;

    public PH_HTTP()
    {
        protoName = "HTTP";

        defaultArgs = new ProtoArgs();
        defaultArgs.setArgValue("Port", "80");
        defaultArgs.setArgValue("Remote File", "/");
    }

    @Override
    public ProtoState execute(String hostName, ProtoArgs protoArgs)
            throws InterruptedException
    {
        ProtoState result = ProtoState.DOWN;

        Log.log("HTTP: starting for " + hostName);

        try {
            int port;
            String remoteFile;

            try {
                port = Integer.decode(protoArgs.getArgValue("Port"));
            } catch (NumberFormatException e) {
                port = 80;
            }

            remoteFile = protoArgs.getArgValue("Remote File");
            if ((remoteFile.length() == 0)
                    || (remoteFile.charAt(0) != '/')) {
                remoteFile = "/";
            }

            NetStreamConnection nsc = new NetStreamConnection();

            try {
                if (nsc.open(hostName, port)) {
                    PrintWriter out = nsc.getWriter();
                    BufferedReader in = nsc.getReader();

                    int numLines = 5;

                    String request = "GET " + remoteFile + " HTTP/1.0\n"
                            + "Host: " + hostName + "\n";
                    out.println(request);

                    String resp = "";
                    String s;
                    while ((s = in.readLine()) != null) {
                        if (Thread.interrupted()) {
                            throw new InterruptedException();
                        }

                        resp += in.readLine();
                    }

                    if (resp.length() > 0) {
                        result = ProtoState.upWithPing(Duration.ZERO);
                    }

                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
            } finally {
                nsc.close();
            }
        } catch (IOException | InterruptedException e) {
            Log.log("HTTP: exception (" + hostName + "): " + e);
        }

        Log.log("HTTP: finished for " + hostName + " with " + result.toString());
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
