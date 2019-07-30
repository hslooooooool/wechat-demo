package qsos.lib.netservice.expand

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import qsos.lib.base.data.http.DataState
import retrofit2.Call
import java.io.IOException
import java.net.ConnectException

/**
 * @author : 华清松
 * @description : Retrofit协程请求
 */
fun <ResultType> CoroutineScope.retrofit(
        // 传递方法
        dsl: RetrofitCoroutineDsl<ResultType>.() -> Unit
) {
    this.launch(Dispatchers.Main) {
        val retrofitCoroutine = RetrofitCoroutineDsl<ResultType>()
        retrofitCoroutine.dsl()
        retrofitCoroutine.api?.let { api ->
            // IO线程执行网络请求
            val work = async(Dispatchers.IO) {
                retrofitCoroutine.onStart?.invoke()
                try {
                    // 协程内同步进行网络请求
                    api.execute()
                } catch (e: ConnectException) {
                    retrofitCoroutine.onFailed?.invoke(DataState.NO_NET.code, DataState.NO_NET.msg)
                    null
                } catch (e: IOException) {
                    retrofitCoroutine.onFailed?.invoke(DataState.ERROR.code, DataState.ERROR.msg)
                    null
                }
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
                    retrofitCoroutine.onSuccess?.invoke(response.body())
                } else {
                    // NOTICE 统一处理 HTTP CODE
                    when (response.code()) {
                        401 -> {
                        }
                        500 -> {
                        }
                    }
                    retrofitCoroutine.onFailed?.invoke(response.code(), response.errorBody().toString())
                }
            }
        }
    }
}

class RetrofitCoroutineDsl<ResultType> {
    var api: (Call<ResultType>)? = null

    /**请求开始*/
    var onStart: (() -> Unit)? = null
        private set
    /**请求成功*/
    var onSuccess: ((ResultType?) -> Unit)? = null
        private set
    /**请求完成*/
    var onComplete: (() -> Unit)? = null
        private set
    /**请求失败*/
    var onFailed: ((code: Int, error: String) -> Unit)? = null
        private set

    fun clean() {
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

