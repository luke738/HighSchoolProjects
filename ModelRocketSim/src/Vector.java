/**
 * A general purpose two-dimensional vector with some methods for manipulation.
 * Encapsulation is not used, but the methods return a new Vector.
 */
public class Vector
{
    /**First dimension of the vector*/
    public double x;
    /**Second dimension of the vector*/
    public double y;

    /**Creates a two-dimensional vector
     * @param a Value of first dimension
     * @param b Value of second dimension*/
    public Vector(double a, double b)
    {
        x=a;
        y=b;
    }

    /**The magnitude of the vector
     * @return A double value for the length of the vector*/
    public double getMag()
    {
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
    }

    /**Adds another vector to this Vector
     * @param v2 The second Vector
     * @return A new Vector resulting from the addition*/
    public Vector add(Vector v2)
    {
        return new Vector(x+v2.x,y+v2.y);
    }

    /**Subtracts another vector from this Vector
     * @param v2 The second Vector
     * @return A new Vector resulting from the subtraction*/
    public Vector sub(Vector v2)
    {
        return new Vector(x-v2.x,y-v2.y) ;
    }

    /**Multiplies the vector by a scalar
     * @param scalar A scalar value, a double
     * @return A new Vector resulting from the multiplication*/
    public Vector scalarMult(double scalar)
    {
        return new Vector(x*scalar, y*scalar);
    }

    public Vector average(Vector v2)
    {
        return new Vector((x+v2.x)/2, (y+v2.y)/2);
    }

    public Vector normalize()
    {
        if(x!=0 || y!=0)
        {
            double invMag = 1/getMag();
            //System.out.println(invMag);
            return this.scalarMult(invMag);
        }
        return new Vector(0,0);
    }

    public String toString()
    {
        return "X: "+x+"  Y: "+y;
    }
}
