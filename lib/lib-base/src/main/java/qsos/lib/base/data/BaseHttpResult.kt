package qsos.lib.base.data

/**
 * @author 华清松
 * 统一的网络请求结果数据实体结构
 */
class BaseHttpResult<T> : IHttpResult<T> {
    /**请求成功与否*/
    var success: Boolean = true
    /**返回信息展示，如请求成功*/
    var msg: String? = ""
    /**请求返回码*/
    var code: Int = 0
    /**请求结果*/
    var data: T? = null

    override fun httpSuccess(): Boolean {
        return success
    }

    override fun httpResult(): T? {
        return data
    }

    override fun httpCode(): Int {
        return code
    }

    override fun httpMsg(): String {
        return msg ?: "null"
    }
}
