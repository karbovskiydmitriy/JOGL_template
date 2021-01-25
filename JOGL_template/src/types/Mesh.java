package types;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;

public class Mesh {

	public int verticesCount;
	public FloatBuffer vertices;
	public FloatBuffer colors;

	public Mesh(int trianglesCount, float[] vertices, float[] colors) {
		this.verticesCount = trianglesCount * 3;
		this.vertices = Buffers.newDirectFloatBuffer(vertices);
		this.colors = Buffers.newDirectFloatBuffer(colors);
	}

}