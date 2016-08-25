import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Name: Luke St. Regis
 * Period: 1
 * Date: 2/10/2015
 * Assignment: PG1A
 * Phone Number: 1-310-560-0942
 * Email: luke7380@gmail.com
 */
public class Rocket extends Body
{
    public double fuelMass;
    public double F_t;
    public double isp;
    public double orientation;

    public Rocket(String id, double m, double r, Vector loc, Vector vel, int[] col, double fuelM, double F_t,  double isp, double orientation)
    {
        ident = id;
        mass = m;
        size = r;
        location = loc;
        velocity = vel;
        color = col;
        fuelMass = fuelM;
        this.F_t = F_t;
        this.isp = isp;
        this.orientation = orientation;
    }

    @Override
    public Vector acceleration()
    {
        return super.acceleration().add(thrust()).add(SystemSimulator.g_earth);
    }

    public Vector thrust()
    {
        double massRate = (F_t/isp)*SystemSimulator.g_earth.getMag();
        if(fuelMass>0)
        {
            mass -= massRate * SystemSimulator.timeCompression;
            fuelMass -= massRate * SystemSimulator.timeCompression;

            double mag = F_t / mass;
            Vector direction = new Vector(Math.cos(orientation), Math.sin(orientation));

            return direction.scalarMult(mag);
        }
        return new Vector(0,0);
    }

    public void draw(Graphics2D g2)
    {
        g2.setColor(new Color(color[0], color[1], color[2]));
        g2.fill(new Ellipse2D.Double((location.x-size-5)/ SystemSimulator.spaceCompression, (location.y-size-5)/ SystemSimulator.spaceCompression, ((size*2)/ SystemSimulator.spaceCompression)+10, ((size*2)/ SystemSimulator.spaceCompression)+10));
    }
}
