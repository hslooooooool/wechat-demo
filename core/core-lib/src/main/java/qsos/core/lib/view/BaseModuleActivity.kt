package qsos.core.lib.view

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import qsos.lib.base.R
import qsos.lib.base.base.BaseActivity
import qsos.lib.base.base.BaseView
import qsos.lib.base.data.http.HttpCode
import qsos.lib.netservice.file.HttpResult

/**
 * @author : 华清松
 * @description : 业务模块公共 Activity
 */
@SuppressLint("CheckResult")
abstract class BaseModuleActivity(
        override var statusBarColor: Int? = R.color.white
) : BaseActivity() {

    var mHttpNetCode = MutableLiveData<HttpResult>()

    override fun initView() {
        mHttpNetCode.observe(this, Observer {
            changHttpView(it.code)
        })
    }

    override fun changHttpView(it: HttpCode) {
        when (it) {
            HttpCode.UNAUTHORIZED -> {
                changeBaseView(BaseView.STATE.SERVICE_ERROR)
            }
            HttpCode.NOT_NETWORK -> {
                changeBaseView(BaseView.STATE.NOT_NET)
            }
            HttpCode.NOT_FOUND -> {
                changeBaseView(BaseView.STATE.NOT_FOUND)
            }
            HttpCode.SERVER_ERROR -> {
                changeBaseView(BaseView.STATE.SERVICE_ERROR)
            }
            HttpCode.RESULT_NULL -> {
                changeBaseView(BaseView.STATE.RESULT_NULL)
            }
            HttpCode.SUCCESS -> changeBaseView(BaseView.STATE.OK)
            HttpCode.LOADING -> changeBaseView(BaseView.STATE.LOADING)
            else -> {
                changeBaseView(BaseView.STATE.SERVICE_ERROR)
            }
        }
    }
}
