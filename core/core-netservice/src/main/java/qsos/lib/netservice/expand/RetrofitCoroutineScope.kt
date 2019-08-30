package qsos.lib.netservice.expand

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import qsos.core.lib.utils.NetUtil
import qsos.lib.netservice.data.BaseDataStatus
import qsos.lib.netservice.data.DataStatusEnum
import qsos.lib.netservice.data.HttpLiveData
import retrofit2.Call
import java.net.ConnectException

/**
 * @author : 华清松
 * Kotlin协程配置Retrofit请求处理逻辑
 */
class RetrofitCoroutineScope {

    class RetrofitCoroutineDsl<ResultType> {
        var api: (Call<ResultType>)? = null

        /**请求开始*/
        var onStart: (() -> Unit?)? = null
            private set
        /**请求成功*/
        var onSuccess: ((ResultType?) -> Unit)? = null
            private set
        /**请求完成*/
        var onComplete: (() -> Unit?)? = null
            private set
        /**请求失败*/
        var onFailed: ((code: Int, error: String) -> Unit?)? = null
            private set

        fun clean() {
            api = null
            onStart = null
            onSuccess = null
            onComplete = null
            onFailed = null
        }

        fun onStart(block: () -> Unit) {
            this.onStart = block
        }

        fun onSuccess(block: (ResultType?) -> Unit) {
            this.onSuccess = block
        }

        fun onComplete(block: () -> Unit) {
            this.onComplete = block
        }

        fun onFailed(block: (code: Int, error: String) -> Unit) {
            this.onFailed = block
        }

    }

    class RetrofitCoroutineDslByLiveData<ResultType> {
        var api: (Call<ResultType>)? = null
        var data: (HttpLiveData<ResultType>)? = null

        fun clean() {
            api = null
            data = null
        }
    }

    class RetrofitCoroutineDslByFunction<ResultType> {
        var api: (Call<ResultType>)? = null
        var data: (HttpLiveData<ResultType>)? = null

        /**请求成功*/
        var onSuccess: ((ResultType?) -> Unit)? = null
            private set

        fun clean() {
            api = null
            data = null
            onSuccess = null
        }

        fun onSuccess(block: (ResultType?) -> Unit) {
            this.onSuccess = block
        }
    }
}

/**
 * @author : 华清松
 * 常用的Retrofit协程请求，方便简单的接口调用，统一的请求状态管理，自行对请求状态进行处理
 */
fun <ResultType> CoroutineScope.retrofit(
        dsl: RetrofitCoroutineScope.RetrofitCoroutineDsl<ResultType>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.RetrofitCoroutineDsl<ResultType>()
    retrofitCoroutine.dsl()
    if (!NetUtil.netWorkIsConnected) {
        retrofitCoroutine.onFailed?.invoke(DataStatusEnum.NO_NET.code, DataStatusEnum.NO_NET.msg)
    } else {
        this.launch(Dispatchers.Main) {
            retrofitCoroutine.api?.let { api ->
                // IO线程执行网络请求
                val work = async(Dispatchers.IO) {
                    retrofitCoroutine.onStart?.invoke()
                    try {
                        // 协程内同步进行网络请求
                        api.execute()
                    } catch (e: ConnectException) {
                        // 网络连接异常
                        retrofitCoroutine.onFailed?.invoke(DataStatusEnum.NO_NET.code, DataStatusEnum.NO_NET.msg)
                        null
                    } catch (e: Exception) {
                        // 其它异常
                        e.printStackTrace()
                        retrofitCoroutine.onFailed?.invoke(DataStatusEnum.ERROR.code, e.message.toString())
                        null
                    }
                }
                // 协程关闭时，取消任务
                work.invokeOnCompletion {
                    if (work.isCancelled) {
                        api.cancel()
                        retrofitCoroutine.clean()
                    }
                }

                val response = work.await()
                retrofitCoroutine.onComplete?.invoke()
                response?.let {
                    // 网络请求完成后执行判断
                    if (response.isSuccessful) {
                        // 服务器处理成功
                        retrofitCoroutine.onSuccess?.invoke(response.body())
                    } else {
                        // 服务器处理失败，按服务标准对异常进行统一处理，回执码如：400,401,403,404,500,501,504等
                        retrofitCoroutine.onFailed?.invoke(response.code(), response.errorBody().toString())
                    }
                }
            }
        }
    }
}

/**
 * @author : 华清松
 * 常用的Retrofit协程请求，方便简单的接口调用，统一的请求状态管理，通过LiveData直接更新UI
 */
fun <ResultType> CoroutineScope.retrofitWithLiveData(
        dsl: RetrofitCoroutineScope.RetrofitCoroutineDslByLiveData<ResultType>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.RetrofitCoroutineDslByLiveData<ResultType>()
    retrofitCoroutine.dsl()
    if (!NetUtil.netWorkIsConnected) {
        retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.NO_NET))
    } else {
        this.launch(Dispatchers.Main) {
            retrofitCoroutine.api?.let { api ->
                val work = async(Dispatchers.IO) {
                    retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.LOADING))
                    try {
                        api.execute()
                    } catch (e: ConnectException) {
                        retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.NO_NET))
                        null
                    } catch (e: Exception) {
                        e.printStackTrace()
                        retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.ERROR))
                        null
                    }
                }
                work.invokeOnCompletion {
                    if (work.isCancelled) {
                        api.cancel()
                        retrofitCoroutine.clean()
                    }
                }
                val response = work.await()
                retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.FINISH))
                response?.let {
                    if (response.isSuccessful) {
                        retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.SUCCESS))
                        retrofitCoroutine.data?.postValue(response.body())
                    } else {
                        retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.ERROR))
                    }
                }
            }
        }
    }
}

/**
 * @author : 华清松
 * 常用的Retrofit协程请求，方便简单的接口调用，统一的请求状态管理，成功后直接执行onSuccess中的Function
 */
fun <ResultType> CoroutineScope.retrofitWithSuccess(
        dsl: RetrofitCoroutineScope.RetrofitCoroutineDslByFunction<ResultType>.() -> Unit
) {
    val retrofitCoroutine = RetrofitCoroutineScope.RetrofitCoroutineDslByFunction<ResultType>()
    retrofitCoroutine.dsl()
    if (!NetUtil.netWorkIsConnected) {
        retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.NO_NET))
    } else {
        this.launch(Dispatchers.Main) {
            retrofitCoroutine.api?.let { api ->
                val work = async(Dispatchers.IO) {
                    retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.LOADING))
                    try {
                        api.execute()
                    } catch (e: ConnectException) {
                        retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.NO_NET))
                        return@async null
                    } catch (e: Exception) {
                        e.printStackTrace()
                        retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.ERROR))
                        return@async null
                    }
                }
                work.invokeOnCompletion {
                    if (work.isCancelled) {
                        api.cancel()
                        retrofitCoroutine.clean()
                    }
                }
                val response = work.await()
                retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.FINISH))
                response?.let {
                    if (response.isSuccessful) {
                        retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.SUCCESS))
                        retrofitCoroutine.onSuccess?.invoke(response.body())
                    } else {
                        retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.ERROR))
                    }
                }
            }
        }
    }
}


