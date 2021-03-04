package template;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFrame;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import com.jogamp.opengl.util.Animator;

import types.CompressedMesh;
import types.Mesh;

public class JOGL_template implements GLEventListener {

	final float SIZE = 100.0f;

	float step = 0.18f;
	float fovY = 60.0f;
	float zNear = 0.1f;
	float zFar = 1000.0f;

	long startTime;
	long lastTime;
	long currentTime;
	float angle;

	int vertexShader;
	int fragmentShader;
	int renderProgram;

	int timeLocation;
	int screenSizeLocation1;
	int depthBufferLocation1;

	Mesh mesh;
	Dimension dimension;

	static Animator animator;

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl2 = drawable.getGL().getGL2();
		GLUgl2 glu2 = (GLUgl2) GLUgl2.createGLU(gl2);

		draw(gl2, glu2);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		animator.stop();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();

		startTime = System.currentTimeMillis();
		lastTime = startTime;

		mesh = generateMesh(Cube.cubeMesh);
		initShaders(gl2);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		final GL2 gl2 = drawable.getGL().getGL2();
		GLUgl2 glu2 = (GLUgl2) GLUgl2.createGLU(gl2);

		dimension = new Dimension(width - x, height - y);

		gl2.glViewport(x, y, width, height);
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		glu2.gluPerspective(fovY, (float) dimension.height / dimension.height, zNear, zFar);
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glShadeModel(GL2.GL_SMOOTH);
		gl2.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
	}

	public static void main(String[] args) {
		final GLProfile glProfile = GLProfile.get(GLProfile.GL2);
		GLCapabilities glCapabilities = new GLCapabilities(glProfile);

		final GLCanvas glCanvas = new GLCanvas(glCapabilities);
		JOGL_template testFrame = new JOGL_template();
		glCanvas.addGLEventListener(testFrame);
		glCanvas.setSize(500, 500);

		animator = new Animator(glCanvas);
		animator.start();

		final JFrame frame = new JFrame("JOGL template");
		frame.getContentPane().add(glCanvas);
		frame.setSize(frame.getContentPane().getPreferredSize());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				e.getWindow().dispose();
			}
		});
		frame.setVisible(true);
	}

	private static Mesh generateMesh(CompressedMesh sourceMesh) {
		int verticesCount = sourceMesh.trianglesCount * 3 * 3;
		float[] vertices = new float[verticesCount];

		for (int i = 0, index = 0; i < sourceMesh.trianglesCount; i++) {
			for (int j = 0; j < 3; j++) {
				int vertexIndex = sourceMesh.triangles[i][j];
				for (int k = 0; k < 3; k++) {
					vertices[index] = sourceMesh.vertices[vertexIndex][k];
					index++;
				}
			}
		}

		return new Mesh(sourceMesh.trianglesCount, vertices);
	}

	private void initShaders(GL2 gl2) {
		gl2.glUseProgram(0);

		if (renderProgram != 0) {
			gl2.glDetachShader(renderProgram, vertexShader);
			gl2.glDetachShader(renderProgram, fragmentShader);
			gl2.glDeleteShader(vertexShader);
			gl2.glDeleteShader(fragmentShader);
			gl2.glDeleteProgram(renderProgram);
		}

		vertexShader = loadShader(gl2, ".\\shaders\\vertex.glsl", GL2.GL_VERTEX_SHADER);
		if (vertexShader == 0) {
			renderProgram = 0;

			return;
		}

		fragmentShader = loadShader(gl2, ".\\shaders\\fragment.glsl", GL2.GL_FRAGMENT_SHADER);
		if (fragmentShader == 0) {
			renderProgram = 0;

			return;
		}

		renderProgram = createProgram(gl2, vertexShader, fragmentShader);
		if (renderProgram != 0) {
			gl2.glUseProgram(renderProgram);

			timeLocation = gl2.glGetUniformLocation(renderProgram, "time");
			screenSizeLocation1 = gl2.glGetUniformLocation(renderProgram, "screenSize");
			depthBufferLocation1 = gl2.glGetUniformLocation(renderProgram, "depthBuffer");
		} else {
			return;
		}
	}

	private static int createProgram(GL2 gl2, int vertexShader, int fragmentShader) {
		int program = gl2.glCreateProgram();

		if (vertexShader != 0) {
			gl2.glAttachShader(program, vertexShader);
		}
		if (fragmentShader != 0) {
			gl2.glAttachShader(program, fragmentShader);
		}
		gl2.glLinkProgram(program);

		int[] linked = new int[1];
		gl2.glGetProgramiv(program, GL2.GL_LINK_STATUS, linked, 0);

		if (linked[0] != 0) {
			return program;
		} else {
			if (vertexShader != 0) {
				gl2.glDetachShader(program, vertexShader);
				gl2.glDeleteShader(vertexShader);
			}
			if (fragmentShader != 0) {
				gl2.glDetachShader(program, fragmentShader);
				gl2.glDeleteShader(fragmentShader);
			}
			gl2.glDeleteProgram(program);

			return 0;
		}
	}

	private static int loadShader(GL2 gl2, String fileName, int shaderType) {
		try {
			String shaderText = new String(Files.readAllBytes(new File(fileName).toPath()));
			int shader = gl2.glCreateShader(shaderType);
			gl2.glShaderSource(shader, 1, new String[] { shaderText }, new int[] { shaderText.length() }, 0);
			gl2.glCompileShader(shader);

			int[] compiled = new int[1];
			gl2.glGetShaderiv(shader, GL2.GL_COMPILE_STATUS, compiled, 0);

			if (compiled[0] != 0) {
				return shader;
			} else {
				gl2.glDeleteShader(shader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	private void draw(GL2 gl2, GLUgl2 glu2) {
		currentTime = System.currentTimeMillis();
		if (currentTime - lastTime >= 10) {
			lastTime = currentTime;
			angle += step;
		}

		gl2.glClearColor(1f, 1f, 1f, 1f);
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		glu2.gluLookAt(0f, SIZE * 1.8f, SIZE * 3.6f, 0f, 0f, 0f, 0f, 1f, 0f);
		gl2.glRotatef(angle, 0f, 1f, 0f);

		if (renderProgram != 0) {
			gl2.glUseProgram(renderProgram);
			gl2.glUniform1f(timeLocation, (float) (currentTime - startTime));
			gl2.glUniform2f(screenSizeLocation1, (float) dimension.width, (float) dimension.height);
		}

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);

		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, mesh.vertices);
		gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, mesh.verticesCount);

		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);

		gl2.glUseProgram(0);
	}

}