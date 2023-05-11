package api_assured;

import api_assured.exceptions.FailedCallException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.json.simple.JSONObject;
import org.junit.Assert;
import records.Pair;
import retrofit2.Call;
import retrofit2.Response;
import utils.FileUtilities;
import utils.Printer;
import utils.PropertyUtility;
import utils.StringUtilities;
import java.io.IOException;
import java.util.Arrays;

import static utils.StringUtilities.Color.*;

/**
 * This abstract class represents a caller that performs API calls, logs the results and returns objects in response bodies.
 * It contains methods for performing and getting responses from API calls, as well as a method for printing the response body.
 * It also has a static boolean variable for keeping logs, an ObjectMapper for JSON serialization and deserialization,
 * a StringUtilities object for string manipulation, and a Printer object for logging.
 *
 * @author Umut Ay Bora
 * @version 1.4.0 (Documented in 1.4.0, released in an earlier version)
 */
@SuppressWarnings("unused")
public abstract class Caller {

    /**
     * A static boolean variable that determines whether logs should be kept for API calls.
     */
    static boolean keepLogs;

    /**
     * An ObjectMapper object for JSON serialization and deserialization.
     */
    static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * A StringUtilities object for string manipulation.
     */
    static StringUtilities strUtils = new StringUtilities();

    /**
     * A Printer object for logging.
     */
    static Printer log = new Printer(Caller.class);

    /**
     * Constructs a Caller object and initializes the ObjectMapper object and the keepLogs variable.
     */
    public Caller(){
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        keepLogs = Boolean.parseBoolean(PropertyUtility.properties.getProperty("keep-api-logs", "true"));
    }

    /**
     * Performs an API call and logs the results.
     *
     * @param call the Call object representing the API call
     * @param strict a boolean indicating whether the call should be strict (i.e. throw an exception if the response is not successful)
     * @param printBody a boolean indicating whether the response body should be printed
     * @return the Model object representing the response body
     * @throws FailedCallException if the call is strict and the response is not successful
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    protected static <SuccessModel, ErrorModel, ResponseType> ResponseType perform(
            Call<SuccessModel> call,
            Boolean strict,
            Boolean printBody,
            Class<ErrorModel>... errorModels){

        String serviceName = getRequestMethod();
        if (keepLogs)
            log.info("Performing " +
                    strUtils.markup(PALE, call.request().method()) +
                    " call for '" +
                    strUtils.markup(PALE, serviceName) +
                    "' service on url: " + call.request().url()
            );
        try {
            Response<SuccessModel> response = call.execute();

            if (response.isSuccessful()){
                if (keepLogs) log.success("The response code is: " + response.code());
                if (response.message().length()>0 && keepLogs) log.info(response.message());
                if (printBody) printBody(response);
                return (ResponseType) response.body();
            }
            else{
                log.warning("The response code is: " + response.code());
                if (response.message().length()>0) log.warning(response.message());
                log.warning(response.raw().toString());
                String errorString = getErrorBody(response);
                if (printBody) printBody(response);
                if (strict) throw new FailedCallException(
                        "The strict call performed for " + serviceName + " service returned response code " + response.code()
                );
                else {
                    ResponseType result;

                    for (Class<ErrorModel> errorModel:errorModels) {
                        try {return (ResponseType) objectMapper.readValue(errorString, errorModel);}
                        catch (JsonProcessingException ignored){}
                    }
                    return null;
                }
            }
        }
        catch (IOException exception) {
            if (strict){
                log.error(exception.getLocalizedMessage(), exception);
                throw new FailedCallException("The call performed for " + serviceName + " has failed.");
            }
            else return null;
        }
    }

    /**
     * Gets the response from an API call and logs the results.
     *
     * @param call the Call object representing the API call
     * @param strict a boolean indicating whether the call should be strict (i.e. throw an exception if the response is not successful)
     * @param printBody a boolean indicating whether the response body should be printed
     * @return the Response object representing the API response
     * @throws FailedCallException if the call is strict and the response is not successful
     */
    protected static <Model> Response<Model> getResponse(Call<Model> call, Boolean strict, Boolean printBody){
        String serviceName = getRequestMethod();
        if (keepLogs)
            log.info("Performing " +
                    strUtils.markup(PALE, call.request().method()) +
                    " call for '" +
                    strUtils.markup(PALE, serviceName) +
                    "' service on url: " + call.request().url()
            );
        try {
            Response<Model> response = call.execute();

            if (response.isSuccessful()){
                if (keepLogs) log.success("The response code is: " + response.code());
                if (response.message().length()>0 && keepLogs) log.info(response.message());
                if (printBody) printBody(response);
            }
            else{
                log.warning("The response code is: " + response.code());
                if (response.message().length()>0) log.warning(response.message());
                log.warning(response.raw().toString());
                if (printBody) printBody(response);
                if (strict) throw new FailedCallException(
                        "The strict call performed for " + serviceName + " service returned response code " + response.code()
                );
            }
            return response;
        }
        catch (IOException exception) {
            if (strict){
                log.error(exception.getLocalizedMessage(), exception);
                throw new FailedCallException("The call performed for " + serviceName + " has failed.");
            }
            else return null;
        }
    }

    /**
     * Gets the response from an API call and logs the results.
     *
     * @param call the Call object representing the API call
     * @param strict a boolean indicating whether the call should be strict (i.e. throw an exception if the response is not successful)
     * @param printBody a boolean indicating whether the response body should be printed
     * @return the Response object representing the API response
     * @throws FailedCallException if the call is strict and the response is not successful
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    protected static <SuccessModel, ErrorModel> Pair<Response<SuccessModel>, ErrorModel> getResponse(
            Call<SuccessModel> call,
            Boolean strict,
            Boolean printBody,
            Class<ErrorModel>... errorModels){

        String serviceName = getRequestMethod();
        if (keepLogs)
            log.info("Performing " +
                    strUtils.markup(PALE, call.request().method()) +
                    " call for '" +
                    strUtils.markup(PALE, serviceName) +
                    "' service on url: " + call.request().url()
            );
        try {
            Response<SuccessModel> response = call.execute();

            if (response.isSuccessful()){
                if (keepLogs) log.success("The response code is: " + response.code());
                if (response.message().length()>0 && keepLogs) log.info(response.message());
                if (printBody) printBody(response);
                return (Pair<Response<SuccessModel>, ErrorModel>) new Pair<>(response, response.body());
            }
            else{
                log.warning("The response code is: " + response.code());
                if (response.message().length()>0) log.warning(response.message());
                log.warning(response.raw().toString());
                String errorString = getErrorBody(response);
                if (printBody) printBody(response);
                if (strict) throw new FailedCallException(
                        "The strict call performed for " + serviceName + " service returned response code " + response.code()
                );
                else
                    for (Class<ErrorModel> errorModel:errorModels)
                        return new Pair<>(
                                response,
                                objectMapper.readValue(errorString, errorModel)
                        );

            }
            return null;
        }
        catch (IOException exception) {
            if (strict){
                log.error(exception.getLocalizedMessage(), exception);
                throw new FailedCallException("The call performed for " + serviceName + " has failed.");
            }
            else return null;
        }
    }

    static ResponseBody buffer(final ResponseBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.source().readAll(buffer);
        return ResponseBody.create(body.contentType(), body.contentLength(), buffer);
    }

    /**
     * Performs an API call and logs the results (deprecated).
     *
     * @param call the Call object representing the API call
     * @param strict a boolean indicating whether the call should be strict (i.e. throw an exception if the response is not successful)
     * @param printBody a boolean indicating whether the response body should be printed
     * @param serviceName the name of the service being called
     * @return the Model object representing the response body
     * @throws FailedCallException if the call is strict and the response is not successful
     * @deprecated This method is deprecated and will be removed in a future version.
     */
    @Deprecated
    protected static <Model> Model perform(Call<Model> call, Boolean strict, Boolean printBody, String serviceName) {
        log.info("Performing " + call.request().method() + " call for '" + serviceName + "' service on url: " + call.request().url());
        try {
            Response<Model> response = call.execute();

            if (printBody) printBody(response);

            if (response.isSuccessful()){
                if (response.message().length()>0) log.info(response.message());
                log.success("The response code is: " + response.code());
            }
            else{
                if (response.message().length()>0)
                    log.warning(response.message());
                log.warning("The response code is: " + response.code());
                log.warning(response.raw().toString());

                if (strict)
                    Assert.fail("The strict call performed for " + serviceName + " service returned response code " + response.code());
            }
            return response.body();
        }
        catch (IOException exception) {
            log.error(exception.getLocalizedMessage(),exception);
            Assert.fail("The call performed for " + serviceName + " failed for an unknown reason.");
        }
        return null;
    }

    /**
     * Gets the response from an API call and logs the results (deprecated).
     *
     * @param call the Call object representing the API call
     * @param strict a boolean indicating whether the call should be strict (i.e. throw an exception if the response is not successful)
     * @param printBody a boolean indicating whether the response body should be printed
     * @param serviceName the name of the service being called
     * @return the Response object representing the API response
     * @throws FailedCallException if the call is strict and the response is not successful
     * @deprecated This method is deprecated and will be removed in a future version.
     */
    @Deprecated
    protected static <Model> Response<Model> getResponse(Call<Model> call, Boolean strict, Boolean printBody, String serviceName) {
        log.info("Performing " + call.request().method() + " call for '" + serviceName + "' service on url: " + call.request().url());
        try {
            Response<Model> response = call.execute();

            if (printBody) printBody(response);

            if (response.isSuccessful()){
                if (response.message().length()>0)
                    log.info(response.message());
                log.success("The response code is: " + response.code());
            }
            else{
                if (response.message().length()>0)
                    log.warning(response.message());
                log.warning("The response code is: " + response.code());
                log.warning(response.raw().toString());

                if (strict)
                    Assert.fail("The strict call performed for " + serviceName + " service returned response code " + response.code());
            }
            return response;
        }
        catch (IOException exception) {
            if (strict){
                log.error(exception.getLocalizedMessage(), exception);
                Assert.fail("The call performed for " + serviceName + " failed for an unknown reason.");
            }
        }
        return null;
    }

    /**
     * Prints the response body of an API call.
     *
     * @param response the Response object representing the API response
     * @throws IOException if there is an error reading the response body
     */
    static <Model> void printBody(Response<Model> response) throws IOException {
        FileUtilities.Json convert = new FileUtilities.Json();
        String message = "The response body is: \n";
        try {
            if (response.body() != null) // Success response with a non-null body
                log.info(message + objectMapper.valueToTree(response.body()).toPrettyString());

            else if (response.errorBody() != null){ // Error response with a non-null body
                String errorMessage = response.errorBody().string();
                JSONObject responseJSON = convert.str2json(errorMessage);
                if (responseJSON!=null)
                    log.warning(message + objectMapper.valueToTree(responseJSON).toPrettyString());
                else // Success response with a non-null & non-json body
                    log.warning(message + errorMessage);
            }
            else log.info("The response body is empty."); // Success response with a null body
        }
        catch (IOException exception){log.warning(Arrays.toString(exception.getStackTrace()));}
    }

    /**
     * Prints the response body of an API call.
     *
     * @param response the Response object representing the API response
     * @throws IOException if there is an error reading the response body
     */
    static <Model> String getErrorBody(Response<Model> response) throws IOException {
        FileUtilities.Json convert = new FileUtilities.Json();
        String message = "The response body is: \n";
        try {
            if (response.body() != null) {// Success response with a non-null body
                log.info(message + objectMapper.valueToTree(response.body()).toPrettyString());
                return objectMapper.valueToTree(response.body()).toPrettyString();
            }

            else if (response.errorBody() != null){ // Error response with a non-null body
                String errorMessage = response.errorBody().string();
                JSONObject responseJSON = convert.str2json(errorMessage);
                if (responseJSON!=null)
                    log.warning(message + objectMapper.valueToTree(responseJSON).toPrettyString());
                else // Success response with a non-null & non-json body
                    log.warning(message + errorMessage);
                return errorMessage;
            }
            else log.info("The response body is empty."); // Success response with a null body
        }
        catch (IOException exception){log.warning(Arrays.toString(exception.getStackTrace()));}
        return null;
    }

    /**
     * Gets the name of the method that called the API.
     *
     * @return the name of the method that called the API
     */
    private static String getRequestMethod(){
        Throwable dummyException = new Throwable();
        StackTraceElement[] stackTrace = dummyException.getStackTrace();
        // LOGGING-132: use the provided logger name instead of the class name
        String method = stackTrace[0].getMethodName();
        // Caller will be the third element
        if( stackTrace.length > 2 ) {
            StackTraceElement caller = stackTrace[2];
            method = caller.getMethodName();
        }
        return method;
    }

    /**
     * Returns whether logs are being kept for API calls.
     *
     * @return a boolean indicating whether logs are being kept for API calls
     */
    public static boolean keepsLogs() {
        return keepLogs;
    }

    /**
     * Sets whether logs should be kept for API calls.
     *
     * @param keepLogs a boolean indicating whether logs should be kept for API calls
     */
    public static void keepLogs(boolean keepLogs) {
        Caller.keepLogs = keepLogs;
    }
}