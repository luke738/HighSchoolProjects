import java.awt.geom.Point2D;

/**
 * Created by Luke on 9/16/2014.
 */
public class OrbitalBody
{
    public double mass;
    public Vector location;
    public Vector velocity;
    public int[] color;
    public String ident;
    public double size;
    double density;// = mass/((4/3)*Math.pow(size,3)*Math.PI);

    public OrbitalBody(double m, Vector l, Vector v, int[] c, String id, double s)
    {
        mass = m;
        location = l;
        velocity = v;
        color = c;
        ident = id;
        size = s;
        density = mass/((4*Math.PI*Math.pow(size,3))/3);
        //System.out.println(mass);
        //System.out.println((4*Math.PI*Math.pow(size,3))/3);
    }

    public Vector totalAccelGrav()
    {
        Vector resultant = new Vector(0,0);
        for (int i = 0; i < GravSystem.bodies.size(); i++)
        {
            if(GravSystem.bodies.get(i).ident!=ident)
            {
                resultant = resultant.add(twoBodyAccel(GravSystem.bodies.get(i)));

            }
        }
        //System.out.println("Acc "+ident+":"+resultant.vecToString());
        return resultant;
    }

    public void colMan()
    {
        for (int i = 0; i < GravSystem.bodies.size(); i++)
        {
            if(GravSystem.bodies.get(i).ident!=ident)
            {
                Vector selfToOther = location.sub(GravSystem.bodies.get(i).location);
                if(selfToOther.getM()<size+GravSystem.bodies.get(i).size)
                {
                    double avgDensity = (density*(mass/(mass+GravSystem.bodies.get(i).mass)))+(GravSystem.bodies.get(i).density*(GravSystem.bodies.get(i).mass/(mass+GravSystem.bodies.get(i).mass)));
                    System.out.println(density);
                    System.out.println(ident);
                    System.out.println("avgDensity = " + avgDensity);
                    //mass+=GravSystem.bodies.get(i).mass;
                    Vector momentum = velocity.scalMult(mass);
                    Vector momentum2 = GravSystem.bodies.get(i).velocity.scalMult(GravSystem.bodies.get(i).mass);
                    mass+=GravSystem.bodies.get(i).mass;
                    size=0.62035*Math.cbrt((mass / avgDensity));
                    velocity=(momentum.add(momentum2)).scalMult((1/mass));
                    for (int j = i+1; j < GravSystem.bodies.size(); j++)
                    {
                        GravSystem.bodies.set(j-1,GravSystem.bodies.get(j));
                    }
                    //GravSystem.bodies.remove(GravSystem.bodies.size()-1);
                    GravSystem.bodies.remove(i);
                }
            }
        }
        /*if(location.getX()<100*GravSystem.cFactor)
        {
            velocity=velocity.add(new Vector(5*(location.getX()/GravSystem.cFactor),0));
        }
        else if(location.getX()>400*GravSystem.cFactor)
        {
            velocity=velocity.add(new Vector(-5*(500-(location.getX()/GravSystem.cFactor)),0));
        }
        if(location.getY()<100*GravSystem.cFactor)
        {
            velocity=velocity.add(new Vector(0,5*(location.getY()/GravSystem.cFactor)));
        }
        else if(location.getY()>400*GravSystem.cFactor)
        {
            velocity=velocity.add(new Vector(0,-5*(500-(location.getY()/GravSystem.cFactor))));
        }*/
    }

    public Vector twoBodyAccel(OrbitalBody other)
    {
        Vector selfToOther = location.sub(other.location);
        double resMag = GravSystem.G*(other.mass/Math.pow(selfToOther.getM(),2));
        //double resAng = selfToOther.getA();
        Vector accel = new Vector(resMag*Math.cos(selfToOther.getTheta()),resMag*Math.sin(selfToOther.getTheta()));
        //System.out.println("Acc to "+other.ident+":"+accel.vecToString());
        return accel;
    }
}
