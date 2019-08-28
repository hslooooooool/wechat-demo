package qsos.lib.netservice

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import qsos.core.lib.utils.json.DateDeserializer
import qsos.core.lib.utils.json.DateSerializer
import qsos.lib.netservice.interceptor.AddCookiesInterceptor
import qsos.lib.netservice.interceptor.NetWorkInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author 华清松
 * @doc 类说明：使用Retrofit+OkHttp搭建网路请求框架
 */
object ApiEngine {

    private var mHost: String = "http://thoughtworks-ios.herokuapp.com/"
    private var mBuild: Retrofit.Builder
    private var mClient: OkHttpClient.Builder
    private var uploadInterceptor: Interceptor? = null
    private var downloadInterceptor: Interceptor? = null

    private val mGsonBuilder: GsonBuilder = GsonBuilder()
    private val mGsonConverterFactory: GsonConverterFactory

    init {
        mGsonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        mGsonBuilder.registerTypeAdapter(Date::class.java, DateSerializer()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        mGsonConverterFactory = GsonConverterFactory.create(mGsonBuilder.create())

        mBuild = Retrofit.Builder()
                .baseUrl(mHost)
                .addConverterFactory(mGsonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        mClient = OkHttpClient.Builder()
                .connectTimeout(8000, TimeUnit.MILLISECONDS)
                // 网络拦截器
                .addNetworkInterceptor(NetWorkInterceptor())
                // COOKIE拦截器
                .addInterceptor(AddCookiesInterceptor())
                // 日志拦截器
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    }

    /**自行配置请调用此方法*/
    fun init(retrofitBuilder: Retrofit.Builder?, clientBuilder: OkHttpClient.Builder?): ApiEngine {
        if (retrofitBuilder != null) {
            mBuild = retrofitBuilder
        }
        if (clientBuilder != null) {
            mClient = clientBuilder
        }
        return this
    }

    var isUnitTest = false

    /**创建普通服务*/
    fun <T> createService(service: Class<T>): T {
        if (downloadInterceptor != null) mClient.interceptors().remove(downloadInterceptor!!)
        return mBuild
                .client(mClient.build())
                .build()
                .create(service)
    }

    /**创建普通服务，变更host*/
    fun <T> createService(service: Class<T>, host: String): T {
        if (mBuild.build().baseUrl().host() != host) {
            mBuild = Retrofit.Builder()
                    .baseUrl(host)
                    .addConverterFactory(mGsonConverterFactory)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }
        return mBuild
                .client(mClient.build())
                .build()
                .create(service)
    }

    /**创建文件上传服务*/
    fun <T> createUploadService(service: Class<T>): T {
        return mBuild
                .client(mClient.build())
                .build()
                .create(service)
    }

    /**创建文件下载服务*/
    fun <T> createDownloadService(service: Class<T>): T {
        return mBuild
                .client(mClient.build())
                .build()
                .create(service)
    }

}
