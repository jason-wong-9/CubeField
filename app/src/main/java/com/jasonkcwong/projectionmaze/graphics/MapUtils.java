package com.jasonkcwong.projectionmaze.graphics;

/**
 * Created by Owner on 2/28/2016.
 */
public class MapUtils {

    public static Cube[] generateMap(int mapSize, int numSquares, int squareSize) {

        Vector[] v = new Vector[numSquares];
        for (int i=0; i<numSquares; i++) {
            v[i] = new Vector (
                Math.random() * (2*mapSize - squareSize) - mapSize,
                Math.random() * (2*mapSize - squareSize) - mapSize,
                Math.random() * (2*mapSize - squareSize) - mapSize
            );
        }

        Cube[] cubes = new Cube[numSquares];
        for (int i=0; i<numSquares; i++) {
            cubes[i] = new Cube(v[i], squareSize);
        }

        return cubes;
    }
}
