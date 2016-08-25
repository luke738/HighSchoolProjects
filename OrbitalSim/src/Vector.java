/**
 * Created by Luke on 9/4/2014.
 */
public class Vector
{
    public double x;
    public double y;
    //public double mag;
    //public double ang;

    public Vector()
    {

    }
    public Vector(double a, double b)
    {
        x=a;
        y=b;
    }

    public double getM()
    {
        double mag=Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
        return mag;
    }

    public double getTheta()
    {
        double ang=Math.atan2(y, x);
        /*if(x<0)
        {
            ang=Math.PI-ang;
        }
        else if(y<0)
        {
            ang=2*Math.PI+ang;
        }*/
        return ang;
    }

    /*public double dotProduct(Vector y)
    {
        double result = getM()*y.getM()*Math.cos(Math.abs(getA()-y.getA()));
        return result;
    }*/

    public Vector add(Vector y)
    {
        Vector resultant = new Vector(x+y.x,this.y+y.y);
        return resultant;
    }

    public Vector sub(Vector y)
    {
        Vector resultant = new Vector(x-y.x,this.y-y.y);
        return resultant;
    }
    /*public Vector magSub(Vector y)
    {
        Vector resultant = new Vector(x-y.x,this.y-y.y);
        return resultant;
    }*/

    public Vector scalMult(double scalar)
    {
        Vector result = new Vector(x*scalar,y*scalar);
        return result;
    }

    public String vecToString()
    {
        String result = "X:"+x+" Y:"+y;
        return result;
    }

    public Vector unitVec()
    {
        return new Vector(x/getM(),y/getM());
    }
}
