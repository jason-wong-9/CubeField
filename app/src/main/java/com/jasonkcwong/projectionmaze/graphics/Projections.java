package com.jasonkcwong.projectionmaze.graphics;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.jasonkcwong.projectionmaze.graphics.Constants.*;

/**
 * Created by Owner on 2/28/2016.
 */
public class Projections {

    public static int calculateScreenDist(int screenWidth, double fov) {
        return (int) ((screenWidth/2) / Math.tan(fov/2));
    }

    public static List<RenderQueueItem> getRenderQueue(Cube[] cubes, Vector observer, Vector angle,
                           int screenWidth, int screenHeight, int screenDist, int renderDist) {

        double screenRadius = Math.sqrt(Math.pow(screenWidth/2, 2) + Math.pow(screenHeight/2, 2));

        Vector normal = getNormal(angle);
        Matrix rotTrans = getRotationTransformMatrix(angle);

        List<RenderQueueItem> renderQueue = new ArrayList<>();

        for (Cube cube : cubes) {

            // Scan every vertex
            // Calculate distances + find closest
            double distToClosestVertex = Integer.MAX_VALUE;
            int closestVertexIndex = -1;
            Vector[] relPos = new Vector[8];    // Position of vertex relative to observer
            double[] absDist = new double[8];  // Magnitude of relative position projected onto normal
            boolean inView = false;

            for (int i=0; i<8; i++) {
                Vector vertex = cube.getVertices()[i];
                relPos[i] = vertex.subtract(observer);
                absDist[i] = relPos[i].dot(normal);
                if (!inView && absDist[i] > screenDist && absDist[i] <= renderDist) {
                    inView = true;
                }
                if (inView) {
                    if (relPos[i].magnitude() < distToClosestVertex) {
                        distToClosestVertex = relPos[i].magnitude();
                        closestVertexIndex = i;
                    }
                }
            }

            if (!inView) {
                continue;
            }

            Vector[] screenPos = new Vector[8]; // Vector projected onto screen
            boolean onScreen = false;

            for (int i = 0; i < 8; i++) {
                screenPos[i] = relPos[i]
                        .subtract(normal.multiply(absDist[i]))
                        .multiply(screenDist / absDist[i]);
                if (!onScreen && screenPos[i].magnitude() <= screenRadius) {
                    onScreen = true;
                }
            }

            if (!onScreen) {
                continue;
            }

            Matrix m = new Matrix(screenPos);
            m = rotTrans.multiply(m);
            screenPos = m.cols();
            Point[] points = new Point[8];
            for (int i=0; i<8; i++) {
                Vector v = screenPos[i];
                points[i] = new Point((int) (v.get(X) + screenWidth/2), (int) (-v.get(Y) + screenHeight/2));
            }

            int[][] surfaces;

            switch (closestVertexIndex) {
                case 0:
                    surfaces = new int[][] {
                            new int[] {0, 1, 2, 3},
                            new int[] {0, 1, 5, 4},
                            new int[] {0, 3, 7, 4}
                    };
                    break;
                case 1:
                    surfaces = new int[][] {
                            new int[] {1, 2, 6, 5},
                            new int[] {1, 5, 4, 0},
                            new int[] {1, 2, 3, 0}
                    };
                    break;
                case 2:
                    surfaces = new int[][] {
                            new int[] {2, 6, 5, 1},
                            new int[] {2, 3, 0, 1},
                            new int[] {2, 6, 7, 3}
                    };
                    break;
                case 3:
                    surfaces = new int[][] {
                            new int[] {3, 2, 1, 0},
                            new int[] {3, 0, 4, 7},
                            new int[] {3, 2, 6, 7}
                    };
                    break;
                case 4:
                    surfaces = new int[][] {
                            new int[] {4, 5, 1, 0},
                            new int[] {4, 7, 3, 0},
                            new int[] {4, 5, 6, 7}
                    };
                    break;
                case 5:
                    surfaces = new int[][] {
                            new int[] {5, 6, 7, 4},
                            new int[] {5, 6, 2, 1},
                            new int[] {5, 4, 0, 1}
                    };
                    break;
                case 6:
                    surfaces = new int[][] {
                            new int[] {6, 5, 1, 2},
                            new int[] {6, 7, 3, 2},
                            new int[] {6, 7, 4, 5}
                    };
                    break;
                case 7:
                    surfaces = new int[][] {
                            new int[] {7, 6, 5, 4},
                            new int[] {7, 6, 2, 3},
                            new int[] {7, 3, 0, 4}
                    };
                    break;
                default:
                    Log.e("ERROR!!!", "ERROR!!!!!!!!!!!!!! YOU SCREWED UP!!!!! YOU ARE BAD!!!!!!!!");
                    continue;
            }

            for (int[] surface : surfaces) {
                renderQueue.add(new RenderQueueItem(distToClosestVertex,
                        points[surface[0]],
                        points[surface[1]],
                        points[surface[2]],
                        points[surface[3]]
                ));
            }

        }

        Collections.sort(renderQueue, new Comparator<RenderQueueItem>() {
            @Override
            public int compare(RenderQueueItem a, RenderQueueItem b) {
                return Double.valueOf(b.depth).compareTo(a.depth);
            }
        });

        return renderQueue;
    }

    public static Matrix getRotationTransformMatrix(Vector angle) {

        Vector t = angle.multiply(-1);

        Matrix cameraTransformX = new Matrix(new double[][] {
                {1, 0, 0},
                {0, Math.cos(t.get(X)), -Math.sin(t.get(X))},
                {0, Math.sin(t.get(X)), Math.cos(t.get(X))}
        });

        Matrix cameraTransformY = new Matrix(new double[][] {
                {Math.cos(t.get(Y)), 0, Math.sin(t.get(Y))},
                {0, 1, 0},
                {-Math.sin(t.get(Y)), 0, Math.cos(t.get(Y))}
        });

        Matrix cameraTransformZ = new Matrix(new double[][] {
                {Math.cos(t.get(Z)), -Math.sin(t.get(Z)), 0},
                {Math.sin(t.get(Z)), Math.cos(t.get(Z)), 0},
                {0, 0, 1}
        });

        return cameraTransformX.multiply(cameraTransformY).multiply(cameraTransformZ);
    }

    public static Vector getNormal(Vector angle) {
        return getRotationTransformMatrix(angle.multiply(-1)).multiply(new Vector(0, 0, 1));
    }
}
