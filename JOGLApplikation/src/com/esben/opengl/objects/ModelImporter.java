package com.esben.opengl.objects;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ModelImporter {
	
	/*
	 * Importere og fortolker Wavefront fil
	 */
	public static float[] parseOBJ(String filename) throws IOException {
		ArrayList<Float> triangleVerts = new ArrayList<Float>();
		ArrayList<Float> vertCoords = new ArrayList<Float>();
		BufferedReader br = new BufferedReader(new InputStreamReader(ModelImporter.class.getResourceAsStream(filename)));
		
		String line;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("v ")) {
				for (String s : (line.substring(2)).split(" ")) {
					vertCoords.add(Float.valueOf(s));
				}
			} else if (line.startsWith("f ")) {
				
				for (String s : (line.substring(2).split(" "))) {
					// Wavefront-index 0-indekseres og multipliceres med 3, da vertexdataene nu lagres sekventielt i vertCoords
					int vertRef = (Integer.valueOf(s)-1)*3;
					
					triangleVerts.add(vertCoords.get(vertRef));
					triangleVerts.add(vertCoords.get(vertRef+1));
					triangleVerts.add(vertCoords.get(vertRef+2));	
				}
			}
		}
		br.close();
		return toFloatArray(triangleVerts);
	}
	
	/*
	 * Returnere float array tilsvarende en givet float ArrayList
	 */
	public static float[] toFloatArray(ArrayList<Float> triangleVerts) {
		
		float[] p = new float[triangleVerts.size()];
		for (int i = 0; i < triangleVerts.size(); i++) {
			p[i] = triangleVerts.get(i);
		}
		return p;
		
	}
	
}
