package complexity;

import complexity.datastructure.Bulk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Turing {
    public static class Output {
        public final FINAL_STATE state;
        public final String[] tapes;
        public final int[] heads;

        private Output(FINAL_STATE state, String[] tapes, int[] heads) {
            this.state = state;
            this.tapes = tapes;
            this.heads = heads;
        }

        @Override
        public String toString() {
            return "{\n\tState: " + state + ",\n\t" + Arrays.toString(tapes) + "\n}";
        }
    }

    public enum FINAL_STATE {
        YES, NO, HALT;

        @Override
        public String toString() {
            return name().substring(0, 1);
        }
    }

    private static final char BLANK_SYMBOL = '#';
    private static final char INITIAL_SYMBOL = '#';
    private static final char RIGHT_DIRECTION = 'R';
    private static final char LEFT_DIRECTION = 'L';
    private static final char STOP_DIRECTION = 'S';
    private static final String INITIAL_STATE = "s";

    private final String input;
    private final int tapesNumber;
    private final Map<String, Map<String, Map<String, List<String>>>> relations = new HashMap<>();
    private int stepCount = 0;

    public Turing(String filePath) throws IOException, TuringException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        String initialInput = "";
        int[] tapesNum = {-1};
        boolean isFirstLine = true;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (isFirstLine) {
                initialInput = line;
                isFirstLine = false;
                continue;
            }
            if (line.isEmpty()) continue;
            parseTransition(line, tapesNum);
        }

        this.input = initialInput;
        if (tapesNum[0] < 1) throw new TuringException("Every Turing machine must have at least 1 tape");
        this.tapesNumber = tapesNum[0];
    }

    private void parseTransition(String line, int[] tapesNum) throws TuringException {
        String[] split = line.split(";", 3);

        // Ensure there are exactly three parts: current state, configuration, and transition
        if (split.length != 3) {
            throw new TuringException("Malformed input: check the following line: " + line);
        }

        String currentState = split[0].trim();
        String config = split[1].trim();
        String transition = split[2].trim();

        int tapeReadingsCount = parseConfiguration(config, line);
        if (tapesNum[0] == -1) {
            tapesNum[0] = tapeReadingsCount;
        } else if (tapesNum[0] != tapeReadingsCount) {
            throw new TuringException("Malformed input: the number of tapes must be consistent across instructions: " + line);
        }

        Map<String, Map<String, List<String>>> stateTransitions = relations.computeIfAbsent(currentState, k -> new HashMap<>());

        String[] parts = parseTransitionParts(transition, tapesNum[0], line);

        String nextState = parts[0].trim();
        List<String> actions = Arrays.asList(parts).subList(1, parts.length);

        Map<String, List<String>> configTransitions = stateTransitions.computeIfAbsent(config, k -> new HashMap<>());
        configTransitions.put(nextState, actions);
    }

    private String[] parseTransitionParts(String transition, int tapesCount, String line) throws TuringException {
        if (!transition.startsWith("(") || !transition.endsWith(")")) {
            throw new TuringException("Malformed input: transition must be enclosed in brackets: " + line);
        }

        String content = transition.substring(1, transition.length() - 1).trim();
        String[] parts = content.split("\\s*,\\s*");

        if (parts.length != 1 + 2 * tapesCount) {
            throw new TuringException("Malformed input: number of transition actions must match the number of tapes: " + line);
        }
        return parts;
    }

    private int parseConfiguration(String config, String line) throws TuringException {
        if (!config.startsWith("(") || !config.endsWith(")")) {
            throw new TuringException("Malformed input: the following configuration must be enclosed between two brackets: " + line);
        }

        String content = config.substring(1, config.length() - 1).trim();
        String[] tapes = content.split("\\s*,\\s*");
        return tapes.length;
    }

    public List<Output> run() throws TuringException {
        return run(input);
    }

    public List<Output> run(String input) throws TuringException {
        return run(input, false);
    }

    public List<Output> run(boolean optimize) throws TuringException {
        return run(input, optimize);
    }

    public List<Output> run(String input, boolean optimize) throws TuringException {
        String[] tapes = new String[tapesNumber];
        int[] heads = new int[tapesNumber];

        for (int i = 0; i < tapes.length; i++) {
            tapes[i] = INITIAL_SYMBOL + String.valueOf(BLANK_SYMBOL) + (i == 0 ? input : "");
            heads[i] = 0;
        }

        List<Output> output = new ArrayList<>();
        Set<Bulk> yetExecuted = optimize ? new HashSet<>() : null;
        run(tapes, heads, INITIAL_STATE, output, yetExecuted);
        return output;
    }

    private void run(String[] tapes, int[] heads, String state, List<Output> output, Set<Bulk> yetExecuted) throws TuringException {
        if (yetExecuted != null && yetExecuted.contains(new Bulk(state, tapes, heads))) return;

        if (yetExecuted != null) yetExecuted.add(new Bulk(state, tapes, heads));

        FINAL_STATE finalState = getFinalState(state);
        if (finalState != null) {
            output.add(new Output(finalState, tapes, heads));
            return;
        }

        Map<String, Map<String, List<String>>> config = relations.get(state);

        if (config == null) {
            throw new TuringException("No transitions defined for state " + state);
        }

        char[] curr = readCurrentSymbols(tapes, heads);
        String[] currStrings = new String[curr.length];
        for (int i = 0; i < curr.length; i++) {
            currStrings[i] = String.valueOf(curr[i]);
        }
        String conf = "(" + String.join(", ", currStrings) + ")";

        Map<String, List<String>> go = config.get(conf);

        if (go == null) {
            throw new TuringException("No transition defined for state " + state + " with configuration " + encodeCurrentConfiguration(conf));
        }

        for (String newState : go.keySet()) {
            List<String> configurations = go.get(newState);

            // Dynamically build configuration for multiple or single tape
            StringBuilder configBuilder = new StringBuilder();

            configBuilder.append("(");
            for (int i = 0; i < tapesNumber; i++) {
                configBuilder.append(configurations.get(i * 2)); // Symbol
                if (i < tapesNumber - 1) {
                    configBuilder.append(", ");
                }
            }
            configBuilder.append("); (");

            for (int i = 0; i < tapesNumber; i++) {
                configBuilder.append(configurations.get(i * 2 + 1)); // Direction
                if (i < tapesNumber - 1) {
                    configBuilder.append(", ");
                }
            }
            configBuilder.append(")");

            String formattedConfig = configBuilder.toString(); // Exclude newState

            processTransition(tapes, heads, newState, formattedConfig, output, yetExecuted);
        }

    }

    private FINAL_STATE getFinalState(String state) {
        return switch (state) {
            case "Y" -> FINAL_STATE.YES;
            case "N" -> FINAL_STATE.NO;
            case "H" -> FINAL_STATE.HALT;
            default -> null;
        };
    }

    private char[] readCurrentSymbols(String[] tapes, int[] heads) {
        char[] curr = new char[tapes.length];
        for (int i = 0; i < tapes.length; i++) {
            if (heads[i] >= tapes[i].length()) {
                tapes[i] += BLANK_SYMBOL;
            }
            curr[i] = tapes[i].charAt(heads[i]);
        }
        return curr;
    }

    private void processTransition(String[] tapes, int[] heads, String newState, String newConfig, List<Output> output, Set<Bulk> yetExecuted) throws TuringException {
        String[] newTapes = Arrays.copyOf(tapes, tapes.length);
        int[] newHeads = Arrays.copyOf(heads, heads.length);

        if (!newConfig.startsWith("(") || !newConfig.endsWith(")")) {
            throw new TuringException("Malformed configuration: " + newConfig);
        }

        String[] sections = newConfig.substring(1, newConfig.length() - 1).split("\\); \\(");
        if (sections.length != 2) {
            throw new TuringException("Configuration must contain exactly two parts: " + newConfig);
        }

        String[] symbols = sections[0].split("\\s*,\\s*");
        String[] directions = sections[1].split("\\s*,\\s*");

        if (symbols.length != tapes.length || directions.length != tapes.length) {
            throw new TuringException("Mismatch between number of tapes and actions in configuration: " + newConfig);
        }

        for (int i = 0; i < tapes.length; i++) {
            char newChar = symbols[i].charAt(0);
            char direction = directions[i].charAt(0);

            if (direction != RIGHT_DIRECTION && direction != LEFT_DIRECTION && direction != STOP_DIRECTION) {
                throw new TuringException("Invalid direction: " + direction);
            }

            if (newHeads[i] < 0 || newHeads[i] >= newTapes[i].length()) {
                throw new TuringException("Invalid head position for tape " + i + ": " + newHeads[i]);
            }
            newTapes[i] = newTapes[i].substring(0, newHeads[i]) + newChar + newTapes[i].substring(newHeads[i] + 1);

            moveHead(newHeads, i, direction);
        }

        stepCount++;
        run(newTapes, newHeads, newState, output, yetExecuted);
    }


    private void moveHead(int[] heads, int m, char direction) throws TuringException {
        if (direction == RIGHT_DIRECTION) {
            heads[m]++;
        } else if (direction == LEFT_DIRECTION) {
            if (heads[m] == 0) throw new TuringException("Cannot go before the universe!");
            heads[m]--;
        } else if (direction != STOP_DIRECTION) {
            throw new TuringException("Cannot understand the following direction: " + direction);
        }
    }

    private String encodeCurrentConfiguration(String conf) {
        return conf;
    }


    public int getStepCount() {
        return stepCount;
    }

    public static final class TuringException extends RuntimeException {
        public TuringException(String s) {
            super(s);
        }
    }
}