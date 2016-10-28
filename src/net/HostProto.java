package net;

import bl.Log;
import bl.ProtoArgs;
import bl.ProtoHandler;
import bl.ProtoMgr;
import bl.ProtoState;
import bl.TaskState;
import java.io.*;

public class HostProto implements Serializable
{

    private ProtoHandler protoHandler;
    private ProtoArgs protoArgs;
    private ProtoState protoState;
    private TaskState taskState;

    public HostProto(ProtoHandler protoHandler,
            ProtoArgs protoArgs)
    {
        this.protoHandler = protoHandler;
        this.protoArgs = protoArgs;
        protoState = ProtoState.UNKNOWN;
        taskState = TaskState.IDLE;
    }

    public ProtoHandler getProtoHandler()
    {
        return protoHandler;
    }

    public ProtoArgs getProtoArgs()
    {
        return protoArgs;
    }

    public ProtoState getProtoState()
    {
        return protoState;
    }

    public void setProtoState(ProtoState protoState)
    {
        this.protoState = protoState;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException
    {
        out.writeObject(protoHandler.getProtoName());
        out.writeObject(protoArgs);
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException
    {
        String protoName = (String) in.readObject();
        protoHandler = ProtoMgr.getProtoHandler(protoName);
        protoArgs = (ProtoArgs) in.readObject();
        protoState = ProtoState.UNKNOWN;
    }

    public TaskState getTaskState()
    {
        return taskState;
    }

    public void setTaskState(TaskState taskState)
    {
        this.taskState = taskState;
        Log.log("HostProto.setTaskState(" + taskState + ")");
    }
}
