package com.esben.opengl.objects;

public class Pyramid extends Object3D {

	public Pyramid() {
		super(new Model("/Pyramid.obj"));
	}

	@Override
	public void update() {
		
		setRotY(System.currentTimeMillis()%36000/10);
		
	}
	
}
