package vip.qsos.exception

import android.net.ParseException
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * @author : 华清松
 * 全局异常处理帮助类
 */
object GlobalExceptionHelper : Thread.UncaughtExceptionHandler {

    val globalException = MutableLiveData<GlobalException>()

    override fun uncaughtException(t: Thread, e: Throwable) {
        e.printStackTrace()
        when (e) {
            is GlobalException.ServerException -> handleServerException(e)
            is HttpException -> handleHttpException(e)
            is ConnectException -> handleConnectException(e)
            is SocketTimeoutException -> handleSocketTimeoutException(e)
            is NullPointerException -> handleNullPointerException(e)
            is JsonParseException, is JSONException, is ParseException -> handleJsonException(e)
            else -> handleUnKnownException(e)
        }
    }

    /**错误码详见 @see https://blog.csdn.net/Gjc_csdn/article/details/80449996 */
    private fun handleHttpException(e: HttpException) {
        Timber.tag("网络服务异常").w(e.message())
        when (e.code()) {
            400 -> {
                Timber.tag("网络服务异常").e("服务接口访问错误")
                handleServerException(GlobalException.ServerException(400, "服务接口访问错误"))
            }
            401 -> {
                Timber.tag("网络服务异常").e("未授权访问")
                postHttpException(e)
            }
            403 -> {
                Timber.tag("网络服务异常").e("服务请求被拒绝")
                handleServerException(GlobalException.ServerException(403, "服务请求被拒绝"))
            }
            404 -> {
                Timber.tag("网络服务异常").e("服务接口不存在")
                handleServerException(GlobalException.ServerException(404, "服务接口不存在"))
            }
            405 -> {
                Timber.tag("网络服务异常").e("服务接口已被禁用")
                handleServerException(GlobalException.ServerException(405, "服务接口已被禁用"))
            }
            500 -> {
                Timber.tag("网络服务异常").e("服务器出现问题")
                handleServerException(GlobalException.ServerException(500, "服务器出现问题"))
            }
            501 -> {
                Timber.tag("网络服务异常").e("服务接口访问方式可能错误")
                handleServerException(GlobalException.ServerException(501, "服务接口访问方式可能错误"))
            }
            503 -> {
                Timber.tag("网络服务异常").e("服务暂时无法访问")
                handleServerException(GlobalException.ServerException(503, "服务暂时无法访问"))
            }
            504 -> {
                Timber.tag("网络服务异常").e("服务响应超时")
                handleServerException(GlobalException.ServerException(504, "服务响应超时"))
            }
        }

    }

    private fun postHttpException(e: HttpException) {
        globalException.postValue(GlobalException(GlobalExceptionType.HttpException, e))
    }

    private fun handleServerException(e: GlobalException.ServerException) {
        Timber.tag("服务器异常").e(e.msg)
        globalException.postValue(GlobalException(GlobalExceptionType.ServerException, e))
    }

    private fun handleJsonException(e: Throwable) {
        Timber.tag("Json解析异常").e(e)
        globalException.postValue(GlobalException(GlobalExceptionType.JsonException, e))
    }

    private fun handleConnectException(e: Throwable) {
        Timber.tag("网络连接故障").e(e)
        globalException.postValue(GlobalException(GlobalExceptionType.ConnectException, e))
    }

    private fun handleSocketTimeoutException(e: Throwable) {
        Timber.tag("服务器响应超时").e(e)
        globalException.postValue(GlobalException(GlobalExceptionType.TimeoutException, e))
    }

    private fun handleNullPointerException(e: Throwable) {
        Timber.tag("空指针异常").e(e)
        globalException.postValue(GlobalException(GlobalExceptionType.NullPointerException, e))
    }

    private fun handleUnKnownException(e: Throwable) {
        Timber.tag("其它异常").e(e)
        globalException.postValue(GlobalException(GlobalExceptionType.OtherException, e))
    }

}