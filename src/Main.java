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
         * inputFilePath is the path to the input file. It can be either BINARY or in NORMAL txt format.
         * If the input file is in binary format, it will be converted to text format and saved to convertedTextFilePath.
         * If the input file is in text format, it will be read directly, but you HAVE TO set isBinaryFile to false.
         *
         */
        String inputFilePath = "src/complexity/homeWork04InBinary.txt";
        boolean isBinaryFile = true;

        // Path to save the converted text file, if the input file is in binary format:
        String convertedTextFilePath = "src/complexity/homeWork03BINARYtoTXT.txt";
        Turing machine;
        List<Turing.Output> outputs;


        if (isBinaryFile) {
            // Convert binary file to text
            String binaryCode = new String(Files.readAllBytes(Paths.get(inputFilePath)));
            String turingMachineText = BinaryTSConverter.convertBinaryCodeToTuringStructure(binaryCode);
            Files.write(Paths.get(convertedTextFilePath), turingMachineText.getBytes(), StandardOpenOption.CREATE);
            inputFilePath = convertedTextFilePath;
            System.out.println("Turing Machine Structure converted from Binary Code to Text has been saved to: " + convertedTextFilePath);
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