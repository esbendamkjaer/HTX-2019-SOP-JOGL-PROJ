import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import com.esben.opengl.Renderer;
import com.esben.opengl.objects.ModelImporter;

class Test {

	public static void main(String[] args) {
		
		Test t = new Test();
		t.test();
		
	}
	
	@org.junit.jupiter.api.Test
	void test() {
		float[] d = {1,2,3};
		assertArrayEquals(d, new float[]{1, 2, 3});
		
		try {
			float[] verts = ModelImporter.parseOBJ("/Pyramid.obj");
			for (float f : verts) {
				
				System.out.print(f + ": ");
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
