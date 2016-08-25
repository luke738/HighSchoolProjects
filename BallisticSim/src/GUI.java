import javax.swing.*;
import java.awt.*;

/**
 * Created by Luke on 9/17/2014.
 */
public class GUI extends JFrame
{
    public GUI() {

        initUI();
    }

    private void initUI() {

        add(new SystemDrawer());

        setResizable(false);
        pack();

        setTitle("Rocket Sim");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame ex = new GUI();
                ex.setVisible(true);
            }
        });
    }
}
