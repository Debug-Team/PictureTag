package com.debugTeam.util.dbscan;

public class Point {
    public double x;
    public double y;
    public double z;
    public double n;
    private boolean isVisit;
    private int cluster;
    private boolean isNoised;

    public Point(double x,double y,double z,double n) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.n = n;
        this.isVisit = false;
        this.cluster = 0;
        this.isNoised = false;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getN() {
        return n;
    }

    public void setN(double n) {
        this.n = n;
    }

    public boolean isVisit() {
        return isVisit;
    }

    public boolean isNoised() {
        return isNoised;
    }

    public double getDistance(Point point) {
        return Math.sqrt((x-point.x)*(x-point.x)+(y-point.y)*(y-point.y)+(z-point.z)*(z-point.z)+(n-point.n)*(n-point.n));
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setVisit(boolean isVisit) {
        this.isVisit = isVisit;
    }

    public boolean getVisit() {
        return isVisit;
    }

    public int getCluster() {
        return cluster;
    }

    public void setNoised(boolean isNoised) {
        this.isNoised = isNoised;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public boolean getNoised() {
        return this.isNoised;
    }

    @Override
    public String toString() {
        return x+" "+y+" "+cluster+" "+(isNoised?1:0);
    }

}
