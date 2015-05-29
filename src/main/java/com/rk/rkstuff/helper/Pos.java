package com.rk.rkstuff.helper;

public class Pos {

    public static final Pos NULL = new Pos(0,0,0);
    public static final Pos UNDEFINED = new Pos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    public int x;
    public int y;
    public int z;

    public Pos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Pos clone(){
        return new Pos(x,y,z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pos pos = (Pos) o;

        if (x != pos.x) return false;
        if (y != pos.y) return false;
        if (z != pos.z) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "Pos{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
