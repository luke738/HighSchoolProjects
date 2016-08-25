import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * A gravitationally interacting body, with methods to compute its current acceleration
 * based on the presence of other bodies.
 */
public class Body
{
    public String ident;/**Name of the body; a body only interacts gravitationally with other bodies with unique names*/
    public double mass;/**Mass in kilograms*/
    public double size;/**Radius in meters*/
    public double density;/**Density in kg/m^3, computed when object is instantiated*/
    public Vector location;/**x-y position of the body in the simulation, origin at top left*/
    public Vector velocity;/**Velocity of the body*/
    int[] color;/**An RGB color for visual identification*/
    public Vector accelOld;/**Acceleration 1 timestep in the past*/
    public Vector accel2Old;/**Acceleration 2 timesteps in the past*/
    public Vector accel3Old;/**Acceleration 3 timesteps in the past*/
    public Vector velOld;/**Velocity 1 timestep in the past*/
    public Vector vel2Old;/**Velocity 2 timesteps in the past*/
    public Vector vel3Old;/**Velocity 3 timesteps in the past*/
    public Boolean doRemove = false;

    public Body(){}

    /**Creates a body with all required information to run the simulation.
     * @param id Name of the Body
     * @param m Mass of the Body
     * @param r Radius of the Body
     * @param loc Initial location of the Body
     * @param vel Initial velocity of the Body
     * @param col An RGB color to identify the Body*/
    public Body(String id, double m, double r, Vector loc, Vector vel, int[] col)
    {
        ident=id;
        mass=m;
        size=r;
        density=m/((4D/3D)*Math.PI*Math.pow(r, 3));
        location = loc;
        velocity = vel;
        color = col;
    }

    /**Computes current acceleration of the body due to the gravitational influence of other bodies in their current positions.
     * @return A Vector corresponding to acceleration along the x and y axis*/
    public Vector acceleration()
    {
        Vector totalAcceleration = new Vector(0, 0);
        for(Body b : SystemSimulator.bodies)
        {
            if (!ident.equals(b.ident) && !doRemove && !b.doRemove)
            {
                totalAcceleration=totalAcceleration.add(twoBodyAcceleration(b));
            }
        }
        return totalAcceleration;
    }

    /**Used by acceleration(), finds gravity due to one other body.
     * @param b2 The other Body
     * @return Acceleration towards that Body along the x and y axis*/
    public Vector twoBodyAcceleration(Body b2)
    {
        Vector displacement = location.sub(b2.location);
        double distance = displacement.getMag();
        //System.out.println(distance);
        double undirectedAccel = -SystemSimulator.G*(b2.mass/Math.pow(distance,2));
        //System.out.println(undirectedAccel);
        return displacement.scalarMult(undirectedAccel).scalarMult(1/distance);
    }

    public void collisionChk()
    {
        for(Body body : SystemSimulator.bodies)
        {
            if(!body.ident.equals(ident) && !body.doRemove && !doRemove)
            {
                Vector positionVector = body.location.sub(location);
                //System.out.println(positionVector.getMag());
                if (positionVector.getMag() < size + body.size)
                {
                    //System.out.println(ident+" "+velocity.toString());
                    Vector momentum = velocity.scalarMult(mass).add(body.velocity.scalarMult(body.mass));
                    mass+=body.mass;
                    double massFrac = body.mass/mass;
                    //body.mass=0;
                    double vol = mass / (body.density*massFrac+density*(1-massFrac));
                    //double oVol = body.mass / body.density;
                    size = Math.cbrt((3D / 4D) * vol / Math.PI);
                    color = new int[]{(int)(color[0]*(1-massFrac)+body.color[0]*(massFrac)),(int)(color[1]*(1-massFrac)+body.color[1]*(massFrac)),(int)(color[2]*(1-massFrac)+body.color[2]*(massFrac))};
                    location=new Vector(location.x*(1-massFrac)+body.location.x*massFrac, location.y*(1-massFrac)+body.location.y*(massFrac));
                    setAllVel(momentum.scalarMult(1/mass));
                    setAllAccel();
                    if(velocity.getMag()>1500)
                    {
                        System.out.println("WARN: "+ident);
                        System.out.println(momentum);
                    }
                    //System.out.println(ident+" "+mass);
                    //body.size = Math.cbrt((3D / 4D) * oVol / Math.PI);
                    body.doRemove = true;
                    //velocity=new Vector(0,0);
                    //SystemSimulator.bodies.remove(body);
                }
            }
        }
    }

    public void setAllVel(Vector newVel)
    {
        velocity=newVel;
        velOld=newVel;
        vel2Old=newVel;
        vel3Old=newVel;
    }

    public void setAllAccel()
    {
        accelOld=acceleration();
        accel2Old=accelOld;
        accel3Old=accel2Old;
    }

    /**Draws the body to the GUI*/
    public void draw(Graphics2D g2)
    {
        g2.setColor(new Color(color[0], color[1], color[2]));
        g2.fill(new Ellipse2D.Double((location.x-size)/ SystemSimulator.spaceCompression, (location.y-size)/ SystemSimulator.spaceCompression, ((size*2)/ SystemSimulator.spaceCompression), ((size*2)/ SystemSimulator.spaceCompression)));
    }
}

