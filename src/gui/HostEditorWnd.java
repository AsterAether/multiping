package gui;

import bl.ProtoArgs;
import bl.ProtoHandler;
import bl.ProtoMgr;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import net.Host;
import net.HostProto;

public class HostEditorWnd
{

    private final JDialog dlg;
    private final Frame parent;
    private final Host origHost;
    private final Host host;
    private final MainWnd mainWnd;

    private final ProtosTableModel protosTableModel;
    private final JTable protosTable;

    private final ProtoArgsTableModel protoArgsTableModel;
    private final JTable protoArgsTable;

    private final JTextField hostNameFld, hostDescFld;

    public HostEditorWnd(MainWnd mainWnd, Frame parent, Host host)
    {
        this.parent = parent;
        this.origHost = host;
        this.host = new Host(host.getHostName());
        this.host.copyFrom(host);
        this.mainWnd = mainWnd;

        dlg = new JDialog(parent, "Host Editor", true);

        Container contentPane = dlg.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        JPanel p = new JPanel();
        p.setLayout(new GridLayout(2, 2));
        p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel l = new JLabel("Host");
        p.add(l);
        hostNameFld = new JTextField();
        hostNameFld.setText(host.getHostName());

        p.add(hostNameFld);
        p.add(new JLabel("Description"));
        hostDescFld = new JTextField();
        hostDescFld.setText(host.getDescription());
        p.add(hostDescFld);
        contentPane.add(p);

        l = new JLabel("Protocols");
        p = new JPanel();
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(l);
        contentPane.add(p);
        protosTableModel = new ProtosTableModel();
        protosTable = new JTable(protosTableModel);
        protosTableModel.addTableModelListener(protosTable);
        protosTable.getSelectionModel().addListSelectionListener(
                new ProtosTableSelectionListener());
        protosTable.setPreferredScrollableViewportSize(new Dimension(300, 70));
        JScrollPane scrollPane = new JScrollPane(protosTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.add(scrollPane);

        l = new JLabel("Protocol Arguments");
        p = new JPanel();
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(l);
        contentPane.add(p);
        protoArgsTableModel = new ProtoArgsTableModel();
        protoArgsTable = new JTable(protoArgsTableModel);
        protoArgsTableModel.addTableModelListener(protoArgsTable);
        protoArgsTable.setPreferredScrollableViewportSize(new Dimension(300, 70));
        scrollPane = new JScrollPane(protoArgsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.add(scrollPane);

        p = new JPanel();
        p.setLayout(new GridLayout(1, 3));
        p.add(new JPanel());
        p.add(new JPanel());
        JPanel p2 = new JPanel(new GridLayout(1, 2));

        JButton b = new JButton("OK");
        b.addActionListener(new OKBtnActionListener());
        p2.add(b);

        b = new JButton("Cancel");
        b.addActionListener(new CancelBtnActionListener());
        p2.add(b);

        p.add(p2);

        contentPane.add(p);

        dlg.pack();
        dlg.setMinimumSize(new Dimension(500, 400));
        dlg.setLocationRelativeTo(dlg);
        dlg.setVisible(true);
    }

    private class ProtosTableSelectionListener implements ListSelectionListener
    {

        @Override
        public void valueChanged(ListSelectionEvent e)
        {
            if (!e.getValueIsAdjusting()) {
                protoArgsTableModel.fireTableDataChanged();
            }
        }
    }

    private class ProtosTableModel extends AbstractTableModel
    {

        @Override
        public int getColumnCount()
        {
            return 2;
        }

        @Override
        public int getRowCount()
        {
            return ProtoMgr.getProtoNames().size();
        }

        @Override
        public String getColumnName(int col)
        {
            if (col == 0) {
                return "Enabled";
            }
            else {
                return "Name";
            }
        }

        @Override
        public Object getValueAt(int row, int col)
        {
            String protoName = (String) ProtoMgr.getProtoNames().get(row);

            if (col == 1) {
                return protoName;
            }
            else
                return host.getProto(protoName) != null;
        }

        @Override
        public void setValueAt(Object val, int row, int col)
        {
            if (col == 0) {
                String protoName = (String) protosTable.getValueAt(row, 1);
                if (((Boolean) val)) {
                    ProtoHandler protoHandler = ProtoMgr.getProtoHandler(protoName);
                    ProtoArgs protoArgs = new ProtoArgs();
                    protoArgs.copyFrom(protoHandler.getDefaultArgs());

                    host.setProto(protoName,
                            new HostProto(protoHandler,
                                    protoArgs));
                }
                else {
                    host.removeProto(protoName);
                }

                protoArgsTableModel.fireTableDataChanged();
            }
        }

        @Override
        public boolean isCellEditable(int row, int col)
        {
            return col != 1;
        }

        @Override
        public Class getColumnClass(int c)
        {
            return getValueAt(0, c).getClass();
        }
    }

    private class ProtoArgsTableModel extends AbstractTableModel
    {

        @Override
        public int getColumnCount()
        {
            return 2;
        }

        @Override
        public int getRowCount()
        {
            int row = protosTable.getSelectedRow();
            if (row >= 0) {
                String protoName = (String) protosTable.getValueAt(row, 1);
                HostProto hp = host.getProto(protoName);
                if (hp == null) {
                    return 0;
                }
                else {
                    return hp.getProtoHandler().getDefaultArgs().getArgNames().size();
                }
            }
            else {
                return 0;
            }
        }

        @Override
        public String getColumnName(int col)
        {
            if (col == 0) {
                return "Name";
            }
            else {
                return "Value";
            }
        }

        @Override
        public Object getValueAt(int row, int col)
        {
            Object result = null;

            int r = protosTable.getSelectedRow();
            if (r >= 0) {
                String protoName = (String) protosTable.getValueAt(r, 1);
                HostProto hp = host.getProto(protoName);
                if (hp != null) {
                    String argName = (String) hp.getProtoHandler().getDefaultArgs().getArgNames().get(row);
                    if (col == 0) {
                        return argName;
                    }
                    else {
                        return hp.getProtoArgs().getArgValue(argName);
                    }
                }
            }

            return result;
        }

        @Override
        public void setValueAt(Object val, int row, int col)
        {
            if (col == 1) {
                int r = protosTable.getSelectedRow();
                if (r >= 0) {
                    String protoName = (String) protosTable.getValueAt(r, 1);
                    String argName = (String) protoArgsTable.getValueAt(row, 0);
                    HostProto hp = host.getProto(protoName);
                    if (hp != null) {
                        hp.getProtoArgs().setArgValue(argName, (String) val);
                    }
                }
            }
        }

        @Override
        public boolean isCellEditable(int row, int col)
        {
            return col > 0;
        }
    }

    private class OKBtnActionListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            host.setHostName(hostNameFld.getText());
            host.setDescription(hostDescFld.getText().replaceAll("[^A-Za-z0-9_]", ""));
            origHost.copyFrom(host);
            mainWnd.acceptHostUpdate(origHost);
            dlg.dispose();
        }
    }

    private class CancelBtnActionListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            dlg.dispose();
        }
    }

}
