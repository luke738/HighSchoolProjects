/**
 * A general purpose two-dimensional vector with some methods for manipulation.
 * Encapsulation is not used, but the methods return a new Vector.
 */
package com.interplanetaryorbitcalculator.rollercoaster;

import com.badlogic.gdx.math.Vector3;

public class Vector3D
{
    /**First dimension of the vector*/
    public double x;
    /**Second dimension of the vector*/
    public double y;
    public double z;

    /**Creates a two-dimensional vector
     * @param a Value of first dimension
     * @param b Value of second dimension*/
    public Vector3D(double a, double b, double c)
    {
        x=a;
        y=b;
        z=c;
    }

    public Vector3D(Vector3 gdxVector)
    {
        x=gdxVector.x;
        y=gdxVector.y;
        z=gdxVector.z;
    }

    /**The magnitude of the vector
     * @return A double value for the length of the vector*/
    public double getMag()
    {
        return Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2));
    }

    /**Adds another vector to this Vector
     * @param v2 The second Vector
     * @return A new Vector resulting from the addition*/
    public Vector3D add(Vector3D v2)
    {
        return new Vector3D(x+v2.x,y+v2.y,z+v2.z);
    }

    /**Subtracts another vector from this Vector
     * @param v2 The second Vector
     * @return A new Vector resulting from the subtraction*/
    public Vector3D sub(Vector3D v2)
    {
        return new Vector3D(x-v2.x,y-v2.y,z-v2.z) ;
    }

    /**Multiplies the vector by a scalar
     * @param scalar A scalar value, a double
     * @return A new Vector resulting from the multiplication*/
    public Vector3D scalarMult(double scalar)
    {
        return new Vector3D(x*scalar, y*scalar, z*scalar);
    }

    public Vector3D average(Vector3D v2)
    {
        return new Vector3D((x+v2.x)/2, (y+v2.y)/2, (z+v2.z)/2);
    }

    public Vector3D rotX(double angle)
    {
        Vector3D[] rotMat = {new Vector3D(1,0,0),new Vector3D(0, Math.cos(angle), Math.sin(angle)), new Vector3D(0, -Math.sin(angle), Math.cos(angle))};
        return new Vector3D(rotMat[0].x*x+rotMat[1].x*y+rotMat[2].x*z, rotMat[0].y*x+rotMat[1].y*y+rotMat[2].y*z, rotMat[0].z*x+rotMat[1].z*y+rotMat[2].z*z);
    }

    public Vector3D rotY(double angle)
    {
        Vector3D[] rotMat = {new Vector3D(Math.cos(angle),0,-Math.sin(angle)),new Vector3D(0, 1, 0), new Vector3D(Math.sin(angle), 0, Math.cos(angle))};
        return new Vector3D(rotMat[0].x*x+rotMat[1].x*y+rotMat[2].x*z, rotMat[0].y*x+rotMat[1].y*y+rotMat[2].y*z, rotMat[0].z*x+rotMat[1].z*y+rotMat[2].z*z);
    }

    public Vector3D rotZ(double angle)
    {
        Vector3D[] rotMat = {new Vector3D(Math.cos(angle), Math.sin(angle), 0), new Vector3D(-Math.sin(angle), Math.cos(angle), 0), new Vector3D(0,0,1)};
        return new Vector3D(rotMat[0].x*x+rotMat[1].x*y+rotMat[2].x*z, rotMat[0].y*x+rotMat[1].y*y+rotMat[2].y*z, rotMat[0].z*x+rotMat[1].z*y+rotMat[2].z*z);
    }

    public static Vector3D cross(Vector3D v1, Vector3D v2)
    {
        return new Vector3D(v1.y*v2.z-v1.z*v2.y, v1.z*v2.x-v1.x*v2.z, v1.x*v2.y-v1.y*v2.x);
    }

    public static double dot(Vector3D v1, Vector3D v2)
    {
        return v1.x*v2.x+v1.y*v2.y+v1.z*v2.z;
    }

    public String toString()
    {
        return "X: "+x+"  Y: "+y+"  Z: "+z;
    }
}
