import graphicslib3D.Vertex3D;

public class ImportedModel {

	private Vertex3D[] vertices;
	private int numVertices;
	
	public ImportedModel(String filename) {
		ModelImporter modelImporter = new ModelImporter();
		
		try {
			modelImporter.parseOBJ(filename);
			
			numVertices = modelImporter.getNumVertices();
			float[] verts = modelImporter.getVertices();
			float[] tcs = modelImporter.getTextureCoordinates();
			float[] normals = modelImporter.getNormals();
			
			vertices = new Vertex3D[numVertices];
			for (int i = 0; i < vertices.length; i++) {
				vertices[i] = new Vertex3D();
				vertices[i].setLocation(verts[i*3], verts[i*3+1], verts[i*3+2]);
				vertices[i].setST(tcs[i*2], tcs[i*2+1]);
				vertices[i].setNormal(normals[i*3], normals[i*3+1], normals[i*3+2]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public Vertex3D[] getVertices() {
		return vertices;
	}
	
	public int getNumVertices() {
		return numVertices;
	}
	
}
