import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by Luke on 9/17/2014.
 */
public class SystemDrawer extends JPanel
        implements Runnable{

    public static final int B_WIDTH = 750;
    public static final int B_HEIGHT = 750;
    private final int DELAY = 1;

    private Thread animator;
    //private int x, y;
    public static double cFac = 100000;
    private double tFac = 100;
    private GravSystem solar = new GravSystem(cFac,tFac);

    public SystemDrawer()
    {
        initBoard();
    }

    private void initBoard() {

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setDoubleBuffered(true);
    }

    @Override
    public void addNotify() {
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawSystem(g);
    }

    private void drawSystem(Graphics g) {

        //g.drawImage(star, x, y, this);
        solar.drawBodies(g);
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();
        //solar.addBody(1.98 *Math.pow(10,30),new Point2D.Double(200*cFac,250*cFac),new Vector(0,0),new int[]{255,255,0},"sun");
        //solar.addBody(5.972*Math.pow(10,24),new Point2D.Double(450*cFac,250*cFac),new Vector(0,180),new int[]{0,255,255},"earth");
        solar.addBody(5.972*Math.pow(10,24),new Vector(350*cFac,350*cFac),new Vector(0,0),new int[]{0,255,255},"earth",6371000);
        //solar.addBody(5.972*Math.pow(10,24),new Point2D.Double(350*cFac,150*cFac),new Vector(0,-4500),new int[]{0,255,255},"earth2",6371000);
        //solar.addBody(5.972*Math.pow(10,24),new Point2D.Double(300*cFac,550*cFac),new Vector(4500,1000),new int[]{0,255,255},"earth3",6371000);
        solar.addBody(7.347*Math.pow(10,20),new Vector(350*cFac,200*cFac),new Vector(5000,0),new int[]{255,255,255},"moon",1737500);
        solar.addBody(7.347*Math.pow(10,16),new Vector(350*cFac,50*cFac),new Vector(-3000,0),new int[]{255,255,255},"moon2",1737500/4);
        /*solar.addBody(-Math.pow(10,26),new Point2D.Double(0*cFac,0*cFac),new Vector(100000,100000),new int[]{255,255,255},"moon0");
        solar.addBody(Math.pow(10,27)/9.99,new Point2D.Double(0*cFac,500*cFac),new Vector(100000,-100000),new int[]{255,255,255},"moon2");
        solar.addBody(Math.pow(10,27)/9.99,new Point2D.Double(500*cFac,0*cFac),new Vector(-100000,100000),new int[]{255,255,255},"moon1");
        solar.addBody(-Math.pow(10,26),new Point2D.Double(500*cFac,500*cFac),new Vector(-100000,-100000),new int[]{255,255,255},"moon");
        solar.addBody(-7.347*Math.pow(10,20),new Point2D.Double(250*cFac,0*cFac),new Vector(0,0),new int[]{255,255,255},"moon");
        solar.addBody(-7.347*Math.pow(10,20),new Point2D.Double(0*cFac,250*cFac),new Vector(0,0),new int[]{255,255,255},"moon");
        solar.addBody(-7.347*Math.pow(10,20),new Point2D.Double(500*cFac,250*cFac),new Vector(0,0),new int[]{255,255,255},"moon");
        solar.addBody(-7.347*Math.pow(10,20),new Point2D.Double(250*cFac,500*cFac),new Vector(0,0),new int[]{255,255,255},"moon");*/

        /*for (int i = 0; i < 100; i++)
        {
            solar.addBody(Math.pow(15*Math.random(),20*Math.random()),new Point2D.Double(Math.random()*500*cFac,Math.random()*500*cFac),new Vector((1-2*Math.random())*1000,(1-2*Math.random())*1000),new int[]{(int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)},String.valueOf(i));
        }

        for (int i = 0; i<20; i++)
        {
            solar.addBody(Math.pow(15,20), new Point2D.Double((20+20*i)*cFac,500*cFac*Math.random()),new Vector(0,0),new int[]{(int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)},String.valueOf(i));
        }*/

        while (true) {

            solar.simStep();
            repaint();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }

            beforeTime = System.currentTimeMillis();
        }
    }
}
