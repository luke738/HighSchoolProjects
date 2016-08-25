import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Luke on 9/16/2014.
 */
public class FlightSystem
{
    //public static List<BallisticBody> bodies = new ArrayList<BallisticBody>(); //Holds all BallisticBodies in sim. No longer used.
    public static List<WaterRocket> rockets = new ArrayList<WaterRocket>(); //Holds all WaterRockets in sim
    public static Vector eG = new Vector(0,-9.81); //Creates a constant vector representing standard Earth gravity
    public static double rho = 1.184; //Air density at 25C 101.325kPa
    public static double rhoW = 997; //Water density at 25C 101.325kPa
    public static double airPres = 101325; //Standard air pressure
    public static double cFactor; //Space compression factor (meters per pixel)
    public static double tFactor; //Time compression factor (seconds per frame)
    public double doneCount = 0; //Counts number of landed rockets, used for sim control.

    //Constructor
    public FlightSystem(double cF, double tF)
    {
        //Sets space and time compression
        cFactor=cF;
        tFactor=tF;
    }

    //Adds new BallisticBody s. No longer used.
    /*public void addBody(double m, Point2D l, Vector v, int[] c, String id, double cD, double r)
    {
        bodies.add(new BallisticBody(m,l,v,c,id,cD,r));
        //doneCount=0;
    }*/

    //Adds new WaterRockets to the master ArrayList
    public void addRocket(double m, Point2D l, int[] c, String id, double cD, double r, double rN, double lP, double vol, double wF, double lAng)
    {
        rockets.add(new WaterRocket(new BallisticBody(m,l,new Vector(0,0),c,id,cD,r), rN, lP, vol, wF, lAng));
    }

    //Iterates 1 frame of simulation
    public void simStep()
    {
        //If rockets are still flying
        if(doneCount<(rockets.size()))//+bodies.size())) BallisticBody no longer used
        {
            //Sims for each body. Unused.
            /*for (int i = 0; i < bodies.size(); i++)
            {
                bodies.get(i).velocity = bodies.get(i).velocity.sub(bodies.get(i).totalAccel().scalMult(tFactor));
                //System.out.println("Vel "+bodies.get(i).ident+":"+bodies.get(i).velocity.vecToString());
                bodies.get(i).location.setLocation(bodies.get(i).location.getX() + bodies.get(i).velocity.x * tFactor, bodies.get(i).location.getY() + bodies.get(i).velocity.y * tFactor);
                bodies.get(i).colMan();
                if (bodies.get(i).ident.contains("D") && !bodies.get(i).ident.contains("C"))
                {
                    bodies.get(i).ident += "C";
                    doneCount++;
                    //System.out.println("TOGO:"+(bodies.size()-doneCount));
                }
            }*/

            //Sims for each rocket
            for (int i = 0; i < rockets.size(); i++)
            {
                rockets.get(i).velocity = rockets.get(i).velocity.sub(rockets.get(i).totalAccel().scalMult(tFactor)); //Adds acceleration to velocity, accounting for time compression
                rockets.get(i).orient(); //Updates orientation of rockets. Experimental.
                //Adds velocity to location, accounting for time compression
                rockets.get(i).location.setLocation(rockets.get(i).location.getX() + rockets.get(i).velocity.x * tFactor, rockets.get(i).location.getY() + rockets.get(i).velocity.y * tFactor);
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
            //List<BallisticBody> validBodies = new ArrayList<BallisticBody>(); //Stores bodies that made it through the goal. Unused.
            List<WaterRocket> validRockets = new ArrayList<WaterRocket>(); //Stores rockets that made it through the goal
            //Adds bodies that made it through the goal to appropriate ArrayList
            /*for (int i = 0; i < bodies.size(); i++)
            {
                if(bodies.get(i).ident.contains("V"))
                {
                    validBodies.add(bodies.get(i));
                }
            }*/
            //Adds rockets that made it through the goal to appropriate ArrayList
            for (int i = 0; i < rockets.size(); i++)
            {
                //If rocket is valid (V)
                if(rockets.get(i).ident.contains("V"))
                {
                    validRockets.add(rockets.get(i));
                }
            }
            //Sort bodies by landing velocity. Unused.
            /*Collections.sort(validBodies,new Comparator<BallisticBody>()
            {
                @Override
                public int compare(BallisticBody o1, BallisticBody o2)
                {
                    if(o1.finVel.getM()>o2.finVel.getM())
                    {
                        return 1;
                    }
                    else if(o1.finVel.getM()==o2.finVel.getM())
                    {
                        return 0;
                    }
                    else
                    {
                        return -1;
                    }
                }
            });*/
            //Sort rockets by landing velocity
            Collections.sort(validRockets,new Comparator<WaterRocket>()
            {
                @Override
                public int compare(WaterRocket o1, WaterRocket o2)
                {
                    //Compares by magnitude of impact velocity
                    if(o1.finVel.getM()>o2.finVel.getM())
                    {
                        return 1;
                    }
                    else if(o1.finVel.getM()==o2.finVel.getM())
                    {
                        return 0;
                    }
                    else
                    {
                        return -1;
                    }
                }
            });
            //If any rockets made it
            if(validRockets.size()>0)
            {
                //Prints info on rocket with slowest impact.
                System.out.println(validRockets.get(0).ident+": "+validRockets.get(0).finVel.vecToString());
                System.out.println(validRockets.get(0).ident+": "+validRockets.get(0).finVel.getM());
            }
            //Iterates control counter
            doneCount++;
        }
    }

    //Deletes all objects of either type
    public void clear()
    {
        //bodies.clear(); //Unused
        rockets.clear();
    }

    //Draw each object to screen
    public void drawBodies(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        /*for (int i = 0; i < bodies.size(); i++)
        {
            g2.setPaint(new Color(bodies.get(i).color[0],bodies.get(i).color[1],bodies.get(i).color[2]));
            g2.fill(new Ellipse2D.Double(bodies.get(i).location.getX()/cFactor,bodies.get(i).location.getY()/cFactor,4,4));
        }*/
        for (int i = 0; i < rockets.size(); i++)
        {
            g2.setPaint(new Color(rockets.get(i).color[0],rockets.get(i).color[1],rockets.get(i).color[2]));
            g2.fill(new Ellipse2D.Double(rockets.get(i).location.getX()/cFactor-3,rockets.get(i).location.getY()/cFactor-3,6,6));
        }
    }
}
