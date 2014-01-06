package com.jbrown.cast;

import java.awt.Dimension;
import java.awt.Rectangle;


public class BrownSpot extends Dimension {
    private double _x;
    private double _y;
    private int _seed;

    public BrownSpot(Rectangle rectangle) {
        super(new Dimension((int) rectangle.getWidth(),
                (int) rectangle.getHeight()));
        _x =  rectangle.getX();
        _y =  rectangle.getY();
        _seed = -1;
    }

    public BrownSpot(double x, double y, int seed) {
        _x = x;
        _y = y;
        _seed = seed;
    }

    public int getX() {
        return (int)_x;
    }

    public int getY() {
        return (int)_y;
    }

    public int getSeed() {
        return _seed;
    }
}
