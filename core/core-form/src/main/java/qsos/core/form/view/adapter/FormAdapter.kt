package qsos.core.form.view.adapter

import android.annotation.SuppressLint
import android.text.InputType
import android.text.TextUtils
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.alibaba.android.arouter.launcher.ARouter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.core.form.view.hodler.*
import qsos.core.form.view.widget.dialog.BottomDialogUtils
import qsos.core.form.view.widget.dialog.OnDateListener
import qsos.core.form.view.widget.dialog.Operation
import qsos.core.form.view.widget.pop.PopupWindowUtils
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.data.app.FileTypeEnum
import qsos.lib.base.data.form.FormItem
import qsos.lib.base.data.form.FormItemType
import qsos.lib.base.data.form.Value
import qsos.lib.base.routepath.FormPath
import qsos.lib.base.utils.ToastUtils
import java.util.*

/**
 * @author : 华清松
 * @desc : 表单列表容器
 */
@SuppressLint("CheckResult")
class FormAdapter(connectId: String?, findItems: ArrayList<FormItem>) : BaseAdapter<FormItem>(findItems) {

    private val mConnectId = connectId

    interface OnFileListener {
        fun getFile(type: FileTypeEnum, position: Int)
    }

    private var fileListener: OnFileListener? = null

    fun setOnFileListener(listener: OnFileListener) {
        this.fileListener = listener
    }

    private var backListener: OnTListener<Boolean>? = null

    override fun getHolder(view: View, viewType: Int): BaseHolder<FormItem>? {
        when (viewType) {
            /*文本*/
            R.layout.form_item_text -> return ItemFormTextHolder(view, this)
            /*输入*/
            R.layout.form_item_input -> return ItemFormInputHolder(view, this)
            /*选项*/
            R.layout.form_item_check -> return ItemFormCheckHolder(view, this)
            /*时间*/
            R.layout.form_item_time -> return ItemFormTimeHolder(view, this)
            /*人员*/
            R.layout.form_item_users -> return ItemFormUserHolder(view, this)
            /*附件*/
            R.layout.form_item_file -> return ItemFormFileHolder(view, this)
            /*分数*/
            R.layout.form_item_text_add -> return ItemFormTextAddHolder(view, this)
            /*其它*/
            else -> return ItemFormTextHolder(view, this)
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (data[position].form_item_type) {
            /*文本*/
            FormItemType.TEXT.tag -> return R.layout.form_item_text
            /*输入*/
            FormItemType.INPUT.tag -> return R.layout.form_item_input
            /*选项*/
            FormItemType.CHOOSE.tag -> return R.layout.form_item_check
            /*时间*/
            FormItemType.TIME.tag -> return R.layout.form_item_time
            /*人员*/
            FormItemType.USER.tag -> return R.layout.form_item_users
            /*附件*/
            FormItemType.FILE.tag -> return R.layout.form_item_file
            /*位置*/
            FormItemType.LOCATION.tag -> return R.layout.form_item_location
            /*添加文本项*/
            FormItemType.ADD_TEXT.tag -> return R.layout.form_item_text_add
        }
        return R.layout.form_item_text
    }

    override fun getLayoutId(viewType: Int): Int? {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        if (data[position].form_item_status == 0) {
            return
        }
        when (view.id) {
            /**表单项提示*/
            R.id.item_form_key -> {
                PopupWindowUtils.showTextOk(mContext, data[position].form_item_hint!!, null)
            }
            /**选择时间*/
            R.id.item_form_time -> {
                chooseTime(view, position)
            }
            /**表单输入项*/
            R.id.item_form_input -> {
                when (data[position].form_item_value!!.limit_max) {
                    in 1..25 -> inputReplace(position)
                    else -> {
                        // 大于25个字的输入跳转页面输入
                        ARouter.getInstance().build(FormPath.ITEM_INPUT)
                                .withLong("item_id", data[position].id!!)
                                .navigation()
                    }
                }
            }
            /**表单文本项输入项*/
            R.id.item_form_text_add -> {
                addTextItem(position)
            }
            /**选择人员*/
            R.id.tv_item_form_users_size -> {
                ARouter.getInstance().build(FormPath.ITEM_USERS)
                        .withLong("item_id", data[position].id!!)
                        .withString("connect_id", mConnectId)
                        .navigation()
            }
            /**选择选项*/
            R.id.form_item_check -> {
                choose(position)
            }
            /**选择附件*/
            R.id.form_item_file_take_photo, R.id.form_item_file_take_album, R.id.form_item_file_take_video, R.id.form_item_file_take_audio -> {
                checkTakeLimit(view, position)
            }
            else -> {

            }
        }
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {}

    private fun chooseTime(view: View, position: Int) {
        var values = data[position].form_item_value?.values
        values = values ?: arrayListOf()
        val size = values.size
        if (size == 1) {
            val date = values[0].limit_type

            var showDay = true
            if ("yyyy-MM-dd HH:mm" == date) {
                showDay = true
            } else if ("yyyy-MM-dd" == date) {
                showDay = true
            }
            backListener?.getItem(false)
            BottomDialogUtils.showRealDateChoseView(view.context,
                    "yyyy-MM-dd HH:mm" == date, showDay,
                    null, null, Date(values[0].time), object : OnDateListener {
                override fun setDate(type: Int?, date: Date?) {
                    backListener?.getItem(true)
                    if (date != null) {
                        data[position].form_item_value!!.values!![0].time = date.time

                        Completable.fromAction {
                            FormDatabase.getInstance(mContext).formItemValueDao.update(data[position].form_item_value!!.values!!)
                        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                {
                                    notifyItemChanged(position)
                                },
                                {
                                    ToastUtils.showToast(mContext, "选择失败")
                                    notifyItemChanged(position)
                                }
                        )
                    }
                }
            }

            )
        } else if (size == 2) {
            val date1 = values[0].limit_type
            val date2 = values[1].limit_type
            var showDay1 = true
            if ("yyyy-MM-dd HH:mm" == date1) {
                showDay1 = true
            } else if ("yyyy-MM-dd" == date1) {
                showDay1 = true
            }
            var showDay2 = true
            if ("yyyy-MM-dd HH:mm" == date1) {
                showDay2 = true
            } else if ("yyyy-MM-dd" == date1) {
                showDay2 = true
            }
            backListener?.getItem(false)
            BottomDialogUtils.showRealDateChoseView(view.context,
                    "yyyy-MM-dd HH:mm" == date1, showDay1,
                    null, null, Date(values[0].time),
                    object : OnDateListener {
                        override fun setDate(type: Int?, date: Date?) {
                            if (date != null) {
                                data[position].form_item_value!!.values!![0].time = date.time
                                BottomDialogUtils.showRealDateChoseView(view.context, "yyyy-MM-dd HH:mm" == date2,
                                        showDay2,
                                        Date(values[0].time), null, Date(values[1].time),
                                        object : OnDateListener {
                                            override fun setDate(type: Int?, date: Date?) {
                                                backListener?.getItem(true)
                                                if (date != null) {
                                                    data[position].form_item_value!!.values!![1].time = date.time
                                                    Completable.fromAction {
                                                        FormDatabase.getInstance(mContext).formItemValueDao.update(data[position].form_item_value!!.values!!)
                                                    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                                            {
                                                                notifyItemChanged(position)
                                                            },
                                                            {
                                                                ToastUtils.showToast(mContext, "选择失败")
                                                                notifyItemChanged(position)
                                                            }
                                                    )
                                                }
                                            }

                                        })

                            }
                        }

                    }
            )
        }

    }

    private fun choose(position: Int) {
        // 单选
        if (data[position].form_item_value!!.limit_max == 1) {
            val operations = arrayListOf<Operation>()
            val values = data[position].form_item_value!!.values
            values!!.forEach {
                val operation = Operation()
                operation.key = it.ck_name
                operation.value = it.id
                operation.isCheck = it.ck_check
                operations.add(operation)
            }
            backListener?.getItem(false)
            BottomDialogUtils.setBottomChoseListView(mContext, operations, object : OnTListener<Operation> {
                override fun getItem(item: Operation) {
                    backListener?.getItem(true)
                    data[position].form_item_value!!.values!!.forEach {
                        it.ck_check = it.id == item.value
                    }
                    if (data[position].form_item_value!!.values!!.isNotEmpty()) {
                        Completable.fromAction {
                            FormDatabase.getInstance(mContext).formItemValueDao.update(data[position].form_item_value!!.values!!)
                        }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        {
                                            notifyItemChanged(position)
                                        },
                                        {
                                            ToastUtils.showToast(mContext, "选择失败")
                                            notifyItemChanged(position)
                                        }
                                )
                    } else {
                        notifyItemChanged(position)
                    }
                }
            })
        } else {
            // 多选
            val operations = arrayListOf<Operation>()
            val values = data[position].form_item_value!!.values
            values!!.forEach {
                val operation = Operation()
                operation.key = it.ck_name
                operation.value = it.id
                operation.isCheck = it.ck_check
                operations.add(operation)
            }
            backListener?.getItem(false)
            BottomDialogUtils.setBottomSelectListView(mContext, data[position].form_item_key, operations, object : OnTListener<List<Operation>> {
                override fun getItem(item: List<Operation>) {
                    backListener?.getItem(true)
                    data[position].form_item_value!!.values!!.forEach { value ->
                        item.forEach {
                            if (value.id == it.value) {
                                value.ck_check = it.isCheck
                            }
                        }
                    }
                    if (data[position].form_item_value!!.values!!.isNotEmpty()) {
                        Completable.fromAction {
                            FormDatabase.getInstance(mContext).formItemValueDao.update(data[position].form_item_value!!.values!!)
                        }.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        {
                                            notifyItemChanged(position)
                                        },
                                        {
                                            ToastUtils.showToast(mContext, "选择失败")
                                            notifyItemChanged(position)
                                        }
                                )
                    } else {
                        notifyItemChanged(position)
                    }
                }

            })
        }
    }

    private fun checkTakeLimit(view: View, position: Int) {
        val limitMax = data[position].form_item_value!!.limit_max
        val valueSize: Int? = data[position].form_item_value!!.values?.size ?: 0
        if (limitMax != null && valueSize ?: 0 >= limitMax) {
            ToastUtils.showToast(view.context, "已达到添加熟练限制")
        } else {
            fileListener?.getFile(when (view.id) {
                /**拍照*/
                R.id.form_item_file_take_photo -> FileTypeEnum.PHOTO
                /**相册*/
                R.id.form_item_file_take_album -> FileTypeEnum.IMAGE
                /**视频*/
                R.id.form_item_file_take_video -> FileTypeEnum.VIDEO
                /**语音*/
                R.id.form_item_file_take_audio -> FileTypeEnum.AUDIO
                else -> FileTypeEnum.PHOTO
            }, position)
        }
    }

    private fun addTextItem(position: Int) {
        val formItem = data[position]
        val mValue = Value()
        mValue.form_item_id = formItem.id
        var inputType: Int = InputType.TYPE_CLASS_TEXT
        var max: Int? = null
        var min: Int? = null
        when (formItem.form_item_value!!.limit_type) {
            "phone" -> {
                max = 11
                min = 11
                inputType = InputType.TYPE_CLASS_PHONE
            }
        }
        MaterialDialog(mContext)
                .title(text = "${formItem.form_item_key}")
                .show {
                    input(inputType = inputType, hint = formItem.form_item_hint, maxLength = max) { _, text ->
                        mValue.input_value = text.toString().trim()
                    }
                    positiveButton(text = "保存") {
                        if (TextUtils.isEmpty(mValue.input_value)) {
                            ToastUtils.showToast(mContext, "您没有输入任何内容")
                        } else {
                            if (min != null && min > mValue.input_value!!.length) {
                                ToastUtils.showToast(mContext, "您输入字数不符合要求")
                            } else {
                                Completable.fromAction {
                                    FormDatabase.getInstance(mContext).formItemValueDao.insert(mValue)
                                }
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                {
                                                    data[position].form_item_value!!.values!!.add(mValue)
                                                    notifyDataSetChanged()
                                                },
                                                {
                                                    ToastUtils.showToast(mContext, "错误,添加失败")
                                                }
                                        )
                            }
                        }
                    }
                    negativeButton(text = "取消") {

                    }
                }
    }

    /**最大字数小于25，可以弹窗输入*/
    private fun inputReplace(position: Int) {
        val formItem = data[position]
        val values = formItem.form_item_value!!.values
        val mValue: Value
        if (values == null || values.isEmpty()) {
            mValue = Value()
            mValue.form_item_id = formItem.id
        } else {
            mValue = values[0]
        }
        var inputContent = mValue.input_value
        var inputType: Int = InputType.TYPE_CLASS_TEXT
        var max = formItem.form_item_value!!.limit_max
        val min = formItem.form_item_value!!.limit_min
        when (formItem.form_item_value!!.limit_type) {
            "phone" -> {
                max = 11
                inputType = InputType.TYPE_CLASS_PHONE
            }
        }
        if (max == 0) max = null
        MaterialDialog(mContext)
                .title(text = "${formItem.form_item_key}")
                .show {
                    input(
                            inputType = inputType, prefill = mValue.input_value,
                            hint = "${formItem.form_item_hint}",
                            maxLength = max
                    ) { _, text ->
                        inputContent = text.toString().trim()
                    }
                    positiveButton(text = "保存") {
                        if (TextUtils.isEmpty(inputContent)) {
                            ToastUtils.showToast(mContext, "您没有输入任何内容")
                        } else {
                            if (min != null && min > inputContent!!.length) {
                                ToastUtils.showToast(mContext, "您输入字数不符合要求")
                            } else {
                                mValue.input_value = inputContent
                                Completable.fromAction {
                                    val id = FormDatabase.getInstance(mContext).formItemValueDao.insert(mValue)
                                    mValue.id = id
                                }.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                {
                                                    data[position].form_item_value!!.values!!.clear()
                                                    data[position].form_item_value!!.values!!.add(mValue)
                                                    notifyDataSetChanged()
                                                },
                                                {
                                                    ToastUtils.showToast(mContext, "错误,更新失败")
                                                }
                                        )
                            }
                        }

                    }
                    negativeButton(text = "取消")
                }
    }
}
