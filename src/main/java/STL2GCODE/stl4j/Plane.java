package STL2GCODE.stl4j;

import java.util.List;

import static STL2GCODE.stl4j.Vec3d.cross;
import org.ejml.simple.SimpleMatrix;

public class Plane {
    private Vec3d normal;
    private double d;

    public Plane(Vec3d normal, double d) {
        this.normal = normal;
        this.d = d;
    }

    public Vec3d getNormal() {
        return normal;
    }

    public void setNormal(Vec3d normal) {
        this.normal = normal;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    /**
     * Calculates the z-coordinate of a point on the plane given its x and y coordinates.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return The z-coordinate of the point.
     */
    public double getZ(double x, double y) {
        if (normal.z == 0) {
            throw new IllegalStateException("The plane normal's z component cannot be zero");
        }
        return (-d - normal.x * x - normal.y * y) / normal.z;
    }

    /**
     * Creates a plane from three points in 3D space.
     *
     * @param p1 The first point.
     * @param p2 The second point.
     * @param p3 The third point.
     * @return A new Plane object representing the plane that passes through the three points.
     */
    public static Plane fromPoints(Vec3d p1, Vec3d p2, Vec3d p3) {
        Vec3d v1 = p2.sub(p1);
        Vec3d v2 = p3.sub(p1);
        Vec3d normal = cross(v1, v2).normalize();
        double d = -normal.dot(p1);
        return new Plane(normal, d);
    }
    /**
     * Calculates the best fit plane for a list of Vec3d points.
     *
     * @param points A list of Vec3d points.
     * @return A new Plane object representing the best fit plane.
     */
    public static Plane bestFitPlane(List<Vec3d> points) {
        if (points == null || points.size() < 3) {
            throw new IllegalArgumentException("At least three points are required to define a plane.");
        }

        int n = points.size();
        double sumX = 0, sumY = 0, sumZ = 0;

        for (Vec3d point : points) {
            sumX += point.x;
            sumY += point.y;
            sumZ += point.z;
        }

        double centerX = sumX / n;
        double centerY = sumY / n;
        double centerZ = sumZ / n;

        SimpleMatrix H = new SimpleMatrix(n, 3);

        for (int i = 0; i < n; i++) {
            Vec3d point = points.get(i);
            H.set(i, 0, point.x - centerX);
            H.set(i, 1, point.y - centerY);
            H.set(i, 2, point.z - centerZ);
        }

        SimpleMatrix HtH = H.transpose().mult(H);
        SimpleMatrix V = HtH.svd().getV();

        Vec3d normal = new Vec3d(V.get(0, 2), V.get(1, 2), V.get(2, 2));
        double d = -normal.dot(new Vec3d(centerX, centerY, centerZ));

        return new Plane(normal, d);
    }
}

