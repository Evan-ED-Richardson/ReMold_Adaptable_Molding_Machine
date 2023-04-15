package STL2GCODE.stl4j;

public final class Vec2d {
    /**
     * The x coordinate.
     */
    public double x;

    /**
     * The y coordinate.
     */
    public double y;

    /**
     * Constructs a 3D vector
     * @param x coordinate in the first dimension
     * @param y coordinate in the second dimension
     */
    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getCoord(int axis) {
        switch (axis){
            case 0: return this.x;
            case 1: return this.y;
            default: return 0;
        }
    }

    public void setCoord(int axis, double coord) {
        switch (axis) {
            case 0: this.x = coord;
            case 1: this.y = coord;
        }
    }

    /**
     * Returns the hashcode for this <code>Vec3f</code>.
     * @return      a hash code for this <code>Vec3f</code>.
     */
    @Override
    public int hashCode() {
        long bits = 7L;
        bits = 31L * bits + Double.doubleToLongBits(x);
        bits = 31L * bits + Double.doubleToLongBits(y);
        return (int) (bits ^ (bits >> 32));
    }

    /**
     * Determines whether two 3D points or vectors are equal.
     * Two instances of <code>Vec3d</code> are equal if the values of their
     * <code>x</code>, <code>y</code> and <code>z</code> member fields,
     * representing their position in the coordinate space, are the same.
     * @param obj an object to be compared with this <code>Vec3d</code>
     * @return <code>true</code> if the object to be compared is
     *         an instance of <code>Vec3d</code> and has
     *         the same values; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Vec2d) {
            Vec2d v = (Vec2d) obj;
            return (x == v.x) && (y == v.y);
        }
        return false;
    }

    /**
     * Returns a <code>String</code> that represents the value
     * of this <code>Vec3f</code>.
     * @return a string representation of this <code>Vec3f</code>.
     */
    @Override
    public String toString() {
        return "Vec2d[" + x + ", " + y + "]";
    }

}
