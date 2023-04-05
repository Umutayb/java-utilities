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
import utils.PropertyUtility;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class ServiceGenerator {

    public static Properties properties = PropertyUtility.properties;

    Headers headers = new Headers.Builder().build();
    boolean printHeaders = Boolean.parseBoolean(properties.getProperty("log-headers", "true"));
    boolean detailedLogging = Boolean.parseBoolean(properties.getProperty("detailed-logging", "false"));
    boolean hostnameVerification = Boolean.parseBoolean(properties.getProperty("verify-hostname", "true"));
    int connectionTimeout = Integer.parseInt(properties.getProperty("connection-timeout", "60"));
    int readTimeout = Integer.parseInt(properties.getProperty("connection-read-timeout", "30"));
    int writeTimeout = Integer.parseInt(properties.getProperty("connection-write-timeout", "30"));


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
            headerInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(headerInterceptor)
                .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
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

        if (!hostnameVerification) client = new OkHttpClient.Builder(client).hostnameVerifier((hostname, session) -> true).build();

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

    public ServiceGenerator setPrintHeaders(boolean printHeaders) {
        this.printHeaders = printHeaders;
        return this;
    }

    public ServiceGenerator setDetailedLogging(boolean detailedLogging) {
        this.detailedLogging = detailedLogging;
        return this;
    }

    public ServiceGenerator setHostnameVerification(boolean hostnameVerification) {
        this.hostnameVerification = hostnameVerification;
        return this;
    }

    public ServiceGenerator setHeaders(Headers headers){
        this.headers = headers;
        return this;
    }

    public Headers getHeaders() {
        return headers;
    }

    public boolean isPrintHeaders() {
        return printHeaders;
    }

    public boolean isDetailedLogging() {
        return detailedLogging;
    }

    public boolean isHostnameVerification() {
        return hostnameVerification;
    }

    public ServiceGenerator setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public ServiceGenerator setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public ServiceGenerator setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public ServiceGenerator setBASE_URL(String BASE_URL) {
        this.BASE_URL = BASE_URL;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }
}