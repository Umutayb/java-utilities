package api_assured;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.protobuf.ProtoConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.converter.wire.WireConverterFactory;
import utils.ObjectUtilities;
import utils.Printer;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static utils.FileUtilities.properties;

public class ServiceGenerator {

    static Headers headers = new Headers.Builder().build();
    static boolean printHeaders = Boolean.parseBoolean(properties.getProperty("log-headers", "true"));
    static boolean detailedLogging = Boolean.parseBoolean(properties.getProperty("detailed-logging", "false"));
    static boolean hostnameVerification = Boolean.parseBoolean(properties.getProperty("verify-hostname", "true"));

    String BASE_URL = "";
    private final Printer log = new Printer(ServiceGenerator.class);

    public ServiceGenerator(Headers headers, String BASE_URL) {
        this.BASE_URL = BASE_URL;
        setHeaders(headers);
    }

    public ServiceGenerator(Headers headers) {setHeaders(headers);}

    public ServiceGenerator(String BASE_URL) {this.BASE_URL = BASE_URL;}

    public ServiceGenerator(){}

    /**
     * Creates Retrofit Service.
     *
     * @param serviceClass Which service class (api data store) going to be used when creating Retrofit Service.
     * @return Created Retrofit Service.
     */
    public <S> S generate(Class<S> serviceClass) {

        if (BASE_URL.isEmpty()) BASE_URL = (String) new ObjectUtilities().getFieldValue("BASE_URL", serviceClass);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        HttpLoggingInterceptor headerInterceptor = new HttpLoggingInterceptor();

        if (detailedLogging){
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(headerInterceptor)
                .hostnameVerifier((hostname, session) -> !hostnameVerification)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor(chain -> {
                    Request request = chain.request().newBuilder().build();
                    request = request.newBuilder()
                            .header("Host", request.url().host())
                            .method(request.method(), request.body())
                            .build();
                    for (String header: headers.names()) {
                        if (!request.headers().names().contains(header)){
                            request = request.newBuilder()
                                    .addHeader(header, Objects.requireNonNull(headers.get(header)))
                                    .build();
                        }
                    }
                    if (request.body() != null) {
                        Boolean contentLength = Objects.requireNonNull(request.body()).contentLength()!=0;
                        Boolean contentType = Objects.requireNonNull(request.body()).contentType() != null;

                        if (contentLength && contentType)
                            request = request.newBuilder()
                                    .header(
                                            "Content-Length",
                                            String.valueOf(Objects.requireNonNull(request.body()).contentLength()))
                                    .header(
                                            "Content-Type",
                                            String.valueOf(Objects.requireNonNull(request.body()).contentType()))
                                    .build();
                    }
                    if (printHeaders)
                        log.new Info(("Headers(" + request.headers().size() + "): \n" + request.headers()).trim());
                    return chain.proceed(request);
                }).build();

        assert BASE_URL != null;
        @SuppressWarnings("deprecation")
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create()) //Deprecated
                .addConverterFactory(MoshiConverterFactory.create())
                .addConverterFactory(WireConverterFactory.create())
                .addConverterFactory(ProtoConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(serviceClass);
    }

    public static void setPrintHeaders(boolean printHeaders) {
        ServiceGenerator.printHeaders = printHeaders;
    }

    public static void setDetailedLogging(boolean detailedLogging) {
        ServiceGenerator.detailedLogging = detailedLogging;
    }

    public static void setHostnameVerification(boolean hostnameVerification) {
        ServiceGenerator.hostnameVerification = hostnameVerification;
    }

    public void setHeaders(Headers headers){this.headers = headers;}

    public static Headers getHeaders() {
        return headers;
    }

    public static boolean isPrintHeaders() {
        return printHeaders;
    }

    public static boolean isDetailedLogging() {
        return detailedLogging;
    }

    public static boolean isHostnameVerification() {
        return hostnameVerification;
    }
}