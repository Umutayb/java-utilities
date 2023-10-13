package api_assured;

import context.ContextStore;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.junit.Assert;
import retrofit2.Call;
import retrofit2.Response;
import utils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Properties;

import static utils.ReflectionUtilities.getPreviousMethodName;
import static utils.ReflectionUtilities.iterativeConditionalInvocation;

public abstract class ApiUtilities extends Caller {
    public static Properties properties = PropertyUtility.getProperties();
    public StringUtilities strUtils = new StringUtilities();
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
        log.info("Creating multipart from " + file.getName() + " file");
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
        log.info("Creating multipart from " + file.getName() + " file");
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
        log.info("Generating request body from " + file.getName() + " file");
        return RequestBody.create(file, MediaType.parse(mediaType));
    }

    /**
     * Monitors the response code of a network call within a specified time limit.
     *
     * @param timeoutInSeconds The time limit (in seconds) for monitoring the response code.
     * @param expectedCode The expected HTTP response code to be matched.
     * @param call The network call to monitor.
     * @param <SuccessModel> The type of the expected response model.
     */
    public <SuccessModel> void monitorResponseCode(
            int timeoutInSeconds,
            int expectedCode,
            Call<SuccessModel> call
    ) {
        iterativeConditionalInvocation(
                timeoutInSeconds,
                ApiUtilities.class,
                "responseCodeMatch",
                getPreviousMethodName(),
                expectedCode,
                call,
                false,
                false
        );
    }

    /**
     * Monitors the response code of a network call within a specified time limit.
     *
     * @param timeoutInSeconds The time limit (in seconds) for monitoring the response code.
     * @param expectedCode The expected HTTP response code to be matched.
     * @param call The network call to monitor.
     * @param <SuccessModel> The type of the expected response model.
     */
    public <SuccessModel> Response<SuccessModel> getResponseForCode(
            int timeoutInSeconds,
            int expectedCode,
            Call<SuccessModel> call
    ) {
        boolean codeMatch = iterativeConditionalInvocation(
                timeoutInSeconds,
                ApiUtilities.class,
                "responseCodeMatch",
                getPreviousMethodName(),
                expectedCode,
                call,
                false,
                false
        );
        Assert.assertTrue("Response code did not match the expected code " + expectedCode + " within " + timeoutInSeconds + " seconds!", codeMatch);
        return ContextStore.get("monitorResponseCodeResponse");
    }

    /**
     * Checks if the HTTP response code of a network call matches the expected code.
     *
     * @param expectedCode The expected HTTP response code to match.
     * @param call The network call to inspect.
     * @param strict If true, perform strict checking of the response code.
     * @param printBody If true, print the response body.
     * @param <SuccessModel> The type of the expected response model.
     * @return True if the response code matches the expected code; otherwise, false.
     */
    public static <SuccessModel> boolean responseCodeMatch(
            String serviceName,
            int expectedCode,
            Call<SuccessModel> call,
            Boolean strict,
            Boolean printBody){
        Printer log = new Printer(ApiUtilities.class);
        boolean condition;
        Call<?> callClone = call.clone();
        Response<?> response = getResponse(serviceName, callClone, strict, printBody);
        condition = response.code() == expectedCode;
        if (condition) {
            log.success("Status code verified as " + expectedCode + "!");
            ContextStore.put("monitorResponseCodeResponse", response);
        }
        return condition;
    }
}