package qsos.lib.base.base

import vip.qsos.lib_data.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 无需额外处理业务的 Activity
 */
abstract class BaseNormalActivity : BaseActivity() {
    override val reload = false

    override fun changHttpView(it: HttpCode) {

    }
}
