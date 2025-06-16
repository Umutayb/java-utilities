package api_assured;

import context.ContextStore;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.converter.protobuf.ProtoConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.converter.wire.WireConverterFactory;
import utils.*;
import utils.reflection.ReflectionUtilities;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static utils.mapping.MappingUtilities.Json.getJsonString;
import static utils.mapping.MappingUtilities.Json.mapper;

/**
 * The ServiceGenerator class is responsible for generating Retrofit Service based on the provided service class
 * and configurations. It also provides methods to set and get different configurations like headers, timeouts,
 * and base URL.
 *
 * @author Umut Ay Bora
 * @version 1.4.0 (Documented in 1.4.0, released in version 1.0.0)
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
@Deprecated(since = "1.7.4")
public class ServiceGenerator {

    OkHttpClient client;

    /**
     * The header object containing the headers to be added to the requests.
     */
    Headers headers = new Headers.Builder().build();

    /**
     * A boolean indicating whether to log the headers in the requests.
     */
    boolean printHeaders = Boolean.parseBoolean(ContextStore.get("log-headers", "true"));

    /**
     * A boolean indicating whether to log detailed information in the requests.
     */
    boolean detailedLogging = Boolean.parseBoolean(ContextStore.get("detailed-logging", "false"));

    /**
     * A boolean indicating whether to verify the hostname in the requests.
     */
    boolean hostnameVerification = Boolean.parseBoolean(ContextStore.get("verify-hostname", "true"));

    /**
     * A boolean indicating whether to print request body in the outgoing requests.
     */
    boolean printRequestBody = Boolean.parseBoolean(ContextStore.get("print-request-body", "false"));

    /**
     * Connection timeout in seconds.
     */
    int connectionTimeout = Integer.parseInt(ContextStore.get("connection-timeout", "60"));

    /**
     * Read timeout in seconds.
     */
    int readTimeout = Integer.parseInt(ContextStore.get("connection-read-timeout", "30"));

    /**
     * Write timeout in seconds.
     */
    int writeTimeout = Integer.parseInt(ContextStore.get("connection-write-timeout", "30"));

    /**
     * Proxy host. (default: null)
     */
    String proxyHost = ContextStore.get("proxy-host", null);

    /**
     * Proxy port (default: 8888)
     */
    int proxyPort = Integer.parseInt(ContextStore.get("proxy-port", "8888"));

    /**
     * Follow redirects?
     */
    boolean followRedirects = Boolean.parseBoolean(ContextStore.get("request-follows-redirects", "false"));

    /**
     * Use proxy?
     */
    boolean useProxy = proxyHost != null;

    /**
     * The base URL for the service.
     */
    String BASE_URL = "";

    /**
     * The logger object for logging information.
     */
    private static final Printer log = new Printer(ServiceGenerator.class);

    /**
     * Constructor for the ServiceGenerator class with headers and base URL.
     *
     * @param headers The headers to be added to the requests.
     * @param BASE_URL The base URL for the service.
     */ // TODO: Constructor should reference each other
    public ServiceGenerator(Headers headers, String BASE_URL) {
        this(BASE_URL);
        setHeaders(headers);
    }

    /**
     * Constructor for the ServiceGenerator class with headers.
     *
     * @param headers The headers to be added to the requests.
     */
    public ServiceGenerator(Headers headers) {setHeaders(headers);}

    /**
     * Constructor for the ServiceGenerator class with base URL.
     *
     * @param BASE_URL The base URL for the service.
     */
    public ServiceGenerator(String BASE_URL) {this.BASE_URL = BASE_URL;}

    /**
     * Default constructor for the ServiceGenerator class.
     */
    public ServiceGenerator(){}

    /**
     * Creates Retrofit Service based on the provided service class and configurations.
     *
     * @param serviceClass The service class (api data store) to be used when creating Retrofit Service.
     * @return The created Retrofit Service.
     */
    public static <S> S generate(
            Class<S> serviceClass,
            String BASE_URL,
            boolean detailedLogging,
            int connectionTimeout,
            int readTimeout,
            int writeTimeout,
            Headers headers,
            boolean printRequestBody,
            boolean printHeaders,
            boolean hostnameVerification,
            String proxyHost
    ) {
        return new ServiceGenerator(headers, BASE_URL)
                .setRequestLogging(printRequestBody)
                .hostnameVerification(hostnameVerification)
                .detailedLogging(detailedLogging)
                .setPoxyHost(proxyHost)
                .printHeaders(printHeaders)
                .setReadTimeout(readTimeout)
                .setWriteTimeout(writeTimeout)
                .setConnectionTimeout(connectionTimeout)
                .generate(serviceClass);
    }

    /**
     * Creates Retrofit Service based on the provided service class and configurations.
     *
     * @param serviceClass The service class (api data store) to be used when creating Retrofit Service.
     * @return The created Retrofit Service.
     */
    public static <S> S generate(Class<S> serviceClass, String BASE_URL) {
        return new ServiceGenerator(BASE_URL).generate(serviceClass);
    }

    /**
     * Creates Retrofit Service based on the provided service class and configurations.
     *
     * @param serviceClass The service class (api data store) to be used when creating Retrofit Service.
     * @return The created Retrofit Service.
     */
    public <S> S generate(Class<S> serviceClass) {

        if (BASE_URL.isEmpty()) BASE_URL = (String) ReflectionUtilities.getFieldValue("BASE_URL", serviceClass);

        client = client == null ? getDefaultHttpClient() : client;

        assert BASE_URL != null;
        @SuppressWarnings("deprecation")
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create()) //Deprecated
                .addConverterFactory(MoshiConverterFactory.create())
                .addConverterFactory(WireConverterFactory.create())
                .addConverterFactory(ProtoConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(serviceClass);
    }

    /**
     * Sets the OkHttpClient instance to be used by the ServiceGenerator.
     *
     * @param client the OkHttpClient instance to be set
     * @return the current instance of ServiceGenerator for method chaining
     */
    public ServiceGenerator setHttpClient(OkHttpClient client){
        this.client = client;
        return this;
    }

    /**
     * Creates and returns a default OkHttpClient instance with predefined configurations.
     * <p>
     * This client includes:
     * <ul>
     *     <li>Logging interceptors for both body and headers.</li>
     *     <li>Connection, read, and write timeouts.</li>
     *     <li>Redirect handling.</li>
     *     <li>A network interceptor for modifying requests before execution.</li>
     * </ul>
     * The interceptor ensures headers are set, logs the request body if enabled,
     * and prints headers when required.
     *
     * @return a configured OkHttpClient instance
     */
    public OkHttpClient getDefaultHttpClient(){
        OkHttpClient client =  new OkHttpClient.Builder()
                .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .followRedirects(followRedirects)
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

                        if (printRequestBody) {
                            Request cloneRequest = request.newBuilder().build();
                            if (cloneRequest.body()!= null){
                                Buffer buffer = new Buffer();
                                cloneRequest.body().writeTo(buffer);
                                String bodyString = buffer.readString(UTF_8);
                                try {
                                    Object jsonObject = mapper.readValue(bodyString, Object.class);
                                    String outgoingRequestLog = "The request body is: \n" + getJsonString(jsonObject);
                                    log.info(outgoingRequestLog);
                                }
                                catch (IOException ignored) {
                                    log.warning("Could not log request body!\nBody: " + bodyString);
                                }
                            }
                            else log.warning("Request body is null!");
                        }
                    }
                    if (printHeaders)
                        log.info(("Headers(" + request.headers().size() + "): \n" + request.headers()).trim());
                    return chain.proceed(request);
                }).build();

        if (detailedLogging)
            client = new OkHttpClient.Builder(client)
                    .addInterceptor(getLogginInterceptor(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(getLogginInterceptor(HttpLoggingInterceptor.Level.HEADERS))
                    .build();

        if (!hostnameVerification)
            client = new OkHttpClient.Builder(client).hostnameVerifier((hostname, session) -> true).build();

        if (useProxy)
            client = new OkHttpClient.Builder(client)
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
                    .build();

        return client;
    }

    /**
     * Creates and returns an {@link HttpLoggingInterceptor} with the specified logging level.
     * <p>
     * This interceptor is used to log HTTP request and response details,
     * such as headers, body, and metadata, depending on the provided level.
     *
     * @param level the logging level to set for the interceptor (e.g., BODY, HEADERS, BASIC, NONE)
     * @return an {@link HttpLoggingInterceptor} instance configured with the specified level
     */
    public HttpLoggingInterceptor getLogginInterceptor(HttpLoggingInterceptor.Level level){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(level);
        return interceptor;
    }

    /**
     * Sets whether to log the headers in the requests.
     *
     * @param printHeaders A boolean indicating whether to log the headers in the requests.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator printHeaders(boolean printHeaders) {
        this.printHeaders = printHeaders;
        return this;
    }

    /**
     * Sets whether to log detailed information in the requests.
     *
     * @param detailedLogging A boolean indicating whether to log detailed information in the requests.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator detailedLogging(boolean detailedLogging) {
        this.detailedLogging = detailedLogging;
        return this;
    }

    /**
     * Sets whether to verify the hostname in the requests.
     *
     * @param hostnameVerification A boolean indicating whether to verify the hostname in the requests.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator hostnameVerification(boolean hostnameVerification) {
        this.hostnameVerification = hostnameVerification;
        return this;
    }

    /**
     * Sets whether to print request bodies in the outgoing requests.
     *
     * @param logRequestBody A boolean indicating whether to print request bodies in the outgoing requests.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator setRequestLogging(boolean logRequestBody) {
        this.printRequestBody = logRequestBody;
        return this;
    }

    /**
     * Sets the headers to be added to the requests.
     *
     * @param headers The headers to be added to the requests.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator setHeaders(Headers headers){
        this.headers = headers;
        return this;
    }

    /**
     * Gets the headers to be added to the requests.
     *
     * @return The headers to be added to the requests.
     */
    public Headers getHeaders() {
        return headers;
    }

    /**
     * Gets whether to log the headers in the requests.
     *
     * @return A boolean indicating whether to log the headers in the requests.
     */
    public boolean printHeaders() {
        return printHeaders;
    }

    /**
     * Gets whether to log detailed information in the requests.
     *
     * @return A boolean indicating whether to log detailed information in the requests.
     */
    public boolean detailedLogs() {
        return detailedLogging;
    }

    /**
     * Gets whether to verify the hostname in the requests.
     *
     * @return A boolean indicating whether to verify the hostname in the requests.
     */
    public boolean isHostnameVerification() {
        return hostnameVerification;
    }

    /**
     * Sets the connection timeout in seconds.
     *
     * @param connectionTimeout The connection timeout in seconds.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    /**
     * Sets the read timeout in seconds.
     *
     * @param readTimeout The read timeout in seconds.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * Sets write timeout in seconds.
     *
     * @param writeTimeout write timeout in seconds.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    /**
     * Sets write timeout in seconds.
     *
     * @param proxyHost proxy host.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator setPoxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        return this;
    }

    /**
     * Sets write timeout in seconds.
     *
     * @param proxyPort proxy host.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator setPoxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }

    /**
     * Sets the base URL for the service.
     *
     * @param BASE_URL The base URL for the service.
     * @return The updated ServiceGenerator object.
     */
    public ServiceGenerator setBASE_URL(String BASE_URL) {
        this.BASE_URL = BASE_URL;
        return this;
    }

    /**
     * Gets connection timeout in seconds.
     *
     * @return connection timeout in seconds.
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Gets read timeout in seconds.
     *
     * @return read timeout in seconds.
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Gets write timeout in seconds.
     *
     * @return write timeout in seconds.
     */
    public int getWriteTimeout() {
        return writeTimeout;
    }
}