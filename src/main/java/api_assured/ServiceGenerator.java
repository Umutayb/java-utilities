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
public class ServiceGenerator {

    /**
     * OkHttpClient instance used to execute HTTP requests.
     */
    private OkHttpClient client;

    /**
     * The header object containing the headers to be added to the requests.
     */
    private Headers headers;

    /**
     * A boolean indicating whether to log the headers in the requests.
     */
    private boolean printHeaders;

    /**
     * A boolean indicating whether to log detailed information in the requests.
     */
    private boolean detailedLogging;

    /**
     * A boolean indicating whether to verify the hostname in the requests.
     */
    private boolean hostnameVerification;

    /**
     * A boolean indicating whether to print request body in the outgoing requests.
     */
    private boolean printRequestBody;

    /**
     * Connection timeout in seconds.
     */
    private int connectionTimeout;

    /**
     * Read timeout in seconds.
     */
    private int readTimeout;

    /**
     * Write timeout in seconds.
     */
    private int writeTimeout;

    /**
     * Proxy host. (default: null)
     */
    private String proxyHost;

    /**
     * Proxy port (default: 8888)
     */
    private int proxyPort;

    /**
     * Follow redirects?
     */
    private final boolean followRedirects;

    /**
     * Use proxy?
     */
    private final boolean useProxy;

    /**
     * The base URL for the service.
     */
    private String BASE_URL;

    /**
     * The logger object for logging information.
     */
    private static final Printer log = new Printer(ServiceGenerator.class);

    /**
     * Default constructor for the {@code ServiceGenerator} class.
     * <p>
     * Initializes configuration settings for the service client using values retrieved
     * from the test.properties. These settings control various aspects of HTTP communication,
     * such as timeouts, proxy settings, logging preferences, and hostname verification.
     * Configurations are adjustable from test.properties and by using the setters.
     * <p>
     * The following configurations are applied by default:
     * <ul>
     *   <li>{@code BASE_URL}: Set to an empty string by default. Use the setBASE_URL() method or define the "BASE_URL" variable on service class.</li>
     *   <li>{@code headers}: Initialized as an empty header set.</li>
     *   <li>{@code printHeaders}: Indicates whether to log headers; defaults to {@code true}.</li>
     *   <li>{@code detailedLogging}: Enables verbose logging; defaults to {@code false}.</li>
     *   <li>{@code hostnameVerification}: Enables hostname verification for SSL; defaults to {@code true}.</li>
     *   <li>{@code printHeaders}: (Overwritten again) Indicates whether to log request body; defaults to {@code false}.</li>
     *   <li>{@code connectionTimeout}: Connection timeout in seconds; defaults to 60.</li>
     *   <li>{@code readTimeout}: Read timeout in seconds; defaults to 30.</li>
     *   <li>{@code writeTimeout}: Write timeout in seconds; defaults to 30.</li>
     *   <li>{@code proxyHost}: Proxy host to use, if any; default is {@code null}.</li>
     *   <li>{@code proxyPort}: Proxy port; defaults to 8888.</li>
     *   <li>{@code followRedirects}: Whether to follow HTTP redirects; defaults to {@code false}.</li>
     *   <li>{@code useProxy}: Set to {@code true} if {@code proxyHost} is not {@code null}.</li>
     * </ul>
     * <p>
     * Note: The {@code printHeaders} field is set twice using different context keys.
     * Ensure the correct behavior is intended.
     */
    public ServiceGenerator() {
        this.client = new OkHttpClient();
        this.BASE_URL = "";
        this.headers = new Headers.Builder().build();
        this.printHeaders = Boolean.parseBoolean(ContextStore.get("log-headers", "true"));
        this.detailedLogging = Boolean.parseBoolean(ContextStore.get("detailed-logging", "false"));
        this.hostnameVerification = Boolean.parseBoolean(ContextStore.get("verify-hostname", "true"));
        this.printHeaders = Boolean.parseBoolean(ContextStore.get("print-request-body", "false"));
        this.connectionTimeout = Integer.parseInt(ContextStore.get("connection-timeout", "60"));
        this.readTimeout = Integer.parseInt(ContextStore.get("connection-read-timeout", "30"));
        this.writeTimeout = Integer.parseInt(ContextStore.get("connection-write-timeout", "30"));
        this.proxyHost = ContextStore.get("proxy-host", null);
        this.proxyPort = Integer.parseInt(ContextStore.get("proxy-port", "8888"));
        this.followRedirects = Boolean.parseBoolean(ContextStore.get("request-follows-redirects", "false"));
        this.useProxy = proxyHost != null;
    }

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
        return new ServiceGenerator()
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
        return new ServiceGenerator().generate(serviceClass);
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