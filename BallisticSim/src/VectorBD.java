import java.math.BigDecimal;

/**
 * Created by Luke on 9/4/2014.
 * A simple 2D Vector using BigDecimal with a few methods for manipulation
 */
public class VectorBD
{
    //x and y component of Vector
    public BigDecimal x;
    public BigDecimal y;

    //Constructors
    public VectorBD(double a, double b)
    {
        x=BigDecimal.valueOf(a);
        y=BigDecimal.valueOf(b);
    }

    public VectorBD(BigDecimal a, BigDecimal b)
    {
        x=a;
        y=b;
    }

    //Returns magnitude via the distance formula
    public BigDecimal getM()
    {
        return Util.sqrt(x.pow(2).add(y.pow(2)));
    }

    //Returns angle in degrees, using atan2 to preserve the direction of the Vector
    public double getTheta()
    {
        double ang=Math.atan2(y.doubleValue(), x.doubleValue());
        return Math.toDegrees(ang);
    }

    //Adds Vector y to Vector x
    public VectorBD add(VectorBD y)
    {
        return new VectorBD(x.add(y.x), this.y.add(y.y));
    }

    //Subtracts Vector y from Vector x
    public VectorBD sub(VectorBD y)
    {
        return new VectorBD(x.subtract(y.x), this.y.subtract(y.y));
    }

    //Multiplies vector by a scalar
    public VectorBD scalMult(double scalard)
    {
        BigDecimal scalar = new BigDecimal(scalard);
        return new VectorBD(x.multiply(scalar),y.multiply(scalar));
    }

    public VectorBD scalMult(BigDecimal scalar)
    {
        return new VectorBD(x.multiply(scalar),y.multiply(scalar));
    }

    //Converts vector to a string displaying X and Y components, for easier printing
    public String toString()
    {
        return "X:"+x+" Y:"+y;
    }

    //Returns the unit Vector in the same direction as this Vector
    public VectorBD unitVec()
    {
        //Handles case of (0,0) to avoid division by 0
        if(getM().compareTo(BigDecimal.ZERO)>0)
        {
            //System.out.println("uv: "+BigDecimal.valueOf(1).divide(getM(), BigDecimal.ROUND_HALF_EVEN));
            return this.scalMult(1/getM().doubleValue());
        }
        return this.scalMult(0);
    }

    public VectorBD setScale(int scale, int roundingMode)
    {
        VectorBD scaled = this;
        scaled.x=scaled.x.setScale(scale, roundingMode);
        scaled.y=scaled.y.setScale(scale, roundingMode);
        return scaled;
    }

    public VectorBD normToUV(VectorBD uV)
    {
        return uV.scalMult(getM());
    }
}
