package com.jasonkcwong.projectionmaze.graphics;

/**
 * Created by Owner on 2/28/2016.
 */
public class Matrix {

    public static class MatrixException extends RuntimeException {
        public MatrixException(String msg) {
            super(msg);
        }
    }

    double[][] matrix;

    public Matrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public Matrix(Vector... cols) {
        matrix = new double[cols[0].dimensions()][cols.length];
        for (int j=0; j<cols.length; j++) {
            for (int i=0; i<cols[j].dimensions(); i++) {
                matrix[i][j] = cols[j].getElements()[i];
            }
        }
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public double get(int i, int j) {
        return matrix[i][j];
    }

    public void set(int i, int j, double value) {
        matrix[i][j] = value;
    }

    public int rowCount() {
        return matrix.length;
    }

    public int colCount() {
        return matrix[0].length;
    }

    public Vector row(int row) {
        double[] a = new double[colCount()];
        for (int i=0; i<colCount(); i++) {
            a[i] = matrix[row][i];
        }
        return new Vector(a);
    }

    public Vector col(int col) {
        double[] a = new double[rowCount()];
        for (int i=0; i<rowCount(); i++) {
            a[i] = matrix[i][col];
        }
        return new Vector(a);
    }

    public Vector[] rows() {
        Vector[] a = new Vector[rowCount()];
        for (int i=0; i<rowCount(); i++) {
            a[i] = row(i);
        }
        return a;
    }

    public Vector[] cols() {
        Vector[] a = new Vector[colCount()];
        for (int i=0; i<colCount(); i++) {
            a[i] = col(i);
        }
        return a;
    }

    public Matrix multiply(Matrix m) {

        if (colCount() != m.rowCount()) {
            throw new MatrixException("Matrix multiplication is not defined");
        }

        double[][] a = new double[rowCount()][m.colCount()];
        for (int i=0; i<rowCount(); i++) {
            for (int j=0; j<m.colCount(); j++) {
                a[i][j] = row(i).dot(m.col(j));
            }
        }
        return new Matrix(a);

    }

    public Vector multiply(Vector v) {
        return multiply(new Matrix(v)).col(0);
    }

    public Matrix multiply(double scalar) {
        double[][] a = new double[rowCount()][colCount()];
        for (int i=0; i<rowCount(); i++) {
            for (int j=0; j<colCount(); j++) {
                a[i][j] = matrix[i][j] * scalar;
            }
        }
        return new Matrix(a);
    }

    public Matrix add(Matrix m) {

        if (rowCount() != m.rowCount() || colCount() != m.colCount()) {
            throw new MatrixException("Matrices must be the same size");
        }

        double[][] a = new double[rowCount()][colCount()];
        for (int i=0; i<rowCount(); i++) {
            for (int j=0; j<colCount(); j++) {
                a[i][j] = matrix[i][j] + m.matrix[i][j];
            }
        }
        return new Matrix(a);
    }

    public Matrix subtract(Matrix m) {
        return add(m.multiply(-1));
    }

    public String toString() {
        String s = "";
        for (int i=0; i<rowCount(); i++) {
            String row = "|";
            for (int j=0; j<colCount(); j++) {
                row += String.format("%10.2f", matrix[i][j]);
                if (j != colCount()-1) {
                    row += " ";
                }
            }
            row += "|";
            if (i != rowCount()-1) {
                row += "\n";
            }
            s += row;
        }
        return s + "\n";
    }

}
