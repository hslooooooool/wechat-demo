package qsos.lib.base.data

/**
 * @author : 华清松
 * 全局异常
 */
data class GlobalException(
        var exceptionType: GlobalExceptionType,
        var exception: Throwable
) {
    /**服务器主动发出的异常，通常为业务限制异常，应在内部处理*/
    class ServerException(var code: Int, var msg: String = "") : RuntimeException()
}

enum class GlobalExceptionType {
    HttpException,
    JsonException,
    ServerException,
    ConnectException,
    TimeoutException,
    NullPointerException,
    OtherException;
}