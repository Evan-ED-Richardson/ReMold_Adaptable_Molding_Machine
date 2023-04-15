package STL2GCODE.util;

import STL2GCODE.stl4j.Triangle;
import STL2GCODE.stl4j.Vec3d;
import STL2GCODE.util.DepthMapUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The TriangleFilterUtil class provides utility methods for applying various filters
 * to a list of triangles representing an STL part. These filters include translating
 * triangle coordinates to the first quadrant, making the part planar, and rotating the
 * part to optimize for small changes in z.
 */
public class TriangleFilterUtil {

    /**
     * Translates all triangle coordinates to the first quadrant by moving the
     * part such that its AABB's minimum coordinates are at the origin.
     *
     * @param triangles The list of triangles representing the STL part
     */
    public static void translateToFirstQuadrant(List<Triangle> triangles) {
        Vec3d minCoords = calculateAABBMin(triangles);
        Vec3d translation = new Vec3d(-minCoords.x, -minCoords.y, -minCoords.z);

        for (Triangle triangle : triangles) {
            triangle.translate(translation);
        }
    }

    /**
     * Makes a list of triangles planar by removing all faces with a positive or negative normal.
     *
     * @param triangles The list of triangles to make planar
     */
    public static void makePlanar(List<Triangle> triangles) {
        List<Triangle> toRemove = new ArrayList<>();
        for (Triangle t : triangles) {
            Vec3d normal = t.getNormal();
            if (normal.z > 0) {
                toRemove.add(t);
            }
        }
        triangles.removeAll(toRemove);
    }

    /**
     * Rotates the part to optimize for small changes in z. This method assumes
     * that the part is already translated to the first quadrant and planar.
     *
     * @param triangles The list of triangles representing the STL part
     * @param angle     The angle of rotation in radians
     */
    public static void rotateToOptimizeZ(List<Triangle> triangles, double angle) {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        for (Triangle triangle : triangles) {
            Vec3d[] vertices = triangle.getVertices();
            for (int i = 0; i < vertices.length; i++) {
                double x = vertices[i].x;
                double y = vertices[i].y;

                // Apply rotation matrix
                double newX = x * cosAngle - y * sinAngle;
                double newY = x * sinAngle + y * cosAngle;

                vertices[i] = new Vec3d(newX, newY, vertices[i].z);
            }
        }
    }

    /**
     Calculates the minimum axis-aligned bounding box (AABB) of the given list of triangles.
     The AABB is the smallest cuboid that can contain all of the vertices of the triangles.

     @param triangles A non-empty list of triangles
     @return The minimum point of the AABB represented as a 3D vector.
     @throws IllegalArgumentException if the list of triangles is null or empty.
     */
    public static Vec3d calculateAABBMin(List<Triangle> triangles) {
        if (triangles == null || triangles.isEmpty()) {
            throw new IllegalArgumentException("The list of triangles cannot be null or empty.");
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;

        for (Triangle triangle : triangles) {
            Vec3d[] vertices = triangle.getVertices();
            for (Vec3d vertex : vertices) {
                minX = Math.min(minX, vertex.x);
                minY = Math.min(minY, vertex.y);
                minZ = Math.min(minZ, vertex.z);
            }
        }

        return new Vec3d(minX, minY, minZ);
    }
}
