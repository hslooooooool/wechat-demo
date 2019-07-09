package qsos.lib.base.data.http

/**
 * @author : 华清松
 * @description : 网络请求码类型
 */
enum class HttpCode(code: Int) {
    /*规定的*/

    /**200*/
    SUCCESS(200),
    /**401 授权失败*/
    UNAUTHORIZED(401),
    /**403*/
    FORBIDDEN(403),
    /**404 找不到服务器*/
    NOT_FOUND(404),
    /**408 请求超时*/
    REQUEST_TIMEOUT(408),
    /**500 服务器错误,有返回*/
    SERVER_ERROR(500),
    /**502*/
    BAD_GATEWAY(502),
    /**503 服务器无响应*/
    SERVICE_UNAVAILABLE(503),
    /**504 网关超时*/
    GATEWAY_TIMEOUT(504),

    /*自定义的*/
    /**未知错误*/
    UNKNOWN(-1),
    /**请求中*/
    LOADING(0),
    /**数据解析错误*/
    DATA_PARSE(1),
    /**没有网络*/
    NOT_NETWORK(2),
    /**空指针异常*/
    NULL_POINT(3),
    /**返回数据为 null，暂无数据*/
    RESULT_NULL(4);

    val value = code
}