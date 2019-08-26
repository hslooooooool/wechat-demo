package vip.qsos.lib_data.data

/**
 * @author : 华清松
 * 统一的网络请求结果数据实体结构
 */
interface IHttpResult<T> {
    /**请求是否成功*/
    fun httpSuccess(): Boolean

    /**请求的结果*/
    fun httpResult(): T?

    /**请求码*/
    fun httpCode(): Int

    /**回执信息*/
    fun httpMsg(): String
}