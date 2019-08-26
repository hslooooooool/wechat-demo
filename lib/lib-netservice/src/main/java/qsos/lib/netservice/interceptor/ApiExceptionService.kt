package qsos.lib.netservice.interceptor

import android.net.ParseException
import com.google.gson.JsonParseException
import io.reactivex.observers.DisposableObserver
import org.json.JSONException
import vip.qsos.lib_data.data.GlobalException
import qsos.lib.netservice.BaseHttpResult
import qsos.lib.netservice.HttpResult
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * @author 华清松
 * 网络请求错误处理类
 */
abstract class ApiExceptionService<T> : DisposableObserver<T>() {

    protected abstract fun onError(it: HttpResult)

    override fun onError(e: Throwable) {
        when (e) {
            is SocketTimeoutException -> onError(BaseHttpResult())
            is JsonParseException, is JSONException, is ParseException -> onError(BaseHttpResult())
            is GlobalException.ServerException -> onError(BaseHttpResult(e.code, e.msg))
            is HttpException, is ConnectException, is NullPointerException -> throw e
            else -> throw e
        }
    }
}
