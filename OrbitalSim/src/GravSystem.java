import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luke on 9/16/2014.
 */
public class GravSystem
{
    public static List<OrbitalBody> bodies = new ArrayList<OrbitalBody>();
    public static double G = 6.673*Math.pow(10,-11);
    public static double cFactor=1000;
    public static double tFactor=1;
    public static DVectorField field = new DVectorField(20);

    public GravSystem(/*double m, Point2D l, Vector v, int[] c, String id, */double cF,double tF)
    {
        //bodies.add(new OrbitalBody(m,l,v,c,id));
        cFactor=cF;
        tFactor=tF;
    }
    public List<OrbitalBody> getBodies()
    {
        return bodies;
    }
    public void addBody(double m, Vector l, Vector v, int[] c, String id, double s)
    {
        bodies.add(new OrbitalBody(m,l,v,c,id,s));
    }
    public void simStep()
    {
        //OrbitalBody cBody;
        for (int i = 0; i < bodies.size(); i++)
        {
            //cBody = bodies.get(i);
            bodies.get(i).velocity=bodies.get(i).velocity.sub(bodies.get(i).totalAccelGrav().scalMult(tFactor));
            //System.out.println("bodies = " + bodies.get(i).velocity.vecToString());
            // /System.out.println("Vel "+bodies.get(i).ident+":"+bodies.get(i).velocity.vecToString());
            bodies.get(i).location.add(bodies.get(i).velocity.scalMult(tFactor));
            System.out.println(bodies.get(i).location.vecToString());
            bodies.get(i).colMan();
            /*if (bodies.get(i).mass<=0)
            {
                System.out.println("WARN: "+bodies.get(i).ident+" has mass "+bodies.get(i).mass);
            }*/
            //System.out.println("Loc:"+bodies.get(i).location.toString());
            //bodies.set(i,cBody);
        }
        field.update();
    }
    public void drawBodies(Graphics g)
    {
        Graphics2D g2 = (Graphics2D)g;
        for (int i = 0; i < bodies.size(); i++)
        {
            g2.setPaint(new Color(bodies.get(i).color[0],bodies.get(i).color[1],bodies.get(i).color[2]));
            g2.fill(new Ellipse2D.Double(bodies.get(i).location.x/cFactor-bodies.get(i).size/cFactor,bodies.get(i).location.y/cFactor-bodies.get(i).size/cFactor,2*bodies.get(i).size/cFactor,2*bodies.get(i).size/cFactor));
        }
        field.draw(g2);
    }
}
