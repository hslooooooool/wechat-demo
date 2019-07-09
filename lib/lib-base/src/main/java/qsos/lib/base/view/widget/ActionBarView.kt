package qsos.lib.base.view.widget

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.base_action_bar.view.*
import qsos.lib.base.R

/**
 * @author : 华清松
 * @description : 普通标题栏
 */
class ActionBarView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.base_action_bar, this)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ActionBarView)

        action_bar_title.text = typedArray.getString(R.styleable.ActionBarView_title)

        val title2 = typedArray.getString(R.styleable.ActionBarView_title2)
        if (TextUtils.isEmpty(title2)) {
            action_bar_title2.visibility = View.GONE
        } else {
            action_bar_title2.visibility = View.VISIBLE
            action_bar_title2.text = title2
        }

        val backIcon = typedArray.getDrawable(R.styleable.ActionBarView_back_icon)
        if (backIcon != null) {
            action_bar_back.setImageDrawable(backIcon)
        }

        val actionIcon0 = typedArray.getString(R.styleable.ActionBarView_action_icon0)
        if (actionIcon0 == null) {
            action_bar_icon0.visibility = View.GONE
        } else {
            action_bar_icon0.visibility = View.VISIBLE
            action_bar_icon0.text = actionIcon0
        }

        val actionIcon1 = typedArray.getDrawable(R.styleable.ActionBarView_action_icon1)
        if (actionIcon1 == null) {
            action_bar_icon1.visibility = View.GONE
        } else {
            action_bar_icon1.visibility = View.VISIBLE
            action_bar_icon1.setImageDrawable(actionIcon1)
        }

        val actionIcon2 = typedArray.getDrawable(R.styleable.ActionBarView_action_icon2)
        if (actionIcon2 == null) {
            action_bar_icon2.visibility = View.GONE
        } else {
            action_bar_icon2.visibility = View.VISIBLE
            action_bar_icon2.setImageDrawable(actionIcon2)
        }
        val actionIcon3 = typedArray.getString(R.styleable.ActionBarView_action_icon3)
        if (TextUtils.isEmpty(actionIcon3)) {
            action_bar_icon3.visibility = View.GONE
        } else {
            action_bar_icon3.visibility = View.VISIBLE
            action_bar_icon3.text = actionIcon3
        }

        typedArray.recycle()

        initView(context)
    }

    /**设置操作栏监听*/
    private fun initView(context: Context) {
        action_bar_back.setOnClickListener {
            (context as Activity).finish()
        }
    }

    fun getBack(): ImageView {
        return this.action_bar_back
    }

    fun getTitle(): TextView {
        return this.action_bar_title
    }

    fun setTitle(title: String): TextView {
        if (TextUtils.isEmpty(title)) {
            action_bar_title.visibility = View.GONE
        } else {
            action_bar_title.visibility = View.VISIBLE
            action_bar_title.text = title
        }
        return this.action_bar_title
    }

    fun setTitle2(title2: String): TextView {
        if (TextUtils.isEmpty(title2)) {
            action_bar_title2.visibility = View.GONE
        } else {
            action_bar_title2.visibility = View.VISIBLE
            action_bar_title2.text = title2
        }
        return this.action_bar_title2
    }

    fun setAction0(name: String): TextView {
        this.action_bar_icon0.text = name
        this.action_bar_icon0.visibility = View.VISIBLE
        return this.action_bar_icon0
    }

    fun getAction0(): TextView {
        return this.action_bar_icon0
    }

    fun getAction1(): ImageView {
        return this.action_bar_icon1
    }

    fun getAction2(): ImageView {
        return this.action_bar_icon2
    }

    fun getAction3(): TextView {
        return this.action_bar_icon3
    }

    fun getActionBarLL(): LinearLayout {
        return this.action_bar_ll
    }
}