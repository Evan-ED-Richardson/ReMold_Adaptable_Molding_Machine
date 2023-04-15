package STL2GCODE.util;

import STL2GCODE.stl4j.Plane;
import STL2GCODE.stl4j.Triangle;
import STL2GCODE.stl4j.Vec3d;

import java.util.ArrayList;
import java.util.List;


/**
 * The DepthMapUtil class provides utility methods for processing and converting
 * a list of triangles representing an STL model into a depth map suitable for
 * an adaptable molding machine. The methods in this class make use of the Triangle
 * and Vec3d classes to perform operations on 3D models and facilitate the generation
 * of the depth map.
 *
 * This class includes methods for:
 * - Calculating the AABB (Axis-Aligned Bounding Box) of a list of triangles.
 * - Generating a 2D grid of points within the AABB, with a specified resolution.
 * - Interpolating the z-coordinate for each point within the AABB, based on the
 *   triangles that make up the 3D model.
 * - Creating a depth map representation of the 3D model by associating each grid
 *   point with its interpolated z-coordinate.
 */
public class DepthMapUtil {

    public static final int MATRIX_SIZE = 10;
    public static final int SAMPLE_POINTS_PER_TRIANGLE = 1000;

    /**
     * Generates a depth map from a list of triangles with a specified resolution.
     *
     * @param triangles A list of Triangle objects representing the surface.
     * @param xMin The minimum x value in the range of the depth map.
     * @param xMax The maximum x value in the range of the depth map.
     * @param yMin The minimum y value in the range of the depth map.
     * @param yMax The maximum y value in the range of the depth map.
     * @return A 2D double array representing the depth map.
     */
    public static double[][] generateDepthMap(List<Triangle> triangles, double xMin, double xMax, double yMin, double yMax) {
        double[][] depthMap = new double[MATRIX_SIZE][MATRIX_SIZE];

        double xStep = (xMax - xMin) / (MATRIX_SIZE - 1);
        double yStep = (yMax - yMin) / (MATRIX_SIZE - 1);

        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                double x = xMin + i * xStep;
                double y = yMin + j * yStep;
                Vec3d pinLocation = new Vec3d(x, y, 0);

                depthMap[i][j] = calculateZForPin(pinLocation, triangles);
            }
        }

        return depthMap;
    }

    private static List<Vec3d> generatePointsOnTriangleFaces(List<Triangle> triangles) {
        List<Vec3d> pointsOnTriangleFaces = new ArrayList<>();
        for (Triangle triangle : triangles) {
            pointsOnTriangleFaces.addAll(generatePointsOnTriangleFace(triangle, SAMPLE_POINTS_PER_TRIANGLE));
        }
        return pointsOnTriangleFaces;
    }

    private static List<Vec3d> generatePointsOnTriangleFace(Triangle triangle, int numPoints) {
        List<Vec3d> points = new ArrayList<>();
        Vec3d[] vertices = triangle.getVertices();

        for (int i = 0; i < numPoints; i++) {
            double r1 = Math.random();
            double r2 = Math.random();
            double sqrtR1 = Math.sqrt(r1);
            double x = (1 - sqrtR1) * vertices[0].x + (sqrtR1 * (1 - r2)) * vertices[1].x + (r2 * sqrtR1) * vertices[2].x;
            double y = (1 - sqrtR1) * vertices[0].y + (sqrtR1 * (1 - r2)) * vertices[1].y + (r2 * sqrtR1) * vertices[2].y;
            double z = (1 - sqrtR1) * vertices[0].z + (sqrtR1 * (1 - r2)) * vertices[1].z + (r2 * sqrtR1) * vertices[2].z;
            points.add(new Vec3d(x, y, z));
        }

        return points;
    }

    /**
     * Determines whether the point (x, y) lies inside the specified triangle.
     *
     * @param triangle The Triangle object to be tested.
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return True if the point is inside the triangle, false otherwise.
     */
    private static boolean isPointInsideTriangle(Triangle triangle, double x, double y) {
        Vec3d[] vertices = triangle.getVertices();
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

        return (u >= 0) && (v >= 0) && (u + v <= 1);
    }

    private static double calculateZForPin(Vec3d pinLocation, List<Triangle> triangles) {
        for (Triangle triangle : triangles) {
            Vec3d[] vertices = triangle.getVertices();
            double[] barycentricCoords = triangle.barycentricCoords(pinLocation.x, pinLocation.y);

            if (barycentricCoords != null) {
                double z = vertices[0].z * barycentricCoords[0]
                        + vertices[1].z * barycentricCoords[1]
                        + vertices[2].z * barycentricCoords[2];
                return z;
            }
        }

        List<Vec3d> pointsWithinAABB = new ArrayList<>();

        return -1;
    }


    /**
     * Calculates the pin heights from the provided depth map using the specified pin layout.
     *
     * @param depthMap The depth map represented as a 2D array of z heights.
     * @param xMin     The minimum x-coordinate of the pin layout.
     * @param xMax     The maximum x-coordinate of the pin layout.
     * @param yMin     The minimum y-coordinate of the pin layout.
     * @param yMax     The maximum y-coordinate of the pin layout.
     * @return A list of pin locations and heights represented as {@link Vec3d}.
     */
    public static List<Vec3d> calculatePinHeights(double[][] depthMap, double xMin, double xMax, double yMin, double yMax) {
        List<Vec3d> pinHeights = new ArrayList<>();

        double xStep = (xMax - xMin) / (MATRIX_SIZE - 1);
        double yStep = (yMax - yMin) / (MATRIX_SIZE - 1);

        for (int row = 0; row < MATRIX_SIZE; row++) {
            for (int col = 0; col < MATRIX_SIZE; col++) {
                double x = xMin + col * xStep;
                double y = yMin + row * yStep;
                double z = depthMap[row][col];
                Vec3d pin = new Vec3d(x, y, z);
                pinHeights.add(pin);
            }
        }

        return pinHeights;
    }
}
