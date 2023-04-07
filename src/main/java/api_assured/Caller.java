package api_assured;

import api_assured.exceptions.FailedCallException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.simple.JSONObject;
import org.junit.Assert;
import retrofit2.Call;
import retrofit2.Response;
import utils.FileUtilities;
import utils.Printer;
import utils.PropertyUtility;
import utils.StringUtilities;
import java.io.IOException;
import java.util.Arrays;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public abstract class Caller {

    static boolean keepLogs;
    static ObjectMapper objectMapper = new ObjectMapper();
    static StringUtilities strUtils = new StringUtilities();
    static Printer log = new Printer(Caller.class);

    public Caller(){
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        keepLogs = Boolean.parseBoolean(PropertyUtility.properties.getProperty("keep-api-logs", "true"));
    }

    protected static <Model> Model perform(Call<Model> call, Boolean strict, Boolean printBody){
        String serviceName = getRequestMethod();
        if (keepLogs)
            log.new Info("Performing " +
                    strUtils.markup(PALE, call.request().method()) +
                    " call for '" +
                    strUtils.markup(PALE, serviceName) +
                    "' service on url: " + call.request().url()
        );
        try {
            Response<Model> response = call.execute();

            if (response.isSuccessful()){
                if (keepLogs) log.new Success("The response code is: " + response.code());
                if (response.message().length()>0 && keepLogs) log.new Info(response.message());
                if (printBody) printBody(response);
            }
            else{
                log.new Warning("The response code is: " + response.code());
                if (response.message().length()>0) log.new Warning(response.message());
                log.new Warning(response.raw().toString());
                if (printBody) printBody(response);
                if (strict) throw new FailedCallException(
                        "The strict call performed for " + serviceName + " service returned response code " + response.code()
                );
            }
            return response.body();
        }
        catch (IOException exception) {
            if (strict){
                log.new Error(exception.getLocalizedMessage(), exception);
                throw new FailedCallException("The call performed for " + serviceName + " has failed.");
            }
            else return null;
        }
    }

    protected static <Model> Response<Model> getResponse(Call<Model> call, Boolean strict, Boolean printBody){
        String serviceName = getRequestMethod();
        if (keepLogs)
            log.new Info("Performing " +
                    strUtils.markup(PALE, call.request().method()) +
                    " call for '" +
                    strUtils.markup(PALE, serviceName) +
                    "' service on url: " + call.request().url()
            );
        try {
            Response<Model> response = call.execute();

            if (response.isSuccessful()){
                if (keepLogs) log.new Success("The response code is: " + response.code());
                if (response.message().length()>0 && keepLogs) log.new Info(response.message());
                if (printBody) printBody(response);
            }
            else{
                log.new Warning("The response code is: " + response.code());
                if (response.message().length()>0) log.new Warning(response.message());
                log.new Warning(response.raw().toString());
                if (printBody) printBody(response);
                if (strict) throw new FailedCallException(
                        "The strict call performed for " + serviceName + " service returned response code " + response.code()
                );
            }
            return response;
        }
        catch (IOException exception) {
            if (strict){
                log.new Error(exception.getLocalizedMessage(), exception);
                throw new FailedCallException("The call performed for " + serviceName + " has failed.");
            }
            else return null;
        }
    }

    @Deprecated
    protected static <Model> Model perform(Call<Model> call, Boolean strict, Boolean printBody, String serviceName) {
        log.new Info("Performing " + call.request().method() + " call for '" + serviceName + "' service on url: " + call.request().url());
        try {
            Response<Model> response = call.execute();

            if (printBody) printBody(response);

            if (response.isSuccessful()){
                if (response.message().length()>0) log.new Info(response.message());
                log.new Success("The response code is: " + response.code());
            }
            else{
                if (response.message().length()>0)
                    log.new Warning(response.message());
                log.new Warning("The response code is: " + response.code());
                log.new Warning(response.raw().toString());

                if (strict)
                    Assert.fail("The strict call performed for " + serviceName + " service returned response code " + response.code());
            }
            return response.body();
        }
        catch (IOException exception) {
            log.new Error(exception.getLocalizedMessage(),exception);
            Assert.fail("The call performed for " + serviceName + " failed for an unknown reason.");
        }
        return null;
    }

    @Deprecated
    protected static <Model> Response<Model> getResponse(Call<Model> call, Boolean strict, Boolean printBody, String serviceName) {
        log.new Info("Performing " + call.request().method() + " call for '" + serviceName + "' service on url: " + call.request().url());
        try {
            Response<Model> response = call.execute();

            if (printBody) printBody(response);

            if (response.isSuccessful()){
                if (response.message().length()>0)
                    log.new Info(response.message());
                log.new Success("The response code is: " + response.code());
            }
            else{
                if (response.message().length()>0)
                    log.new Warning(response.message());
                log.new Warning("The response code is: " + response.code());
                log.new Warning(response.raw().toString());

                if (strict)
                    Assert.fail("The strict call performed for " + serviceName + " service returned response code " + response.code());
            }
            return response;
        }
        catch (IOException exception) {
            if (strict){
                log.new Error(exception.getLocalizedMessage(), exception);
                Assert.fail("The call performed for " + serviceName + " failed for an unknown reason.");
            }
        }
        return null;
    }

    static <Model> void printBody(Response<Model> response) throws IOException {
        FileUtilities.Json convert = new FileUtilities.Json();
        String message = "The response body is: \n";
        try {
            if (response.body() != null) // Success response with a non-null body
                log.new Info(message + objectMapper.valueToTree(response.body()).toPrettyString());

            else if (response.errorBody() != null){ // Error response with a non-null body
                String errorMessage = response.errorBody().string();
                JSONObject responseJSON = convert.str2json(errorMessage);
                if (responseJSON!=null)
                    log.new Warning(message + objectMapper.valueToTree(responseJSON).toPrettyString());
                else // Success response with a non-null & non-json body
                    log.new Warning(message + errorMessage);
            }
            else log.new Info("The response body is empty."); // Success response with a null body
        }
        catch (IOException exception){log.new Warning(Arrays.toString(exception.getStackTrace()));}
    }

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

    public static boolean keepsLogs() {
        return keepLogs;
    }

    public static void keepLogs(boolean keepLogs) {
        Caller.keepLogs = keepLogs;
    }
}