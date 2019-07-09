package qsos.lib.netservice

import android.net.ParseException
import com.google.gson.JsonParseException
import org.json.JSONException
import qsos.lib.base.data.http.ApiException
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.data.http.ServerException
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * @author 华清松
 * @doc 类说明：HTTP 请求异常统一处理类
 */
object ApiExceptionServiceImpl {

    fun handleException(e: Throwable): ApiException {
        e.printStackTrace()
        val ex: ApiException
        if (e is ApiException) {
            ex = e
        } else if (e is HttpException) {
            // HTTP错误
            ex = when (e.code()) {
                HttpCode.UNAUTHORIZED.value -> ApiException(e, HttpCode.UNAUTHORIZED, "认证失败")
                HttpCode.NOT_FOUND.value -> ApiException(e, HttpCode.NOT_FOUND, "找不到服务器")
                else -> ApiException(e, HttpCode.SERVER_ERROR, "服务器错误")
            }
        } else if (e is ServerException) {
            // 服务器定义的错误
            ex = ApiException(e, HttpCode.SERVER_ERROR, e.msg)
            Timber.tag("服务器异常").e(e.msg)
        } else if (e is JsonParseException || e is JSONException || e is ParseException) {
            // 均视为解析错误
            ex = ApiException(e, HttpCode.DATA_PARSE, "数据解析错误")
            Timber.tag("数据解析异常").e("$e")
        } else if (e is ConnectException) {
            // 均视为网络错误
            ex = ApiException(e, HttpCode.NOT_NETWORK, "网络连接失败")
            Timber.tag("网络连接失败").e("$e")
        } else if (e is SocketTimeoutException) {
            // 访问超时
            ex = ApiException(e, HttpCode.GATEWAY_TIMEOUT, "网络连接超时")
            Timber.tag("服务器超时").e("$e")
        } else if (e is NullPointerException) {
            ex = ApiException(e, HttpCode.NULL_POINT, "空指针错误")
            Timber.tag("空指针异常，访问路径对么？").e("$e")
        } else {
            ex = ApiException(e, HttpCode.UNKNOWN, "未知异常 ${e.message}")
            Timber.tag("未知异常").e("$e")
        }
        return ex
    }
}
