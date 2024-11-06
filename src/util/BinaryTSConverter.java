package util;


public class BinaryTSConverter {

    public static String convertBinaryCodeToTuringStructure(String binaryCode) {
        StringBuilder text = new StringBuilder();
        binaryCode = binaryCode.replaceAll("\\s", ""); // Remove all whitespace
        for (int i = 0; i < binaryCode.length(); i += 8) {
            String byteString = binaryCode.substring(i, i + 8);
            int charCode = Integer.parseInt(byteString, 2);
            text.append((char) charCode);
        }
        return text.toString();
    }
}