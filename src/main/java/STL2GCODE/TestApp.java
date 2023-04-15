package STL2GCODE;

import STL2GCODE.stl4j.STLParser;
import STL2GCODE.stl4j.Triangle;
import STL2GCODE.stl4j.Vec3d;
import STL2GCODE.util.DepthMapUtil;
import STL2GCODE.util.TriangleFilterUtil;
import STL2GCODE.gcode.GCodeWriter;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestApp {

    public static void main(String[] args) {
        File f = askForFile();
        if(f == null){
            // canceled by user
            Logger.getLogger(STLParser.class.getName()).log(Level.WARNING, "Canceled by user");
            System.exit(0);
        }

        try {
            // Step 2: Read the STL file and parse the triangles
            List<Triangle> triangles = STLParser.parseSTLFile(f.toPath());

            // Step 3: Translate triangles to the first quadrant
            TriangleFilterUtil.translateToFirstQuadrant(triangles);

            // Step 4: Make triangles planar
            TriangleFilterUtil.makePlanar(triangles);

            // Step 5: Rotate triangles to optimize the Z-axis (optional)
            // You can ask the user for the rotation angle or use a default value
            double rotationAngle = 0.0;
            TriangleFilterUtil.rotateToOptimizeZ(triangles, rotationAngle);

            // Step 6: Generate depth map from the triangles
            double[][] depthMap = DepthMapUtil.generateDepthMap(triangles, 12, 462, 25, 475);

            // Step 7: Calculate pin heights from the depth map
            List<Vec3d> pinHeights = DepthMapUtil.calculatePinHeights(depthMap, 12, 462, 25, 475);

            // Step 8: Generate G-code from the pin heights
            GCodeWriter.writeGCode(pinHeights, "test");

//            // Step 9: Save the G-code to a file
//            System.out.print("Please enter the path to save the G-code file: ");
//            String outputFilePath = scanner.nextLine();
//            GCodeWriter.saveGCodeToFile(gcode, new File(outputFilePath));
//
//            System.out.println("G-code saved to: " + outputFilePath);

        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }

    private static File askForFile(){
        JFileChooser jfc = new JFileChooser();
        int action = jfc.showOpenDialog(null);
        if(action != JFileChooser.APPROVE_OPTION){
            return null;
        }
        return jfc.getSelectedFile();
    }
}
