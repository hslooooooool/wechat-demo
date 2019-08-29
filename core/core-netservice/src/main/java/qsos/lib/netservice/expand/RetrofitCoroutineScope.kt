package qsos.lib.netservice.expand

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
 * Retrofit协程请求
 */
fun <ResultType> CoroutineScope.retrofit(
        dsl: RetrofitCoroutineScope.RetrofitCoroutineDsl<ResultType>.() -> Unit
) {
    this.launch(Dispatchers.Main) {
        val retrofitCoroutine = RetrofitCoroutineScope.RetrofitCoroutineDsl<ResultType>()
        retrofitCoroutine.dsl()
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
                    retrofitCoroutine.onFailed?.invoke(DataStatusEnum.ERROR.code, e.message.toString())
                    null
                }
                // ...TODO 其它异常捕获处理
            }
            work.invokeOnCompletion {
                // 协程关闭时，取消任务
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
                    // 服务器处理成功，对服务器回传数据进行统一判断解析可在此进行
                    retrofitCoroutine.onSuccess?.invoke(response.body())
                } else {
                    // 标准网络HTTP CODE处理逻辑
                    when (response.code()) {
                        401 -> {
                            // 授权失败或未授权，一般需要重新登录，通过自定义注解统一处理
                        }
                        500 -> {
                            // 服务器报错，一般有错误信息提示即可，通过自定义注解统一处理
                        }
                    }
                    // FIXME 暂时统一处理，抛出自行处理
                    retrofitCoroutine.onFailed?.invoke(response.code(), response.errorBody().toString())
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
    this.launch(Dispatchers.Main) {
        val retrofitCoroutine = RetrofitCoroutineScope.RetrofitCoroutineDslByLiveData<ResultType>()
        retrofitCoroutine.dsl()
        retrofitCoroutine.api?.let { api ->
            // IO线程执行网络请求
            val work = async(Dispatchers.IO) {
                retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.LOADING))
                try {
                    // 协程内同步进行网络请求
                    api.execute()
                } catch (e: ConnectException) {
                    // 网络连接异常
                    retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.NO_NET))
                    null
                } catch (e: Exception) {
                    retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.ERROR))
                    null
                }
            }
            // 监听协程关闭时，取消任务
            work.invokeOnCompletion {
                if (work.isCancelled) {
                    api.cancel()
                    retrofitCoroutine.clean()
                }
            }
            val response = work.await()
            retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.FINISH))
            response?.let {
                // 网络请求完成后执行判断
                if (response.isSuccessful) {
                    // 服务器处理成功，对服务器回传数据进行统一判断解析可在此进行
                    retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.SUCCESS))
                    retrofitCoroutine.data?.postValue(response.body())
                } else {
                    retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.ERROR))
                }
            }
        }
    }
}

/**
 * @author : 华清松
 * 常用的Retrofit协程请求，方便简单的接口调用，统一的请求状态管理，成功后直接执行onSuccess中的Function
 */
fun <ResultType> CoroutineScope.retrofitWithFunction(
        dsl: RetrofitCoroutineScope.RetrofitCoroutineDslByFunction<ResultType>.() -> Unit
) {
    this.launch(Dispatchers.Main) {
        val retrofitCoroutine = RetrofitCoroutineScope.RetrofitCoroutineDslByFunction<ResultType>()
        retrofitCoroutine.dsl()
        retrofitCoroutine.api?.let { api ->
            // IO线程执行网络请求
            val work = async(Dispatchers.IO) {
                retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.LOADING))
                try {
                    // 协程内同步进行网络请求
                    api.execute()
                } catch (e: ConnectException) {
                    // 网络连接异常
                    retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.NO_NET))
                    null
                } catch (e: Exception) {
                    retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.ERROR))
                    null
                }
            }
            // 监听协程关闭时，取消任务
            work.invokeOnCompletion {
                if (work.isCancelled) {
                    api.cancel()
                    retrofitCoroutine.clean()
                }
            }
            val response = work.await()
            retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.FINISH))
            response?.let {
                // 网络请求完成后执行判断
                if (response.isSuccessful) {
                    // 服务器处理成功，对服务器回传数据进行统一判断解析可在此进行
                    retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.SUCCESS))
                    retrofitCoroutine.onSuccess?.invoke(response.body())
                } else {
                    retrofitCoroutine.data?.httpState?.postValue(BaseDataStatus.base(DataStatusEnum.ERROR))
                }
            }
        }
    }
}


