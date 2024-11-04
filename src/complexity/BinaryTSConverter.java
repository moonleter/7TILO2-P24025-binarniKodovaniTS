package complexity;

import java.io.*;

/**
 * https://cryptii.com/pipes/binary-decoder
 */
public class BinaryTSConverter { //TODO send

    public static void convertTextToBinary(String inputFilePath, String outputFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             FileOutputStream fos = new FileOutputStream(outputFilePath);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {

            int charRead;
            StringBuilder binaryString = new StringBuilder();

            // Read the text file character by character
            while ((charRead = reader.read()) != -1) {
                // Convert each character to a binary string
                String binary = String.format("%8s", Integer.toBinaryString(charRead)).replace(' ', '0');
                binaryString.append(binary);
            }

            // Write the binary string to the output file
            writer.write(binaryString.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}