import javax.swing.*;
import java.awt.*;

/**
 * This class runs the animation itself.
 * Most of its methods are simple overrides of Runnable that keep the simulation running.
 * Creates a single SystemSimulator by default, but the addition of multiple could allow for comparison of initial conditions.
 */
public class SystemAnimator extends JPanel
        implements Runnable
{
    public static final int B_WIDTH = 50*24;
    public static final int B_HEIGHT = 50*12;
    private final int DELAY = 1;

    private Thread animator;

    public SystemAnimator()
    {
        initBoard();
    }

    public SystemSimulator sysSim = new SystemSimulator();

    private void initBoard()
    {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setDoubleBuffered(true);
    }

    @Override
    public void addNotify()
    {
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        for(int i=0; i<B_HEIGHT+50; i+=50)
        {
            if(i%10!=0)
            {
                g2.setColor(new Color(0,0,0));
            }
            else
            {
                g2.setColor(new Color(100,100,100));
            }
            g2.drawLine(0,i-50,B_WIDTH,i-50);
        }
        g2.setColor(new Color(255,0,0));
        g2.drawLine(0,B_HEIGHT-225,B_WIDTH,B_HEIGHT-225);
        sysSim.draw(g2);
    }

    @Override
    public void run()
    {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (true)
        {
            sysSim.updateSystem();
            repaint();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0)
            {
                sleep = 2;
            }

            try
            {
                Thread.sleep(sleep);
            } catch (InterruptedException e)
            {
                System.out.println("Interrupted: " + e.getMessage());
            }

            beforeTime = System.currentTimeMillis();
        }
    }
}