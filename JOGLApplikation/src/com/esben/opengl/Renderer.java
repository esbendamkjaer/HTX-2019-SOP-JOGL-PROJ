package com.esben.opengl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.swing.JFrame;

import com.esben.opengl.objects.Cube;
import com.esben.opengl.objects.Object3D;
import com.esben.opengl.objects.Pyramid;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import graphicslib3D.Matrix3D;

public class Renderer extends JFrame implements GLEventListener {
	private static final long serialVersionUID = 3746059804968448472L;
	
	private GLCanvas glCanvas;
	
	private int renderingProgram;
	
	public final int MAX_NUMBER_OF_OBJECTS = 20;
	
	private int vao[] = new int[1];
	private int vbo[] = new int[MAX_NUMBER_OF_OBJECTS];
	private Object3D[] objects = new Object3D[MAX_NUMBER_OF_OBJECTS];
	
	private float cameraX, cameraY, cameraZ;
	private Matrix3D pMat;
	
	public Renderer() {
		setSize(800, 800);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		glCanvas = new GLCanvas();
		glCanvas.addGLEventListener(this);
		this.add(glCanvas);
		
		setVisible(true);
		
		// Gentegn GLCanvas 50 gange i sekundet
		FPSAnimator animtr = new FPSAnimator(glCanvas, 50);
		animtr.start();
	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		renderingProgram = createShaderProgram();
		gl.glUseProgram(renderingProgram);
		
		gl.glPolygonMode(GL4.GL_FRONT_AND_BACK, GL4.GL_LINE);

		gl.glEnable(GL4.GL_CULL_FACE);
		gl.glFrontFace(GL4.GL_CW);
		
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		
		cameraX = 2.0f; cameraY = 0.0f; cameraZ = 8.0f;
		
		float aspect = (float) glCanvas.getWidth() / (float) glCanvas.getHeight();
		pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);
		
		initObjects();
		initBuffers();
	}
	
	/*
	 * Instantierer anvendte objekter
	 */
	private void initObjects() {
		Pyramid pyramid = new Pyramid();
		pyramid.setY(2.0f);
		objects[0] = pyramid;
		
		Cube cube = new Cube();
		cube.setY(-2);
		objects[1] = cube;
	}
	
	/*
	 * Erklærer og initialiserer buffere
	 */
	private void initBuffers() {
		
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null) continue;
			
			float[] vertValues = objects[i].getModel().getVertices();
			
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[i]);
			FloatBuffer vertDataBuffer = Buffers.newDirectFloatBuffer(vertValues);
			gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertDataBuffer.limit()*4, vertDataBuffer, GL4.GL_STATIC_DRAW);
		}
		
	}
	
	/*
	 * Kaldes hver gang GLCanvas tegnes
	 */
	public void display(GLAutoDrawable drawable) {
		for (Object3D object : objects) {
			if (object == null) continue;
			object.update();
		}
		
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL4.GL_DEPTH_BUFFER_BIT);
		
		float bkg[] = {1.0f, 1.0f, 1.0f, 1.0f};
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL4.GL_COLOR, 0, bkgBuffer);
		
		Matrix3D vMat = new Matrix3D();
		vMat.translate(-cameraX, -cameraY, -cameraZ);
		
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null) continue;
			
			Matrix3D mMat = new Matrix3D();
			mMat.translate(objects[i].getX(), objects[i].getY(), objects[i].getZ());
			mMat.rotate(objects[i].getRotX(), objects[i].getRotY(), objects[i].getRotZ());
			mMat.scale(objects[i].getScale(), objects[i].getScale(), objects[i].getScale());
			
			Matrix3D mvpMat = new Matrix3D();
			mvpMat.concatenate(pMat);
			mvpMat.concatenate(vMat);
			mvpMat.concatenate(mMat);
			
			int mvp_loc = gl.glGetUniformLocation(renderingProgram, "mvpMatrix");
			gl.glUniformMatrix4fv(mvp_loc, 1, false, mvpMat.getFloatValues(), 0);
			
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[i]);
			gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
			
			int numVerts = objects[i].getModel().getNumVertices();
			
			gl.glDrawArrays(GL4.GL_TRIANGLES, 0, numVerts);
		}
	}
	
	/*
	 * Genererer en perspektivmatrice ud fra givne parametre
	 */
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
	
	/*
	 * Kompilerer og opsætter shader programmet
	 */
	private int createShaderProgram() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = readShaderSource("/shaders/vert.shader");
		String fshaderSource[] = readShaderSource("/shaders/frag.shader");
		
		int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null);
		gl.glCompileShader(vShader);

		int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null);
		gl.glCompileShader(fShader);
		
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		return vfprogram;
	}
	
	/*
	 * Indlæser kildekode fra shaderfil med givent filnavn
	 */
	private String[] readShaderSource(String filename) {
		
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Renderer.class.getResourceAsStream(filename)));
			
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

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

	public void dispose(GLAutoDrawable drawable) {}	
	
}