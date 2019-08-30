package qsos.core.lib.config

/**
 * @author : 华清松
 * 配置参数
 */
object BaseConfig {
    /**配置是否调试模式*/
    var DEBUG = true
    /**配置请求地址*/
    var HOST = "http://192.168.1.17:8084/"
}

/**
 * @author : 华清松
 * 配置接口
 */
interface AbsNetServiceConfig {
    fun config(): BaseConfig
}