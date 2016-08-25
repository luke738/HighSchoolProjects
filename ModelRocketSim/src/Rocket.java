/**
 * Created by Luke on 5/11/2015.
 */
public class Rocket
{
    //Ident
    public String ident;/**Name of the rocket*/
    int[] color;/**An RGB color for visual identification*/
    //Kinematics
    public double mass;/**Dry mass in kilograms*/
    public double area;/**Frontal area in square meters*/
    public double cD;/**Coefficient of drag*/
    public Vector location;/**x-y position of the body in the simulation, origin at top left*/
    public Vector velocity;/**Velocity of the body*/
    public Vector orientation;
    //Rocket variables
    public ModelEngine engine;
    //Integration variables
    public Vector accelOld;/**Acceleration 1 timestep in the past*/
    public Vector accel2Old;/**Acceleration 2 timesteps in the past*/
    public Vector accel3Old;/**Acceleration 3 timesteps in the past*/
    public Vector velOld;/**Velocity 1 timestep in the past*/
    public Vector vel2Old;/**Velocity 2 timesteps in the past*/
    public Vector vel3Old;/**Velocity 3 timesteps in the past*/
    //Housekeeping
    public Boolean doRemove = false;

    public Rocket(String id, int[] col, double m, double A, double coeDrag, Vector loc, Vector vel, Vector orient, ModelEngine eng)
    {
        ident=id;
        color = col;
        mass=m;
        area=A;
        cD=coeDrag;
        location = loc;
        velocity = vel;
        orientation=orient;
        engine = eng;
    }

    public Vector acceleration()
    {
        Vector acc = thrust().add(SystemSimulator.g_earth).add(drag());
        //System.out.println(ident+" "+acc.toString());
        return acc;
    }

    public Vector thrust()
    {
        double massRate = engine.thrust.getYofX(SystemSimulator.getTime())/(engine.isp*SystemSimulator.g_earth.getMag());
        //System.out.println(thrustCurve.getYofX(SystemSimulator.getTime()));
        if(engine.fuelMass>0)
        {
            engine.mass -= massRate * SystemSimulator.timeCompression;
            engine.fuelMass -= massRate * SystemSimulator.timeCompression;
            //System.out.println(engine.mass+mass);

            double mag = engine.thrust.getYofX(SystemSimulator.getTime()) / (mass+engine.mass);

            if(engine.fuelMass>0)
            {
                return new Vector(orientation.scalarMult(mag).x,-orientation.scalarMult(mag).y);
            }
            else
            {
                return new Vector(orientation.scalarMult(mag).x,-orientation.scalarMult(mag).y).scalarMult((engine.fuelMass+massRate * SystemSimulator.timeCompression)/(massRate * SystemSimulator.timeCompression));
            }
        }
        return new Vector(0,0);
    }

    public Vector drag()
    {
        double mag = 0.5*SystemSimulator.rho*cD*area*Math.pow(velocity.getMag(),2);
        System.out.println(ident+" "+mag);
        Vector drag = new Vector(velocity.normalize().scalarMult(mag).x,-velocity.normalize().scalarMult(mag).y).scalarMult((mass+engine.mass));
        System.out.println(drag.getMag());
        return drag;
    }

    public void collisionChk()
    {
        if(SystemAnimator.B_HEIGHT<location.y)
        {
            location.y=SystemAnimator.B_HEIGHT;
            velocity.y=0;
            //System.out.println("Rocket "+ident+" made it!");
        }
        if(SystemAnimator.B_HEIGHT-location.y>225 && Math.signum(velocity.y)>Math.signum(velOld.y))
        {
            System.out.println(ident + " made it "+(SystemAnimator.B_HEIGHT-location.y)+"meters");
        }
    }
}
