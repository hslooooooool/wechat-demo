package qsos.lib.netservice.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @author 华清松
 * @doc 类说明：添加 COOKIE 拦截器
 */
class AddCookiesInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
//        val cookie = SharedPreUtils.getCookie(BaseApplication.appContext)
//        Timber.tag("网络拦截器").i("添加 cookie 进 HttpHeader cookie=$cookie")
//        builder.addHeader("token", cookie)
//        builder.addHeader("device", "Android")
        return chain.proceed(builder.build())
    }
}