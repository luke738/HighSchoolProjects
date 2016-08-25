import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;

/**
 * Created by Luke on 9/17/2014.
 */
public class SystemDrawer extends JPanel
        implements Runnable,ActionListener,ChangeListener{

    //Set window size and frame time
    private final int B_WIDTH = 1000;
    private final int B_HEIGHT = 300;
    private final int DELAY = 1;

    //Create animator object
    private Thread animator;

    private BigDecimal cFac = new BigDecimal("0.1000000000000000000"); //Space compression factor (meters per pixel)
    private BigDecimal tFac = new BigDecimal("0.0001000000000000000"); //Time compression factor (seconds per frame)

    //Create user input variables
    private double angLow = 35.000000000000;
    private double angHigh = 45.000000000000;
    private double vol = 0.0020000000000;
    private double plm = 0.175;

    //Create the rocket management object
    //private FlightSystem launch = new FlightSystem(cFac.doubleValue(),tFac.doubleValue());
    private FlightSystemBD launchBD = new FlightSystemBD(cFac,tFac);

    //Create JPanels for each button and slider
    JPanel resetPanel = new JPanel();
    JPanel angLowInput = new JPanel();
    JPanel angHighInput = new JPanel();
    JPanel plmInput = new JPanel();

    //Create reset button
    JButton resetButton = new JButton("(Re)Start Sim");

    //Constructor
    public SystemDrawer()
    {
        //Create sliders
        JSlider angLB = new JSlider(JSlider.HORIZONTAL,0,90,35);
        JSlider angHB = new JSlider(JSlider.HORIZONTAL,0,90,45);
        JSlider plmass = new JSlider(JSlider.HORIZONTAL,0,100,34);

        //Name sliders
        angLB.setName("Low");
        angHB.setName("High");
        plmass.setName("Mass");

        //Add this object (SystemDrawer) as the ChangeListener for the sliders
        angLB.addChangeListener(this);
        angHB.addChangeListener(this);
        plmass.addChangeListener(this);

        //Set tick spacing and labeling for each slider
        angLB.setMajorTickSpacing(10);
        angLB.setMinorTickSpacing(1);
        angLB.setPaintTicks(true);
        angLB.setPaintLabels(true);
        angLB.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
        angHB.setMajorTickSpacing(10);
        angHB.setMinorTickSpacing(1);
        angHB.setPaintTicks(true);
        angHB.setPaintLabels(true);
        angHB.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
        plmass.setMajorTickSpacing(10);
        plmass.setMinorTickSpacing(1);
        plmass.setPaintTicks(true);
        plmass.setPaintLabels(true);
        plmass.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 10, 0));

        //Name the action message used by the reset button
        //Add SystemDrawer as the ActionListener of the button
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(this);

        //Prepare window to draw GUI elements
        this.setLayout(null);

        //Set the size and location of the button, and add it to the screen
        resetPanel.setLayout(null);
        resetButton.setSize(100, 50);
        resetPanel.add(resetButton);
        add(resetPanel);
        resetPanel.setBounds(0, 0, 100, 50);

        //Set size and location of the sliders, and adds them to the screen
        angLowInput.setLayout(null);
        angLB.setSize(300,60);
        angLowInput.add(angLB);
        add(angLowInput);
        angLowInput.setBounds(100,0,300,50);

        angHighInput.setLayout(null);
        angHB.setSize(300,60);
        angHighInput.add(angHB);
        add(angHighInput);
        angHighInput.setBounds(400,0,300,50);

        plmInput.setLayout(null);
        plmass.setSize(300, 60);
        plmInput.add(plmass);
        add(plmInput);
        plmInput.setBounds(700, 0, 300, 50);

        //Set background and size of the screen
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setDoubleBuffered(true);
        setVisible(true);
    }

    //Animator starter
    @Override
    public void addNotify()
    {
        super.addNotify();

        //Start the animator
        animator = new Thread(this);
        animator.start();
    }

    //This method draws the GUI and sim
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        //Draw the gridlines and point of goalposts
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.RED);
        g2.fill(new Ellipse2D.Double(45.67 / cFac.doubleValue(), 290 - 3.05 / cFac.doubleValue(), 4, 4));
        for(int i=0; i<B_WIDTH; i+=1/cFac.doubleValue())
        {
            if(i%(10/cFac.doubleValue())==0)
            {
                g2.setColor(new Color(0,0,0));
            }
            else
            {
                g2.setColor(new Color(200,200,200));
            }
            g2.drawLine(i,0,i,B_HEIGHT);
        }
        for(int i=0; i<B_HEIGHT+10; i+=1/cFac.doubleValue())
        {
            if((300-i)%(10/cFac.doubleValue())==0)
            {
                g2.setColor(new Color(0,0,0));
            }
            else
            {
                g2.setColor(new Color(200,200,200));
            }
            g2.drawLine(0,i-10,B_WIDTH,i-10);
        }

        //Instruct the rocket manager to draw each rocket
        //launch.drawBodies(g);
        launchBD.drawBodies(g);
        Toolkit.getDefaultToolkit().sync();
    }

    //Creates initial bodies and runs animation
    @Override
    public void run()
    {
        //Animator stuff
        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();

        //Create the rockets to be used in the sim
        createBodies();

        //Don't stop animating
        while (true)
        {
            //Reset button can interrupt simulation steps and cause null pointer exceptions. Catch this and report to console.
            try
            {
                //Call the physics simulator of the rocket manager
                //launch.simStep();
                launchBD.simStep();
                //System.out.println("called step");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("RESET DROPPED SIM STEP");
            }

            //Draw everything again
            repaint();

            //More animator stuff
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

    //Adds rockets to the manager
    public void createBodies()
    {
        //Iterate through 2 variables: Lower and Upper angle bounds, and percentage of water.
        for (double i = angLow-0.5; i<angHigh; i+=0.5)
        {
            //Range of water percentage hard-coded to between 10% and 50%. Any other values not useful, and user controls add unneeded complexity.
            for(double j=14; j<20; j+=1)
            {
                //launch.addRocket(0.2 + vol * 30, new Point2D.Double(0 * cFac.doubleValue(), 290 * cFac.doubleValue()), new int[]{0, (int) (255 * (j/0.5)), 0/*(int) (255 * (i / angHigh))*/}, String.valueOf(i)+" "+String.valueOf(j), 0.75, 0.049, 0.014, 275790+101325, vol, j, i);
                //launchBD.addRocket(plm, new VectorBD(cFac.multiply(BigDecimal.ZERO), cFac.multiply(new BigDecimal("290.000000"))), new int[]{(int) (0*255 * (j/0.5)), 0,0/*(int) (255 * (i / angHigh))*/}, String.valueOf(i)+"deg "+String.valueOf(j)+"% "+plm+"kg_pl ", 0.45, 0.049, 0.014, 413685, vol, j, i);
                System.out.println(plm);
                for (double k = plm-1; k < plm; k++)
                {
                    //launchBD.addRocket(k + vol * 30, new VectorBD(cFac.multiply(BigDecimal.ZERO), cFac.multiply(new BigDecimal("290.000000"))), new int[]{(int) (0*255 * (j/0.5)), (int)(0*255*k/0.4),(int) (255 * (i / angHigh))}, String.valueOf(i)+"deg "+String.valueOf(j)+"% "+k+"kg_pl ", 0.45, 0.049, 0.014, 275790+101325, vol, j, i);
                    //launchBD.addRocket(k*0.01, new VectorBD(cFac.multiply(BigDecimal.ZERO), cFac.multiply(BigDecimal.valueOf(290D))), new int[]{(int) (255 * (0.36/0.5)), 0,0/*(int) (255 * (i / angHigh))*/}, String.valueOf(j)+", "+String.valueOf(k), 0.2, 0.055, 0.01067, 275790+101000, 0.00206, j*0.01, i); //plm is in Dg
                }
            }
        }
        for (double i = 0.22; i <= 0.35; i+=0.01)
        {
            launchBD.addRocket(0.15, new VectorBD(BigDecimal.ZERO, cFac.multiply(new BigDecimal("290.000"))),new int[]{0,0,(int)(i-0.15)*1000}, "waterFrac: "+i, 0.4, 0.055, 0.01067, 413685, vol, i, 26);
        }
        //launchBD.addRocket(0.15, new VectorBD(BigDecimal.ZERO, cFac.multiply(new BigDecimal("290.0000"))), new int[]{0,0,0}, "test", 0.2, 0.055, 0.01067, 413685, vol, 0.25, 45);
        //System.out.println("first");
        //launchBD.addRocket(0.2, new VectorBD(cFac.multiply(BigDecimal.ZERO), cFac.multiply(BigDecimal.valueOf(290D))), new int[]{(int) (255 * (0.36/0.5)), 0,0/*(int) (255 * (i / angHigh))*/}, String.valueOf(45)+" "+String.valueOf(0.25), 0.2, 0.055, 0.01067, 275790+101000, 0.00206, 0.25, 45);
        //launchBD.addRocket(plm -0.12 + vol * 30, new VectorBD(cFac.multiply(BigDecimal.ZERO), cFac.multiply(BigDecimal.valueOf(290D))), new int[]{(int) (255 * (0.36/0.5)), 0,0/*(int) (255 * (i / angHigh))*/}, String.valueOf(39.5)+": "+String.valueOf(0.36), 0.75, 0.049, 0.014, 275790+101325, vol, 0.4, 39.5);
    }

    //Event handling for reset button
    public void actionPerformed(ActionEvent e) {
        if ("reset".equals(e.getActionCommand()))
        {
            //launch.clear();
            //launch.doneCount=0;

            launchBD.clear();
            launchBD.doneCount=0;
            createBodies();
        }
    }

    //Event handler for sliders
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting())
        {
            if(source.getName().equals("Low"))
            {
                //Set lower bound of tested angles
                angLow=source.getValue();
            }
            else if(source.getName().equals("High"))
            {
                //Set upper bound of tested angles
                angHigh=source.getValue();
            }
            else if(source.getName().equals("Mass"))
            {
                //Convert from L to m^3
                //Sets rocket volume
                plm=(double)source.getValue()/200D;
            }
        }
    }
}
