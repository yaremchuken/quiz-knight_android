package yaremchuken.quizknight.api

/**
 * Exception wrapper for REST service.
 */
class RestException(
    private val url: String,
    private val params: String,
    cause: Throwable
): RuntimeException(cause) {
    val debugMessage: String
        get() = "Error is raised while accessing REST endpoint $url with params $params\n cause: ${super.message}"
}