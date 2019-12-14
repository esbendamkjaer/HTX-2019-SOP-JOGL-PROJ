public class ImportedModel {

	private float[] vertices;
	private int numVertices;
	
	public ImportedModel(String filename) {
		ModelImporter modelImporter = new ModelImporter();
		
		try {
			modelImporter.parseOBJ(filename);
			
			numVertices = modelImporter.getNumVertices();
			vertices = modelImporter.getVertices();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public float[] getVertices() {
		
		return vertices;
		
	}
	
	public int getNumVertices() {
		return numVertices;
	}
	
}
