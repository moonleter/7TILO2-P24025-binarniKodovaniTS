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

    public static void convertBinaryToTxt(String inputFilePath, String outputFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             FileOutputStream fos = new FileOutputStream(outputFilePath);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos))) {

            StringBuilder binaryString = new StringBuilder();
            int charRead;

            // Read the binary file character by character
            while ((charRead = reader.read()) != -1) {
                binaryString.append((char) charRead);
            }

            // Convert the binary string to text
            String text = binaryStringToText(binaryString.toString());

            // Write the text to the output file
            writer.write(text);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String binaryStringToText(String binaryString) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binaryString.length(); i += 8) {
            String byteString = binaryString.substring(i, i + 8);
            int charCode = Integer.parseInt(byteString, 2);
            text.append((char) charCode);
        }
        return text.toString();
    }
}