package bl;

import java.util.Vector;

public interface ProtoHandler {

    public ProtoState execute(String hostName, ProtoArgs protoArgs)
            throws InterruptedException;

    public String getProtoName();

    public Vector validateArgs(ProtoArgs protoArgs);

    public ProtoArgs getDefaultArgs();
}
