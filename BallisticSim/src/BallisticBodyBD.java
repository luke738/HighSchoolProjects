import java.math.BigDecimal;

/**
 * Created by Luke on 9/16/2014.
 */
public class BallisticBodyBD
{
    //Creates basic properties of an object flying through air
    public double mass;
    public double cD; //Coefficient of drag
    public double r; //Radius
    public VectorBD location;
    public VectorBD velocity;
    public int[] color; //Color in RGB form
    public String ident; //Identifying string. Holds a name/miscellaneous info, as well as current status
    //Stores values that are only updated at either creation or end of flight
    public VectorBD finVel; //Impact velocity
    public VectorBD initVel; //Initial velocity
    public VectorBD initLoc;

    //Default constructor
    public BallisticBodyBD()
    {

    }

    //Creates a fully detailed BallisticBody
    public BallisticBodyBD(double m, VectorBD l, VectorBD v, int[] c, String id, double coeffDrag, double rad)
    {
        mass = m;
        location = l;
        velocity = v;
        color = c;
        ident = id;
        cD = coeffDrag;
        r = rad;
        initVel = v;
        initLoc = l;
        //System.out.println(initLoc.getX()+" "+initLoc.getY());
    }

    //Computes total acceleration acting on the object
    public VectorBD totalAccel()
    {
        //Creates the base vector
        VectorBD resultant = new VectorBD(0,0);
        resultant = resultant.add(FlightSystemBD.eG); //Adds gravity
        resultant = resultant.add(drag()); //Adds drag
        System.out.println(resultant);
        return resultant;
    }

    //Computes acceleration due to drag
    public VectorBD drag()
    {
        VectorBD drag = new VectorBD(0,0);
        double dragf;
        //Compute force of drag by F=0.5*rho*v^2*Cd*A
        double ang = velocity.getTheta();
        dragf = 0.5*FlightSystemBD.rho*Math.pow(velocity.getM().doubleValue(),2)*cD*(Math.PI*Math.pow(r,2));
        drag = new VectorBD(dragf*Math.cos(ang),dragf*Math.sin(ang)).scalMult(1/mass); //Convert force to acceleration by a=F/m
        return drag;
    }

    //Checks for collision with ground or goalpost
    public void colMan()
    {
        //If rocket is past and above the goalpost
        if(location.x.doubleValue()>45.67&&290*FlightSystemBD.cFactor.doubleValue()-location.y.doubleValue()>3.05)
        {
            //And it hasn't already been marked as being so
            if(!ident.contains("V"))
            {
                //System.out.println(location.vecToString());
                //Mark rocket as a valid design/launch
                ident = ident + "V";
            }
        }
        //If rocket is on or past the ground
        if(location.y.compareTo(BigDecimal.valueOf(290).multiply(FlightSystemBD.cFactor))>0)
        {
            //System.out.println("impact detected");
            //If final velocity hasn't been recorded (R)
            if(!ident.contains("R"))
            {
                //Record final velocity and mark as recorded
                finVel=velocity;
                ident+="R";
            }
            //Set velocity to 0 to give the appearance of collision with the ground
            velocity=new VectorBD(0,0);
            //Set the location to the current X at ground level
            location= new VectorBD(location.x,BigDecimal.valueOf(290).multiply(FlightSystemBD.cFactor));
            //If it hasn't been marked as landed, mark it
            if(!ident.contains("D"))
            {
                ident = ident + "D";
            }
        }
    }
}
