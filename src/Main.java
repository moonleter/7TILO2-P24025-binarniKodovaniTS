
import java.io.IOException;
import java.util.List;

import complexity.BinaryTSConverter;
import complexity.Turing;
import complexity.Turing.TuringException;

public class Main {
    public static void main(String[] args) throws Exception {
        String fileName = "src//complexity//main-homeWork02.txt";
        Turing machine;
        List<Turing.Output> outputs;

        BinaryTSConverter.convertTextToBinary("src//complexity//main-homeWork02.txt", "src//complexity//main-homeWork02BINARY.txt");

        try {
            machine = new Turing(fileName);
            outputs = machine.run(true);
            System.out.println(outputs);

            if (!outputs.isEmpty()) {
                String specificOutput = outputs.getLast().tapes[machine.getTapesNumber() - 1];
                System.out.println("Final Output is: " + specificOutput);
            }

            System.out.println("The turing machine has " + machine.getTapesNumber() + " tapes");
            System.out.println("Time Complexity: " + machine.getStepCount());
            System.out.println("Space Complexity: " + machine.getTotalWrittenCells());
        } catch (IOException | TuringException e) {
            System.out.println(e);
        }
    }
}