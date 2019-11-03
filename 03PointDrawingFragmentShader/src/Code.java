import javax.swing.JFrame;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

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

	private int createShaderProgram() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = {
				"#version 430 \n",
				"void main(void) \n",
				"{ gl_Position = vec4(0.0, 0.0, 0.0, 1.0); } \n",
				};
		
		String fshaderSource[] = {
				"#version 430 \n",
				"out vec4 color; \n",
				"void main(void) \n",
				"{ if (gl_FragCoord.x < 300) color = vec4(1.0, 0.0, 0.0, 1.0); else color = vec4(0.0, 0.0, 1.0, 1.0);} \n",
				};
				
		
		int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);		//setLocation(200, 200);

		gl.glShaderSource(vShader, 3, vshaderSource, null, 0); // note: 3 lines of code
		gl.glCompileShader(vShader);

		int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, 4, fshaderSource, null, 0); // note: 4 lines of code
		gl.glCompileShader(fShader);
		
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);

		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		return vfprogram;
	}

}