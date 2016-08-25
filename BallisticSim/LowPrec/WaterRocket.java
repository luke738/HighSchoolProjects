/**
 * Name: Luke St. Regis
 * Period: 5
 * Date: 10/8/2014
 * Assignment:
 * Phone Number: 1-310-560-0942
 * Email: luke7380@gmail.com
 */

//A special case of BallisticBody that can propel itself through water and pressure
public class WaterRocket extends BallisticBody
{
    //Creates the properties specific to water rockets
    public double rNozzle; //Nozzle radius
    public double lPressure; //Launch pressure
    public double cPressure; //Current pressure
    public double volume; //Total volume inside the rocket
    public double waterFrac; //Fraction of volume that is water at launch
    public double waterMass; //Mass of water
    public Vector launchAngleVec; //Unit vector pointing along the launch angle
    public double dMdt; //Change in mass over change in time, mass loss rate kg/s
    public Vector orientation; //Unit vector pointing in the direction of the rocket. Experimental.

    //Create the object. Uses a BallisticBody for shared properties
    public WaterRocket(BallisticBody base, double rN, double lP, double vol, double wF, double lAng)
    {
        mass = base.mass;
        location = base.location;
        velocity = new Vector(0,0); //Rockets have no initial velocity, instead being propelled solely by themselves
        color = base.color;
        ident = base.ident;
        cD = base.cD;
        r = base.r;
        initVel = base.velocity;
        secInitLoc[0] = base.location.getX();
        secInitLoc[1] = base.location.getY();
        rNozzle = rN;
        lPressure = lP;
        cPressure = lP;
        volume = vol;
        waterFrac = wF;
        waterMass = wF*vol*FlightSystem.rhoW; //Compute mass of water from total volume, water fraction, and water density kg/m^3
        launchAngleVec = new Vector(Math.cos(Math.toRadians(lAng)),-Math.sin(Math.toRadians(lAng))); //Computes the launch vector from launch angle
        orientation = launchAngleVec; //Orientation starts pointing in the launch angle.
    }

    //Experimental orientation function
    public void orient()
    {
        double diffFromVel = velocity.getTheta()-orientation.getTheta(); //Difference between velocity vector angle and current orientation angle
        double newTheta = orientation.getTheta()+diffFromVel;//*FlightSystem.tFactor; //Attempts to compute a new angle for orientation. I have no idea what the equation for this is.
        //orientation = new Vector(Math.cos(Math.toRadians(newTheta)),Math.sin(Math.toRadians(newTheta))); //Updates orientation. Commented out because it's too untested.
    }

    //Rocket specific acceleration, overrides BallisticBody's method
    @Override
    public Vector totalAccel()
    {
        Vector resultant = super.totalAccel(); //Starts by using BallisticBody's method to compute drag+gravity
        resultant = resultant.sub(thrustAccel()); //Subtracts thrust from acceleration vector. Subtraction because it it negative for some reason. I'm not sure.
        return resultant;
    }

    //Computes acceleration due to thrust
    public Vector thrustAccel()
    {
        //If there is water left in the rocket;
        if(waterMass>0)
        {
            Vector fThrust; //Vector representing force
            double initAirV = volume - waterFrac * volume; //Compute initial volume of air in rocket
            double initWaterMass = volume * waterFrac * FlightSystem.rhoW; //Compute initial mass of water in rocket
            //Computes current pressure in rocket from launch pressure, inital volume and current volume. Assumes adiabatic expansion of air.
            cPressure = lPressure * Math.pow((initAirV + (initWaterMass - waterMass) / FlightSystem.rhoW) / initAirV, -1.4);
            //Uses current pressure to find current exhaust speed, relative to the rocket
            double vE = Math.sqrt(2 * (cPressure - FlightSystem.airPres) / FlightSystem.rhoW);
            //Uses exhaust speed and nozzle radius to find mass loss rate
            dMdt = -Math.PI * Math.pow(rNozzle, 2) * FlightSystem.rhoW * vE;
            //Computes scalar force from nozzle radius, current rocket pressure, and air pressure outside the rocket
            double force = 2 * Math.PI * Math.pow(rNozzle, 2) * (cPressure - FlightSystem.airPres);
            //Converts scalar to vector by multiplying by the unit vector of orientation
            fThrust = orientation.scalMult(force);
            //Updates remaining mass of water in rocket
            waterMass += dMdt * FlightSystem.tFactor;
            //Returns the acceleration due to thrust from F=ma
            return fThrust.scalMult(1 / (mass + waterMass));
        }
        else
        {
            //If no water remains, thrust=0
            return new Vector(0,0);
        }
    }
}
