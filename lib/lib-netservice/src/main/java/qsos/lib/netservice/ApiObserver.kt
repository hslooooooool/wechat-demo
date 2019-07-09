package qsos.lib.netservice

import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.routepath.AppPath
import qsos.lib.base.utils.ToastUtils
import qsos.lib.netservice.file.HttpResult
import qsos.lib.netservice.interceptor.ApiExceptionService
import timber.log.Timber

/**
 * @author 华清松
 * @doc 类说明：请求回调处理
 */
open class ApiObserver<T>(private val httpResult: MutableLiveData<HttpResult>) : ApiExceptionService<T>() {

    override fun onNext(it: T) {
        httpResult.postValue(HttpResult(HttpCode.SUCCESS, null))
        Timber.tag("网络请求：").d("onNext: 请求结果= ${it.toString()}")
    }

    override fun onError(it: HttpResult) {
        if (it.code == HttpCode.UNAUTHORIZED) {
            ToastUtils.showToast(BaseApplication.appContext, "请重新登录")
            //ARouter.getInstance().build(AppPath.LOGIN).navigation()
        } else {
            httpResult.postValue(it)
        }

        Timber.tag("网络请求：").d("onError: 请求异常 ${it.code}-${it.msg}")
    }

    override fun onComplete() {
        Timber.tag("网络请求：").d("onComplete: 处理完毕")
    }

    override fun onStart() {
        httpResult.postValue(HttpResult(HttpCode.LOADING, null))
        Timber.tag("网络请求：").d("onStart: 开始请求")
    }

}
