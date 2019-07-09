package qsos.lib.netservice

import io.reactivex.Flowable
import io.reactivex.functions.Function
import qsos.lib.base.data.http.ApiException

/**
 * @author 华清松
 * @doc 类说明：网络请求异常处理
 */
class HttpErrorFunctionFlowable<T> : Function<Throwable, Flowable<T>> {

    @Throws(Exception::class)
    override fun apply(throwable: Throwable): Flowable<T> {
        return Flowable.error(ApiExceptionServiceImpl.handleException(throwable))
    }

}