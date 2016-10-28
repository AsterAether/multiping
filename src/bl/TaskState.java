package bl;

public class TaskState
{

    public static final TaskState IDLE =
        new TaskState("IDLE");
    public static final TaskState WAIT =
        new TaskState("WAIT");
    public static final TaskState EXECUTE =
        new TaskState("EXECUTE");
    private final String name;
    private TaskState(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
