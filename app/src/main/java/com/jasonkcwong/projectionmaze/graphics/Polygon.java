package com.jasonkcwong.projectionmaze.graphics;

/**
 * Created by Owner on 2/28/2016.
 */
public class Polygon {

    public final int[] xpoints;
    public final int[] ypoints;
    public final int npoints;

    public Polygon(int[] xpoints, int[] ypoints, int npoints) {
        this.xpoints = xpoints;
        this.ypoints = ypoints;
        this.npoints = npoints;
    }

}
