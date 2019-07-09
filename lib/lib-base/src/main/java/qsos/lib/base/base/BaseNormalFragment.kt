package qsos.lib.base.base

import qsos.lib.base.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 无需额外处理业务的 Fragment
 */
abstract class BaseNormalFragment : BaseFragment() {
    override val reload = false

    override fun changHttpView(it: HttpCode) {

    }
}
