import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import graphicslib3D.Matrix3D;

public class Code extends JFrame implements GLEventListener {
	private static final long serialVersionUID = 3746059804968448472L;
	
	private GLCanvas myCanvas;
	
	private int rendering_program;
	
	private int vao[] = new int[1];
	private int vbo[] = new int[1];
	
	private float cameraX, cameraY, cameraZ;
	private float locX, locY, locZ;
	private Matrix3D pMat;
	
	private ImportedModel myObj = new ImportedModel("Plane.obj");
	
	public Code() {
		setSize(800, 800);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		
		setVisible(true);
		
		FPSAnimator animtr = new FPSAnimator(myCanvas, 50);
		animtr.start();
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glClear(GL4.GL_DEPTH_BUFFER_BIT);
		
		float bkg[] = {0.0f, 0.0f, 0.0f, 1.0f};
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL4.GL_COLOR, 0, bkgBuffer);
		
		Matrix3D vMat = new Matrix3D();
		vMat.translate(-cameraX, -cameraY, -cameraZ);
		
		Matrix3D mMat = new Matrix3D();
		mMat.translate(locX, locY, locZ);
		mMat.rotateY(System.currentTimeMillis()/10);
		
		Matrix3D mvpMat = new Matrix3D();
		mvpMat.concatenate(pMat);
		mvpMat.concatenate(vMat);
		mvpMat.concatenate(mMat);
		
		int mvp_loc = gl.glGetUniformLocation(rendering_program, "mvp_matrix");
		
		gl.glUniformMatrix4fv(mvp_loc, 1, false, mvpMat.getFloatValues(), 0);
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		int numVerts = myObj.getNumVertices();
		
		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, numVerts);
	}

	public static void main(String[] args) {
		new Code();
	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		rendering_program = createShaderProgram();
		gl.glUseProgram(rendering_program);
		
		gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_LINE);

		gl.glEnable(GL4.GL_CULL_FACE);
		gl.glFrontFace(GL4.GL_CW);
		
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		
		setupVertices();
		cameraX = 0.0f; cameraY = 0.0f; cameraZ = 3.0f;
		locX = 0.0f; locY = 0.0f; locZ = 0.0f;
		
		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);
	}

	private void setupVertices() {
		
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		
		float[] vertValues = myObj.getVertices();
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertDataBuffer = Buffers.newDirectFloatBuffer(vertValues);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertDataBuffer.limit()*4, vertDataBuffer, GL4.GL_STATIC_DRAW);
		
	}
	
	private Matrix3D perspective(float fovy, float aspect, float n, float f) {
		
		float A = 1.0f / (float) (Math.tan(Math.toRadians(fovy/2))*aspect);
		float B = 1.0f / (float) Math.tan(Math.toRadians(fovy/2));
		float C = (-(f + n)) / (f - n);
		float D = (-2.0f * f * n) / (f - n);
		
		Matrix3D pMat = new Matrix3D();
		pMat.setElementAt(0, 0, A);
		pMat.setElementAt(1, 1, B);
		pMat.setElementAt(2, 2, C);
		pMat.setElementAt(3, 2, -1.0f);
		pMat.setElementAt(2, 3, D);
		pMat.setElementAt(3, 3, 0.0f);
		
		return pMat;
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

	public void dispose(GLAutoDrawable drawable) {}
	
	private int createShaderProgram() {
		int[] vertCompiled = new int[1];
		int[] fragCompiled = new int[1];
		int[] linked = new int[1];
		
		GL4 gl = (GL4) GLContext.getCurrentGL();

		String[] vshaderSource = readShaderSource("vert.shader");
		
		String[] fshaderSource = readShaderSource("frag.shader");
		
		int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null);
		gl.glCompileShader(vShader);

		gl.glGetShaderiv(vShader, GL4.GL_COMPILE_STATUS, vertCompiled, 0);
		if (vertCompiled[0] == 1) {
			System.out.println(". . . vertex compilation success.");
		} else {
			System.out.println(". . . vertex compilation failed.");;
		}
		
		int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null);
		gl.glCompileShader(fShader);
		
		gl.glGetShaderiv(fShader, GL4.GL_COMPILE_STATUS, fragCompiled, 0);
		if (fragCompiled[0] == 1) {
			System.out.println(". . . fragment compilation success.");
		} else {
			System.out.println(". . . fragment compilation failed.");
		}
		
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		
		gl.glGetProgramiv(vfprogram, GL4.GL_LINK_STATUS, linked, 0);
		if (linked[0] == 1) {
			System.out.println(". . . linking succeeded.");
		} else {
			System.out.println(". . . linking failed.");
		}
		
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		return vfprogram;
	}
	
	private String[] readShaderSource(String filename) {
		
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Code.class.getResourceAsStream(filename)));
			
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		String[] program = new String[lines.size()];

		for(int i = 0; i < lines.size(); i++) {
			program[i] = (String) lines.get(i) + "\n";
		}
		
		return program;
	}

}