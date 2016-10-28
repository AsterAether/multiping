package bl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    private static final Logger LOG = Logger.getLogger(Log.class.getName());

    public static void log(String logtext) {
        LOG.log(Level.INFO, logtext);
    }

    private Log() {
    }
}
