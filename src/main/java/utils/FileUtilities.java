package utils;

import api_assured.exceptions.JavaUtilitiesException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import utils.mapping.MappingUtilities;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class FileUtilities {

    static Printer log = new Printer(FileUtilities.class);

    /**
     * Checks if a given string is a valid file path.
     *
     * @param path The string representing the file path to validate.
     * @return true if the string is a valid file path, false otherwise.
     */
    public static boolean isValidFilePath(String path) {
        if (path == null || path.isEmpty()) return false;

        try {
            Path filePath = Paths.get(path);
            filePath.toRealPath();
            return true;
        }
        catch (InvalidPathException | SecurityException | IOException e) {
            return false;
        }
    }

    /**
     * Retrieves the Base64-encoded string representation of an image file.
     *
     * @param image The image file to be encoded.
     * @return The Base64-encoded string representation of the image file.
     * @throws RuntimeException if an IOException occurs during file reading.
     */
    public static String getEncodedString(File image) {
        try {
            // Read the contents of the image file into a byte array
            byte[] fileContent = FileUtils.readFileToByteArray(image);
            // Encode the byte array to a Base64 string and return it
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            // If an IOException occurs during file reading, wrap it in a RuntimeException and throw
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves a Base64-encoded string as an image file in the specified directory.
     *
     * @param base64String The Base64-encoded string representing the image.
     * @param outputFile The file where the image should be saved.
     * @throws RuntimeException if an IOException occurs during file writing.
     */
    public static void saveDecodedImage(String base64String, File outputFile) {
        try {
            // Decode the Base64 string to a byte array
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            // Write the byte array to the specified file
            Files.write(outputFile.toPath(), imageBytes);
        } catch (IOException e) {
            // Wrap IOException in a RuntimeException and throw it
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the Base64-encoded string representation of an image file from the given file path.
     *
     * @param imagePath The file path of the image to be encoded.
     * @return The Base64-encoded string representation of the image file.
     */
    public static String getEncodedString(String imagePath) {
        // Delegate to the method that accepts a File object, passing a File object created from the provided file path
        return getEncodedString(new File(imagePath));
    }

    /**
     * Returns the absolute path of a file given its relative path.
     *
     * @param relativePath The relative path of the file.
     * @return The absolute path of the file.
     * @throws JavaUtilitiesException If the file is not found.
     */
    public static String getAbsolutePath(String relativePath){
        if (verifyFilePresence(relativePath)) {
            File file = new File(relativePath);
            return file.getAbsolutePath().replaceAll("#","%23");
        }
        else throw new JavaUtilitiesException("File not found @" + relativePath);
    }

    /**
     * Returns the contents of a file as a string.
     *
     * @param directory The directory where the file is located.
     * @param fileName The name of the file.
     * @return The contents of the file as a string.
     */
    public static String getString(String directory, String fileName) {
        return getString(directory+"/"+fileName);
    }

    /**
     * Returns the contents of a file as a string.
     *
     * @param directory The directory where the file is located.
     * @return The contents of the file as a string.
     */
    public static String getString(String directory) {
        try {return new String(Files.readAllBytes(Paths.get(directory)));}
        catch (IOException exception){
            Assert.fail(StringUtilities.markup(YELLOW, "File at '" + directory + "' not found!"));
            return null;
        }
    }

    /**
     * Creates a file if it does not exist.
     *
     * @param pathname The path of the file to be created.
     * @throws RuntimeException If the file cannot be created.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Optional<Boolean> createIfAbsent(String pathname){
        try {if (!verifyFilePresence(pathname)) return Optional.of(new File(pathname).createNewFile());}
        catch (IOException e) {throw new RuntimeException(e);}
        return Optional.empty();
    }

    /**
     * Saves a content to a file.
     *
     * @param directory The directory where the file should be saved.
     * @throws RuntimeException if an exception occurs while writing the file.
     */
    public static void saveFile(String content, String directory){
        try {
            FileWriter file = new FileWriter(directory);
            file.write(String.valueOf(content));
            file.close();
        }
        catch (Exception gamma){Assert.fail(String.valueOf(gamma));}
    }


    /**
     * Writes a string to a file.
     *
     * @param classString The string to be written to the file.
     * @param className The name of the file to be written to.
     * @throws IOException If the file cannot be written to.
     */
    public static void classWriter(String classString, String className) throws IOException {
        FileWriter file = new FileWriter("src/main/java/utils/classes/"+className+".java");
        if(file.toString().isEmpty())
            file.write(classString);
        else
            file.append(classString);
        file.close();
    }

    /**
     * Deletes a directory and all its contents.
     *
     * @param directoryToBeDeleted The directory to be deleted.
     * @return True if the directory was successfully deleted, false otherwise.
     */
    @SuppressWarnings("UnusedReturnValue")
    static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) for (File file : allContents) deleteDirectory(file);
        return directoryToBeDeleted.delete();
    }

    /**
     * Verifies the presence of a file at a given directory.
     *
     * @param fileDirectory The directory where the file should be located.
     * @return True if the file is present, false otherwise.
     */
    public static boolean verifyFilePresence(String fileDirectory) {
        boolean fileIsPresent = false;
        try {
            File file = new File(fileDirectory);
            fileIsPresent = file.exists();
        }
        catch (Exception gamma) {
            gamma.printStackTrace();
        }
        return fileIsPresent;
    }

    /**
     * Downloads PDF from url and returns a text of PDF.
     *
     * @param url The url which is used for downloading PDF.
     * @param fileDestinationPath The destination path where the PDF is downloaded.
     * @return The text of PDF.
     */
    public static String getPDFFileText(URL url, String fileDestinationPath) throws IOException {
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(fileDestinationPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
        }
        return parsePDFFileToText(fileDestinationPath);
    }

    /**
     * Returns a text of the PDF file.
     *
     * @param fileDestinationPath The destination path for the PDF file.
     * @return The text of PDF.
     */
    public static String parsePDFFileToText(String fileDestinationPath) throws IOException {
        File file = new File(fileDestinationPath);
        PDDocument document = Loader.loadPDF(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String invoiceText = stripper.getText(document);
        document.close();
        return invoiceText;
    }

    /**
     * A static subclass for handling zip-related operations.
     */
    public static class Zip {

        /**
         * Compresses all files in a directory with a given extension into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param directory The directory where the files are located.
         * @param extensionFilter The file extension to be compressed.
         * @return The compressed zip file.
         */
        public File compress(String zipName, String directory, String extensionFilter) {
            File screenshotsDirectory = new File(directory);
            File[] files = screenshotsDirectory.listFiles();
            List<File> toBeCompressed = new ArrayList<>();
            assert files != null;
            for (File file : files) {
                String mediaType;
                try {mediaType = Files.probeContentType(file.toPath());}
                catch (IOException e) {throw new RuntimeException(e);}
                if (mediaType != null && mediaType.contains(extensionFilter)){toBeCompressed.add(file);}
            }
            return createZip(zipName, toBeCompressed);
        }

        /**
         * Compresses an array of files with a given extension into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param files The array of files to be compressed.
         * @param extensionFilter The file extension to be compressed.
         * @return The compressed zip file.
         */
        public File compress(String zipName, File[] files, String extensionFilter) {
            assert files != null;
            List<File> toBeCompressed = new ArrayList<>();
            for (File file:files) {
                String mediaType;
                try {mediaType = Files.probeContentType(file.toPath());}
                catch (IOException e) {throw new RuntimeException(e);}
                if (mediaType != null && mediaType.contains(extensionFilter)){toBeCompressed.add(file);}
            }
            return createZip(zipName, toBeCompressed);
        }

        /**
         * Compresses a list of files with a given extension into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param files The list of files to be compressed.
         * @param extensionFilter The file extension to be compressed.
         * @return The compressed zip file.
         */
        public static File compress(String zipName, List<File> files, String extensionFilter) {
            assert files != null;
            List<File> toBeCompressed = new ArrayList<>();
            for (File file:files) {
                String mediaType;
                try {mediaType = Files.probeContentType(file.toPath());}
                catch (IOException e) {throw new RuntimeException(e);}
                if (mediaType != null && mediaType.contains(extensionFilter)){toBeCompressed.add(file);}
            }
            return createZip(zipName, toBeCompressed);
        }

        /**
         * Compresses a file with a given extension into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param file The file to be compressed.
         * @param extensionFilter The file extension to be compressed.
         * @return The compressed zip file.
         * @throws RuntimeException If the file does not contain the correct file extension.
         */
        public static File compress(String zipName, File file, String extensionFilter) {
            String mediaType;
            File zip;
            try {mediaType = Files.probeContentType(file.toPath());}
            catch (IOException e) {throw new RuntimeException(e);}
            if (mediaType != null && mediaType.contains(extensionFilter)){zip = createZip(zipName, file);}
            else throw new RuntimeException("File does not contain the correct file extension.");
            return zip;
        }

        /**
         * Compresses a file into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param file The file to be compressed.
         * @return The compressed zip file.
         */
        public static File compress(String zipName, File file) {return createZip(zipName, file);}

        /**
         * Compresses an array of files into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param files The array of files to be compressed.
         * @return The compressed zip file.
         */
        public static File compress(String zipName, File[] files) {return createZip(zipName, List.of(files));}

        /**
         * Compresses a list of files into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param files The list of files to be compressed.
         * @return The compressed zip file.
         */
        public static File compress(String zipName, List<File> files) {return createZip(zipName, files);}

        /**
         * Compresses all files in a directory into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param directory The directory where the files are located.
         * @return The compressed zip file.
         */
        public static File compress(String zipName, String directory) {
            File screenshotsDirectory = new File(directory);
            File[] files = screenshotsDirectory.listFiles();
            assert files != null;
            return createZip(zipName, List.of(files));
        }

        /**
         * Creates a zip file containing a single file.
         *
         * @param zipName The name of the zip file to be created.
         * @param file The file to be zipped.
         * @return The compressed zip file.
         * @throws RuntimeException If the file cannot be compressed.
         */
        public static File createZip(String zipName, File file){
            if (!zipName.contains(".zip")) zipName = zipName + ".zip";
            try {
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipName));
                FileInputStream in = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(file.getName()));
                byte[] b = new byte[1024];
                int count;
                while ((count = in.read(b)) > 0) out.write(b, 0, count);
                in.close();
                out.close();
            }
            catch (IOException e) {throw new RuntimeException(e);}
            return new File(zipName);
        }

        /**
         * Creates a zip file containing a list of files.
         *
         * @param zipName The name of the zip file to be created.
         * @param files The file list to be zipped.
         * @return The compressed zip file.
         * @throws RuntimeException If the file cannot be compressed.
         */
        public static File createZip(String zipName, List<File> files) {
            if (!zipName.contains(".zip")) zipName = zipName + ".zip";
            try {
                Path tmpPath = Path.of("temp");
                Files.createDirectory(tmpPath);
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipName));
                for (File file : files) {
                    out.putNextEntry(new ZipEntry(file.getName()));
                    FileInputStream in = new FileInputStream(file);
                    byte[] b = new byte[1024];
                    int count;
                    while ((count = in.read(b)) > 0) out.write(b, 0, count);
                    in.close();
                }
                out.close();
                FileUtilities.deleteDirectory(tmpPath.toFile());
            }
            catch (IOException e) {throw new RuntimeException(e);}
            return new File(zipName);
        }
    }

    public static class Excel {
        static Printer log = new Printer(Excel.class);

        /**
         * Retrieves a map of Excel sheet data.
         *
         * @param directory The directory containing the Excel file.
         * @param selector  The column label to use as the key for the resulting map.
         * @return A map containing the Excel sheet data, where each entry has a key of the value in the specified selector column,
         *         and a value of a map representing a single row of data, with keys representing column labels and values representing
         *         cell values.
         * @throws RuntimeException if an IOException occurs while reading the file.
         */
        public static Map<String, Map<String, Object>> getExcelList(String directory, String selector){
            Map<String, Map<String, Object>> excelMap = new HashMap<>();
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(Files.newInputStream(Paths.get(directory)));
                XSSFSheet sheet = workbook.getSheetAt(0);
                List<String> labels = new ArrayList<>();
                sheet.getRow(0).iterator().forEachRemaining((cell -> labels.add(cell.getStringCellValue())));
                FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
                for (int y = 0; y < sheet.getPhysicalNumberOfRows(); y++) {
                    Map<String, Object> rowMap = new HashMap<>();
                    for (int i = 0; i < labels.size(); i++) {
                        switch (formulaEvaluator.evaluateInCell(sheet.getRow(y).getCell(i)).getCellType()) {
                            case NUMERIC ->   //field that represents numeric cell type
                                    rowMap.put(labels.get(i), sheet.getRow(y).getCell(i).getNumericCellValue());
                            case STRING ->    //field that represents string cell type
                                    rowMap.put(labels.get(i), sheet.getRow(y).getCell(i).getStringCellValue());
                            default -> log.warning("Empty cell labeled: " + labels.get(i));
                        }
                    }
                    excelMap.put((String) rowMap.get(selector), rowMap);
                }
            }
            catch (IOException e) {throw new RuntimeException(e);}
            return excelMap;
        }
    }

    public static class Json {

        public JSONObject urlsJson = new JSONObject();
        public JSONObject notificationJson = new JSONObject();
        private final Printer log = new Printer(Json.class);
        private static final Gson gson = new Gson();

        /**
         * Saves a JSON object to a file.
         *
         * @param inputJson The JSON object to be saved.
         * @param directory The directory where the file should be saved.
         * @throws RuntimeException if an exception occurs while writing the file.
         */
        public static void saveJSON(JSONObject inputJson, String directory){
            try {

                FileWriter file = new FileWriter(directory);

                String JSON = MappingUtilities.Json.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inputJson);

                if(file.toString().isEmpty()) file.write(String.valueOf(JSON));
                else file.append(String.valueOf(JSON));

                file.close();

            }
            catch (Exception gamma){Assert.fail(String.valueOf(gamma));}
        }

        /**
         * Saves a Json object to a file.
         *
         * @param inputJson The JSON object to be saved.
         * @param directory The directory where the file should be saved.
         * @throws RuntimeException if an exception occurs while writing the file.
         */
        public static void saveJson(JsonObject inputJson, String directory){
            try {

                FileWriter file = new FileWriter(directory);
                ObjectMapper mapper = MappingUtilities.Json.mapper;
                mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

                String json = MappingUtilities.Json.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inputJson);

                if(file.toString().isEmpty()) file.write(String.valueOf(json));
                else file.append(String.valueOf(json));

                file.close();

            }
            catch (Exception gamma){Assert.fail(String.valueOf(gamma));}
        }

        /**
         * Parses a JSON file located at the given directory and returns it as a JsonObject.
         *
         * @param directory The directory where the JSON file is located.
         * @return The JsonObject representing the JSON file, or null if the file is not found.
         */
        public static JsonObject parseJsonFile(String directory) {
            try {
                JsonElement object;

                FileReader fileReader = new FileReader(directory);

                object = JsonParser.parseReader(fileReader);
                JsonObject jsonObject = (JsonObject) object;

                assert jsonObject != null;

                return jsonObject;
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        }

        /**
         * Parses a JSON file and returns its contents as a JSONObject.
         *
         * @param directory the path to the JSON file to be parsed
         * @return the contents of the JSON file as a JSONObject
         * @throws RuntimeException if an IOException or ParseException occurs during parsing
         */
        public static JSONObject parseJSONFile(String directory) {
            try {
                FileReader fileReader = new FileReader(directory);
                JSONParser jsonParser = new JSONParser();
                JSONObject object;
                object = (JSONObject) jsonParser.parse(fileReader);
                return object;
            }
            catch (IOException | ParseException e) {throw new RuntimeException(e);}
        }

        /**
         * Returns a JsonObject from the given JsonObject by the specified key.
         *
         * @param json the JsonObject to retrieve the nested JsonObject from
         * @param key the key of the nested JsonObject to retrieve
         * @return the nested JsonObject with the specified key
         * @throws NullPointerException if the JsonObject retrieved by the specified key is null
         */
        public static JsonObject getJsonObject(JsonObject json, String key){

            JsonObject jsonObject = json.get(key).getAsJsonObject();

            assert jsonObject != null;

            return jsonObject;
        }

        /**
         * Returns a String representation of the attribute value with the specified attribute type.
         *
         * @param attributes the JsonObject containing the attributes to retrieve from
         * @param attributeType the type of the attribute to retrieve
         * @return the value of the attribute with the specified type as a String
         */
        public static String getElementAttribute(JsonObject attributes, String attributeType){
            return attributes.get(attributeType).getAsString();
        }

        /**
         * Parses a given input string into a JSONObject.
         *
         * @param inputString the input string to be parsed into a JSONObject
         * @return the JSONObject representation of the input string
         */
        public static JSONObject str2JSON(String inputString){
            JSONObject object = null;
            try {
                JSONParser parser = new JSONParser();
                object = (JSONObject) parser.parse(inputString);
            }
            catch (Exception gamma){
                //log.warning(gamma.fillInStackTrace());
            }
            return object;
        }

        /**
         * Parses a given input string into a JsonObject.
         *
         * @param inputString the input string to be parsed into a JsonObject
         * @return the JsonObject representation of the input string
         */
        public static JsonElement str2json(String inputString){
            return JsonParser.parseString(inputString);
        }

        /**
         * Formats a given JSON string into a more human-readable form.
         *
         * @param json the JSON string to be formatted
         * @return the formatted JSON string
         */
        public static String formatJsonString(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Object jsonObject = mapper.readValue(json, Object.class);
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            }
            catch (IOException e) {e.printStackTrace();}
            return null;
        }

        /**
         * Converts a given input object to another type using Gson serialization/deserialization.
         *
         * @param <T> The target class type.
         * @param input The input object to be converted.
         * @param tClass The target class type to convert into.
         * @return An instance of the target class {@code T} populated with data from the input object, or null if conversion fails.
         */
        public static <T> T typeConversion(Object input, Class<T> tClass) {
            return gson.fromJson(getJsonString(input), tClass);
        }

        /**
         * Converts a given input object to its JSON string representation using Gson.
         *
         * @param input The input object to be serialized into JSON.
         * @return A JSON string representing the input object, or null if the input is null.
         */
        public static String getJsonString(Object input) {
            return gson.toJson(input);
        }
    }
}
