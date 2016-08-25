import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Luke on 9/16/2014.
 */
public class FlightSystemBD
{
    public static List<WaterRocketBD> rockets = new ArrayList<WaterRocketBD>(); //Holds all WaterRockets in sim
    public static VectorBD eG = new VectorBD(0,-9.796110000); //Creates a constant vector representing standard Earth gravity at El Segundo
    public static double rho = 1.2000000; //Air density
    public static double rhoW = 998.2000000; //Water density
    public static BigDecimal airPres = BigDecimal.valueOf(101000.0000); //Standard air pressure
    public static BigDecimal c = BigDecimal.valueOf(343.21);
    public static double launchV = 0.25; //Volume of launcher, needs measuring
    public static double launchTubeL = 0.1524; //Length of launch tube
    public static double launchTubeAo = Math.pow(0.002,2)*Math.PI; //Outer and inner area of launch tube
    public static double launchTubeAi = Math.pow(0.0015,2)*Math.PI;
    public static BigDecimal cFactor; //Space compression factor (meters per pixel)
    public static BigDecimal tFactor; //Time compression factor (seconds per frame)
    public double doneCount = 0; //Counts number of landed rockets, used for sim control.
    public double step = 0; //I wanted to know which simstep it was on (and thus the time simulated)

    //Constructor
    public FlightSystemBD(BigDecimal cF, BigDecimal tF)
    {
        //Sets space and time compression
        cFactor=cF;
        tFactor=tF;
    }

    //Adds new WaterRockets to the master ArrayList
    public void addRocket(double m, VectorBD l, int[] c, String id, double cD, double r, double rN, double lP, double vol, double wF, double lAng)
    {
        rockets.add(new WaterRocketBD(new BallisticBodyBD(m,l,new VectorBD(0,0),c,id,cD,r), rN, lP, vol, wF, lAng));
    }

    //Iterates 1 frame of simulation
    public void simStep()
    {
        //If rockets are still flying
        if(doneCount<(rockets.size()))
        {
            step++;
            System.out.println("time = " + BigDecimal.valueOf(step).multiply(tFactor).setScale(4, BigDecimal.ROUND_FLOOR));
            //Sims for each rocket
            for (int i = 0; i < rockets.size(); i++)
            {
                rockets.get(i).velocity = rockets.get(i).velocity.add(rockets.get(i).totalAccel().scalMult(tFactor)); //Adds acceleration to velocity, accounting for time compression
                rockets.get(i).velocity = rockets.get(i).velocity.setScale(30, BigDecimal.ROUND_FLOOR);
                if (rockets.get(i).phase==1)
                {
                   rockets.get(i).velocity=rockets.get(i).velocity.normToUV(rockets.get(i).launchAngleVec);
                }
                //Adds velocity to location, accounting for time compression
                VectorBD corLoc = new VectorBD(rockets.get(i).location.x, BigDecimal.valueOf(290).multiply(cFactor).subtract(rockets.get(i).location.y));
                corLoc=corLoc.add(rockets.get(i).velocity.scalMult(tFactor));
                rockets.get(i).location = new VectorBD(corLoc.x, BigDecimal.valueOf(290).multiply(cFactor).subtract(corLoc.y));
                rockets.get(i).location=rockets.get(i).location.setScale(30, BigDecimal.ROUND_FLOOR);
                rockets.get(i).colMan(); //Check for collision with ground or goalposts
                //If rocket is landed (D) and hasn't alredy been counted (C)
                if (rockets.get(i).ident.contains("D") && !rockets.get(i).ident.contains("C"))
                {
                    rockets.get(i).ident += "C"; //Mark rocket as counted
                    doneCount++; //Add to completion counter
                }
            }
        }
        else if(doneCount==rockets.size())//+bodies.size()) Unused
        {
            List<WaterRocketBD> validRockets = new ArrayList<WaterRocketBD>(); //Stores rockets that made it through the goal

            //Adds rockets that made it through the goal to appropriate ArrayList
            for (int i = 0; i < rockets.size(); i++)
            {
                //If rocket is valid (V)
                if(rockets.get(i).ident.contains("V"))
                {
                    validRockets.add(rockets.get(i));
                }
            }

            //Sort rockets by landing velocity
            Collections.sort(validRockets,new Comparator<WaterRocketBD>()
            {
                @Override
                public int compare(WaterRocketBD o1, WaterRocketBD o2)
                {
                    //Compares by magnitude of impact velocity
                    return o1.finVel.getM().compareTo(o2.finVel.getM());
                }
            });
            //If any rockets made it
            if(validRockets.size()>0)
            {
                //Prints info on rocket with slowest impact.
                try
                {
                    PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
                    for (int i = 0; i < validRockets.size(); i++) {
                        String data = (validRockets.get(i).ident);
                        System.out.println(data.substring(0,data.length()-4) + ", " + validRockets.get(i).finVel.getM() + " " + validRockets.get(i).location.x);
                        writer.println(data.substring(0,data.length()-4) + ", " + validRockets.get(i).finVel.getM());
                    }
                    writer.close();
                }
                catch (Exception e)
                {
                    System.err.println("You broke it.");
                }
            }
            //Iterates control counter
            doneCount++;
        }
    }

    //Deletes all objects of either type
    public void clear()
    {
        rockets.clear();
        step=0;
    }

    //Draw each object to screen
    public void drawBodies(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;

        for (int i = 0; i < rockets.size(); i++)
        {
            g2.setPaint(new Color(rockets.get(i).color[0],rockets.get(i).color[1],rockets.get(i).color[2]));
            g2.fill(new Ellipse2D.Double(rockets.get(i).location.x.doubleValue()/cFactor.doubleValue()-3,rockets.get(i).location.y.doubleValue()/cFactor.doubleValue()-3,6,6));
        }
    }
}
