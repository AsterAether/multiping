package net;

public class PingTask
{
    private static long nextSer = 1;
    private final PingHost pingHost;
    private final HostProto hostProto;
    private final long ser;

    public PingTask(PingHost pingHost, HostProto hostProto)
    {
        this.pingHost = pingHost;
        this.hostProto = hostProto;

        synchronized(this)
        {
            ser = nextSer++;
        }
    }

    public PingHost getPingHost()
    {
        return pingHost;
    }

    public HostProto getHostProto()
    {
        return hostProto;
    }

    public long getSer()
    {
        return ser;
    }
}
