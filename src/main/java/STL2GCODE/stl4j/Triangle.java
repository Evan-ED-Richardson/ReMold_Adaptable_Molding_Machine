package STL2GCODE.stl4j;

import java.util.Arrays;


public class Triangle {
    private final Vec3d[] vertices;
    private final Vec3d normal;

    /**
     * Creates a triangle with the given vertices at its corners. The normal is
     * calculated by assuming that the vertices were provided in right-handed
     * coordinate space (counter-clockwise)
     * @param v1 A corner vertex
     * @param v2 A corner vertex
     * @param v3 A corner vertex
     */
    public Triangle(Vec3d v1, Vec3d v2, Vec3d v3){
        vertices = new Vec3d[3];
        vertices[0] = v1;
        vertices[1] = v2;
        vertices[2] = v3;
        Vec3d edge1 = v2.sub(v1);
        Vec3d edge2 = v3.sub(v1);
        normal = Vec3d.cross(edge1, edge2).normalize();
    }
    /**
     * Moves the triangle in the X,Y,Z direction
     * @param translation A vector of the delta for each coordinate.
     */
    public void translate(Vec3d translation){
        for(int i = 0; i < vertices.length; i++){
            vertices[i] = vertices[i].add(translation);
        }
    }

    /**
     * Calculates the barycentric coordinates of a given point (x, y) with respect to this triangle.
     * Barycentric coordinates (u, v, w) represent a point's position within a triangle as a weighted average
     * of the triangle's vertices. If the point lies within the triangle, the method returns an array with
     * three barycentric coordinates [u, v, w]. If the point is outside the triangle, the method returns null.
     *
     * @param x The x-coordinate of the point for which barycentric coordinates are to be calculated.
     * @param y The y-coordinate of the point for which barycentric coordinates are to be calculated.
     * @return An array with three barycentric coordinates [u, v, w] if the point is inside the triangle,
     *         or null if the point is outside the triangle.
     */
    public double[] barycentricCoords(double x, double y) {
        Vec3d v0 = vertices[1].sub(vertices[0]);
        Vec3d v1 = vertices[2].sub(vertices[0]);
        Vec3d v2 = new Vec3d(x - vertices[0].x, y - vertices[0].y, 0);

        double dot00 = v0.dot(v0);
        double dot01 = v0.dot(v1);
        double dot02 = v0.dot(v2);
        double dot11 = v1.dot(v1);
        double dot12 = v1.dot(v2);

        double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        if (u >= 0 && v >= 0 && u + v <= 1) {
            return new double[]{u, v, 1 - u - v};
        } else {
            return null;
        }
    }

    /**
     * @see java.lang.Object#toString()
     * @return A string that provides some information about this triangle
     */
    @Override public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Triangle[");
        for(Vec3d v : vertices){
            sb.append(v.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Gets the vertices at the corners of this triangle
     * @return An array of vertices
     */
    public Vec3d[] getVertices(){
        return vertices;
    }

    /**
     * Gets the normal vector
     * @return A vector pointing in a direction perpendicular to the surface of
     * the triangle.
     */
    public Vec3d getNormal(){
        return normal;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj Object to test equality
     * @return True if the other object is a triangle whose verticese are the
     * same as this one.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triangle other = (Triangle) obj;
        if (!Arrays.deepEquals(this.vertices, other.vertices)) {
            return false;
        }
        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     * @return A hashCode for this triangle
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Arrays.deepHashCode(this.vertices);
        return hash;
    }

}