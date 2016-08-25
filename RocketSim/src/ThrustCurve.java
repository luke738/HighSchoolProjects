/**
 * Name: Luke St. Regis
 * Period: 1
 * Date: 5/11/2015
 * Assignment: PG1A
 * Phone Number: 1-310-560-0942
 * Email: luke7380@gmail.com
 */
public class ThrustCurve
{
    double[] timeKeys;
    double[] thrustKeys;

    public ThrustCurve(double[] times, double[] thrusts)
    {
        timeKeys = times;
        thrustKeys = thrusts;
    }

    public double getCurrentThrust(double t)
    {
        int index = 0;
        for (int i = 0; i < timeKeys.length; i++)
        {
            if(timeKeys[i]>t)
            {
                break;
            }
            index = i;
        }
        double thrustSlope;
        if(index<timeKeys.length)
        {
            thrustSlope = (thrustKeys[index + 1] - thrustKeys[index]) / (timeKeys[index + 1] - timeKeys[index]);
        }
        else
        {
            return 0;
        }
        return thrustKeys[index]+(t-timeKeys[index])*thrustSlope;
    }
}
