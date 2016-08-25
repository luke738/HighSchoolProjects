package com.interplanetaryorbitcalculator.rollercoaster;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class MapView implements ApplicationListener
{
	public ShapeRenderer renderer;
    public PerspectiveCamera camera;
    public Model earthModel;
    public ModelInstance earthInstance;
    public Model rocketModel;
    public ModelInstance rocketInstance;
    public Matrix4 rocketInitTransform;
    public ModelBatch modelBatch;
    public Environment environment;
    public CameraInputController cameraInputController;
    public double ellX;
    public double ellY;
    public double ellZ;
    public Body rocket;
    public float sF = 1000000;
    public double time=-600;
	
	@Override
	public void create () {
		renderer = new ShapeRenderer();
        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0*6371000f*2/sF,2*6371000f*2/sF,0*6371000f*2/sF);
        camera.lookAt(0,0,0);
        camera.near = 1;
        camera.far = 6371000f*4/sF*100;
        camera.update();

        cameraInputController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(cameraInputController);

        ModelBuilder modelBuilder = new ModelBuilder();
        //model = modelBuilder.createBox(6.371f,6.371f,6.371f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        earthModel = modelBuilder.createSphere(6371000f/sF,6371000f/sF,6371000f/sF,100,100, new Material(ColorAttribute.createDiffuse(Color.GREEN)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        rocketModel = modelBuilder.createSphere(1,1,1,50,50, new Material(ColorAttribute.createDiffuse(Color.BLUE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        earthInstance = new ModelInstance(earthModel);
        //earthInstance.transform.translate((float)2.5,(float)2.5,(float)2.5);
        System.out.println(earthInstance.transform);
        rocketInstance = new ModelInstance(rocketModel);
        rocketInitTransform = rocketInstance.transform;

        rocket = new Body("rocket", 1000, new Orbit(7371000,1000000+6371000, Math.toRadians(0), 0, 0, 0, Body.G* Body.earthMass));
        //t=0 defined to be 01/15/2007, 12:30AM
        rocket.parentBody = new Body("earth", Body.earthMass, new Orbit(0.98329* Body.AU, 1.0167* Body.AU, Math.toRadians(0.00005), Math.toRadians(-11.26061), Math.toRadians(102.94717), 0, Body.G* Body.sunMass));
        rocket.parentBody.addSatellite(rocket);
        rocket.parentBody.parentBody = new Body("sun", Body.sunMass);
        rocket.parentBody.parentBody.addSatellite(rocket.parentBody);
        //Mars reached periapsis at 06/01/2007, 7:20AM
        rocket.parentBody.parentBody.addSatellite(new Body("mars", 6.39*Math.pow(10, 23), new Orbit(1.3814* Body.AU, 1.666* Body.AU, Math.toRadians(1.85061), Math.toRadians(49.57854), Math.toRadians(336.04084), 11861400, Body.G* Body.sunMass)));
        rocket.parentBody.parentBody.getSatelliteByName("mars").parentBody=rocket.parentBody.parentBody;
        System.out.println(rocket.orbit);
	}

	@Override
	public void render () {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Gdx.graphics.setVSync(true);

        //Vector3 argAxis = new Vector3(0,0,1).rotateRad((float)rocket.orbit.inclination, 0,1,0).rotateRad((float)rocket.orbit.longitude, 0,0,1);
        Vector3 position = rocket.orbit.getPosition(time);//.rotate(argAxis, 180);
        rocketInstance.transform = rocketInstance.transform.setTranslation(position.scl(1f/sF).rotate(90, 1,0,0));

        if (time==0)
        {
            System.out.println(rocket.orbit.getVelocity(time));
            System.out.println(rocket.orbit.getPosition(time));
            rocket.orbit = rocket.orbit.perturbOrbit(time, new Vector3(0,100,0));
            System.out.println(rocket.orbit.getVelocity(time));
            System.out.println(rocket.orbit.getPosition(time));
            System.out.println(rocket.orbit);
        }

        if (time%600==0)
        {
            System.out.println(rocket.orbit.getPosition(time));
        }

        time+=10;

        cameraInputController.update();
        renderer.setProjectionMatrix(camera.combined);

        //System.out.println(rocketInstance.transform);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.line(-1,0,0,10,0,0);
        renderer.line(0,-1,0,0,10,0);
        renderer.line(0,0,-1,0,0,10);
        float[] par = rocket.orbit.parameterize();
        Vector3 center = new Vector3(par[2]/sF,par[3]/sF,par[4]/sF).rotate(90, 1,0,0);
        Vector3 semiMajorAxis = new Vector3(par[5],par[6],par[7]).rotate(90, 1,0,0);
        Vector3 semiMinorAxis = new Vector3(par[8],par[9],par[10]).rotate(90, 1,0,0);
        float sMaA = par[0]/sF;
        float sMiA = par[1]/sF;
        for (int t = 0; t <= 100; t++)
        {
            double i = ((double)t/100)*(Math.PI*2);
            double x = center.x + sMaA * Math.cos(i) * semiMajorAxis.x + sMiA * Math.sin(i) * semiMinorAxis.x;
            double y = center.y + sMaA * Math.cos(i) * semiMajorAxis.y + sMiA * Math.sin(i) * semiMinorAxis.y;
            double z = center.z + sMaA * Math.cos(i) * semiMajorAxis.z + sMiA * Math.sin(i) * semiMinorAxis.z;
            if (i==0)
            {
                renderer.line((float)x,(float)y,(float)z,(float)x,(float)y,(float)z);
            }
            else
            {
                renderer.line((float)ellX,(float)ellY,(float)ellZ,(float)x,(float)y,(float)z);
            }
            ellX=x;
            ellY=y;
            ellZ=z;

        }
        renderer.end();

        modelBatch.begin(camera);
        modelBatch.render(earthInstance, environment);
        modelBatch.render(rocketInstance, environment);
        modelBatch.end();
	}

    public void dispose()
    {
        earthModel.dispose();
        rocketModel.dispose();
        modelBatch.dispose();
    }

    public void resume()
    {

    }

    public void pause()
    {

    }

    public void resize(int x, int y)
    {

    }
}
