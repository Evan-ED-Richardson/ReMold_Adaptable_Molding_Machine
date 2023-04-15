package STL2GCODE.gcode;

import STL2GCODE.stl4j.Vec3d;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GCodeWriter {

    /**
     * Writes G-code for an adaptable molding machine based on a list of pin heights.
     *
     * @param pinHeights A list of Vec3d objects representing the X, Y, and Z coordinates of each pin.
     * @param fileName The name of the output file.
     */
    public static void writeGCode(List<Vec3d> pinHeights, String fileName) {
        try {
            FileWriter writer = null;
            try {
                writer = new FileWriter(fileName);
            } catch (IOException e) {
                System.out.println("Error initializing file writer");
            }

            writer.write("G28 X Y\n");

            double sum = 0;
            int count = 0;

            for (Vec3d pin: pinHeights) {
                if (pin.z != 0) {
                    count++;
                    sum += pin.z;
                }
            }

            double avg = sum / count;

            for (Vec3d pin : pinHeights) {
                if (pin.z == -1) {
                    writer.write("G0 X" + pin.x + " Y" + pin.y + "\n");
                    writer.write("G0 Z" + avg + "\n");
                    writer.write("G0 Z0\n");
                }
                else if (pin.z != 0) {
                    writer.write("G0 X" + pin.x + " Y" + pin.y + "\n");
                    writer.write("G0 Z" + pin.z + "\n");
                    writer.write("G0 Z0\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing G-code file");
        }
    }
}
