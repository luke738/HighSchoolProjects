import java.awt.geom.Point2D;

/**
 * Created by Luke on 9/16/2014.
 */
public class BallisticBody
{
    //Creates basic properties of an object flying through air
    public double mass;
    public double cD; //Coefficient of drag
    public double r; //Radius
    public Point2D location;
    public Vector velocity;
    public int[] color; //Color in RGB form
    public String ident; //Identifying string. Holds a name/miscellaneous info, as well as current status
    //Stores values that are only updated at either creation or end of flight
    public Vector finVel; //Impact velocity
    public Vector initVel; //Initial velocity
    public double[] secInitLoc = new double[2]; //Stores x and y as an array. Using a Point2D had bugs.

    //Default constructor
    public BallisticBody()
    {

    }

    //Creates a fully detailed BallisticBody
    public BallisticBody(double m, Point2D l, Vector v, int[] c, String id, double coeffDrag, double rad)
    {
        mass = m;
        location = l;
        velocity = v;
        color = c;
        ident = id;
        cD = coeffDrag;
        r = rad;
        initVel = v;
        secInitLoc[0] = l.getX();
        secInitLoc[1] = l.getY();
        //System.out.println(initLoc.getX()+" "+initLoc.getY());
    }

    //Computes total acceleration acting on the object
    public Vector totalAccel()
    {
        //Creates the base vector
        Vector resultant = new Vector(0,0);
        resultant = resultant.add(FlightSystem.eG); //Adds gravity
        resultant = resultant.add(drag()); //Adds drag
        return resultant;
    }

    //Computes acceleration due to drag
    public Vector drag()
    {
        Vector drag;
        //Compute force of drag by F=0.5*rho*v^2*Cd*A
        drag = velocity.unitVec().scalMult(0.5*FlightSystem.rho*Math.pow(velocity.getM(),2)*cD*(Math.PI*Math.pow(r,2)));
        drag = drag.scalMult(1/mass); //Convert force to acceleration by a=F/m
        //System.out.println("d2: "+drag.vecToString());
        return drag;
    }

    //Checks for collision with ground or goalpost
    public void colMan()
    {
        //If rocket is past and above the goalpost
        if(location.getX()>36.58&&location.getY()>3.05)
        {
            //And it hasn't already been marked as being so
            if(!ident.contains("V"))
            {
                //Mark rocket as a valid design/launch
                ident = ident + "V";
            }
        }
        //If rocket is on or past the ground
        if(location.getY()>290*FlightSystem.cFactor)
        {
            //If final velocity hasn't been recorded (R)
            if(!ident.contains("R"))
            {
                //Record final velocity and mark as recorded
                finVel=velocity;
                ident+="R";
            }
            //Set velocity to 0 to give the appearance of collision with the ground
            velocity=new Vector(0,0);
            //Set the location to the current X at ground level
            location.setLocation(location.getX(),290*FlightSystem.cFactor);
            //If it hasn't been marked as landed, mark it
            if(!ident.contains("D"))
            {
                ident = ident + "D";
            }
        }
    }
}
