package main;

import java.awt.*;
import java.awt.event.*;

public class bootstrap
{
    public static void main(String[] args)
    {
        bootstrap b = new bootstrap();
        b.run(args);
    }

    public void run(String[] args)
    {
        boolean bootErr = false;

        try
        {
            main.main(args);
        }

        catch (UnsupportedClassVersionError | Exception ve)
        {
            bootErr = true;
        }

        if (bootErr)
        {
            Frame f = new Frame("Error");
            f.setResizable(false);
            Label l = new Label("MultiPing requires Java v1.5.0 or later");
            Button b = new Button("OK");
            b.addActionListener(new OKBtnListener());

            Panel p = new Panel(new GridLayout(2,1));
            p.add(l);
            Panel p2 = new Panel(new GridLayout(1,3));
            p2.add(new Panel());
            p2.add(b);
            p2.add(new Panel());
            p.add(p2);
            f.add(p);

            f.pack();
            f.setVisible(true);
        }
    }

    private class OKBtnListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    }
}
