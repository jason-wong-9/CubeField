package com.jasonkcwong.projectionmaze.graphics;

/**
 * Created by Owner on 2/28/2016.
 */
public class Vector {

    public static class VectorException extends RuntimeException {
        public VectorException(String msg) {
            super(msg);
        }
    }

    private double[] elements;

    public Vector(double... elements) {
        this.elements = elements;
    }

    public double[] getElements() {
        return elements;
    }

    public int dimensions() {
        return elements.length;
    }

    public double get(int position) {
        return elements[position];
    }

    public void set(int position, double value) {
        elements[position] = value;
    }

    public double dot(Vector v) {

        if (dimensions() != v.dimensions()) {
            throw new VectorException("Vectors must have the same dimensions");
        }

        double product = 0;
        for (int i=0; i<dimensions(); i++) {
            product += (elements[i] * v.elements[i]);
        }
        return product;

    }

    public Vector cross(Vector v) {

        if (dimensions() != 3 || v.dimensions() != 3) {
            throw new VectorException("Cross product can only be performed on vectors in R3");
        }

        double[] a = elements;
        double[] b = v.elements;

        return new Vector(
                a[1]*b[2] - a[2]*b[1],
                a[2]*b[0] - a[0]*b[2],
                a[0]*b[1] - a[1]*b[0]
        );
    }

    public Vector multiply(double scalar) {
        double[] a = new double[dimensions()];
        for (int i=0; i<dimensions(); i++) {
            a[i] = elements[i] * scalar;
        }
        return new Vector(a);
    }

    public Vector add(Vector v) {

        if (dimensions() != v.dimensions()) {
            throw new VectorException("Vectors must have the same dimensions");
        }

        double[] a = new double[dimensions()];
        for (int i=0; i<dimensions(); i++) {
            a[i] = elements[i] + v.elements[i];
        }
        return new Vector(a);

    }

    public Vector subtract(Vector v) {
        return add(v.multiply(-1));
    }

    public double magnitude() {
        return Math.sqrt(dot(this));
    }

    public String toString() {
        String s = "(";
        for (int i=0; i<dimensions(); i++) {
            s += elements[i];
            if (i != dimensions()-1) {
                s += ", ";
            }
        }
        s += ")";
        return s;
    }

}
