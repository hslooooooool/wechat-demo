package qsos.lib.base.utils.record

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.base_center_audio_view.view.*
import qsos.lib.base.R

@SuppressLint("SetTextI18n")
class AudioDialogUtil(context: Context) {
    private var builder: AlertDialog.Builder? = null
    // 用于取消AlertDialog.Builder
    private var dialog: AlertDialog? = null

    var mIcon: ImageView? = null
    var mTitle: TextView? = null
    var mTime: TextView? = null

    init {
        builder = AlertDialog.Builder(context, R.style.CenterDialogStyle)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.base_center_audio_view, null)

        mIcon = view.center_audio_icon
        mTitle = view.center_audio_content
        mTime = view.center_audio_time

        builder?.setView(view)

        dialog = builder?.create()
        // 设置点击外部不消失
        dialog?.setCanceledOnTouchOutside(true)
    }

    // 显示录音的对话框
    private fun init() {
        mTitle?.text = "按下录音,上滑取消"
        mTime?.text = "0 s"
    }

    fun show() {
        if (dialog != null && !dialog!!.isShowing) {
            init()
            dialog?.show()
        }
    }

    // 显示想取消的对话框
    fun wantToCancel() {
        if (dialog != null && dialog!!.isShowing) {
            mTitle?.text = "松开手指，取消录音"
        }
    }

    // 显示不想取消的对话框
    fun noWantToCancel() {
        if (dialog != null && dialog!!.isShowing) {
            mTitle?.text = "正在录音,上滑取消"
        }
    }

    fun updateTime(time: Int) {
        if (dialog != null && time > -1) {
            mTime?.text = "$time s"
        }
    }

    // 隐藏对话框
    fun dismissDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog?.dismiss()
        }
    }

}
