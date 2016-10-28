package net;

import bl.ProtoHandler;
import bl.ProtoState;
import bl.TaskState;

public interface PingListener
{
    public void pingResult(Host host, ProtoHandler protoHandler, ProtoState protoState);
    public void updateTaskState(Host host, ProtoHandler protoHandler, TaskState taskState);
}
