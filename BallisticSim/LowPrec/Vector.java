/**
 * Created by Luke on 9/4/2014.
 * A simple 2D Vector with a few methods for manipulation
 */
public class Vector
{
    //x and y component of Vector
    public double x;
    public double y;

    //Constructor
    public Vector(double a, double b)
    {
        x=a;
        y=b;
    }

    //Returns magnitude via the distance formula
    public double getM()
    {
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    }

    //Returns angle in degrees, using atan2 to preserve the direction of the Vector
    public double getTheta()
    {
        double ang=Math.atan2(y, x);
        return Math.toDegrees(ang);
    }

    //Adds Vector y to Vector x
    public Vector add(Vector y)
    {
        return new Vector(x+y.x, this.y+y.y);
    }

    //Subtracts Vector y from Vector x
    public Vector sub(Vector y)
    {
        return new Vector(x-y.x, this.y-y.y);
    }

    //Multiplies vector by a scalar
    public Vector scalMult(double scalar)
    {
        return new Vector(x*scalar,y*scalar);
    }

    //Converts vector to a string displaying X and Y components, for easier printing
    public String vecToString()
    {
        return "X:"+x+" Y:"+y;
    }

    //Returns the unit Vector in the same direction as this Vector
    public Vector unitVec()
    {
        //Handles case of (0,0) to avoid division by 0
        if(getM()>0)
        {
            return this.scalMult(1 / getM());
        }
        return this.scalMult(0);
    }
}
