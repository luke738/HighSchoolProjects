package com.interplanetaryorbitcalculator.rollercoaster.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.interplanetaryorbitcalculator.rollercoaster.MapView;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title="Interplanetary Orbit Calculator";
		config.height=540;
		config.width=1920/2;
		new LwjglApplication(new MapView(), config);
	}
}
