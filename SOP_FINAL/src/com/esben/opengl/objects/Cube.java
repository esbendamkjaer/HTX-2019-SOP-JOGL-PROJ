package com.esben.opengl.objects;

public class Cube extends Object3D {

	public Cube() {
		super(new Model("/Cube.obj"));
	}

	@Override
	public void update() {
		setRotY(-System.currentTimeMillis()/10%360);
	}
	
}
