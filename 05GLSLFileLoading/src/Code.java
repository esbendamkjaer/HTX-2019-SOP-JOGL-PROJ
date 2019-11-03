import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFrame;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

public class Code extends JFrame implements GLEventListener {
	private static final long serialVersionUID = 3746059804968448472L;
	private GLCanvas myCanvas;
	private int rendering_program;
	private int vao[] = new int[1];

	public Code() {
		setTitle("Chapter2 - program1");
		setSize(600, 400);
		//setLocation(200, 200);
		setLocationRelativeTo(null);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		setVisible(true);
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glUseProgram(rendering_program);
		gl.glPointSize(30.0f);
		gl.glDrawArrays(GL4.GL_POINTS, 0, 1);
	}

	public static void main(String[] args) {
		new Code();
	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
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
		
		Vector<String> lines = new Vector<String>();
		
		Scanner sc;
		try {
			sc = new Scanner(new File(filename));
		} catch (IOException e) {
			System.err.println("IOException reading file: " + e);
			return null;
		}
		
		while (sc.hasNext()) {
			lines.addElement(sc.nextLine());
		}
		
		sc.close();
		
		String[] program = new String[lines.size()];
		
		for(int i = 0; i < lines.size(); i++) {
			program[i] = (String) lines.elementAt(i) + "\n";
		}
		
		return program;
	}

}