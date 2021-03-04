package types;

public class CompressedMesh {

	public int trianglesCount;
	public float[][] vertices;
	public int[][] triangles;

	public CompressedMesh(int trianglesCount, float[][] vertices, int[][] triangles) {
		this.trianglesCount = trianglesCount;
		this.vertices = vertices;
		this.triangles = triangles;
	}

}