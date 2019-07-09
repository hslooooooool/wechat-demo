package qsos.core.form.view.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.R
import qsos.core.form.db.FormDatabase
import qsos.core.form.view.hodler.ItemFormTextAddItemHolder
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.form.Value
import qsos.lib.base.utils.ToastUtils

/**
 * @author : 华清松
 * @description : 添加文本列表容器
 */
class FormAddTextAdapter(texts: ArrayList<Value>)
    : BaseAdapter<Value>(texts) {

    override fun getHolder(view: View, viewType: Int): BaseHolder<Value>? {
        return ItemFormTextAddItemHolder(view, this)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.form_item_text_add_item
    }

    override fun getLayoutId(viewType: Int): Int? {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        when (view.id) {
            R.id.form_item_text_add_item_edit -> {
                edit(position)
            }
            R.id.form_item_text_add_item_delete -> {
                if (!data[position].limit_edit) {
                    Completable.fromAction {
                        FormDatabase.getInstance(mContext).formItemValueDao.delete(data[position])
                    }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        data.removeAt(position)
                                        notifyDataSetChanged()
                                    },
                                    {
                                        ToastUtils.showToast(mContext, "删除失败")
                                        notifyDataSetChanged()
                                    }
                            )
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }

    fun edit(position: Int) {
        val value = data[position]
        var inputContent = value.input_value
        MaterialDialog(mContext)
                .title(text = "修改")
                .show {
                    input(hint = "请输入", prefill = value.input_value, maxLength = 25) { _, text ->
                        inputContent = text.toString().trim()
                    }
                    positiveButton(text = "保存") {
                        if (TextUtils.isEmpty(inputContent)) {
                            ToastUtils.showToast(mContext, "您没有输入任何内容")
                        } else {
                            value.input_value = inputContent
                            Completable.fromAction {
                                FormDatabase.getInstance(mContext).formItemValueDao.update(value)
                            }
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            {
                                                data[position] = value
                                                notifyDataSetChanged()
                                            },
                                            {
                                                ToastUtils.showToast(mContext, "错误,更新失败")
                                            }
                                    )
                        }

                    }
                    negativeButton(text = "取消") {

                    }
                }
    }
}
