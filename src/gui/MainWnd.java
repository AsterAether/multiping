package gui;

import bl.ProtoHandler;
import bl.ProtoMgr;
import bl.ProtoState;
import bl.TaskState;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import net.Host;
import net.HostProto;
import net.PingListener;
import net.PingMgr;

public class MainWnd
{

    private MainWnd outer;
    private JFrame mainFrame;
    private HostsTableModel hostsTableModel;
    private ProtoStateRenderer protoStateRenderer;
    private JTable hostsTable;

    private JPopupMenu popupMenu;
    private JMenuItem addMenuItem, editMenuItem, delMenuItem;
    private int contextRow;
    private LinkedList<TableColumn> cols;

    private PingMgr pingMgr;

    public MainWnd()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                createAndShowGUI();
            }
        });
    }

    private String getDataFile()
    {
        String file = System.getProperty("user.dir") + File.separator + "multiping.dat";

        return file;
    }

    private void save()
    {
        try {
            File f = new File(getDataFile());
            FileOutputStream fos = new FileOutputStream(f);
            try (ObjectOutputStream out = new ObjectOutputStream(fos)) {
                hostsTableModel.save(out);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load()
    {
        try {
            File f = new File(getDataFile());
            if (!f.exists()) {
                f.createNewFile();
            }
            else {
                FileInputStream fis = new FileInputStream(f);
                try (ObjectInputStream in = new ObjectInputStream(fis)) {
                    hostsTableModel.load(in);
                } catch (InvalidClassException e) {
                    f.delete();
                    f.createNewFile();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptHostUpdate(Host host)
    {
        if (contextRow == -1) {
            hostsTableModel.addHost(host);
        }
        else {
            hostsTableModel.refreshHost(contextRow);
        }
    }

    public void createAndShowGUI()
    {
        cols = new LinkedList<>();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.out.println(ex.toString());
        }
        outer = this;
        mainFrame = new JFrame("MultiPing v1.337 (thomas-beta)");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        hostsTableModel = new HostsTableModel();
        hostsTable = new JTable(hostsTableModel);
        hostsTable.setAutoCreateColumnsFromModel(true);
        for (int i = 2; i < hostsTable.getColumnCount(); i++) {
            cols.add(hostsTable.getColumnModel().getColumn(i));
        }
        hostsTable.setBackground(Color.WHITE);
        protoStateRenderer = new ProtoStateRenderer();
        for (int x = 2; x <= ProtoMgr.getProtoNames().size(); x++) {
            hostsTable.getColumnModel().getColumn(x).setCellRenderer(protoStateRenderer);
        }
        hostsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hostsTable.setRowSelectionAllowed(true);
        hostsTable.setColumnSelectionAllowed(false);
        hostsTable.setPreferredScrollableViewportSize(new Dimension(400, 350));
        JScrollPane scrollPane = new JScrollPane(hostsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        addMenuItem = new JMenuItem("Add Host");
        addMenuItem.addActionListener(new onHostAdd());
        editMenuItem = new JMenuItem("Edit Host");
        editMenuItem.addActionListener(new onHostEdit());
        delMenuItem = new JMenuItem("Delete Host");
        delMenuItem.addActionListener(new onHostDelete());
        popupMenu = new JPopupMenu();
        popupMenu.add(addMenuItem);
        popupMenu.add(editMenuItem);
        popupMenu.add(delMenuItem);

        JMenu menuHeader = new JMenu("Hide");
        for (Object o : ProtoMgr.getProtoNames()) {

        }
        for (int x = 2; x <= ProtoMgr.getProtoNames().size() + 1; x++) {
            String s = ProtoMgr.getProtoNames().get(x - 2).toString();
            HideMenuItem hmi = new HideMenuItem(s, cols.get(x - 2));
            menuHeader.add(hmi);
        }
        JMenuItem showMenuItem = new JMenuItem("Show all");
        showMenuItem.addActionListener(new onShowAll());
        menuHeader.add(showMenuItem);
        popupMenu.add(menuHeader);
        scrollPane.addMouseListener(new HostsMouseListener(scrollPane));
        hostsTable.addMouseListener(new HostsMouseListener(hostsTable));
        pingMgr = new PingMgr();
        load();
        save();
        mainFrame.addWindowListener(new OnClosing());
        mainFrame.pack();
        mainFrame.setSize(900, 500);
        mainFrame.setLocationRelativeTo(mainFrame);
        mainFrame.setVisible(true);
    }

    private class OnClosing extends WindowAdapter
    {

        @Override
        public void windowClosing(WindowEvent e)
        {
            for (Host h : hostsTableModel.hosts) {
                h.closeFileHandler();
            }
        }
    }

    private class onShowAll implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            for (TableColumn col : cols) {
                boolean found = false;
                for (int i = 0; i < hostsTable.getColumnModel().getColumnCount(); i++) {
                    if (hostsTable.getColumnModel().getColumn(i).equals(col)) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    hostsTable.getColumnModel().addColumn(col);
            }

        }
    }

    private class HideMenuItem extends JMenuItem implements ActionListener
    {

        private final String name;
        private final TableColumn col;

        public HideMenuItem(String name, TableColumn col)
        {
            super("Hide " + name);
            this.name = name;
            this.col = col;
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            hostsTable.getColumnModel().removeColumn(col);
        }

    }

    private class HostsMouseListener extends MouseInputAdapter
    {

        private final Component parent;

        HostsMouseListener(Component parent)
        {
            this.parent = parent;
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            if (e.isPopupTrigger()) {
                Point pt = e.getPoint();

                int row = hostsTable.rowAtPoint(pt);
                contextRow = row;
                if (row == -1) {
                    hostsTable.getSelectionModel().clearSelection();
                    editMenuItem.setEnabled(false);
                    delMenuItem.setEnabled(false);
                }
                else {
                    hostsTable.getSelectionModel().setSelectionInterval(row, row);
                    editMenuItem.setEnabled(true);
                    delMenuItem.setEnabled(true);
                }

                popupMenu.show(parent, (int) pt.getX(), (int) pt.getY());
            }
        }
    }

    private class onHostAdd implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            contextRow = -1;
            HostEditorWnd hostEditorWnd
                    = new HostEditorWnd(outer, mainFrame, new Host(""));
        }
    }

    private class onHostEdit implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            HostEditorWnd hostEditorWnd
                    = new HostEditorWnd(outer, mainFrame, hostsTableModel.getHost(contextRow));
        }
    }

    private class onHostDelete implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            hostsTableModel.deleteHost(contextRow);
        }
    }

    private class HostsTableModel extends AbstractTableModel
    {

        private LinkedList<Host> hosts;
        private final PingListenerImpl pingListenerImpl;

        HostsTableModel()
        {
            hosts = new LinkedList();
            pingListenerImpl = new PingListenerImpl();
        }

        public void addHost(Host h)
        {
            hosts.add(h);
            sort();
            fireTableRowsInserted(hosts.size(), hosts.size());
            outer.save();
            pingMgr.addHost(h, pingListenerImpl);
        }

        public Host getHost(int index)
        {
            return (Host) hosts.get(index);
        }

        public void refreshHost(int index)
        {
            fireTableRowsUpdated(index, index);
            sort();
            outer.save();
            pingMgr.updateHost((Host) hosts.get(index));
        }

        public void deleteHost(int index)
        {
            pingMgr.removeHost((Host) hosts.get(index));
            hosts.remove(index);

            fireTableRowsDeleted(index, index);
            outer.save();
        }

        public void save(ObjectOutput out)
        {
            try {
                out.writeObject(hosts);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void load(ObjectInput in) throws InvalidClassException
        {
            try {
                Object o = in.readObject();
                if (o instanceof LinkedList) {
                    hosts = (LinkedList<Host>) o;
                    sort();
                    for (int i = 0; i < hosts.size(); i++) {
                        Host h = (Host) hosts.get(i);

                        pingMgr.addHost(h, pingListenerImpl);
                    }
                }
            } catch (InvalidClassException e) {
                throw e;
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getColumnCount()
        {
            return ProtoMgr.getProtoNames().size() + 2;
        }

        @Override
        public int getRowCount()
        {
            return hosts.size();
        }

        @Override
        public String getColumnName(int col)
        {
            switch (col) {
                case 0:
                    return "Host";
                case 1:
                    return "Description";
                default:
                    Vector pn = ProtoMgr.getProtoNames();
                    return ProtoMgr.getProtoHandler((String) pn.get(col - 2)).getProtoName();
            }
        }

        @Override
        public Object getValueAt(int row, int col)
        {
            Host h = (Host) hosts.get(row);

            switch (col) {
                case 0:
                    return h.getHostName();
                case 1:
                    return h.getDescription();
                default:
                    String protoName = (String) ProtoMgr.getProtoNames().get(col - 2);
                    HostProto hp = h.getProto(protoName);
                    if (hp == null) {
                        return null;
                    }
                    else {
                        String s = "";
                        ProtoState[] states = h.getLastStates(protoName);
                        if (states != null) {
                            for (ProtoState state : states) {
                                if (state != null)
                                    s += state.toString() + ", ";
                            }
                            if (s.length() > 2)
                                s = s.substring(0, s.length() - 2);
                        }
                        return s;
                    }

            }
        }

        @Override
        public boolean isCellEditable(int row, int col)
        {
            return col == 1;
        }

        @Override
        public void setValueAt(Object o, int row, int col)
        {
            switch (col) {
                case 1:
                    ((Host) hosts.get(row)).setDescription(o.toString());
                    sort();
                    fireTableRowsUpdated(0, hosts.size());
                    outer.save();
                    break;
            }
        }

        public void sort()
        {
            Collections.sort(hosts);
            fireTableDataChanged();
        }

        private class PingListenerImpl implements PingListener
        {

            @Override
            public void pingResult(Host host, ProtoHandler protoHandler, ProtoState protoState)
            {
                host.getProto(protoHandler.getProtoName()).setProtoState(protoState);
                ProtoState[] states = host.getLastStates(protoHandler.getProtoName());
                for (int i = states.length - 1; i > 0; i--) {
                    states[i] = states[i - 1];
                }
                states[0] = protoState;

                for (int i = 0; i < hosts.size(); i++) {
                    Host h = (Host) hosts.get(i);
                    if (h == host) {
                        fireTableRowsUpdated(i, i);
                    }
                }
            }

            @Override
            public void updateTaskState(Host host, ProtoHandler protoHandler, TaskState taskState)
            {
                host.getProto(protoHandler.getProtoName()).setTaskState(taskState);

                for (int i = 0; i < hosts.size(); i++) {
                    Host h = (Host) hosts.get(i);
                    if (h == host) {
                        fireTableRowsUpdated(i, i);
                    }
                }
            }
        }
    }

    private class ProtoStateRenderer extends JLabel implements TableCellRenderer
    {

        private final Color cEmpty = new Color(255, 255, 255);
        private final Color cUnknown = new Color(200, 200, 200);
        private final Color cDown = new Color(200, 100, 100);
        private final Color cUp = new Color(100, 200, 100);

        private Font idleFont = null;
        private Font executeFont = null;

        ProtoStateRenderer()
        {
            setOpaque(true);

            Font f = getFont();
            idleFont = f.deriveFont(Font.PLAIN);
            executeFont = f.deriveFont(Font.BOLD);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object color,
                boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            String v = (String) table.getValueAt(row, column);

            if (v == null) {
                setText("");
                setBackground(cEmpty);
            }
            else {
                setText(v);

                if (v.startsWith("UP")) {
                    setBackground(cUp);
                }
                else if (v.startsWith("DOWN")) {
                    setBackground(cDown);
                }
                else if (v.startsWith("UNKNOWN")) {
                    setBackground(cUnknown);
                }

                setHorizontalAlignment(JLabel.CENTER);

                Host h = hostsTableModel.getHost(row);
                String protoName = (String) ProtoMgr.getProtoNames().get(hostsTable.convertColumnIndexToModel(column) - 2);
                HostProto hp = h.getProto(protoName);
                TaskState ts = hp.getTaskState();
                if (ts == TaskState.EXECUTE) {
                    setFont(executeFont);
                }
                else {
                    setFont(idleFont);
                }
            }

            return this;
        }
    }

}
