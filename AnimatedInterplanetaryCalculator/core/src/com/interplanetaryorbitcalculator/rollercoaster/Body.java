package com.interplanetaryorbitcalculator.rollercoaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luke on 5/6/2016.
 */

public class Body
{
    public static final double G = 6.67408 * Math.pow(10,-11);
    public static final double sunMass = 1.989 * Math.pow(10,30);
    public static final double earthMass = 5.972 * Math.pow(10,24);
    public static final double AU = 149597870700D;
    public String name;
    public double mass;
    public Orbit orbit;
    public List<Body> satellites = new ArrayList<Body>();
    public Body parentBody;

    public Body(String name, double mass)
    {
        this.name = name;
        this.mass = mass;
    }

    public Body(String name, double mass, Orbit orbit)
    {
        this.name = name;
        this.mass = mass;
        this.orbit = orbit;
    }

    public void addSatellite(Body satellite)
    {
        satellites.add(satellite);
    }

    public Body getSatelliteByName(String name)
    {
        for (int i = 0; i < satellites.size(); i++)
        {
            if (satellites.get(i).name.equalsIgnoreCase(name))
            {
                return satellites.get(i);
            }
        }
        return null;
    }

    public double getSOI()
    {
        //return orbit.getSemiMajorAxis()*Math.pow(mass/);
        return 0;
    }
}
