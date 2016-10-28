package bl;

import java.io.Serializable;
import java.time.Duration;

public class ProtoState implements Serializable
{

    public static final ProtoState DOWN = new ProtoState(State.DOWN, Duration.ZERO);
    public static final ProtoState UNKNOWN = new ProtoState(State.DOWN, Duration.ZERO);

    public enum State
    {
        DOWN, UP, UNKNOWN
    }

    private final Duration ping;
    private final State state;

    private ProtoState(State state, Duration ping)
    {
        this.state = state;
        this.ping = ping;
    }

    public static ProtoState upWithPing(Duration ping)
    {
        return new ProtoState(State.UP, ping);
    }

    public Duration getPing()
    {
        return ping;
    }

    public State getState()
    {
        return state;
    }

    @Override
    public String toString()
    {
        return state == State.UP ? state.toString() + ": " + ping.toMillis() + "ms" : state.toString();
    }
}
