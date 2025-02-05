package api_assured;

import api_assured.exceptions.FailedCallException;
import com.fasterxml.jackson.core.JsonProcessingException;
import okio.Buffer;
import properties.PropertyUtilities;
import retrofit2.Call;
import retrofit2.Response;
import utils.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static utils.MappingUtilities.Json.*;
import static utils.reflection.ReflectionUtilities.getPreviousMethodName;
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
    protected static boolean keepLogs;

    /**
     * A Printer object for logging.
     */
    private static final Printer log = new Printer(Caller.class);

    /**
     * Constructs a Caller object and initializes the ObjectMapper object and the keepLogs variable.
     */
    public Caller(){
        keepLogs = Boolean.parseBoolean(PropertyUtilities.getProperty("keep-api-logs", "true"));
    }

    /**
     * Performs the given call and processes the response. This method provides advanced error handling capabilities.
     *
     * @param call The call to be executed. This is a retrofit2.Call object, which represents a request that has been prepared for execution.
     * @param strict If true, throws a FailedCallException when the call fails or the response is not successful. If false, returns null in these cases.
     * @param printBody If true, prints the body of the response. This may be useful for debugging purposes.
     * @param errorModels Varargs parameter. Each ErrorModel class is used to try to parse the error response if the call was not successful.
     *
     * @return A ResponseType object. If the call was successful, this is the body of the response. If the call was not successful and strict is false, this may be a parsed error response, or null if parsing the error response failed.
     *
     * @throws FailedCallException If strict is true, and the call failed or the response was not successful.
     *
     * @param <SuccessModel> The type of the successful response body.
     * @param <ReturnType> The type of the return value in this method. This is either SuccessModel or ErrorModel.
     */
    @SuppressWarnings("unchecked")
    protected static <SuccessModel, ReturnType> ReturnType perform(
            Call<SuccessModel> call,
            Boolean strict,
            Boolean printBody,
            Class<?>... errorModels){
        Response<?> response = call(call, strict, printBody, getPreviousMethodName());
        return response.isSuccessful() ? (ReturnType) response.body() : getErrorBody(response, errorModels);
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
        return call(call, strict, printBody, getPreviousMethodName());
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
    protected static <Model> Response<Model> getResponse(String serviceName, Call<Model> call, Boolean strict, Boolean printBody){
        return call(call, strict, printBody, serviceName);
    }

    /**
     * Executes the given call and returns a Pair containing the response and potential error model.
     * This method provides advanced error handling capabilities.
     *
     * @param call The call to be executed. This is a retrofit2.Call object, which represents a request that has been prepared for execution.
     * @param strict If true, throws a FailedCallException when the call fails or the response is not successful. If false, returns null in these cases.
     * @param printBody If true, prints the body of the response. This may be useful for debugging purposes.
     * @param errorModels Varargs parameter. Each ErrorModel class is used to try to parse the error response if the call was not successful.
     *
     * @return A Pair object with the SuccessModel response as the first element and ErrorModel as the second element. If the call was successful, the second element is null.
     * If the call was not successful and strict is false, the second element may be a parsed error response, or null if parsing the error response failed.
     *
     * @throws FailedCallException If strict is true and the call failed or the response was not successful.
     *
     * @param <SuccessModel> The type of the successful response body.
     * @param <ErrorModel> The type of the error response body.
     */
    protected static <SuccessModel, ErrorModel> ResponsePair<Response<SuccessModel>, ErrorModel> getResponse(
            Call<SuccessModel> call,
            Boolean strict,
            Boolean printBody,
            Class<?>... errorModels
    ){
        Response<SuccessModel> response = call(call, strict, printBody, getPreviousMethodName());
        return response.isSuccessful() ?
                new ResponsePair<>(response, null) :
                new ResponsePair<>(response, getErrorBody(response, errorModels));
    }

    /**
     * Clones and logs the given response.
     *
     * @param <T>         The type of the response body.
     * @param call        The original call to execute and log.
     * @param printBody   Flag to indicate whether the response body should be logged.
     * @return            A new cloned response object.
     */
    private static <T> Response<T> getResponse(Call<T> call, boolean printBody) throws IOException {
        Response<T> response = call.execute();
        if (response.isSuccessful()) {
            String contentType = response.headers().get("content-type");
            boolean printableResponse = contentType != null && contentType.contains("application/json");
            T body = response.body();
            if (keepLogs) log.success("The response code is: " + response.code());
            if (keepLogs && !response.message().isEmpty()) log.info(response.message());
            if (printBody && printableResponse) log.info("The response body is: \n" + getJsonString(body));
            return Response.success(body, response.raw());
        }
        else {
            log.warning("The response code is: " + response.code());
            if (response.message().length()>0) log.warning(response.message());
            if (response.errorBody() != null && printBody) {
                Object errorBody = getJsonString(getErrorObject(response, Object.class));
                String errorLog = errorBody.equals("null") ? "The error body is empty." : "The error body is: \n" + errorBody;
                log.warning(errorLog);
            }
            return Response.error(response.errorBody(), response.raw());
        }
    }

    /**
     * Extracts the error object from the given response and attempts to deserialize it into the specified model.
     * <p>
     * The method tries to read the error content from the response and then deserialize it into a generic model type.
     * If the deserialization fails or other issues occur while processing the error content, a runtime exception is thrown.
     * </p>
     *
     * @param <ErrorModel> The generic type representing the desired structure of the error object.
     * @param response The response containing the potential error data.
     * @return A deserialized error object instance of type {@code Model}.
     * @throws RuntimeException if there's an issue processing the error content or deserializing it.
     *
     * @see MappingUtilities.Json#fromJsonString(String, Class)
     */
    private static <ErrorModel> ErrorModel getErrorObject(Response<?> response, Class<ErrorModel> errorModel) throws JsonProcessingException {
        assert response.errorBody() != null;
        try (Buffer errorBuffer = response.errorBody().source().getBuffer().clone()) {
            String bodyString = errorBuffer.readString(StandardCharsets.UTF_8);
            if (!StringUtilities.isBlank(bodyString))
                return fromJsonString(bodyString, errorModel);
            else
                return null;
        }
    }

    /**
     * Logs the HTTP method, service name, and URL for a given call if logging is enabled.
     *
     * @param <T>          The type of the response body.
     * @param call         The call object containing request details.
     * @param serviceName  The name of the service being called.
     */
    private static <T> void printCallSpecifications(Call<T> call, String serviceName){
        if (keepLogs)
            log.info("Performing " +
                    StringUtilities.markup(PALE, call.request().method()) +
                    " call for '" +
                    StringUtilities.markup(PALE, serviceName) +
                    "' service on url: " + call.request().url()
            );
    }

    /**
     * Executes the given call, logs the response or error, and optionally throws an exception for non-successful responses.
     *
     * @param call         The call object to execute.
     * @param strict       Flag to indicate whether an exception should be thrown for non-successful responses.
     * @param printBody    Flag to indicate whether the response body should be logged.
     * @param serviceName  The name of the service being called.
     * @return             The response object from the executed call.
     * @throws RuntimeException      If there's an I/O error during call execution.
     * @throws FailedCallException   If the call is strict and the response is not successful.
     */
    private static <ResponseModel> Response<ResponseModel> call(
            Call<ResponseModel> call,
            boolean strict,
            boolean printBody,
            String serviceName){
        try {
            printCallSpecifications(call, serviceName);
            Response<ResponseModel> response = getResponse(call, printBody);
            if (strict && !Objects.requireNonNull(response).isSuccessful())
                throw new FailedCallException(
                        "The strict call performed for " + serviceName + " service returned response code " + response.code()
                );
            else return response;
        }
        catch (IOException e) {throw new RuntimeException(e);}
    }

    /**
     * Attempts to extract and deserialize the error model from the given response using the specified error models.
     * <p>
     * The method iterates through the provided error models, trying to deserialize the error content of the response
     * to each one until a successful deserialization is found or all error models have been tested.
     * If none of the error models match, a runtime exception is thrown.
     * </p>
     *
     * @param <ErrorModel> The generic type representing the expected error model structure.
     * @param response The response containing the potential error data.
     * @param errorModels Varargs array of error model classes to attempt deserialization.
     * @return A deserialized error model instance of type {@code ErrorModel} if a match is found.
     * @throws RuntimeException if none of the provided error models match the error content of the response.
     *
     * @see MappingUtilities.Json#fromJsonString(String, Class)
     * @see MappingUtilities.Json#getJsonStringFor(Object)
     * @see #getErrorObject(Response, Class)
     */
    @SuppressWarnings("unchecked")
    private static <ErrorModel> ErrorModel getErrorBody(Response<?> response, Class<?>... errorModels){
        for (Class<?> errorModel:errorModels){
            try {
                return (ErrorModel) fromJsonString(getJsonStringFor(getErrorObject(response, errorModel)), errorModel);
            }
            catch (JsonProcessingException ignored) {}
        }
        throw new RuntimeException("Error model(s) did not match the error body!");
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