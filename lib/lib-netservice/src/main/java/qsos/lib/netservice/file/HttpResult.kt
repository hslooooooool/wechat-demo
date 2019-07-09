package qsos.lib.netservice.file

import qsos.lib.base.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 网络请求数据处理实体类
 */
data class HttpResult(
        val code: HttpCode,
        val msg: String?
)