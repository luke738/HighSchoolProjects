import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * SystemSimulator handles the simulation for a group of Bodies.
 * It is responsible for updating their position and velocity through numerical linear multistep integration.
 */
public class SystemSimulator
{
    /**Bodies are stored in this ArrayList; this is how the simulator keeps track of them.
     * Any number of bodies can be added.*/
    public static List<Rocket> rockets = new ArrayList<Rocket>();
    /**The universal gravitational constant.*/
    public static final Vector g_earth = new Vector(0,9.81);
    public static final double rho = 1.275; //kg/m^3
    /**A conversion factor from pixels to meter; each pixel is equal to this number of meters.*/
    public static double spaceCompression = 1;
    /**A conversion factor from each frame in the simulation to seconds; each frame is equal to this number of seconds*/
    public static double timeCompression = 0.001;
    /**Keeps track of how many frames have passed; used to pick an integration method and for timekeeping*/
    private static int stepCount = 0;

    /**Accepts no parameters, populates bodies with contents of populateSystem().*/
    public SystemSimulator()
    {
        populateSystem();
    }

    public static double getTime()
    {
        return stepCount*timeCompression;
    }

    /**Creates the Rocket objects and adds them to the bodies ArrayList.
     * Once the simulation is launched, bodies can only be removed (through collision)*/
    public void populateSystem()
    {
        ModelEngine estesB64 = new ModelEngine(0.019, 0.00624, 81.76, new VarCurve(new double[]{0,0.023,0.057,0.089,0.116,0.148,0.171,0.191,0.2,0.209,0.230,0.255,0.305,0.375,0.477,0.58,0.671,0.746,0.786,0.802,0.825,0.86}, new double[]{0,0.688,2.457,4.816,7.274,9.929,12.14,11.695,10.719,9.24,7.667,6.488,5.505,4.816,4.620,4.620,4.521,4.226,4.325,3.145,1.572,0}));
        double start = 0.04497;
        for (double i = start; i < 0.04498; i+=0.00001)
        {
            rockets.add(new Rocket(String.valueOf(i), new int[]{(int)(((i-start)*1000000)/4),128,255}, i, 0.00114, 1.35, new Vector(10*(i-start)*100000,SystemAnimator.B_HEIGHT), new Vector(0,0), new Vector(0,1), estesB64.copy()));
        }
        rockets.add(new Rocket("85deg", new int[]{0,255,0}, 0.04497, 0.00114, 1.35, new Vector(50,SystemAnimator.B_HEIGHT), new Vector(0,0), new Vector(Math.cos(Math.toRadians(60)), Math.sin(Math.toRadians(60))), estesB64.copy()));
        //rockets.add(new Rocket("test", new int[]{255,0,0}, 0.05, 5*Math.pow(10,-4), 0.75, new Vector(100,SystemAnimator.B_HEIGHT), new Vector(0,0), new Vector(0,1), ModelEngine.ESTESB64));
        //rockets.add(new Rocket("test", new int[]{255,255,255}, 0.06, 5*Math.pow(10,-4), 0.5, new Vector(100,200), new Vector(0,0), new Vector(0,1), 0, 81.76, new VarCurve(new double[]{0,0.1,0.2,0.3,0.5,0.7,0.8,0.85}, new double[]{0,6,12,6,3.6,3.5,3.5,0})));
    }

    public void updateSystem()
    {
        List<Rocket> copyBodies = new ArrayList<Rocket>(rockets);
        //System.out.println(stepCount);
        for (Rocket rocket : rockets)
        {
            if(!rocket.doRemove)
            {
                if (stepCount == 0)
                {
                    eulerStep(rocket);
                    //System.out.println(rocket.velocity.toString());
                }
                else if (stepCount == 1)
                {
                    adamBash2ndOrdStep(rocket);
                    //System.out.println(rocket.velocity.toString());
                }
                else
                {
                    adamBash3rdOrdStep(rocket);
                    //System.out.println(rocket.velocity.toString());
                }
                //Rocket.collisionChk();
                //System.out.println(Rocket.ident+" "+Rocket.velocity.toString());
            }
            else
            {
                copyBodies.remove(rocket);
            }
        }
        rockets = copyBodies;
        for (Rocket rocket : rockets)
        {
            rocket.collisionChk();
        }
        stepCount++;
    }

    public void draw(Graphics2D g2)
    {
        for (Rocket rocket : rockets)
        {
            g2.setPaint(new Color(rocket.color[0],rocket.color[1],rocket.color[2]));
            g2.fill(new Ellipse2D.Double(rocket.location.x/spaceCompression-3,rocket.location.y/spaceCompression-3,6,6));
        }
    }

    /**Updates a bodies position-velocity through the Euler method, a 1st order linear explicit method for numerical integration.
     * Requires no more information than the current position and velocity, but stores the last values of them before simulating for future use.
     * Least accurate of the three implemented methods, but requires little information, used only for the first frame.
     * @param Rocket The Rocket to be simulated.*/
    public void eulerStep(Rocket Rocket)
    {
        Rocket.accelOld=Rocket.acceleration();
        Rocket.velOld=Rocket.velocity;
        Rocket.velocity=Rocket.velocity.add(Rocket.acceleration().scalarMult(timeCompression));
        //System.out.println(Rocket.acceleration().x+" "+Rocket.acceleration().y);
        Rocket.location=Rocket.location.add(Rocket.velocity.scalarMult(timeCompression));
    }

    /**Updates a bodies position-velocity through the 2nd order explicit Adams-Bashford method.
     * Requires both the current information and information from 1 frame ago, but stores info from 2 frames ago.
     * Intermediate accuracy, intermediate information needs, used only for the second frame.
     * @param Rocket The Rocket to be simulated.*/
    public void adamBash2ndOrdStep(Rocket Rocket)
    {
        Rocket.accel2Old=Rocket.accelOld;
        Rocket.accelOld=Rocket.acceleration();

        Rocket.vel2Old=Rocket.velOld;
        Rocket.velOld=Rocket.velocity;
        Rocket.velocity=Rocket.velocity.add(Rocket.accelOld.scalarMult(1.5*timeCompression)).sub(Rocket.accel2Old.scalarMult(0.5*timeCompression));

        Rocket.location=Rocket.location.add(Rocket.velOld.scalarMult(1.5*timeCompression).sub(Rocket.vel2Old.scalarMult(0.5*timeCompression)));
    }

    public void adamBash3rdOrdStep(Rocket Rocket)
    {
        Rocket.accel3Old = Rocket.accel2Old;
        Rocket.accel2Old = Rocket.accelOld;
        Rocket.accelOld = Rocket.acceleration();

        Rocket.vel3Old=Rocket.vel2Old;
        Rocket.vel2Old=Rocket.velOld;
        Rocket.velOld=Rocket.velocity;
        Rocket.velocity=Rocket.velocity.add(Rocket.accelOld.scalarMult(timeCompression*(23D/12D))).sub(Rocket.accel2Old.scalarMult(timeCompression*(4D/3D))).add(Rocket.accel3Old.scalarMult(timeCompression*(5D/12D)));

        Rocket.location=Rocket.location.add(Rocket.velOld.scalarMult(timeCompression*(23D/12D))).sub(Rocket.vel2Old.scalarMult(timeCompression*(4D/3D))).add(Rocket.vel3Old.scalarMult(timeCompression*(5D/12D)));
    }
}
