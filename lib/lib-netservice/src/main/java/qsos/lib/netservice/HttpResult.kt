package qsos.lib.netservice

/**
 * @author : 华清松
 * 网络请求数据处理实体类
 */
interface HttpResult {
    val httpCode: Int
    val httpMsg: String?
}

class BaseHttpResult(override val httpCode: Int = 200, override val httpMsg: String = "请求成功") : HttpResult