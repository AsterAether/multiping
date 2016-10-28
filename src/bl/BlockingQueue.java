package bl;

import java.util.Vector;

public class BlockingQueue
{
    private final Vector queue;

    public BlockingQueue()
    {
        queue = new Vector();
    }

    synchronized public void put(Object o)
    {
        queue.addElement(o);
        notifyAll();
    }

    synchronized public Object get(long timeout)
    {
        while(true)
        {
            if (queue.size() > 0)
            {
                Object o = queue.elementAt(0);
                queue.removeElementAt(0);
                return o;
            }
            else
            {
                try
                {
                    if (timeout > 0)
                    {
                        wait(timeout);
                        if (queue.isEmpty())
                        {
                            return null;
                        }
                    }
                    else
                    {
                        wait();
                    }
                }

                catch(InterruptedException ie)
                {
                }
            }
        }
    }

    synchronized public void clear()
    {
        queue.clear();
    }
}
