package qsos.lib.base.base

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.yanzhenjie.sofia.Bar
import com.yanzhenjie.sofia.Sofia
import qsos.lib.base.R
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.helper.GlobalExceptionHelper
import qsos.lib.base.utils.LogUtil
import qsos.lib.base.utils.ToastUtils
import qsos.lib.base.utils.activity.ActivityUtils

/**
 * @author : 华清松
 * @description : Base Activity
 */
abstract class BaseActivity : AppCompatActivity(), BaseView {

    lateinit var mSofia: Bar

    final override var mContext: Context? = null
        protected set(value) {
            field = value
        }

    /**设置视图ID*/
    abstract val layoutId: Int?

    /**视图重载是否重新加载数据*/
    abstract val reload: Boolean

    /**默认视图ID*/
    override val defLayoutId: Int = R.layout.activity_default

    /**判断当前Activity是否在前台*/
    override var isActive: Boolean = false
        protected set(value) {
            field = value
        }

    /*注意调用顺序*/

    /**初始化数据*/
    abstract fun initData(savedInstanceState: Bundle?)

    /**初始化试图*/
    abstract fun initView()

    /**获取数据*/
    abstract fun getData()

    /**网络请求统一更新布局*/
    abstract fun changHttpView(it: HttpCode)

    override fun startActivity(intent: Intent) {
        LogUtil.i("启动:$localClassName")
        super.startActivity(intent)
    }

    override fun onCreate(bundle: Bundle?) {
        LogUtil.i("创建:$localClassName")
        super.onCreate(bundle)
        mContext = this
        mSofia = Sofia.with(this)
        // 全部竖屏显示
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        ARouter.getInstance().inject(this)

        ActivityUtils.instance.addActivity(this)

        initData(bundle)

        if (layoutId == null) {
            setContentView(defLayoutId)
        } else {
            setContentView(layoutId!!)
            initView()
        }

    }

    override fun onStart() {
        LogUtil.i("开启:$localClassName")
        super.onStart()
        isActive = true
    }

    override fun onResume() {
        LogUtil.i("当前:$localClassName")
        super.onResume()
        if (reload) {
            getData()
        }
    }

    override fun onPause() {
        LogUtil.i("暂停:$localClassName")
        super.onPause()
    }

    override fun onStop() {
        LogUtil.i("停止:$localClassName")
        super.onStop()
        isActive = false
    }

    override fun finish() {
        LogUtil.i("结束:$localClassName")
        super.finish()
    }

    override fun onDestroy() {
        LogUtil.i("销毁:$localClassName")
        super.onDestroy()

        ActivityUtils.instance.finishSingle(this)
    }

    override fun showToast(msg: String?) {
        if (!TextUtils.isEmpty(msg)) ToastUtils.showToast(this, msg!!)
    }

    override fun finishThis() {
        finish()
    }

    /**隐藏软键盘*/
    fun hideKeyboard() {
        val imm: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            if (this.currentFocus?.windowToken != null) {
                imm.hideSoftInputFromWindow(this.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

}
