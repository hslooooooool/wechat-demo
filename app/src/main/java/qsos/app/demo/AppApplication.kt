package qsos.app.demo

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import qsos.base.core.ModelApplication
import vip.qsos.exception.GlobalExceptionHelper

/**
 * @author : 华清松
 * Application 类，此类被替换了怎么办？TODO
 */
open class AppApplication : ModelApplication(), LifecycleOwner {

    override fun getLifecycle(): Lifecycle {
        return LifecycleRegistry(this)
    }

    override fun onCreate() {
        super.onCreate()

        GlobalExceptionHelper.globalException.observe(this, Observer {
            dealGlobalException(it)
        })
    }

    /**统一处理异常，如重新登录、强制下线、异常反馈、网络检查*/
    private fun dealGlobalException(ex: vip.qsos.exception.GlobalException) {
        when (ex.exceptionType) {
            vip.qsos.exception.GlobalExceptionType.HttpException -> {
                /**在此处理，应在弹出对应处置窗口*/
            }
            vip.qsos.exception.GlobalExceptionType.ConnectException -> {
                /**在此处理，应在弹出网络检测窗口，前往网络设置*/
            }
            vip.qsos.exception.GlobalExceptionType.JsonException, vip.qsos.exception.GlobalExceptionType.ServerException, vip.qsos.exception.GlobalExceptionType.TimeoutException -> {
                /**在此无需处理，应在具体请求页面进行交互*/
            }
            vip.qsos.exception.GlobalExceptionType.NullPointerException, vip.qsos.exception.GlobalExceptionType.OtherException -> {
                /**在此处理，应在弹出异常提交窗口，将异常发送给服务器*/
            }
        }
    }
}