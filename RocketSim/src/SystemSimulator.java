import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SystemSimulator handles the simulation for a group of Bodies.
 * It is responsible for updating their position and velocity through numerical linear multistep integration.
 */
public class SystemSimulator
{
    /**Bodies are stored in this ArrayList; this is how the simulator keeps track of them.
     * Any number of bodies can be added.*/
    public static List<Body> bodies = new ArrayList<Body>();
    /**The universal gravitational constant.*/
    public static double G = 6.673*Math.pow(10,-11);
    public static Vector g_earth = new Vector(0, 9.81);
    public static double rho = 1.275; //kg/m^3
    /**A conversion factor from pixels to meter; each pixel is equal to this number of meters.*/
    public static double spaceCompression = 10;
    /**A conversion factor from each frame in the simulation to seconds; each frame is equal to this number of seconds*/
    public static double timeCompression = 0.1;
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

    /**Creates the Body objects and adds them to the bodies ArrayList.
     * Once the simulation is launched, bodies can only be removed (through collision)*/
    public void populateSystem()
    {
        //bodies.add(new Body("test", 5.972*Math.pow(10, 24), 6371000000D, new Vector(1000+-6371000 * SystemSimulator.spaceCompression, 450 * SystemSimulator.spaceCompression), new Vector(0,0), new int[]{255, 0, 255}));
        //bodies.add(new Rocket("rocket", 5*Math.pow(10, 5), 300, new Vector(400 * SystemSimulator.spaceCompression, 200 * SystemSimulator.spaceCompression), new Vector(0,0), new int[]{255, 255, 255}, 0*0.99*Math.pow(10,8), 0*Math.pow(10,6)*Math.pow(10,10), 35000, 0));
        //bodies.add(new Body("planet", 5*Math.pow(10, 24), 600000000D, new Vector(400 * SystemSimulator.spaceCompression, (300+600000) * SystemSimulator.spaceCompression), new Vector(0,0), new int[]{255, 0, 255}));

    }

    /**Updates the velocity and position of each Body each frame.
     * The preferred method of integration is the 3rd order Adams-Bashford method, but this requires information from 3 timesteps in the past.
     * As this is not available at the start of the simulation, it first makes use of the Euler method for a frame.
     * It then uses the two sets of position-velocity to use a more accurate 2nd order method.
     * At this point, the 3rd frame in the simulation, it has enough data to use the 3rd order method and does so for the rest of the simulation.
     * This system allows for roughly a 4-fold increase in speed with no loss of accuracy, or a 4-fold increase in accuracy with no loss of speed
     * (compared to using solely the Euler method).*/
    public void updateSystem()
    {
        List<Body> copyBodies = new ArrayList<Body>(bodies);
        for (Body body : bodies)
        {
            if(!body.doRemove)
            {
                if (stepCount == 0)
                {
                    eulerStep(body);
                }
                else if (stepCount == 1)
                {
                    adamBash2ndOrdStep(body);
                }
                else
                {
                    adamBash3rdOrdStep(body);
                }
                //body.collisionChk();
                //System.out.println(body.ident+" "+body.velocity.toString());
            }
            else
            {
                copyBodies.remove(body);
            }
        }
        bodies = copyBodies;
        for (Body body : bodies)
        {
            body.collisionChk();
        }
        stepCount++;
    }

    public void draw(Graphics2D g2)
    {
        for (Body body : bodies)
        {
            g2.setColor(new Color(body.color[0], body.color[1], body.color[2]));
            g2.fill(new Ellipse2D.Double((body.location.x-body.size)/ spaceCompression, (body.location.y-body.size)/ spaceCompression, ((body.size*2)/ spaceCompression), ((body.size*2)/ spaceCompression)));
        }
    }

    /**Updates a bodies position-velocity through the Euler method, a 1st order linear explicit method for numerical integration.
     * Requires no more information than the current position and velocity, but stores the last values of them before simulating for future use.
     * Least accurate of the three implemented methods, but requires little information, used only for the first frame.
     * @param body The body to be simulated.*/
    public void eulerStep(Body body)
    {
        body.accelOld=body.acceleration();
        body.velOld=body.velocity;
        body.velocity=body.velocity.add(body.acceleration().scalarMult(timeCompression));
        //System.out.println(body.acceleration().x+" "+body.acceleration().y);
        body.location=body.location.add(body.velocity.scalarMult(timeCompression));
    }

    /**Updates a bodies position-velocity through the 2nd order explicit Adams-Bashford method.
     * Requires both the current information and information from 1 frame ago, but stores info from 2 frames ago.
     * Intermediate accuracy, intermediate information needs, used only for the second frame.
     * @param body The body to be simulated.*/
    public void adamBash2ndOrdStep(Body body)
    {
        body.accel2Old=body.accelOld;
        body.accelOld=body.acceleration();

        body.vel2Old=body.velOld;
        body.velOld=body.velocity;
        body.velocity=body.velocity.add(body.accelOld.scalarMult(1.5*timeCompression)).sub(body.accel2Old.scalarMult(0.5*timeCompression));

        body.location=body.location.add(body.velOld.scalarMult(1.5*timeCompression).sub(body.vel2Old.scalarMult(0.5*timeCompression)));
    }

    public void adamBash3rdOrdStep(Body body)
    {
        body.accel3Old = body.accel2Old;
        body.accel2Old = body.accelOld;
        body.accelOld = body.acceleration();

        body.vel3Old=body.vel2Old;
        body.vel2Old=body.velOld;
        body.velOld=body.velocity;
        body.velocity=body.velocity.add(body.accelOld.scalarMult(timeCompression*(23D/12D))).sub(body.accel2Old.scalarMult(timeCompression*(4D/3D))).add(body.accel3Old.scalarMult(timeCompression*(5D/12D)));

        body.location=body.location.add(body.velOld.scalarMult(timeCompression*(23D/12D))).sub(body.vel2Old.scalarMult(timeCompression*(4D/3D))).add(body.vel3Old.scalarMult(timeCompression*(5D/12D)));
    }
}
