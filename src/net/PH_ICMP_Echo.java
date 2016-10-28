package net;

import bl.Log;
import bl.ProtoArgs;
import bl.ProtoHandler;
import bl.ProtoState;
import java.time.Duration;
import java.util.*;
import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;

public class PH_ICMP_Echo implements ProtoHandler
{

    private final String protoName;
    private final ProtoArgs defaultArgs;
    private final IcmpPingRequest request;

    public PH_ICMP_Echo()
    {
        protoName = "ICMP Echo";

        defaultArgs = new ProtoArgs();
        defaultArgs.setArgValue("Timeout", "3000");
        request = IcmpPingUtil.createIcmpPingRequest();
    }

    @Override
    public ProtoState execute(String hostName, ProtoArgs protoArgs)
            throws InterruptedException
    {
        ProtoState result = ProtoState.DOWN;

        Log.log("ICMP: starting for " + hostName);

        request.setHost(hostName);

        int timeout = Integer.decode(defaultArgs.getArgValue("Timeout"));
        request.setTimeout(timeout);
        IcmpPingResponse response = IcmpPingUtil.executePingRequest(request);
        if (response.getSuccessFlag()) {
            result = ProtoState.upWithPing(Duration.ofMillis(response.getDuration()));
        }

        Log.log("ICMP: finished for " + hostName + " with " + result.toString() + " in " + result.getPing() + " ms");

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
