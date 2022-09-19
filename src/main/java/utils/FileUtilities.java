package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static resources.Colors.*;

public class FileUtilities {

    public static final Properties properties = new Properties();

    static {
        try {properties.load(new FileReader("src/test/resources/test.properties"));}
        catch (Exception exception) {exception.printStackTrace();}
    }

    public String getString(String directory, String fileName) {
        try {return new String(Files.readAllBytes(Paths.get(directory+"/"+fileName)));}
        catch (IOException exception){
            Assert.fail(YELLOW+fileName+" not found");
            return null;
        }
    }

    public void createIfAbsent(String pathname){
        try {if (!verifyFilePresence(pathname)) new File(pathname).createNewFile();}
        catch (IOException e) {throw new RuntimeException(e);}
    }

    public void classWriter(String classString, String className) throws IOException {
        FileWriter file = new FileWriter("src/main/java/utils/classes/"+className+".java");

        if(file.toString().isEmpty())
            file.write(classString);
        else
            file.append(classString);

        file.close();
    }

    public boolean verifyFilePresence(String fileDirectory) { //Verifies presence of a file at a given directory

        boolean fileIsPresent = false;

        try {
            File file = new File(fileDirectory);

            fileIsPresent = file.exists();

        } catch (Exception gamma) {

            gamma.printStackTrace();

        }
        return fileIsPresent;
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
                        switch(formulaEvaluator.evaluateInCell(sheet.getRow(y).getCell(i)).getCellType())
                        {
                            case NUMERIC:   //field that represents numeric cell type
                                rowMap.put(labels.get(i), sheet.getRow(y).getCell(i).getNumericCellValue());
                                break;
                            case STRING:    //field that represents string cell type
                                rowMap.put(labels.get(i), sheet.getRow(y).getCell(i).getStringCellValue());
                                break;
                            default:
                                log.new Warning("Empty cell labeled: " + labels.get(i));
                                break;
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

                //JSONParser parser = new JSONParser();

                //JSONObject object = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream("src/test/java/resources/database/"+"Getir"+".JSON")));

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
                JsonParser jsonParser = new JsonParser();
                JsonElement object;

                FileReader fileReader = new FileReader(directory);

                object = jsonParser.parse(fileReader);
                JsonObject jsonObject = (JsonObject) object;

                assert jsonObject != null;

                return jsonObject;
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        }

        public JSONObject parseJSONFile(String distance) throws IOException, ParseException {

            JSONParser jsonParser = new JSONParser();
            JSONObject object;

            FileReader fileReader = new FileReader(distance);

            object = (JSONObject) jsonParser.parse(fileReader);
            JSONObject jsonObject = object;

            assert jsonObject != null;

            return jsonObject;

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
