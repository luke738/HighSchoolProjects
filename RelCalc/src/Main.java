import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Main
{
    public static void main(String[] args)
    {
        BigDecimal mass = new BigDecimal(10);
        BigDecimal c = new BigDecimal("299792458");
        BigDecimal distance = new BigDecimal("600");
        BigDecimal acceleration = new BigDecimal(8.7*Math.pow(10,21));
        BigDecimal chicxulub = new BigDecimal(5*Math.pow(10,23));
        BigDecimal tsar = new BigDecimal(2.092*Math.pow(10,17));
        BigDecimal boy = new BigDecimal(6.3*Math.pow(10,13));
        BigDecimal mk82 = new BigDecimal(1.216*Math.pow(10,9));
        BigDecimal a1030mm = new BigDecimal(2.26118*Math.pow(10,5));
        BigDecimal m16 = new BigDecimal(1.796*Math.pow(10,3));

        BigDecimal velocity = c.multiply(BigDecimalMath.tanh(BigDecimalMath.acosh(acceleration.multiply(distance).divide(c.pow(2), BigDecimal.ROUND_HALF_UP).add(BigDecimal.ONE))));
        System.out.println("Velocity: "+velocity+"m/s (in terms of c: "+velocity.divide(c, BigDecimal.ROUND_HALF_UP)+"c)");

        //BigDecimal recGamma = BigDecimalMath.pow(BigDecimalMath.sqrt(BigDecimal.ONE.subtract(velocity.pow(2).divide(c.pow(2), BigDecimal.ROUND_HALF_UP))), new BigDecimal (-1));
        BigDecimal gamma = BigDecimal.ONE.divide(BigDecimalMath.sqrt(BigDecimal.ONE.subtract(velocity.pow(2).divide(c.pow(2), 100, BigDecimal.ROUND_HALF_UP))), 100, BigDecimal.ROUND_HALF_UP);
        //BigDecimal recGamma = new BigDecimal(10.67988);
        System.out.println("Relativistic gamma: "+gamma);

        BigDecimal energy = gamma.subtract(BigDecimal.ONE).multiply(mass).multiply(c.pow(2)).setScale(100, BigDecimal.ROUND_HALF_UP);
        System.out.println("Kinetic energy: "+energy+"J");

        int scale = BigDecimalMath.log(energy).divide(BigDecimalMath.log(BigDecimal.TEN), BigDecimal.ROUND_HALF_UP).toBigInteger().intValue();
        System.out.println("Order of magnitude: "+scale);
        if (scale>=22)
        {
            System.out.println("Chicxulubs: " + energy.divide(chicxulub, BigDecimal.ROUND_HALF_UP));
        }
        else if (scale>=16)
        {
            System.out.println("Tsar Bombas: " + energy.divide(tsar, BigDecimal.ROUND_HALF_UP));
        }
        else if (scale>=12)
        {
            System.out.println("Hiroshimas: " + energy.divide(boy, BigDecimal.ROUND_HALF_UP));
        }
        else if (scale>=8)
        {
            System.out.println("Mk82 500lbs: " + energy.divide(mk82, BigDecimal.ROUND_HALF_UP));
        }
        else if (scale>=4)
        {
            System.out.println("30mm rounds: " + energy.divide(a1030mm, BigDecimal.ROUND_HALF_UP));
        }
        else
        {
            System.out.println("5.56mm rounds: " + energy.divide(m16, BigDecimal.ROUND_HALF_UP));
        }
        String x = "2 + 2 " + 3 * 4;
        System.out.println(x);
    }
}
