import org.jzy3d.plot3d.builder.Mapper;

public class Main
{
    public static void main(String[] args)
    {
        Body rocket = new Body("rocket", 1000, new Orbit(7371000,7371000, 0, 0, 0, 0, Body.G*Body.earthMass));
        //t=0 defined to be 01/15/2007, 12:30AM
        rocket.parentBody = new Body("earth", Body.earthMass, new Orbit(0.98329*Body.AU, 1.0167*Body.AU, Math.toRadians(0.00005), Math.toRadians(-11.26061), Math.toRadians(102.94717), 0, Body.G*Body.sunMass));
        rocket.parentBody.addSatellite(rocket);
        rocket.parentBody.parentBody = new Body("sun", Body.sunMass);
        rocket.parentBody.parentBody.addSatellite(rocket.parentBody);
        //Mars reached periapsis at 06/01/2007, 7:20AM
        rocket.parentBody.parentBody.addSatellite(new Body("mars", 6.39*Math.pow(10, 23), new Orbit(1.3814*Body.AU, 1.666*Body.AU, Math.toRadians(1.85061), Math.toRadians(49.57854), Math.toRadians(336.04084), 11861400, Body.G*Body.sunMass)));
        rocket.parentBody.parentBody.getSatelliteByName("mars").parentBody=rocket.parentBody.parentBody;
        System.out.println(rocket.parentBody.orbit);
        //printAbsoluteOrbitPoints(rocket);
    }

    public static void printOrbitPoints(Orbit orbit)
    {
        for (int i = 0; i < 7; i++)
        {
            System.out.println(orbit.getVelocity(orbit.getPeriod()/6*i));
        }
    }
}
