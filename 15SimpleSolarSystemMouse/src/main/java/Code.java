import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import graphicslib3D.GLSLUtils;
import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;

public class Code extends JFrame implements GLEventListener {
	private static final long serialVersionUID = 3746059804968448472L;
	private GLCanvas myCanvas;
	private int rendering_program;
	private int vao[] = new int[1];
	private int vbo[] = new int[2];
	private float cameraX, cameraY, cameraZ;
	private float cubeLocX, cubeLocY, cubeLocZ;
	private float pyrLocX, pyrLocY, pyrLocZ;
	private GLSLUtils util = new GLSLUtils();
	private Matrix3D pMat;
	
	private int angleY = 0;
	private int angle = 0;
	
	public Code() {
		setTitle("Chapter2 - program1");
		setSize(600, 600);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		
		setVisible(true);
		
		myCanvas.addMouseMotionListener(new MouseMotionListener() {
	
			private int lastX = -1, lastY = -1;
			
			@Override
			public void mouseMoved(MouseEvent e) {
				
				if (lastX == -1) lastX = e.getX();
				if (lastY == -1) lastY = e.getY();

				angleY += e.getX() - lastX;
				angle += e.getY() - lastY;
				System.out.println(angleY);
				System.out.println(angle);
				lastX = e.getX();
				lastY = e.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		FPSAnimator animtr = new FPSAnimator(myCanvas, 50);
		animtr.start();
		
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		gl.glClear(GL4.GL_DEPTH_BUFFER_BIT);
		gl.glUseProgram(rendering_program);
		gl.glEnable(GL4.GL_CULL_FACE);

		float bkg[] = {0.0f, 0.0f, 0.0f, 1.0f};
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL4.GL_COLOR, 0, bkgBuffer);
		
		int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
		int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");
		gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
		
		MatrixStack mvStack = new MatrixStack(20);
		
		mvStack.pushMatrix();
		mvStack.translate(-cameraX, -cameraY, -cameraZ);

		mvStack.rotate(angleY, 0, 1, 0);
		mvStack.rotate(Math.cos(Math.toRadians(angleY))*angle, 1, 0, 0);
		mvStack.rotate(Math.sin(Math.toRadians(angleY))*angle, 0, 0, 1);
		
		double amt = (double)(System.currentTimeMillis())/1000.0;
		
		mvStack.pushMatrix();
		mvStack.translate(pyrLocX, pyrLocY, pyrLocZ);
		//mvStack.rotate((System.currentTimeMillis()) / 10.0, 1.0, 0.0, 0.0);
		
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);		
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		gl.glFrontFace(GL4.GL_CCW);
		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 18);
		
		mvStack.popMatrix();
		
		
		mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt)*4.0f, 0.0f, Math.cos(amt)*4.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0, 0.0, 1.0, 0.0);
		
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		gl.glFrontFace(GL4.GL_CW);
		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 36);
		
		mvStack.popMatrix();
		
		
		mvStack.pushMatrix();
		mvStack.translate(0.0f, Math.sin(amt)*2.0f, Math.cos(amt)*2.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0, 0.0, 0.0, 1.0);
		mvStack.scale(0.25, 0.25, 0.25);
		
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		gl.glFrontFace(GL4.GL_CW);
		gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 36);
		
		mvStack.popMatrix(); mvStack.popMatrix(); mvStack.popMatrix(); mvStack.popMatrix();
		
	}

	public static void main(String[] args) {
		new Code();
	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		setupVertices();
		cameraX = 0.0f; cameraY = 3.0f; cameraZ = 15.0f;
		cubeLocX = 0.0f; cubeLocY = 0.0f; cubeLocZ = 0.0f;
		pyrLocX = 0.0f; pyrLocY = 0.0f; pyrLocZ = 0.0f;
		
		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);
	}

	private void setupVertices() {
		
		GL4 gl = (GL4) GLContext.getCurrentGL();
		
		float[] cube_positions = {
			-1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
			1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
			-1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f
		};
		
		float[] pyramid_positions = {
			-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
			1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
			-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
			-1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
			1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
		};
		
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(cube_positions);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, cubeBuf.limit()*4, cubeBuf, GL4.GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(pyramid_positions);
		gl.glBufferData(GL4.GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL4.GL_STATIC_DRAW);
	}
	
	private Matrix3D perspective(float fovy, float aspect, float n, float f) {
		float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		
		Matrix3D r = new Matrix3D();
		r.setElementAt(0, 0, A);
		r.setElementAt(1, 1, q);
		r.setElementAt(2, 2, B);
		r.setElementAt(3, 2, -1.0f);
		r.setElementAt(2, 3, C);
		r.setElementAt(3, 3, 0.0f);
		
		return r;
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	public void dispose(GLAutoDrawable drawable) {
	}

	private void printShaderLog(int shader) {
		
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;
		
		gl.glGetShaderiv(shader, GL4.GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
			System.out.println("Shader Info Log: ");
			for (int i = 0; i < log.length; i++) {
				System.out.print((char) log[i]);
			}
		}
		
	}
	
	private void printProgramLog(int prog) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;
		
		gl.glGetProgramiv(prog, GL4.GL_INFO_LOG_LENGTH, len, 0);
		
		if (len[0] > 0) {
			log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
			System.out.println("Program Info Log: ");
			for (int i = 0; i < log.length; i++) {
				System.out.print((char) log[i]);
			}
		}
	}
	
	private boolean checkOpenGLError() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while(glErr != GL4.GL_NO_ERROR) {
			System.err.println("glError: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}
	
	private int createShaderProgram() {
		int[] vertCompiled = new int[1];
		int[] fragCompiled = new int[1];
		int[] linked = new int[1];
		
		GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = readShaderSource("vert.shader");
		
		String fshaderSource[] = readShaderSource("frag.shader");
				
		
		int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);

		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glCompileShader(vShader);

		checkOpenGLError();
		gl.glGetShaderiv(vShader, GL4.GL_COMPILE_STATUS, vertCompiled, 0);
		if (vertCompiled[0] == 1) {
			System.out.println(". . . vertex compilation success.");
		} else {
			System.out.println(". . . vertex compilation failed.");
			printShaderLog(vShader);
		}
		
		int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(fShader);
		
		checkOpenGLError();
		gl.glGetShaderiv(fShader, GL4.GL_COMPILE_STATUS, fragCompiled, 0);
		if (fragCompiled[0] == 1) {
			System.out.println(". . . fragment compilation success.");
		} else {
			System.out.println(". . . fragment compilation failed.");
			printShaderLog(fShader);
		}
		
		if ((vertCompiled[0] != 1) || (fragCompiled[0] != 1)) {
			System.out.println("\nCompilation error; return-falgs:");
			System.out.println(" vertCompiled = " + vertCompiled[0] + " ; fragCompiled = " + fragCompiled[0]);
		} else {
			System.out.println("Successful compilation");
		}
		
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);

		checkOpenGLError();
		gl.glGetProgramiv(vfprogram, GL4.GL_LINK_STATUS, linked, 0);
		if (linked[0] == 1) {
			System.out.println(". . . linking succeeded.");
		} else {
			System.out.println(". . . linking failed.");
			printProgramLog(vfprogram);
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