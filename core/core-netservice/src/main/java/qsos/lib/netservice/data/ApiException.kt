package qsos.lib.netservice.data

/**
 * @author 华清松
 * @doc 类说明：接口异常实体
 */
class ApiException(throwable: Throwable, var code: Int, var msg: String) : Exception(throwable)