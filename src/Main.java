import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import complexity.BinaryTSConverter;
import complexity.Turing;
import complexity.Turing.TuringException;

public class Main {
    public static void main(String[] args) throws Exception {
        String inputFilePath = "src//complexity//homeWork03.txt";
        String binaryFilePath = "src//complexity//homeWork03BINARY.txt";
        String textFilePath = "src//complexity//homeWork03BINARYtoTXT.txt";
        Turing machine;
        List<Turing.Output> outputs;

        if (isBinaryFile(inputFilePath)) {
            // Convert binary file to text
            BinaryTSConverter.convertBinaryToTxt(inputFilePath, textFilePath);
            inputFilePath = textFilePath;
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

    private static boolean isBinaryFile(String filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        for (byte b : fileBytes) {
            if (b < 0x09 || (b > 0x0D && b < 0x20) || b > 0x7E) {
                return true;
            }
        }
        return false;
    }
}