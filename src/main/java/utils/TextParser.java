package utils;

import java.util.Scanner;

/**
 * A utility class for parsing substrings from text.
 *
 * @author Umut Ay Bora
 * @version 1.4.0 (Documented in 1.4.0, released in an earlier version)
 */
public class TextParser {

    public static void main(String[] args) {//Sample execution
        Printer log = new Printer(TextParser.class);
        Scanner scanner = new Scanner(System.in);
        log.important("Enter the input");
        String input = scanner.nextLine(); // "ajsKAagq5J3w._CoolButton-sg-j3yaG3 a3TGb"
        log.important("Enter the first keyword"); //._
        String initialKeyword = scanner.nextLine();
        log.important("Enter the final keyword"); // -
        String finalKeyword = scanner.nextLine();
        scanner.close();
        log.plain(parse(initialKeyword,finalKeyword,input));
    }

    /**
     * Parses a substring from the input string between two keywords.
     *
     * @param initialKeyword the keyword marking the beginning of the substring, or null if the substring should start from the beginning of the input string
     * @param finalKeyword the keyword marking the end of the substring, or null if the substring should end at the end of the input string
     * @param input the input string to be parsed
     * @return the substring between the two keywords, or null if either keyword is not found in the input string
     */
    public static String parse(String initialKeyword, String finalKeyword, String input){
        int firstIndex = 0;
        Scanner scanner = new Scanner(input);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (initialKeyword != null)
                firstIndex = line.indexOf(initialKeyword) + initialKeyword.length();
            if (initialKeyword != null && finalKeyword != null){
                //This is the case where the string will be cut from both sides
                if (line.contains(initialKeyword) && line.contains(finalKeyword)){
                    final int lastIndex = line.indexOf(finalKeyword);
                    scanner.close();
                    return line.substring(firstIndex, lastIndex);
                }
            }
            else if (initialKeyword != null){
                //This is the case where only a single side of the string will be cut (left side)
                if (line.contains(initialKeyword)){
                    scanner.close();
                    return line.substring(firstIndex);
                }
            }
            else if (finalKeyword != null){
                //This is the case where only a single side of the string will be cut (right side)
                if (line.contains(finalKeyword)){
                    final int lastIndex = line.indexOf(finalKeyword);
                    scanner.close();
                    return line.substring(0, lastIndex);
                }
            }
        }
        scanner.close();
        return null;
    }
}
