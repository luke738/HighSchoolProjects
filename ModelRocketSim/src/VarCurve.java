/**
 * Name: Luke St. Regis
 * Period: 1
 * Date: 5/11/2015
 * Assignment: PG1A
 * Phone Number: 1-310-560-0942
 * Email: luke7380@gmail.com
 */
public class VarCurve
{
    double[] xKeys;
    double[] yKeys;

    public VarCurve(double[] x, double[] y)
    {
        xKeys = x;
        yKeys = y;
    }

    public double getYofX(double x)
    {
        int index = 0;
        for (int i = 0; i < xKeys.length; i++)
        {
            if(xKeys[i]>x)
            {
                break;
            }
            index = i;
        }
        double slope;
        if(index < xKeys.length-1)
        {
            slope = (yKeys[index + 1] - yKeys[index]) / (xKeys[index + 1] - xKeys[index]);
        }
        else
        {
            return 0;
        }
        return yKeys[index]+(x- xKeys[index])*slope;
    }
}
