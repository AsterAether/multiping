package net;

public class PingHost
{
    private final Host host;
    private final PingListener pingListener;

    public PingHost(Host host, PingListener pingListener)
    {
        this.host = host;
        this.pingListener = pingListener;
    }

    public Host getHost()
    {
        return host;
    }

    public PingListener getPingListener()
    {
        return pingListener;
    }
}
