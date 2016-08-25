import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;

/**
 * Name: Luke St. Regis
 * Period: 1
 * Date: 10/13/2014
 * Assignment:
 * Phone Number: 1-310-560-0942
 * Email: luke7380@gmail.com
 */

//A class for doing math with BigDecimals
public class Util
{
    public static final BigDecimal PI = new BigDecimal(3.1415926535897932384626433832795);

    public static BigDecimal sqrt(BigDecimal input)
    {
        if(input.doubleValue()>0)
        {
            input = input.setScale(30, BigDecimal.ROUND_FLOOR);
            BigDecimal current = BigDecimal.valueOf(Math.sqrt(input.doubleValue()));
            //System.out.println("current = " + current);
            for (int i = 0; i < 50; i++)
            {
                current = current.subtract(current.pow(2).subtract(input).divide(current.multiply(BigDecimal.valueOf(2.0)), BigDecimal.ROUND_FLOOR));
                current = current.setScale(30, BigDecimal.ROUND_FLOOR);
                return current;
            }
        }
        return BigDecimal.ZERO;
        //current=current.setScale(20, BigDecimal.ROUND_FLOOR);
        //System.out.println(current.precision());
        //System.out.println(current.scale());
    }
    /*public static BigDecimal f5thrt(BigDecimal input)
    {
        input=input.setScale(30, BigDecimal.ROUND_FLOOR);
        BigDecimal current = BigDecimal.ONE;
        BigDecimalMath.exp(input);
        for (int i = 0; i < 100; i++)
        {
            current = current.subtract(current.pow(2).subtract(input).divide(current.multiply(BigDecimal.valueOf(2)), BigDecimal.ROUND_HALF_UP));
            current=current.setScale(30, BigDecimal.ROUND_FLOOR);
        }
    }*/

    public static BigDecimal atan2(BigDecimal x, BigDecimal y)
    {
        BigDecimal yOx = y.divide(x, BigDecimal.ROUND_HALF_UP);
        BigDecimal result = new BigDecimal(0);
        if(x.compareTo(BigDecimal.ZERO)>0)
        {
            result = atan(yOx);
        }
        else if(x.compareTo(BigDecimal.ZERO)<0&&y.compareTo(BigDecimal.ZERO)>=0)
        {
            result = atan(yOx).add(PI);
        }
        else if(x.compareTo(BigDecimal.ZERO)<0&&y.compareTo(BigDecimal.ZERO)<0)
        {
            result = atan(yOx).subtract(PI);
        }
        else if(x.compareTo(BigDecimal.ZERO)==0&&y.compareTo(BigDecimal.ZERO)>0)
        {
            result = PI.divide(BigDecimal.ONE.add(BigDecimal.ONE));
        }
        else if(x.compareTo(BigDecimal.ZERO)==0&&y.compareTo(BigDecimal.ZERO)>0)
        {
            result = PI.divide(BigDecimal.ONE.add(BigDecimal.ONE)).negate();
        }
        else if(x.compareTo(BigDecimal.ZERO)==0&&y.compareTo(BigDecimal.ZERO)==0)
        {
            result = null;
        }

        return result;
    }

    public static BigDecimal atan(BigDecimal yOx)
    {
        BigDecimal result = new BigDecimal(0);
        yOx=yOx.setScale(30,BigDecimal.ROUND_FLOOR);
        for (int i = 0; i < 100; i++)
        {
            BigDecimal i2plus1 = new BigDecimal(2*i+1);
            result=result.add(BigDecimal.ONE.negate().pow(i).multiply(yOx.pow(2*i+1)).divide(i2plus1, BigDecimal.ROUND_HALF_UP));
            result=result.setScale(30,BigDecimal.ROUND_FLOOR);
        }

        return result;
    }

    public static BigDecimal toDegrees(BigDecimal angrad)
    {
        return angrad.multiply(new BigDecimal(180)).divide(PI, BigDecimal.ROUND_HALF_UP);
    }
}
