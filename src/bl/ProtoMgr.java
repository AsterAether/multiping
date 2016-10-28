package bl;

import java.util.Hashtable;
import java.util.Vector;
import net.PH_HTTP;
import net.PH_ICMP_Echo;
import net.PH_SMTP;

public class ProtoMgr
{

    private static Hashtable protoHandlers;
    private static Vector protoHandlerNames;
    private static boolean initialized = false;

    private static void initProto(Hashtable protoHandlers, Vector protoHandlerNames, ProtoHandler ph)
    {
        protoHandlers.put(ph.getProtoName(), ph);
        protoHandlerNames.add(ph.getProtoName());
    }

    private static void initialize()
    {
        if (!initialized) {
            protoHandlers = new Hashtable();
            protoHandlerNames = new Vector();

//            File f = new File(System.getProperty("user.dir") + File.separator + "protos");
//            if (!f.exists()) {
//                f.mkdir();
//            } else {
//                URLClassLoader cl;
//                try {
//                    cl = new URLClassLoader(new URL[]{f.toURI().toURL()});
//                    ProtoHandler ph;
//                    for (File classF : f.listFiles()) {
//                        try {
//                            Class c = cl.loadClass(classF.getName().substring(0, classF.getName().length() - 6));
//                            ph = (ProtoHandler) c.newInstance();
//                            protoHandlers.put(ph.getProtoName(), ph);
//                            protoHandlerNames.add(ph.getProtoName());
//
//                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                    try {
//                        cl.close();
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                } catch (MalformedURLException ex) {
//                    ex.printStackTrace();
//                }
//            }
            for (ProtoHandler ph : new ProtoHandler[]{new PH_ICMP_Echo(), new PH_HTTP(), new PH_HTTP(), new PH_SMTP()}) {
                initProto(protoHandlers, protoHandlerNames, ph);
            }
            initialized = true;
        }
    }

    public static Vector getProtoNames()
    {
        initialize();

        return protoHandlerNames;
    }

    public static ProtoHandler getProtoHandler(String protoName)
    {
        initialize();

        return (ProtoHandler) protoHandlers.get(protoName);
    }

    private ProtoMgr()
    {
    }
}
