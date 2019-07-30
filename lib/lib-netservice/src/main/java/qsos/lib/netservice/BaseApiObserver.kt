package qsos.lib.netservice

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.utils.ToastUtils
import qsos.lib.netservice.interceptor.ApiExceptionService
import timber.log.Timber

/**
 * @author 华清松
 * 默认的 Retrofit 请求进度观察者
 */
open class BaseApiObserver<T>(private val httpResult: MutableLiveData<HttpResult>) : ApiExceptionService<T>() {

    override fun onNext(it: T) {
        httpResult.postValue(BaseHttpResult(HttpCode.SUCCESS.code, "请求成功"))
        Timber.tag("网络请求：").d("onNext: 请求结果= ${Gson().toJson(it)}")
    }

    override fun onError(it: HttpResult) {
        if (it.httpCode == HttpCode.UNAUTHORIZED.code) {
            ToastUtils.showToast(BaseApplication.appContext, "请重新登录")
        } else {
            httpResult.postValue(it)
        }

        Timber.tag("网络请求：").d("onError: 请求异常  ${Gson().toJson(it)}")
    }

    override fun onComplete() {
        Timber.tag("网络请求：").d("onComplete: 处理完毕")
    }

    override fun onStart() {
        httpResult.postValue(BaseHttpResult(HttpCode.LOADING.code, "请求中..."))
        Timber.tag("网络请求：").d("onStart: 开始请求")
    }

}
