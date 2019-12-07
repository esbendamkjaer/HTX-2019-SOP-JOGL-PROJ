import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ModelImporter {

	private ArrayList<Float> vertVals = new ArrayList<Float>();
	private ArrayList<Float> stVals = new ArrayList<Float>();
	private ArrayList<Float> normVals = new ArrayList<Float>();
	
	private ArrayList<Float> triangleVerts = new ArrayList<Float>();
	private ArrayList<Float> textureCoords = new ArrayList<Float>();
	private ArrayList<Float> normals = new ArrayList<Float>();

	public void parseOBJ(String filename) throws IOException {
<<<<<<< ours
		BufferedReader br = new BufferedReader(new InputStreamReader(ModelImporter.class.getResourceAsStream(filename)));
=======
		BufferedReader br = new BufferedReader(new InputStreamReader(ModelImporter.class.getResourceAsStream("Pyramid.obj")));
>>>>>>> theirs
		
		String line;
		while ((line = br.readLine()) != null) {
			
			if (line.startsWith("v ")) {
				
				for (String s : (line.substring(2)).split(" ")) {
					vertVals.add(Float.valueOf(s));
				}
				
			} else if (line.startsWith("vt")) {
				for (String s : (line.substring(3)).split(" ")) {
					stVals.add(Float.valueOf(s));
				}
			} else if (line.startsWith("vn")) {
				
				for (String s : (line.substring(3)).split(" ")) {
					normVals.add(Float.valueOf(s));
				}
				
			} else if (line.startsWith("f")) {
				
				for (String s : (line.substring(2).split(" "))) {
					
					String v = s.split("/")[0];
					String vt = s.split("/")[1];
					String vn = s.split("/")[2];
					
					int vertRef = (Integer.valueOf(v)-1)*3;
					int tcRef = (Integer.valueOf(vt)-1)*2;
					int normRef = (Integer.valueOf(vn)-1)*3;
					
					triangleVerts.add(vertVals.get(vertRef));
					triangleVerts.add(vertVals.get(vertRef+1));
					triangleVerts.add(vertVals.get(vertRef+2));
					
				}
				
			}
			
		}
		br.close();
		
	}
	
	public int getNumVertices() {
		
		return triangleVerts.size() / 3;
		
	}
	
	public float[] getVertices() {
		
		float[] p = new float[triangleVerts.size()];
		for (int i = 0; i < triangleVerts.size(); i++) {
			p[i] = triangleVerts.get(i);
		}
		return p;
		
	}
	
}
