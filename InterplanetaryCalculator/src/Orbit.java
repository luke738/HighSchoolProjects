/**
 * Created by Luke on 5/4/2016.
 */
public class Orbit
{
    public double pe;
    public double ap;
    public double inclination;
    public double longitude;
    public double argument;
    public double periEpoch;
    public double gParameter;

    public Orbit(double pe, double ap, double inclination, double longitude, double argument, double periEpoch, double gParameter)
    {
        this.pe=pe;
        this.ap=ap;
        this.inclination=inclination;
        this.longitude=longitude;
        this.argument=argument;
        this.periEpoch=periEpoch;
        this.gParameter=gParameter;
    }

    public double getSemiMajorAxis()
    {
        return (pe+ap)/2;
    }

    public double getEccentricity()
    {
        return (ap-pe)/(2*getSemiMajorAxis());
    }

    public double getPeriod()
    {
        return 2*Math.PI*Math.sqrt(Math.pow(getSemiMajorAxis(),3)/gParameter);
    }

    public double getMeanAnomaly(double time)
    {
        return ((2*Math.PI)/getPeriod())*(time-periEpoch);
    }

    public double getEccentricAnomaly(double time)
    {
        double guess = getMeanAnomaly(time);
        for (int i = 0; i < 100; i++)
        {
            guess=guess-(guess-getEccentricity()*Math.sin(guess)-getMeanAnomaly(time))/(1-getEccentricity()*Math.cos(guess));
        }
        return guess;
    }

    public double getTrueAnomaly(double time)
    {
        return Math.PI-2*Math.atan2(Math.sqrt(1-getEccentricity())*Math.cos(getEccentricAnomaly(time) / 2),Math.sqrt(1+getEccentricity())*Math.sin(getEccentricAnomaly(time) / 2));
    }

    public Vector3D getPosition(double time)
    {
        Vector position = new Vector(0,0);
        double distance = getSemiMajorAxis()*(1-Math.pow(getEccentricity(),2))/(1+getEccentricity()*Math.cos(getTrueAnomaly(time)));
        position.x=distance*Math.cos(getTrueAnomaly(time));
        position.y=distance*Math.sin(getTrueAnomaly(time));

        Vector3D correctedPosition = new Vector3D(position.x, position.y, 0);
        correctedPosition = correctedPosition.rotY(inclination);
        correctedPosition = correctedPosition.rotZ(longitude);
        correctedPosition = correctedPosition.rotX(argument);

        return correctedPosition;
    }

    //velocity = meanMotion*semiMajorAxis/sqrt(1-eccentricity^2)*(-sin(trueAnomaly), eccentricity+cos(trueAnomaly), 0)
    //rotate to match position
    public Vector3D getVelocity(double time)
    {
        double coefficient = ((2*Math.PI)/getPeriod())*getSemiMajorAxis()/Math.sqrt(1-Math.pow(getEccentricity(), 2));
        Vector3D velocity = new Vector3D(-Math.sin(getTrueAnomaly(time)), (getEccentricity()+Math.cos(getTrueAnomaly(time))), 0).scalarMult(coefficient);

        Vector3D correctedVelocity = new Vector3D(velocity.x, velocity.y, 0);
        correctedVelocity = correctedVelocity.rotY(inclination);
        correctedVelocity = correctedVelocity.rotZ(longitude);
        correctedVelocity = correctedVelocity.rotX(argument);

        return correctedVelocity;
    }

    public Vector3D getAcceleration(double time)
    {
        double alpha = -Math.pow(((2*Math.PI)/getPeriod()),2)*Math.pow(getSemiMajorAxis(), 3)/Math.pow(getPosition(time).getMag(), 2);
        return getPosition(time).scalarMult(1.0/getPosition(time).getMag()).scalarMult(alpha);
    }

    public Orbit perturbOrbit(double time, Vector3D dV)
    {
        Vector3D position = getPosition(time);
        Vector3D velocity = getVelocity(time).add(dV);
        //System.out.println(position);
        //System.out.println(velocity);

        Vector3D momentum = Vector3D.cross(position, velocity);
        //System.out.println(momentum);
        Vector3D eccentricity = Vector3D.cross(velocity,momentum).scalarMult(1.0/gParameter).sub(position.scalarMult(1.0/position.getMag()));
        //System.out.println(eccentricity);
        Vector3D nodeVector = new Vector3D(-momentum.y, momentum.x, 0);
        //System.out.println(nodeVector);

        double inclination = Math.acos(momentum.z/momentum.getMag());
        double ecc = eccentricity.getMag();

        double trueAnomaly;
        if(ecc>0.002) {
            if (Vector3D.dot(position, velocity) >= 0)
            {
                trueAnomaly = Math.acos(Vector3D.dot(eccentricity, position) / (position.getMag() * eccentricity.getMag()));
            }
            else
            {
                trueAnomaly = 2 * Math.PI - Math.acos(Vector3D.dot(eccentricity, position) / (position.getMag() * eccentricity.getMag()));
            }
        }
        else if(inclination!=0)
        {
            if(Vector3D.dot(nodeVector, velocity)<=0)
            {
                trueAnomaly = Math.acos(Vector3D.dot(nodeVector, position)/(position.getMag()*nodeVector.getMag()));
            }
            else
            {
                trueAnomaly = 2*Math.PI-Math.acos(Vector3D.dot(nodeVector, position)/(position.getMag()*nodeVector.getMag()));
            }
        }
        else
        {
            if(velocity.x<=0)
            {
                trueAnomaly = Math.acos(position.x/position.getMag());
            }
            else
            {
                trueAnomaly = 2*Math.PI-Math.acos(position.x/position.getMag());
            }
        }
        //System.out.println(trueAnomaly);

        //System.out.println(inclination);
        //System.out.println(ecc);

        double eccentricAnomaly = 2*Math.atan2(Math.tan(trueAnomaly/2),Math.sqrt((1+ecc)/(1-ecc)));
        //System.out.println(eccentricAnomaly);

        double longitude;
        double argument;
        if (Math.abs(nodeVector.getMag())>0)
        {
            if (nodeVector.y >= 0) {
                longitude = Math.acos(nodeVector.x / nodeVector.getMag());
            } else {
                longitude = 2 * Math.PI - Math.acos(nodeVector.x / nodeVector.getMag());
            }
            //System.out.println(longitude);

            if (eccentricity.z >= 0) {
                argument = Math.acos(Vector3D.dot(nodeVector, eccentricity) / (nodeVector.getMag() * eccentricity.getMag()));
            } else {
                argument = 2 * Math.PI - Math.acos(Vector3D.dot(nodeVector, eccentricity) / (nodeVector.getMag() * eccentricity.getMag()));
            }
            //System.out.println(argument);
        }
        else
        {
            longitude = 0;
            argument = 0;
        }

        double meanAnomaly = eccentricAnomaly - ecc*Math.sin(eccentricAnomaly);
        double periEpoch = -meanAnomaly/(2*Math.PI/getPeriod())+time;
        //System.out.println(meanAnomaly);
        //System.out.println(periEpoch);

        double semiMajorAxis = 1/((2/position.getMag())-(Math.pow(velocity.getMag(),2)/gParameter));
        double focalDistance = semiMajorAxis*ecc;
        double periapsis = semiMajorAxis-focalDistance;
        double apoapsis = semiMajorAxis+focalDistance;
        //System.out.println(semiMajorAxis);
        //System.out.println(focalDistance);
        //System.out.println(periapsis);
        //System.out.println(apoapsis);

        return new Orbit(periapsis, apoapsis, inclination, longitude, argument, periEpoch, gParameter);
    }

    public String toString()
    {
        String elements = "Periapsis: "+pe+
                "\nApoapsis: "+ap+
                "\nInclination: "+Math.toDegrees(inclination)+
                "\nLongitude: "+Math.toDegrees(longitude)+
                "\nArgument: "+Math.toDegrees(argument)+
                "\nEpoch: "+periEpoch+
                "\nGravitational Parameter: "+gParameter;
        return elements;
    }
}
