package qsos.lib.netservice

import io.reactivex.functions.Function
import qsos.lib.base.data.Result
import qsos.lib.base.data.http.ApiException
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.data.http.ServerException

/**
 * @author : 华清松
 * @description : 拦截固定格式的公共数据类型 Response<T>,判断里面状态码 <T>
 */
class ApiResponseFunc<T> : Function<Result<T>, T> {

    @Throws(ApiException::class)
    override fun apply(result: Result<T>): T {
        if (result.isError()) {
            throw ServerException(result.getResultCode(), result.getResultMsg())
        }
        when (result.getResultCode()) {
            // 服务器错误码,统一处理
            401 -> throw ApiException(ServerException(result.getResultCode(), result.getResultMsg()), HttpCode.UNAUTHORIZED, "认证失败")
            400, 404, 500 -> throw ApiException(ServerException(result.getResultCode(), result.getResultMsg()), HttpCode.SERVER_ERROR, result.getResultMsg())
        }
        if (result.getResultCode() == 200 && result.getResult() == null) {
            return result.getResultMsg() as T
        }
        return result.getResult()!!
    }
}