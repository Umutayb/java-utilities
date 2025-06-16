package api_assured;

/**
 * A record that represents a response and its errorBody.
 *
 * @param <Response> expected Response-SuccessModel-
 * @param <ErrorBody> potential error body
 */
@Deprecated(since = "1.7.4")
public record ResponsePair<Response, ErrorBody>(Response response, ErrorBody errorBody) { }
