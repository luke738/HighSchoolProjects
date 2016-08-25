/**
 * Created by Luke on 5/4/2016.
 */
package com.interplanetaryorbitcalculator.rollercoaster;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.math.BigDecimal;

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

    public Vector3 getPosition(double time)
    {
        Vector position = new Vector(0,0);
        double distance = getSemiMajorAxis()*(1-Math.pow(getEccentricity(),2))/(1+getEccentricity()*Math.cos(getTrueAnomaly(time)));
        position.x=distance*Math.cos(getTrueAnomaly(time));
        position.y=distance*Math.sin(getTrueAnomaly(time));

        Vector3 correctedPosition = new Vector3((float)position.x, (float)position.y, 0);
        correctedPosition = correctedPosition.rotateRad((float)inclination, 0,1,0);
        correctedPosition = correctedPosition.rotateRad((float)longitude, 0,0,1);
        Vector3 argAxis = new Vector3(0,0,1).rotateRad((float)inclination, 0,1,0).rotateRad((float)longitude, 0,0,1);
        correctedPosition = correctedPosition.rotateRad(argAxis, (float)-argument);
        correctedPosition = correctedPosition.rotate(argAxis, 180);
        return correctedPosition;
    }

    //velocity = meanMotion*semiMajorAxis/sqrt(1-eccentricity^2)*(-sin(trueAnomaly), eccentricity+cos(trueAnomaly), 0)
    //rotate to match position
    public Vector3 getVelocity(double time)
    {
        double coefficient = ((2*Math.PI)/getPeriod())*getSemiMajorAxis()/Math.sqrt(1-Math.pow(getEccentricity(), 2));
        Vector3D velocity = new Vector3D(-Math.sin(getTrueAnomaly(time)), (getEccentricity()+Math.cos(getTrueAnomaly(time))), 0).scalarMult(coefficient);

        Vector3 correctedVelocity = new Vector3((float)velocity.x, (float)velocity.y, 0);
        correctedVelocity = correctedVelocity.rotateRad((float)inclination, 0,1,0);
        correctedVelocity = correctedVelocity.rotateRad((float)longitude, 0,0,1);
        Vector3 argAxis = new Vector3(0,0,1).rotateRad((float)inclination, 0,1,0).rotateRad((float)longitude, 0,0,1);
        correctedVelocity = correctedVelocity.rotateRad(argAxis, (float)argument);
        correctedVelocity = correctedVelocity.rotate(argAxis, 180);
        correctedVelocity.y = -correctedVelocity.y;
        return correctedVelocity;
    }

    public Vector3 getAcceleration(double time)
    {
        double alpha = -Math.pow(((2*Math.PI)/getPeriod()),2)*Math.pow(getSemiMajorAxis(), 3)/Math.pow(getPosition(time).len(), 2);
        return getPosition(time).scl(1.0f/getPosition(time).len()).scl((float)alpha);
    }

    public Orbit perturbOrbit(double time, Vector3 dV)
    {
        Vector3 r = getPosition(time);
        Vector3 v = getVelocity(time).add(dV);
        System.out.println(r.len());
        System.out.println(v.len());
        System.out.println(v.len2());

        Vector3 h = r.crs(v);
        Vector3 n = Vector3.Z.crs(h);

        Vector3 copyV = v.cpy();
        Vector3 copyR = r.cpy();
        Vector3 rDotvV = copyV.scl(copyR.dot(copyV));
        Vector3 v2gPrR = copyR.scl((float)(v.len2()-gParameter/r.len()));
        double rgP = 1d/gParameter;
        Vector3 e = v2gPrR.sub(rDotvV).scl((float)rgP);

        double rlen = r.len();
        System.out.println(rlen);
        double rd2 = 2d/rlen;
        System.out.println(rd2);
        double v2 = v.len2();
        System.out.println("v"+v2);
        System.out.println("gP"+gParameter);
        double rgv2 = v2/gParameter;
        System.out.println(rgv2);
        double ra = rd2-rgv2;
        System.out.println(ra);
        double a = 1/ra;
        System.out.println(a);
        double ecc = e.len();
        System.out.println(ecc);

        double i = Math.acos(h.z/h.len());

        double o = Math.acos(n.x/n.len());
        if (o>=Math.PI && n.y>0)
        {
            o = 2*Math.PI-o;
        }

        double arg = Math.acos(n.dot(e)/(n.len()*e.len()));
        if (arg>=Math.PI && e.z>0)
        {
            arg = 2*Math.PI-arg;
        }

        double ta = Math.acos(e.dot(r)/(e.len()*r.len()));
        if (ta>=Math.PI && r.dot(v)>0)
        {
            ta = 2*Math.PI-ta;
        }

        double ea = Math.atan2(Math.sqrt(1-Math.pow(ecc, 2))*Math.sin(ta), ecc+Math.cos(ta));

        double ma = ea-ecc*Math.sin(ea);

        double per = 2*Math.PI*Math.sqrt(Math.pow(a, 3)/gParameter);
        double mam = 2*Math.PI/per;

        double t = time - ma/mam;

        double pe = (1-ecc)*a;
        double ap = (1+ecc)*a;


        return new Orbit(pe, ap, i, o, arg, t, gParameter);
    }

    public float[] parameterize()
    {
        float[] parameters = new float[11];
        parameters[0]=(float)getSemiMajorAxis();
        parameters[1]=(float)(getSemiMajorAxis()*Math.sqrt(1-Math.pow(getEccentricity(),2)));

        double focal = getEccentricity()*getSemiMajorAxis();
        Vector3 center = new Vector3((float)focal, 0, 0);
        center=center.rotateRad((float)inclination, 0,1,0);
        center=center.rotateRad((float)longitude, 0,0,1);
        Vector3 argAxis = new Vector3(0,0,1).rotateRad((float)inclination, 0,1,0).rotateRad((float)longitude, 0,0,1);
        center = center.rotateRad(argAxis, (float)-argument);
        parameters[2]=center.x;
        parameters[3]=center.y;
        parameters[4]=center.z;

        Vector3 sMaA = new Vector3(1,0,0).rotateRad((float)inclination, 0,1,0).rotateRad((float)longitude, 0,0,1).rotateRad(argAxis, (float)-argument);
        Vector3 sMiA = new Vector3(0,1,0).rotateRad((float)inclination, 0,1,0).rotateRad((float)longitude, 0,0,1).rotateRad(argAxis, (float)-argument);
        parameters[5]=sMaA.x;
        parameters[6]=sMaA.y;
        parameters[7]=sMaA.z;
        parameters[8]=sMiA.x;
        parameters[9]=sMiA.y;
        parameters[10]=sMiA.z;

        return parameters;
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
