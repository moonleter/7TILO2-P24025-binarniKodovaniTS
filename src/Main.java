import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import util.BinaryTSConverter;
import complexity.Turing;
import complexity.Turing.TuringException;

public class Main {
    public static void main(String[] args) throws Exception {
        /**
         *
         * inputFilePath is the path to the input file. It can be either BINARY or in NORMAL txt format. -> Change this path to the path of the file you want to test(homeWork04InBinary.txt or homeWork04InTxt.txt).
         * If the input file is in binary format, it will be converted to text format and saved to convertedTextFilePath.
         * If the input file is in text format, it will be read directly and converted to binary format and saved to convertedBinaryFilePath.
         * But you HAVE TO set isBinaryFile to false.
         *
         */
        String inputFilePath = "src/complexity/homeWork04InBinary.txt";
        boolean isBinaryFile = true;

        // Path to save the converted text file, if the input file is in binary format:
        String convertedTextFilePath = "src/complexity/homeWork03BINARYtoTXT.txt";
        // Path to save the converted binary file, if the input file is in text format:
        String convertedBinaryFilePath = "src/complexity/homeWork03TXTtoBINARY.txt";
        Turing machine;
        List<Turing.Output> outputs;


        if (isBinaryFile) {
            // Convert binary file to text
            String binaryCode = new String(Files.readAllBytes(Paths.get(inputFilePath)));
            String turingMachineText = BinaryTSConverter.convertBinaryCodeToTuringStructure(binaryCode);
            Files.write(Paths.get(convertedTextFilePath), turingMachineText.getBytes(), StandardOpenOption.CREATE);
            inputFilePath = convertedTextFilePath;
            System.out.println("Turing Machine Structure converted from Binary Code to Text has been saved to: " + convertedTextFilePath);
        } else {
            // Convert text file to binary
            String textCode = new String(Files.readAllBytes(Paths.get(inputFilePath)));
            String binaryCode = BinaryTSConverter.getEncodedTuringMachineStructure(textCode);
            Files.write(Paths.get(convertedBinaryFilePath), binaryCode.getBytes(), StandardOpenOption.CREATE);
            System.out.println("Turing Machine Structure converted from Text to Binary Code has been saved to: " + convertedBinaryFilePath);
        }

        try {
            machine = new Turing(inputFilePath);
            outputs = machine.run(true);
            System.out.println(outputs);

            if (!outputs.isEmpty()) {
                String specificOutput = outputs.get(outputs.size() - 1).toString();
                System.out.println("Final Output is: " + specificOutput);
            }
        } catch (IOException | TuringException e) {
            System.out.println(e);
        }
    }
}