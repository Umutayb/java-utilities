package api_assured;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import utils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public abstract class ApiUtilities extends Caller {
    public static Properties properties = PropertyUtility.properties;
    public StringUtilities strUtils = new StringUtilities();
    /**
     * @deprecated This object utility is deprecated since version 1.4.0. "objectUtils" replaced by "reflection"
     */
    @Deprecated(since = "1.4.0")
    public ReflectionUtilities objectUtils = new ReflectionUtilities();
    public ReflectionUtilities reflection = new ReflectionUtilities();
    public NumericUtilities numUtils = new NumericUtilities();
    public Printer log = new Printer(this.getClass());

    /**
     * Converts file to multipart
     *
     * @param file target file
     * @param name desired name for the multipart
     * @return returns the multipart
     */
    public MultipartBody.Part getMultipartFromFile(File file, String name) {
        RequestBody body = getRequestBodyFromFile(file);
        log.new Info("Creating multipart from " + file.getName() + " file");
        return MultipartBody.Part.createFormData(name, file.getName(), body);
    }

    /**
     * Converts file to multipart
     *
     * @param file target file
     * @param name desired name for the multipart
     * @param mediaType desired media type
     * @return returns the multipart
     */
    public MultipartBody.Part getMultipartFromFile(File file, String name, String mediaType) {
        RequestBody body = getRequestBodyFromFile(file, mediaType);
        log.new Info("Creating multipart from " + file.getName() + " file");
        return MultipartBody.Part.createFormData(name, file.getName(), body);
    }

    /**
     * Converts file to RequestBody
     *
     * @param file target file
     * @return returns the RequestBody
     */
    public RequestBody getRequestBodyFromFile(File file) {
        String mediaType;
        try {mediaType = Files.probeContentType(file.toPath());}
        catch (IOException e) {throw new RuntimeException(e);}
        return getRequestBodyFromFile(file, mediaType);
    }

    /**
     * Converts file to RequestBody
     *
     * @param file target file
     * @param mediaType desired media type
     * @return returns the RequestBody
     */
    public RequestBody getRequestBodyFromFile(File file, String mediaType) {
        log.new Info("Generating request body from " + file.getName() + " file");
        return RequestBody.create(file, MediaType.parse(mediaType));
    }
}