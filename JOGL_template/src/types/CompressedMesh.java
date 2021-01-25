package types;

public class CompressedMesh {

	public int trianglesCount;
	public float[][] vertices;
	public float[][] colors;
	public int[][] triangles;

	public CompressedMesh(int trianglesCount, float[][] vertices, float[][] colors, int[][] triangles) {
		this.trianglesCount = trianglesCount;
		this.vertices = vertices;
		this.colors = colors;
		this.triangles = triangles;
	}

}