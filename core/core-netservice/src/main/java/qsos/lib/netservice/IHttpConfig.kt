package qsos.lib.netservice

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * @author : 华清松
 * 网络配置清单
 */
interface IHttpConfig {

    /**获取RetrofitBuilder自有配置*/
    fun getRetrofitBuilder(): Retrofit.Builder

    /**获取OkHttpBuilder自有配置*/
    fun getOkHttpBuilder(): OkHttpClient.Builder

}