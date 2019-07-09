package qsos.lib.netservice.file

import androidx.lifecycle.MutableLiveData
import qsos.lib.base.data.http.ApiException
import qsos.lib.base.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 基础数据服务
 */
open class BaseRepository {

    val httpResult = MutableLiveData<HttpResult>()

    val loading=HttpResult(HttpCode.LOADING,"请求数据")
    val success=HttpResult(HttpCode.SUCCESS,"请求成功")

}