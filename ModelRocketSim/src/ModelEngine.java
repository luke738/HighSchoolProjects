/**
 * Name: Luke St. Regis
 * Period: 3
 * Date: 5/12/2015
 * Assignment: Rocket
 * Phone Number: 1-310-560-0942
 * Email: luke7380@gmail.com
 */
public class ModelEngine
{
    public double mass;
    public double fuelMass;
    public final double isp;
    public final VarCurve thrust;

    public ModelEngine(double m, double fuelM, double specImp, VarCurve F_t)
    {
        mass = m;
        fuelMass = fuelM;
        isp = specImp;
        thrust = F_t;
    }

    public ModelEngine copy()
    {
        return new ModelEngine(mass, fuelMass, isp, thrust);
    }
}
