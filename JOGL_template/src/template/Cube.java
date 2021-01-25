package template;

import types.CompressedMesh;

public abstract class Cube {
	
	static float[][] cubeVertices = new float[][] {
		{ -100.0f, -100.0f, 100.0f },
		{ -100.0f, 100.0f, 100.0f },
		{ 100.0f, -100.0f, 100.0f },
		{ 100.0f, 100.0f, 100.0f },
		{ 100.0f, -100.0f, -100.0f },
		{ 100.0f, 100.0f, -100.0f },
		{ -100.0f, -100.0f, -100.0f },
		{ -100.0f, 100.0f, -100.0f }};
		
	static float[][] cubeColors = new float[][] {
		{ 0f, 0f, 0f },
		{ 0f, 0f, 0f },
		{ 0f, 0f, 0f },
		{ 0f, 0f, 0f },
		{ 0f, 0f, 0f },
		{ 0f, 0f, 0f },
		{ 0f, 0f, 0f },
		{ 0f, 0f, 0f }};

	static int[][] cubeTriangles = new int[][] {
		{ 2, 3, 1 },
		{ 2, 1, 0 },
		{ 4, 5, 3 },
		{ 4, 3, 2 },
		{ 6, 7, 5 },
		{ 6, 5, 4 },
		{ 0, 1, 7 },
		{ 0, 7, 6 },
		{ 3, 5, 7 },
		{ 3, 7, 1 },
		{ 4, 2, 0 },
		{ 4, 0, 6 }};

	public static CompressedMesh cubeMesh = new CompressedMesh(cubeVertices.length, cubeVertices, cubeColors, cubeTriangles);
	
}