import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Name: Luke St. Regis
 * Period: 5
 * Date: 10/8/2014
 * Assignment:
 * Phone Number: 1-310-560-0942
 * Email: luke7380@gmail.com
 */

//A special case of BallisticBody that can propel itself through water and pressure
public class WaterRocketBD extends BallisticBodyBD
{
    //Creates the properties specific to water rockets
    public double height;
    public double rNozzle; //Nozzle radius
    public BigDecimal lPressure; //Launch pressure
    public BigDecimal cPressure; //Current pressure
    public double volume; //Total volume inside the rocket
    public double waterFrac; //Fraction of volume that is water at launch
    public BigDecimal waterMass; //Mass of water
    public double waterHeight; //Height of water-air interface
    public VectorBD launchAngleVec; //Unit vector pointing along the launch angle
    public BigDecimal dMdt; //Change in mass over change in time, mass loss rate kg/s
    public double dHdt; //Change in height of water-air interface in time, m/s
    public VectorBD orientation; //Unit vector pointing in the direction of the rocket. Experimental.
    public int phase = 1;
    public BigDecimal nozzleTconst;
    private double t = 0;
    private double vE;

    //Create the object. Uses a BallisticBody for shared properties
    public WaterRocketBD(BallisticBodyBD base, double rN, double lP, double vol, double wF, double lAng)
    {
        mass = base.mass;
        location = base.location;
        velocity = new VectorBD(0,0); //Rockets have no initial velocity, instead being propelled solely by themselves
        color = base.color;
        ident = base.ident;
        cD = base.cD;
        r = base.r;
        initVel = base.velocity;
        initLoc = base.location;
        rNozzle = rN;
        lPressure = BigDecimal.valueOf(lP);
        cPressure = BigDecimal.valueOf(lP);
        volume = vol;
        waterFrac = wF;
        waterMass = BigDecimal.valueOf(wF).multiply(BigDecimal.valueOf(vol)).multiply(BigDecimal.valueOf(FlightSystemBD.rhoW)); //Compute mass of water from total volume, water fraction, and water density kg/m^3
        height = vol/(Math.PI*Math.pow(r,2));
        waterHeight = wF*height;
        launchAngleVec = new VectorBD(Math.cos(Math.toRadians(lAng)),Math.sin(Math.toRadians(lAng))); //Computes the launch vector from launch angle
        orientation = launchAngleVec; //Orientation starts pointing in the launch angle.
        BigDecimal nT1 = BigDecimal.valueOf(volume).divide(BigDecimalMath.pi(new MathContext(30)).multiply(BigDecimalMath.pow(BigDecimal.valueOf(rNozzle),BigDecimal.valueOf(2))).multiply(FlightSystemBD.c), BigDecimal.ROUND_FLOOR);
        BigDecimal nT2 = BigDecimal.valueOf(2).divide(BigDecimal.valueOf(0.4), BigDecimal.ROUND_FLOOR);
        BigDecimal nT3 = BigDecimal.valueOf(1.2);
        BigDecimal nT4 = BigDecimal.valueOf(2.4).divide(BigDecimal.valueOf(0.8), BigDecimal.ROUND_FLOOR);
        nozzleTconst = nT1.multiply(nT2).multiply(BigDecimalMath.pow(nT3,nT4));
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
    public VectorBD totalAccel()
    {
        //VectorBD resultant = super.totalAccel(); //Starts by using BallisticBody's method to compute drag+gravity
        VectorBD resultant = new VectorBD(0,0);
        resultant = resultant.add(FlightSystemBD.eG);
        resultant=resultant.setScale(30, BigDecimal.ROUND_FLOOR);
        resultant = resultant.sub(drag());
        resultant=resultant.setScale(30, BigDecimal.ROUND_FLOOR);
        //System.out.println("r: "+resultant.vecToString());
        resultant = resultant.add(thrustAccel());
        //System.out.println("thrust = "+thrustAccel());
        return resultant;
    }

    @Override
    public VectorBD drag()
    {
        VectorBD drag = new VectorBD(0,0);
        double dragf;
        //Compute force of drag by F=0.5*rho*v^2*Cd*A
        double ang = velocity.getTheta();
        dragf = 0.5*FlightSystemBD.rho*Math.pow(velocity.getM().doubleValue(),2)*cD*(Math.PI*Math.pow(r,2));
        drag = new VectorBD(dragf*Math.cos(Math.toRadians(ang)),dragf*Math.sin(Math.toRadians(ang))).scalMult(1/mass+waterMass.doubleValue()); //Convert force to acceleration by a=F/m
        drag = drag.setScale(20, BigDecimal.ROUND_FLOOR);
        return drag;
    }

    //Computes acceleration due to thrust
    public VectorBD thrustAccel()
    {
        //If there is water left in the rocket;
        VectorBD corLoc = new VectorBD(location.x, BigDecimal.valueOf(290*FlightSystemBD.cFactor.doubleValue()).subtract(location.y));
        double aNozzle = Math.PI*Math.pow(rNozzle,2);
        if(waterMass.compareTo(BigDecimal.ZERO)>0&&corLoc.getM().compareTo(BigDecimal.valueOf(FlightSystemBD.launchTubeL))<0)
        {
            double initAirV = volume + FlightSystemBD.launchV - waterFrac * volume - (FlightSystemBD.launchTubeL*(FlightSystemBD.launchTubeAo-FlightSystemBD.launchTubeAi)); //Compute initial volume of air in rocket
            cPressure = lPressure.multiply(BigDecimalMath.pow(BigDecimal.valueOf(initAirV).divide(BigDecimal.valueOf(initAirV).subtract(corLoc.getM().multiply(BigDecimal.valueOf(FlightSystemBD.launchTubeAo))), BigDecimal.ROUND_FLOOR),BigDecimal.valueOf(1.4)));
            double force = FlightSystemBD.launchTubeAo*(cPressure.subtract(FlightSystemBD.airPres).doubleValue());
            return launchAngleVec.scalMult(force).scalMult(1 / (mass + waterMass.doubleValue())).setScale(30, BigDecimal.ROUND_FLOOR).add(FlightSystemBD.eG.scalMult(-1)).add(FlightSystemBD.eG.scalMult(-1));
        }
        else if(waterMass.compareTo(BigDecimal.ZERO)>0)
        {
            if(phase==1)
            {
                lPressure = cPressure;
                t=0;
            }
            phase = 2;
            VectorBD fThrustTot; //Vector representing force
            double initAirV = volume - waterFrac * volume; //Compute initial volume of air in rocket
            BigDecimal initWaterMass = BigDecimal.valueOf(waterFrac).multiply(BigDecimal.valueOf(volume)).multiply(BigDecimal.valueOf(FlightSystemBD.rhoW)); //Compute initial mass of water in rocket
            //Computes current pressure in rocket from launch pressure, inital volume and current volume. Assumes adiabatic expansion of air.
            cPressure = lPressure.multiply(BigDecimalMath.pow(BigDecimal.valueOf(initAirV).divide(BigDecimal.valueOf(initAirV).add(initWaterMass.subtract(waterMass).divide(BigDecimal.valueOf(FlightSystemBD.rhoW),BigDecimal.ROUND_FLOOR)), BigDecimal.ROUND_FLOOR),BigDecimal.valueOf(1.4)));
            //Uses current pressure to find current exhaust speed, relative to the rocket
            vE = Math.sqrt(2 * (cPressure.subtract(FlightSystemBD.airPres).doubleValue()) / FlightSystemBD.rhoW);
            //Uses exhaust speed and nozzle radius to find mass loss rate
            dMdt = BigDecimal.valueOf(-aNozzle * FlightSystemBD.rhoW * vE);
            dMdt = dMdt.setScale(30, BigDecimal.ROUND_FLOOR);
            dHdt = -(aNozzle*vE)/(Math.PI*Math.pow(r,2));
            //Computes scalar force from nozzle radius, current rocket pressure, and air pressure outside the rocket
            double forceT = FlightSystemBD.rhoW*aNozzle*Math.pow(vE,2);
            //Converts scalar to vector by multiplying by the unit vector of orientation
            fThrustTot = velocity.unitVec().scalMult(forceT);//+forceI);
            fThrustTot.setScale(30, BigDecimal.ROUND_FLOOR);
            //Updates remaining mass of water in rocket
            waterMass=waterMass.add(dMdt.multiply(FlightSystemBD.tFactor));
            waterHeight += dHdt * FlightSystemBD.tFactor.doubleValue();
            //Returns the acceleration due to thrust from F=ma
            return fThrustTot.scalMult(1 / (mass + waterMass.doubleValue()));
        }
        else if(cPressure.compareTo(FlightSystemBD.airPres.multiply(BigDecimal.valueOf(1.0594)))>0)
        {
            if(phase==2)
            {
                lPressure = cPressure;
                t=0;
            }
            phase=3;
            t += FlightSystemBD.tFactor.doubleValue();
            cPressure = lPressure.multiply(BigDecimal.valueOf(0.99134716302768435407020454022032)).multiply(BigDecimalMath.pow(BigDecimal.valueOf(1.4119932575).scaleByPowerOfTen(-32),BigDecimal.valueOf(t)));
            BigDecimal fThrust = BigDecimal.valueOf(2).multiply(cPressure).multiply(BigDecimal.valueOf(aNozzle)).multiply(BigDecimal.valueOf(0.633938145260608927612233544908)).subtract(FlightSystemBD.airPres.multiply(BigDecimal.valueOf(aNozzle)));
            return velocity.unitVec().scalMult(fThrust.divide(BigDecimal.valueOf(mass).add(waterMass), BigDecimal.ROUND_FLOOR)).setScale(30, BigDecimal.ROUND_FLOOR);

        }
        else
        {
            phase = 4;
            //If no water remains, thrust=0
            return new VectorBD(0,0);
        }
    }
}
