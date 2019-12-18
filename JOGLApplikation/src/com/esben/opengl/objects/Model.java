package com.esben.opengl.objects;
public class Model {

	private float[] vertices;
	private int numVertices;
	
	public Model(String filename) {
		
		try {
			vertices = ModelImporter.parseOBJ(filename);
			numVertices = vertices.length / 3;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Returnerer vertex koordinater
	 */
	public float[] getVertices() {
		
		return vertices;
		
	}
	
	/*
	 * Returnerer antallet af vertices.
	 */
	public int getNumVertices() {
		return numVertices;
	}
	
}
