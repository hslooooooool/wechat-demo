package qsos.lib.base.data

import androidx.lifecycle.MutableLiveData
import qsos.lib.base.data.http.DataStateEntity

/**
 * @author : 华清松
 * @description : 如果数据请求存在多种状态，而这些状态需要被观察，则使用此类代替，
 * 观察的数据将默认持有一个可观测请求状态的 MutableLiveData ，即 httpState
 */
class HttpLiveData<T> : MutableLiveData<T>() {
    /**网络请求状态被观察者*/
    val httpState = MutableLiveData<DataStateEntity>()
}