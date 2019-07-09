package qsos.core.lib.view.widget.label

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import qsos.lib.lib.R
import java.util.*

class StackLabel : RelativeLayout {

    private var textColor = 0
    private var textSize = 0
    private var paddingVertical = 0
    private var paddingHorizontal = 0
    private var itemMargin = 0
    private var deleteButton = false

    private var deleteButtonImage = -1
    private var labelBackground = -1

    private var selectMode = false
    private var selectBackground = -1
    private var selectTextColor = -1
    private var maxSelectNum = 0

    private var onLabelClickListener: OnLabelClickListener? = null
    private var labels: List<String>? = null

    private var items: MutableList<View>? = null
    private var newHeight = 0

    private var selectIndexs: MutableList<Int> = ArrayList()
    // 初始化已选择列表
    private var whichIsSelected: List<String>? = null

    val selectIndexList: List<Int>
        get() = selectIndexs

    val selectIndexArray: IntArray
        get() {
            val arrays = IntArray(selectIndexs.size)
            for (i in selectIndexs.indices) {
                arrays[i] = selectIndexs[i]
            }
            return arrays
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        loadAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        loadAttrs(context, attrs)
    }

    private fun loadAttrs(context: Context, attrs: AttributeSet) {
        try {
            // 默认值
            textColor = Color.argb(230, 0, 0, 0)
            textSize = dp2px(12f)
            paddingVertical = dp2px(8f)
            paddingHorizontal = dp2px(12f)
            itemMargin = dp2px(4f)
            deleteButton = false

            // 加载值
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StackLabel)
            textColor = typedArray.getColor(R.styleable.StackLabel_textColor, textColor)
            textSize = typedArray.getDimensionPixelOffset(R.styleable.StackLabel_textSize, textSize)
            paddingVertical = typedArray.getDimensionPixelOffset(R.styleable.StackLabel_paddingVertical, paddingVertical)
            paddingHorizontal = typedArray.getDimensionPixelOffset(R.styleable.StackLabel_paddingHorizontal, paddingHorizontal)
            itemMargin = typedArray.getDimensionPixelOffset(R.styleable.StackLabel_itemMargin, itemMargin)
            deleteButton = typedArray.getBoolean(R.styleable.StackLabel_deleteButton, deleteButton)

            deleteButtonImage = typedArray.getResourceId(R.styleable.StackLabel_deleteButtonImage, deleteButtonImage)
            labelBackground = typedArray.getResourceId(R.styleable.StackLabel_labelBackground, labelBackground)

            selectMode = typedArray.getBoolean(R.styleable.StackLabel_selectMode, selectMode)
            selectBackground = typedArray.getResourceId(R.styleable.StackLabel_selectBackground, selectBackground)
            selectTextColor = typedArray.getColor(R.styleable.StackLabel_selectTextColor, selectTextColor)
            maxSelectNum = typedArray.getInt(R.styleable.StackLabel_maxSelectNum, maxSelectNum)

            if (selectBackground == -1) selectBackground = R.color.colorPrimary
            if (labelBackground == -1) labelBackground = R.color.colorPrimary
            if (selectTextColor == -1) selectTextColor = R.color.black_light
            typedArray.recycle()
        } catch (e: Exception) {
        }

    }

    private fun dp2px(dpValue: Float): Int {
        return (0.5f + dpValue * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun px2dp(pxValue: Int): Float {
        return pxValue / Resources.getSystem().displayMetrics.density
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        refreshViews()
        // 设置宽高
        setMeasuredDimension(measuredWidth, newHeight)
    }

    private fun refreshViews() {
        val maxWidth = measuredWidth

        if (labels != null && labels!!.isNotEmpty()) {
            newHeight = 0
            if (items != null && items!!.isNotEmpty()) {
                for (i in items!!.indices) {
                    val item = items!![i]

                    val mWidth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    val mHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    item.measure(mWidth, mHeight)

                    var n_x = 0
                    var n_y = 0
                    var o_y = 0

                    if (i != 0) {
                        n_x = items!![i - 1].x.toInt() + items!![i - 1].measuredWidth
                        n_y = items!![i - 1].y.toInt() + items!![i - 1].measuredHeight
                        o_y = items!![i - 1].y.toInt()
                    }

                    if (n_x + item.measuredWidth > maxWidth) {
                        n_x = 0
                        o_y = n_y
                    }

                    item.y = o_y.toFloat()
                    item.x = n_x.toFloat()

                    newHeight = (item.y + item.measuredHeight).toInt()
                }
            }
        }
    }

    fun getLabels(): List<String>? {
        return labels
    }

    fun setLabels(l: List<String>?): StackLabel {
        labels = l

        removeAllViews()
        items = ArrayList()
        if (labels != null && labels!!.isNotEmpty()) {

            newHeight = 0
            for (i in labels!!.indices) {
                val item = LayoutInflater.from(context).inflate(R.layout.layout_label, this, false)

                newHeight = item.measuredHeight

                addView(item)
                items!!.add(item)
            }

            initItem()
        }
        return this
    }

    private fun initItem() {
        if (labels!!.isNotEmpty()) {
            selectIndexs = ArrayList()
            for (i in items!!.indices) {
                val item = items!![i]

                val s = labels!![i]
                val boxLabel = item.findViewById<LinearLayout>(R.id.box_label)
                val txtLabel = item.findViewById<TextView>(R.id.txt_label)
                val imgDelete = item.findViewById<ImageView>(R.id.img_delete)

                txtLabel.text = s
                txtLabel.setTextColor(textColor)
                txtLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())

                boxLabel.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
                val p = boxLabel.layoutParams as MarginLayoutParams
                p.setMargins(0, itemMargin, itemMargin, 0)
                boxLabel.requestLayout()

                if (deleteButton) {
                    imgDelete.visibility = View.VISIBLE
                } else {
                    imgDelete.visibility = View.GONE
                }
                if (deleteButtonImage != -1) imgDelete.setImageResource(deleteButtonImage)
                boxLabel.setBackgroundResource(labelBackground)

                val mWidth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                val mHeight = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                item.measure(mWidth, mHeight)

                boxLabel.setOnClickListener { v ->
                    if (selectMode) {
                        for (mItem in items!!) {
                            mItem.findViewById<LinearLayout>(R.id.box_label).setBackgroundResource(labelBackground)
                        }
                        if (selectIndexs.contains(i)) {
                            var ind = 0
                            for (mi in selectIndexs.indices) {
                                if (selectIndexs[mi] == i) {
                                    ind = mi
                                    break
                                }
                            }
                            selectIndexs.removeAt(ind)
                        } else {
                            if (maxSelectNum == 1) selectIndexs.clear()
                            if (maxSelectNum <= 0 || maxSelectNum > 0 && selectIndexs.size < maxSelectNum) {
                                selectIndexs.add(i)
                            }
                        }
                        for (mIndex in selectIndexs) {
                            val mItem = items!![mIndex]
                            val mBoxLabel = mItem.findViewById<LinearLayout>(R.id.box_label)
                            val mTxtLabel = mItem.findViewById<TextView>(R.id.txt_label)
                            mBoxLabel.setBackgroundResource(selectBackground)
                            mTxtLabel.setTextColor(selectTextColor)
                        }
                    }
                    if (onLabelClickListener != null)
                        onLabelClickListener!!.onClick(i, v, labels!![i])
                }

                if (whichIsSelected != null) {
                    for (selectStr in whichIsSelected!!) {
                        if (s == selectStr) {
                            selectIndexs.add(i)
                            boxLabel.setBackgroundResource(selectBackground)
                            txtLabel.setTextColor(selectTextColor)
                        }
                    }
                }
            }
            whichIsSelected = null
        }
    }

    fun getOnLabelClickListener(): OnLabelClickListener? {
        return onLabelClickListener
    }

    fun setOnLabelClickListener(onLabelClickListener: OnLabelClickListener): StackLabel {
        this.onLabelClickListener = onLabelClickListener
        return this
    }

    fun isDeleteButton(): Boolean {
        return deleteButton
    }

    fun setDeleteButton(deleteButton: Boolean): StackLabel {
        this.deleteButton = deleteButton
        initItem()
        return this
    }

    fun isSelectMode(): Boolean {
        return selectMode
    }

    fun setSelectMode(selectMode: Boolean): StackLabel {
        this.selectMode = selectMode
        setLabels(labels)
        return this
    }

    fun setSelectMode(selectMode: Boolean, whichIsSelected: List<String>?): StackLabel {
        val mWhichIsSelected = whichIsSelected
        this.selectMode = selectMode
        if (selectMode) {
            this.whichIsSelected = mWhichIsSelected
        }
        setLabels(labels)
        return this
    }

    fun getSelectBackground(): Int {
        return selectBackground
    }

    fun setSelectBackground(selectBackground: Int): StackLabel {
        this.selectBackground = selectBackground
        setLabels(labels)
        return this
    }

    fun getSelectTextColor(): Int {
        return selectTextColor
    }

    fun setSelectTextColor(selectTextColor: Int): StackLabel {
        this.selectTextColor = selectTextColor
        return this
    }

    fun getMaxSelectNum(): Int {
        return maxSelectNum
    }

    fun setMaxSelectNum(maxSelectNum: Int): StackLabel {
        this.maxSelectNum = maxSelectNum
        setLabels(labels)
        return this
    }
}