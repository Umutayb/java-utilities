package api_assured;

import context.ContextStore;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.junit.Assert;
import retrofit2.Call;
import retrofit2.Response;
import utils.*;
import utils.reflection.ReflectionUtilities;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static utils.StringUtilities.*;
import static utils.reflection.ReflectionUtilities.getPreviousMethodName;
import static utils.reflection.ReflectionUtilities.iterativeConditionalInvocation;

@SuppressWarnings("unused")
public abstract class ApiUtilities extends Caller {

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
     * @param file      target file
     * @param name      desired name for the multipart
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
        try {
            mediaType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getRequestBodyFromFile(file, mediaType);
    }

    /**
     * Converts file to RequestBody
     *
     * @param file      target file
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
     * @param expectedCode     The expected HTTP response code to be matched.
     * @param call             The network call to monitor.
     * @param <SuccessModel>   The type of the expected response model.
     */
    public <SuccessModel> void monitorResponseCode(
            int timeoutInSeconds,
            int expectedCode,
            Call<SuccessModel> call
    ) {
        iterativeConditionalInvocation(
                timeoutInSeconds,
                () -> {
                    boolean condition;
                    Call<?> callClone = call.clone();
                    Response<?> response = getResponse(getPreviousMethodName(), callClone, false, false);
                    condition = response.code() == expectedCode;
                    if (condition) {
                        log.success("Status code verified as " + expectedCode + "!");
                        ContextStore.put("monitorResponseCodeResponse", response);
                    }
                    return condition;
                }
        );
    }

    /**
     * Monitors the response code of a network call within a specified time limit, print the response body of the last successful call.
     *
     * @param timeoutInSeconds The time limit (in seconds) for monitoring the response code.
     * @param expectedCode     The expected HTTP response code to be matched.
     * @param call             The network call to monitor.
     * @param <SuccessModel>   The type of the expected response model.
     * @param printLastCallBody If true, print the response body for successful call.
     */
    public <SuccessModel> Response<SuccessModel> getResponseForCode(
            int timeoutInSeconds,
            int expectedCode,
            Call<SuccessModel> call,
            boolean printLastCallBody
    ) {
        boolean codeMatch = iterativeConditionalInvocation(
                timeoutInSeconds,
                () -> responseCodeMatch(
                        getPreviousMethodName(),
                        expectedCode,
                        call,
                        false,
                        false,
                        printLastCallBody
                )
        );
        Assert.assertTrue("Response code did not match the expected code " + expectedCode + " within " + timeoutInSeconds + " seconds!", codeMatch);
        return ContextStore.get("monitorResponseCodeResponse");
    }

    /**
     * Monitors the response code of a network call within a specified time limit, not print the response body.
     *
     * @param timeoutInSeconds The time limit (in seconds) for monitoring the response code.
     * @param expectedCode     The expected HTTP response code to be matched.
     * @param call             The network call to monitor.
     * @param <SuccessModel>   The type of the expected response model.
     */
    public <SuccessModel> Response<SuccessModel> getResponseForCode(
            int timeoutInSeconds,
            int expectedCode,
            Call<SuccessModel> call
    ) {
        String serviceName = getPreviousMethodName();
        boolean codeMatch = iterativeConditionalInvocation(
                timeoutInSeconds,
                () -> responseCodeMatch(
                        serviceName,
                        expectedCode,
                        call,
                        false,
                        false,
                        false
                )
        );
        Assert.assertTrue("Response code did not match the expected code " + expectedCode + " within " + timeoutInSeconds + " seconds!", codeMatch);
        return ContextStore.get("monitorResponseCodeResponse");
    }

    /**
     * Monitors the response field value for compliance with the expected value, print the response body of the last successful call.
     *
     * @param timeoutInSeconds The time limit (in seconds) for monitoring the response code.
     * @param expectedValue    The expected value to be matched.
     * @param call             The network call to monitor.
     * @param <SuccessModel>   The type of the expected response model.
     * @param printLastCallBody If true, print the response body for successful call
     */
    public <SuccessModel> Response<SuccessModel> monitorFieldValueFromResponse(
            int timeoutInSeconds,
            String expectedValue,
            Call<SuccessModel> call,
            String fieldName,
            boolean printLastCallBody
    ) {
        String serviceName = getPreviousMethodName();
        boolean codeMatch = iterativeConditionalInvocation(
                timeoutInSeconds,
                () -> fieldValueMatch(
                        serviceName,
                        call,
                        false,
                        false,
                        fieldName,
                        expectedValue,
                        printLastCallBody
                )
        );
        Assert.assertTrue(highlighted(Color.BLUE, fieldName) + " did not match the expected value "
                + highlighted(Color.BLUE, expectedValue) + " within "
                + highlighted(Color.BLUE, String.valueOf(timeoutInSeconds)) + " seconds!", codeMatch);
        return ContextStore.get("monitorFieldValueResponse");
    }

    /**
     * Checks if the HTTP response code of a network call matches the expected code.
     *
     * @param <SuccessModel> The type of the expected response model.
     * @param serviceName The name of the service for identification purposes.
     * @param expectedCode The expected HTTP response code.
     * @param call The network call to inspect.
     * @param strict If true, performs strict checking of the response code.
     * @param printBody If true, prints the response body.
     * @param printLastCallBody If true, prints the response body of the last call.
     * @return True if the response code matches the expected code; otherwise, false.
     */

    public static <SuccessModel> boolean responseCodeMatch(String serviceName,
                                                           int expectedCode,
                                                           Call<SuccessModel> call,
                                                           Boolean strict,
                                                           Boolean printBody,
                                                           Boolean printLastCallBody) {
        Printer log = new Printer(ApiUtilities.class);
        boolean condition;
        Call<?> callClone = call.clone();
        Response<?> response = getResponse(serviceName, callClone, strict, printBody);
        condition = response.code() == expectedCode;
        if (condition) {
            if (printLastCallBody) {
                log.info("Response body: " + MappingUtilities.Json.getJsonStringFor(response.body()));
            }
            log.success("Status code verified as " + expectedCode + "!");
            ContextStore.put("monitorResponseCodeResponse", response);
        }
        return condition;
    }

    /**
     * Checks if the field value of the response body matches the expected value.
     *
     * @param <SuccessModel> The type of the expected response model.
     * @param serviceName The name of the service for identification purposes.
     * @param call The network call to inspect.
     * @param strict If true, performs strict checking of the response code.
     * @param printBody If true, prints the response body.
     * @param fieldName The name of the field to inspect.
     * @param expectedValue The expected field value to match.
     * @param printLastCallBody If true, prints the response body of the last call.
     * @return True if the field value matches the expected value; otherwise, false.
     */
    public static <SuccessModel> boolean fieldValueMatch(
            String serviceName,
            Call<SuccessModel> call,
            boolean strict,
            boolean printBody,
            String fieldName,
            String expectedValue,
            boolean printLastCallBody
    ) {
        Printer log = new Printer(ApiUtilities.class);
        boolean condition;
        Call<SuccessModel> callClone = call.clone();
        Response<SuccessModel> response = getResponse(serviceName, callClone, strict, printBody);
        SuccessModel responseBody = response.body();
        if (responseBody == null) return false;
        condition = ReflectionUtilities.getField(fieldName, responseBody).toString().equals(expectedValue);
        if (condition) {
            if (printLastCallBody) {
                log.info("Response body: " + MappingUtilities.Json.getJsonStringFor(response.body()));
            }
            log.success(fieldName + " is verified as " + expectedValue + "!");
            ContextStore.put("monitorFieldValueResponse", response);
        }
        return condition;
    }
}