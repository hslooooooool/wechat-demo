package qsos.core.form.data

import androidx.lifecycle.ViewModel
import qsos.lib.base.data.form.FormEntity
import qsos.lib.base.data.form.FormItem
import qsos.lib.base.data.form.FormType
import qsos.lib.base.data.form.Value
import qsos.lib.netservice.file.FileRepository

/**
 * @author : 华清松
 * @description : 表单数据实现
 */
class FormModelIml(
        val formRepo: FormRepository
) : ViewModel(), IFormModel {

    val fileRepo = FileRepository()

    override fun getForm(formType: FormType, connectId: String?) {
        formRepo.getForm(formType, connectId)
    }

    override fun getFormByDB(formId: Long) {
        formRepo.getFormByDB(formId)
    }

    override fun insertForm(form: FormEntity) {
        formRepo.insertForm(form)
    }

    override fun insertFormItem(formItem: FormItem) {
        formRepo.insertFormItem(formItem)
    }

    override fun insertValue(formItemValue: Value) {
        formRepo.insertValue(formItemValue)
    }

    override fun addValueToFormItem(formItemValue: Value) {
        formRepo.addValueToFormItem(formItemValue)
    }

    override fun deleteForm(form: FormEntity) {
        formRepo.deleteForm(form)
    }

    override fun postForm(formType: String, connectId: String?, formId: Long) {
        formRepo.postForm(formType, connectId, formId)
    }

    override fun getFormItemByDB(formItemId: Long) {
        formRepo.getFormItemByDB(formItemId)
    }

    override fun updateValue(value: Value) {
        formRepo.updateValue(value)
    }

    override fun getUsers(connectId: String?, formItem: FormItem, key: String?) {
        formRepo.getUsers(connectId, formItem, key)
    }
}