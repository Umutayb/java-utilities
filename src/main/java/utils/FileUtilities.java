package utils;

import api_assured.exceptions.JavaUtilitiesException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class FileUtilities {

    StringUtilities strUtils = new StringUtilities();

    /**
     * Returns the absolute path of a file given its relative path.
     *
     * @param relativePath The relative path of the file.
     * @return The absolute path of the file.
     * @throws JavaUtilitiesException If the file is not found.
     */
    public String getAbsolutePath(String relativePath){
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
    public String getString(String directory, String fileName) {
        try {return new String(Files.readAllBytes(Paths.get(directory+"/"+fileName)));}
        catch (IOException exception){
            Assert.fail(strUtils.markup(YELLOW, fileName+" not found!"));
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
    public Optional<Boolean> createIfAbsent(String pathname){
        try {if (!verifyFilePresence(pathname)) return Optional.of(new File(pathname).createNewFile());}
        catch (IOException e) {throw new RuntimeException(e);}
        return Optional.empty();
    }

    /**
     * Writes a string to a file.
     *
     * @param classString The string to be written to the file.
     * @param className The name of the file to be written to.
     * @throws IOException If the file cannot be written to.
     */
    public void classWriter(String classString, String className) throws IOException {
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
    boolean deleteDirectory(File directoryToBeDeleted) {
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
    public boolean verifyFilePresence(String fileDirectory) {
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
        public File compress(String zipName, List<File> files, String extensionFilter) {
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
        public File compress(String zipName, File file, String extensionFilter) {
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
        public File compress(String zipName, File file) {return createZip(zipName, file);}

        /**
         * Compresses an array of files into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param files The array of files to be compressed.
         * @return The compressed zip file.
         */
        public File compress(String zipName, File[] files) {return createZip(zipName, List.of(files));}

        /**
         * Compresses a list of files into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param files The list of files to be compressed.
         * @return The compressed zip file.
         */
        public File compress(String zipName, List<File> files) {return createZip(zipName, files);}

        /**
         * Compresses all files in a directory into a zip file.
         *
         * @param zipName The name of the zip file to be created.
         * @param directory The directory where the files are located.
         * @return The compressed zip file.
         */
        public File compress(String zipName, String directory) {
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
        public File createZip(String zipName, File file){
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
        public File createZip(String zipName, List<File> files) {
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
                new FileUtilities().deleteDirectory(tmpPath.toFile());
            }
            catch (IOException e) {throw new RuntimeException(e);}
            return new File(zipName);
        }
    }

    public static class Excel {
        Printer log = new Printer(Excel.class);
        public Map<String, Map<String, Object>> getExcelList(String directory, String selector){
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
                            default -> log.new Warning("Empty cell labeled: " + labels.get(i));
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
        FileUtilities fileUtil = new FileUtilities();

        public void saveJson(JSONObject inputJson, String directory){
            try {

                FileWriter file = new FileWriter(directory);

                ObjectMapper mapper = new ObjectMapper();

                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inputJson);

                if(file.toString().isEmpty()) file.write(String.valueOf(json));
                else file.append(String.valueOf(json));

                file.close();

            }
            catch (Exception gamma){Assert.fail(String.valueOf(gamma));}
        }

        public JsonObject parseJsonFile(String directory) {
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

        public JSONObject parseJSONFile(String directory) {
            try {
                FileReader fileReader = new FileReader(directory);
                JSONParser jsonParser = new JSONParser();
                JSONObject object;
                object = (JSONObject) jsonParser.parse(fileReader);
                return object;
            }
            catch (IOException | ParseException e) {throw new RuntimeException(e);}
        }

        public JsonObject getJsonObject(JsonObject json, String key){

            JsonObject jsonObject = json.get(key).getAsJsonObject();

            assert jsonObject != null;

            return jsonObject;
        }

        public String getElementAttribute(JsonObject attributes, String attributeType){
            return attributes.get(attributeType).getAsString();
        }

        public JSONObject str2json(String inputString){
            JSONObject object = null;
            try {
                JSONParser parser = new JSONParser();
                object = (JSONObject) parser.parse(inputString);
            }
            catch (Exception gamma){
                //log.new Warning(gamma.fillInStackTrace());
            }
            return object;
        }

        public String formatJsonString(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Object jsonObject = mapper.readValue(json, Object.class);
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
            }
            catch (IOException e) {e.printStackTrace();}
            return null;
        }

    }
}
