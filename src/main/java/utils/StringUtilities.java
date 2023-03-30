package utils;

import context.ContextStore;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import java.text.Normalizer;
import java.util.*;

import static utils.PropertyUtility.properties;
import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class StringUtilities {   //Utility methods

    private final ObjectUtilities objectUtils = new ObjectUtilities();


    /**
     * Highlights a given text with a specified color (resets to plain)
     *
     * @param color target color
     * @param text target text
     */
    public String highlighted(Color color, CharSequence text){
        StringJoiner colorFormat = new StringJoiner("", color.getValue(), RESET.getValue());
        return String.valueOf(colorFormat.add(text));
    }

    /**
     * Highlights a given text with a specified color (resets to GRAY)
     *
     * @param color target color
     * @param text target text
     */
    public String markup(Color color, CharSequence text){
        StringJoiner colorFormat = new StringJoiner("", color.getValue(), GRAY.getValue());
        return String.valueOf(colorFormat.add(text));
    }

    public String reverse(String input){
        StringBuilder reversed = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {reversed.append(input.charAt(input.length() - i - 1));}
        return reversed.toString();
    }

    public String firstLetterCapped(String inputString){ //Capitalizes the first letter of the input string

        if (inputString!=null){
            String firstLetter = inputString.substring(0, 1);
            String remainingLetters = inputString.substring(1);
            firstLetter = firstLetter.toUpperCase();

            return firstLetter + remainingLetters;
        }
        else
            return null;
    }

    public String firstLetterDeCapped(String inputString){ //Capitalizes the first letter of the input string

        if (inputString!=null){
            String firstLetter = inputString.substring(0, 1);
            String remainingLetters = inputString.substring(1);
            firstLetter = firstLetter.toLowerCase();

            return firstLetter + remainingLetters;
        }
        else
            return null;
    }

    public String cleanText(String inputString){ //Cleans the input string of spaces, numbers etc.

        inputString = inputString
                .replaceAll("\\s", "")                    //Cleans spaces
                .replaceAll("[0-9]", "")                  //Cleans numbers
                .replaceAll("[-+^.,'&%/()=\"?!:;_*]*", "") //Cleans special characters
                .replaceAll("[^\\x00-\\x7F]", "");        //Cleans non english characters

        if (inputString.isEmpty())
            inputString = generateRandomString("element",4, true, false);

        return inputString;
    }

    public String normalize(String inputString){ //Replaces non english characters in input string

        return Normalizer
                .normalize(inputString, Normalizer.Form.NFD)
                .replaceAll("ç", "c")
                .replaceAll("ğ", "g")
                .replaceAll("ü", "u")
                .replaceAll("ş", "s")
                .replaceAll("ı", "i")
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public String shorten(String inputString, int length) { //Shortens string to the given length

        return inputString.substring(0, Math.min(inputString.length(), length));
    }

    //Generates random string according to the input rules
    public String generateRandomString(String keyword, int length, boolean useLetters, boolean useNumbers) {
        return keyword + RandomStringUtils.random(length, useLetters, useNumbers);
    }

    public int measureDistanceBetween(String input, String firstKeyword, String lastKeyword){

        // Remove any special chars from string
        final String strOnlyWords = input.replace(",", "").replace(".", "");

        final List<String> words = Arrays.asList(strOnlyWords.split(" "));
        final int index1 = words.indexOf(firstKeyword);
        final int index2 = words.indexOf(lastKeyword);
        int distance = -1;

        // Check index of two words
        if (index1 != -1 && index2 != - 1) {
            distance = index2 - index1;
        }

        return distance;
    }

    public Map<String, String> str2Map(String inputString){

        Map<String, String> outputMap = new HashMap<>();

        inputString = inputString.replaceAll("[{}]*", "");

        String[] pairs = inputString.split(",");

        for(String pair: pairs) {

            String[] keyValue = pair.split("=");

            try{
                if (keyValue[0] != null && keyValue[1] != null)
                    outputMap.put(keyValue[0].trim(), keyValue[1].trim());

                else if (keyValue[0] == null)
                    throw new Exception( "First value of this pair was found to be null");

                else throw new Exception( "Second value of this pair was found to be null");

            }
            catch (Exception gamma){throw new RuntimeException(GRAY+gamma.getMessage()+RESET,gamma);}
        }
        return outputMap;
    }

    /**
     * Custom context checker to re-format an input text or acquire context data
     *
     * @param input string that is to be context checked
     * @return value depending on the context (could be from ContextStore, Properties, Random etc)
     */
    public String contextCheck(@NotNull String input){
        TextParser parser = new TextParser();
        if (input.contains("CONTEXT-"))
            input = ContextStore.get(parser.parse("CONTEXT-", null, input)).toString();
        else if (input.contains("RANDOM-")){
            boolean useLetters = input.contains("LETTER");
            boolean useNumbers = input.contains("NUMBER");
            String keyword = "";
            if (input.contains("KEYWORD")) keyword = parser.parse("-K=", "-", input);
            int length = Integer.parseInt(parser.parse("-L=", null, input));
            input = generateRandomString(keyword, length, useLetters, useNumbers);
        }
        else if (input.contains("UPLOAD-")){
            String relativePath = parser.parse("UPLOAD-", null, input);
            input = new FileUtilities().getAbsolutePath(relativePath);
        }
        else if (input.contains("PROPERTY-")){
            String propertyName = parser.parse("PROPERTY-", null, input);
            input = properties.getProperty(propertyName, "NULL");
        }
        return input;
    }

    public enum Color {
        RESET( "\033[0m"),      // Text Reset

        // Preferred Colors
        CYAN("\033[0;36m"),     // CYAN
        RED("\033[1;31m"),      // RED
        GREEN("\033[1;32m"),    // GREEN
        YELLOW("\033[1;33m"),   // YELLOW
        PURPLE("\033[1;35m"),   // PURPLE
        GRAY("\033[1;90m"),     // GRAY
        BLUE("\033[1;34m"),     // BLUE
        PALE("\033[1;37m"),     // WHITE BOLD


        // Regular Colors
        BLACK_("\033[0;30m"),   // BLACK
        GRAY_("\033[1;90m"),    // GRAY
        RED_("\033[0;31m"),     // RED
        GREEN_("\033[0;32m"),   // GREEN
        YELLOW_("\033[0;33m"),  // YELLOW
        BLUE_("\033[0;34m"),    // BLUE
        PURPLE_("\033[0;35m"),  // PURPLE
        CYAN_("\033[0;36m"),    // CYAN
        WHITE_("\033[0;37m"),  // WHITE

        // Bold
        BLACK_BOLD("\033[1;30m"),  // BLACK
        RED_BOLD("\033[1;31m"),    // RED
        GREEN_BOLD("\033[1;32m"),  // GREEN
        YELLOW_BOLD("\033[1;33m"), // YELLOW
        BLUE_BOLD("\033[1;34m"),   // BLUE
        PURPLE_BOLD("\033[1;35m"), // PURPLE
        CYAN_BOLD("\033[1;36m"),   // CYAN
        WHITE_BOLD("\033[1;37m"),  // WHITE

        // Underline
        BLACK_UNDERLINED("\033[4;30m"),  // BLACK
        RED_UNDERLINED("\033[4;31m"),    // RED
        GREEN_UNDERLINED("\033[4;32m"),  // GREEN
        YELLOW_UNDERLINED("\033[4;33m"), // YELLOW
        BLUE_UNDERLINED("\033[4;34m"),   // BLUE
        PURPLE_UNDERLINED("\033[4;35m"), // PURPLE
        CYAN_UNDERLINED("\033[4;36m"),   // CYAN
        WHITE_UNDERLINED("\033[4;37m"),  // WHITE

        // Background
        BLACK_BACKGROUND("\033[40m"),  // BLACK
        RED_BACKGROUND("\033[41m"),    // RED
        GREEN_BACKGROUND("\033[42m"),  // GREEN
        YELLOW_BACKGROUND("\033[43m"), // YELLOW
        BLUE_BACKGROUND("\033[44m"),   // BLUE
        PURPLE_BACKGROUND("\033[45m"), // PURPLE
        CYAN_BACKGROUND("\033[46m"),   // CYAN
        WHITE_BACKGROUND("\033[47m"),  // WHITE

        // High Intensity
        BLACK_BRIGHT("\033[0;90m"),  // BLACK
        RED_BRIGHT("\033[0;91m"),    // RED
        GREEN_BRIGHT ("\033[0;92m"), // GREEN
        YELLOW_BRIGHT("\033[0;93m"), // YELLOW
        BLUE_BRIGHT("\033[0;94m"),   // BLUE
        PURPLE_BRIGHT("\033[0;95m"), // PURPLE
        CYAN_BRIGHT("\033[0;96m"),   // CYAN
        WHITE_BRIGHT("\033[0;97m"),  // WHITE

        BLACK_BOLD_BRIGHT("\033[1;90m"),    // BLACK
        RED_BOLD_BRIGHT("\033[1;91m"),      // RED
        GREEN_BOLD_BRIGHT("\033[1;92m"),    // GREEN
        YELLOW_BOLD_BRIGHT("\033[1;93m"),   // YELLOW
        BLUE_BOLD_BRIGHT("\033[1;94m"),     // BLUE
        PURPLE_BOLD_BRIGHT("\033[1;95m"),   // PURPLE
        CYAN_BOLD_BRIGHT("\033[1;96m"),     // CYAN
        WHITE_BOLD_BRIGHT("\033[1;97m"),    // WHITE

        BLACK_BACKGROUND_BRIGHT("\033[0;100m"), // BLACK
        RED_BACKGROUND_BRIGHT("\033[0;101m"),   // RED
        GREEN_BACKGROUND_BRIGHT("\033[0;102m"), // GREEN
        YELLOW_BACKGROUND_BRIGHT("\033[0;103m"),// YELLOW
        BLUE_BACKGROUND_BRIGHT("\033[0;104m"),  // BLUE
        PURPLE_BACKGROUND_BRIGHT("\033[0;105m"),// PURPLE
        CYAN_BACKGROUND_BRIGHT("\033[0;106m"),  // CYAN
        WHITE_BACKGROUND_BRIGHT("\033[0;107m"); // WHITE

        private final String value;

        Color(String value){this.value = value;}

        public String getValue() {return value;}
    }
}