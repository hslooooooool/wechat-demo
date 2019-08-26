package qsos.lib.netservice

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vip.qsos.lib_data.data.BaseHttpResult

/**
 * @author 华清松
 * @doc 类说明：统一请求处理方式
 */
object ObservableService {

    /**设置统一的请求处理，结果数据标准化*/
    fun <T> setObservableBase(observable: Observable<BaseHttpResult<T>>): Observable<T> {
        return observable.map(ApiResponseFunc())
                .onErrorResumeNext(HttpErrorFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**设置统一的请求处理，结果数据非标准化*/
    fun <T> setObservable(observable: Observable<T>): Observable<T> {
        return observable.onErrorResumeNext(HttpErrorFunc<T>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> setFlowableBaseResult(observable: Flowable<BaseHttpResult<T>>): Flowable<T> {
        return observable.map(ApiResponseFunc())
                .onErrorResumeNext(HttpErrorFunctionFlowable<T>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> setObservableSingle(observable: Single<T>): Single<T> {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}