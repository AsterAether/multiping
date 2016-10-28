package net;

import bl.ProtoArgs;
import bl.ProtoState;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Host implements Serializable, Comparable<Host>
{

    private transient FileHandler fh;
    private transient Logger logger;

    private String hostName;
    private String description;
    private final Hashtable protos;
    private final LinkedHashMap<String, ProtoState[]> lastStates;

    public Host(String hostName)
    {
        this.hostName = hostName;
        this.description = "";
        protos = new Hashtable();
        lastStates = new LinkedHashMap<>();
    }

    private Logger initLogger()
    {
        try {
            String fileName = hostName.replaceAll("\\.", "") + (description == null || description.isEmpty() ? "" : "_" + description.replaceAll("\\.", ""));
            File f = new File(System.getProperty("user.dir") + File.separator + "logs" + File.separator + fileName);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            logger = Logger.getLogger(hostName);
            fh = new FileHandler(f.getAbsolutePath(), true);
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            return logger;
        } catch (IOException | SecurityException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ProtoState[] getLastStates(String protoName)
    {
        return lastStates.get(protoName);
    }

    public Host(String hostName, String description)
    {
        this.hostName = hostName;
        this.description = description;
        this.protos = new Hashtable();
        lastStates = new LinkedHashMap<>();
    }

    public Logger getLogger()
    {
        return logger == null ? initLogger() : logger;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void closeFileHandler()
    {
        if (fh != null) {
            fh.close();
        }
    }

    public String getHostName()
    {
        return hostName;
    }

    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    public Vector getProtoNames()
    {
        Vector result = new Vector();

        for (Enumeration e = protos.keys(); e.hasMoreElements();) {
            result.add(e.nextElement());
        }

        return result;
    }

    public void removeAllProtos()
    {
        protos.clear();
        lastStates.clear();
    }

    public HostProto getProto(String protoName)
    {
        return (HostProto) protos.get(protoName);
    }

    public void setProto(String protoName, HostProto hostProto)
    {
        protos.put(protoName, hostProto);
        lastStates.put(protoName, new ProtoState[3]);
    }

    public void removeProto(String protoName)
    {
        protos.remove(protoName);
    }

    public void copyFrom(Host host)
    {
        setHostName(host.getHostName());
        setDescription(host.getDescription());
        removeAllProtos();
        Vector protoNames = host.getProtoNames();
        for (int i = 0; i < protoNames.size(); i++) {
            String protoName = (String) protoNames.get(i);
            HostProto hostProto = host.getProto(protoName);
            ProtoArgs protoArgs = hostProto.getProtoArgs();
            ProtoArgs newProtoArgs = new ProtoArgs();
            Vector argNames = protoArgs.getArgNames();
            for (int j = 0; j < argNames.size(); j++) {
                String argName = (String) argNames.get(j);
                newProtoArgs.setArgValue(argName, protoArgs.getArgValue(argName));
            }
            HostProto newHostProto = new HostProto(hostProto.getProtoHandler(), newProtoArgs);
            setProto(protoName, newHostProto);
        }
    }

    @Override
    public int compareTo(Host o)
    {
        return this.getDescription().compareTo(o.getDescription());
    }
}
