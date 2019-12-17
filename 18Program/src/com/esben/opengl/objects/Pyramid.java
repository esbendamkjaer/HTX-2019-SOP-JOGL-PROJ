package com.esben.opengl.objects;

public class Pyramid extends Object3D {

	public Pyramid() {
		super(new Model("/Plane.obj"));
	}

	@Override
	public void update() {
		
		setRotY(System.currentTimeMillis()/10%360);
		
	}
	
}
