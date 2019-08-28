package qsos.core.lib.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build

/**
 * @author 华清松
 * @doc 类说明：网络链接判断工具
 */
object NetUtil {

    /**判断网络是否连接*/
    fun isConnected(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        if (null != info && info.isConnected) {
            if (info.detailedState == NetworkInfo.DetailedState.CONNECTED) {
                return true
            }
        }
        return false
    }

    /**判断是否是wifi连接*/
    fun isWifi(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo.subtype == 1
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        // 新版本调用方法获取网络状态
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val networks = connectivity?.allNetworks ?: arrayOf()
            var networkInfo: NetworkInfo?
            for (mNetwork in networks) {
                networkInfo = connectivity?.getNetworkInfo(mNetwork)
                if (networkInfo!!.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        } else {
            //否则调用旧版本方法
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                for (anInfo in info) {
                    if (anInfo.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
