package net;

import bl.ProtoState;

public class PingTaskResponse
{
    private final PingTask pingTask;
    private final ProtoState protoState;

    public PingTaskResponse(PingTask pingTask, ProtoState protoState)
    {
        this.pingTask = pingTask;
        this.protoState = protoState;
    }

    public PingTask getPingTask()
    {
        return pingTask;
    }

    public ProtoState getProtoState()
    {
        return protoState;
    }
}