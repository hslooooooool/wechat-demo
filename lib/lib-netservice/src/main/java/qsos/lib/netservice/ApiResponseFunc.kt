package qsos.lib.netservice

import io.reactivex.functions.Function
import qsos.lib.base.data.GlobalException
import qsos.lib.base.data.IHttpResult
import qsos.lib.base.data.http.ApiException
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.data.http.ServerException

/**
 * @author : 华清松
 * 拦截固定格式的公共数据类型 HttpResult<T>,判断里面状态码 httpCode
 */
class ApiResponseFunc<T> : Function<IHttpResult<T>, T> {

    @Throws(ApiException::class)
    override fun apply(mHttpResult: IHttpResult<T>): T {
        if (mHttpResult.httpSuccess()) {
            throw GlobalException.ServerException(mHttpResult.httpCode(), mHttpResult.httpMsg())
        }
        when (mHttpResult.httpCode()) {
            // 服务器错误码,统一处理
            401 -> throw ApiException(ServerException(mHttpResult.httpCode(), mHttpResult.httpMsg()), HttpCode.UNAUTHORIZED.code, "认证失败")
            400, 404, 500 -> throw ApiException(ServerException(mHttpResult.httpCode(), mHttpResult.httpMsg()), HttpCode.SERVER_ERROR.code, mHttpResult.httpMsg())
        }
        if (mHttpResult.httpCode() == 200 && mHttpResult.httpResult() == null) {
            return mHttpResult.httpMsg() as T
        }
        return mHttpResult.httpResult()!!
    }
}