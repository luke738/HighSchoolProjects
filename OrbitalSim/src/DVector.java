import java.awt.*;

/**
 * Name: Luke St. Regis
 * Period: 1
 * Date: 11/18/2014
 * Assignment: PG1A
 * Phone Number: 1-310-560-0942
 * Email: luke7380@gmail.com
 */
public class DVector extends Vector
{
    double a;
    double b;

    public DVector(double magx, double magy, double loca, double locb)
    {
        x=magx;
        y=magy;
        a=loca;
        b=locb;
    }

    public void draw(Graphics2D g2)
    {
        double correctedMag = DVectorField.scale(getM());
        g2.setColor(new Color((int)correctedMag, (int)(255-correctedMag), 0));
        g2.drawLine((int)(a/SystemDrawer.cFac),(int)(b/SystemDrawer.cFac),(int)(a/SystemDrawer.cFac+x),(int)(b/SystemDrawer.cFac+y));
    }
}
