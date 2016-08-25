import com.sun.org.apache.xerces.internal.impl.dv.DVFactoryException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Name: Luke St. Regis
 * Period: 1
 * Date: 11/18/2014
 * Assignment: PG1A
 * Phone Number: 1-310-560-0942
 * Email: luke7380@gmail.com
 */
public class DVectorField
{
    public static List<DVector> vectors = new ArrayList<DVector>();
    public static int density;
    private static double stretch = 10;

    public DVectorField(int d)
    {
        density=d;
        createField();
    }

    public void createField()
    {
        for (int i = 0; i < SystemDrawer.B_WIDTH; i+=density)
        {
            for (int j = 0; j < SystemDrawer.B_HEIGHT; j+=density)
            {
                vectors.add(new DVector(0,0,i*SystemDrawer.cFac,j*SystemDrawer.cFac));
            }
        }
        //vectors.add(new DVector(0,0,100*SystemDrawer.cFac,100*SystemDrawer.cFac));
    }

    public void update()
    {
        double maxVal = 0;
        for (int i = 0; i < vectors.size(); i++)
        {
            Vector dif;
            vectors.get(i).x=0;
            vectors.get(i).y=0;
            double mag = 0;
            for (int j = 0; j < GravSystem.bodies.size(); j++)
            {
                dif=new Vector(vectors.get(i).a-GravSystem.bodies.get(j).location.x, vectors.get(i).b-GravSystem.bodies.get(j).location.y);
                if (dif.getM()>GravSystem.bodies.get(j).size)
                {
                    mag = (GravSystem.G * GravSystem.bodies.get(j).mass) / Math.pow(dif.getM(), 2);
                    //mag = Math.sqrt(Math.abs(mag)) * (mag / Math.abs(mag));
                }
                else
                {
                    mag = 0;
                }
                //System.out.println(dif.unitVec().scalMult(mag).vecToString());
                vectors.set(i, new DVector(vectors.get(i).x + dif.unitVec().scalMult(mag).x, vectors.get(i).y + dif.unitVec().scalMult(mag).y, vectors.get(i).a, vectors.get(i).b));
            }
            if(vectors.get(i).getM()>maxVal)
            {
                maxVal=vectors.get(i).getM();
            }
            //System.out.println(mag);
        }
        stretch=maxVal;
    }

    public void draw(Graphics2D g2)
    {
        g2.setColor(new Color(255,255,255));
        for (int i = 0; i < vectors.size(); i++)
        {
            vectors.get(i).draw(g2);
        }
    }

    public static double scale(double val)
    {
        double retVal = 254*(val/stretch);
        return retVal+1;
    }
}
