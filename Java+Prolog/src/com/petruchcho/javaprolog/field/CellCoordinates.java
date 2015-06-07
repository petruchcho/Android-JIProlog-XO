package com.petruchcho.javaprolog.field;

public final class CellCoordinates {
    private final int x;
    private final int y;

    public CellCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CellCoordinates)) return false;
        CellCoordinates other = (CellCoordinates) o;
        return x == other.getX() && y == other.getY();
    }
}
