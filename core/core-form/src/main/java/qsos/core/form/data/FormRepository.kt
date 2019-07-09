package qsos.core.form.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import qsos.core.form.db.FormDatabase
import qsos.core.form.utils.FormVerifyUtils
import qsos.lib.base.data.form.*
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.ApiObserver
import qsos.lib.netservice.ObservableService
import qsos.lib.netservice.file.BaseRepository
import qsos.lib.netservice.file.HttpResult
import timber.log.Timber

/**
 * @author : 华清松
 * @description : 表单数据获取
 */
@SuppressLint("CheckResult")
class FormRepository(
        private val mContext: Context
) : IFormModel, BaseRepository() {

    /**NOTICE 数据库操作*/

    override fun getFormByDB(formId: Long) {
        FormDatabase.getInstance(mContext).formDao.getFormById(formId)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    it.form_item = FormDatabase.getInstance(mContext).formItemDao.getFormItemByFormId(it.id!!)
                    it.form_item?.forEach { formItem ->
                        formItem.form_item_value!!.values = arrayListOf()
                        formItem.form_item_value!!.values!!.addAll(FormDatabase.getInstance(mContext).formItemValueDao.getValueByFormItemId(formItem.id!!))
                        Timber.tag("数据库").i("查询formItem={formItem.id}下Value列表：${Gson().toJson(formItem.form_item_value!!.values)}")
                    }
                }
                .observeOn(Schedulers.io())
                .subscribe(
                        {
                            dbFormEntity.postValue(it)
                        },
                        {
                            dbFormEntity.postValue(null)
                        }
                )
    }

    override fun insertForm(form: FormEntity) {
        Observable.create<FormEntity> {
            val id = FormDatabase.getInstance(mContext).formDao.insert(form)
            form.id = id
            form.form_item?.forEach { formItem ->
                formItem.form_id = id
                insertFormItem(formItem)
            }
            if (form.id != null) {
                it.onNext(form)
            } else {
                it.onError(Exception("数据插入失败"))
            }
        }.subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            getFormByDB(it.id!!)
                        },
                        {
                            it.printStackTrace()
                            dbFormEntity.postValue(null)
                        }
                )
    }

    override fun insertFormItem(formItem: FormItem) {
        val formItemId = FormDatabase.getInstance(mContext).formItemDao.insert(formItem)
        formItem.form_item_value!!.values?.forEach {
            it.form_item_id = formItemId
            insertValue(it)
        }
    }

    override fun insertValue(formItemValue: Value) {
        FormDatabase.getInstance(mContext).formItemValueDao.insert(formItemValue)
    }

    override fun addValueToFormItem(formItemValue: Value) {
        Observable.create<Value> {
            val id = FormDatabase.getInstance(mContext).formItemValueDao.insert(formItemValue)
            formItemValue.id = id
            it.onNext(formItemValue)
        }.subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            addValueToFormItem.postValue(it)
                        },
                        {
                            addValueToFormItem.postValue(null)
                        }
                )
    }

    override fun deleteForm(form: FormEntity) {
        Observable.create<Boolean> {
            FormDatabase.getInstance(mContext).formDao.delete(form)
            it.onNext(true)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dbDeleteForm.postValue(it)
                }
    }

    override fun getFormItemByDB(formItemId: Long) {
        FormDatabase.getInstance(mContext).formItemDao.getFormItemByIdF(formItemId)
                .observeOn(Schedulers.io())
                .map {
                    it.form_item_value!!.values = arrayListOf()
                    it.form_item_value!!.values?.addAll(FormDatabase.getInstance(mContext).formItemValueDao.getValueByFormItemId(formItemId))
                    it
                }.observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    dbFormItem.postValue(it)
                }
    }

    override fun updateValue(value: Value) {
        FormDatabase.getInstance(mContext).formItemValueDao.update(value)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    updateValueStatus.postValue(true)
                }
    }

    override fun postForm(formType: String, connectId: String?, formId: Long) {
        FormDatabase.getInstance(mContext).formDao.getFormById(formId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .subscribe(
                        {
                            postForm(formType, connectId, it)
                        },
                        {
                            postFormStatus.postValue(FormVerifyUtils.Verify(false, "提交失败 $it"))
                        }
                )

    }

    /**NOTICE 网络请求操作*/

    override fun getForm(formType: FormType, connectId: String?) {
        val form = when (formType) {
            FormType.ADD_EXECUTE -> FormTransUtils.getTestExecuteData()
            FormType.ADD_ORDER_FEEDBACK -> FormTransUtils.getTestOrderFeedbackData()
            else -> FormEntity()
        }
        if (form.form_item == null) {
            dbFormEntity.postValue(null)
        } else {
            insertForm(form)
        }
    }

    /**将从数据库中查出的表单，提交表单到服务器*/
    private fun postForm(formType: String, connectId: String?, form: FormEntity) {
        ObservableService.setObservableBase(
                ApiEngine.createService(ApiForm::class.java)
                        .postForm(formType, connectId, form)
        ).subscribe(
                object : ApiObserver<String>(httpResult) {

                    override fun onNext(it: String) {
                        super.onNext(it)
                        postFormStatus.postValue(FormVerifyUtils.Verify(true, "提交成功"))
                        // NOTICE 提交成功后清除表单
                        deleteForm(form)
                    }

                    override fun onError(it: HttpResult) {
                        super.onError(it)
                        postFormStatus.postValue(FormVerifyUtils.Verify(false, "提交失败 ${it.msg}"))
                    }
                }
        )
    }

    override fun getUsers(connectId: String?, formItem: FormItem, key: String?) {
        ObservableService.setObservableBase(
                ApiEngine.createService(ApiForm::class.java)
                        .getUsers(null, formItem.form_item_value?.limit_type, key)
        ).subscribe(
                object : ApiObserver<List<Value>>(httpResult) {

                    override fun onNext(it: List<Value>) {
                        super.onNext(it)
                        val newUsers = ArrayList<FormUserEntity>()
                        it.forEach { value ->
                            var cb = false
                            val oldUsers = dbFormItem.value?.form_item_value?.values
                            oldUsers!!.forEach { user ->
                                if (user.user_phone == value.user_phone) {
                                    cb = true
                                }
                            }
                            val user = FormUserEntity(
                                    formItem.id,
                                    value.user_id,
                                    value.user_name,
                                    value.user_header,
                                    value.user_phone,
                                    cb,
                                    value.limit_edit
                            )
                            newUsers.add(user)
                        }
                        userList.postValue(newUsers)
                    }

                    override fun onError(it: HttpResult) {
                        super.onError(it)
                        userList.postValue(null)
                    }
                }
        )
    }

    // 获取表单数据结果
    val dbFormEntity = MutableLiveData<FormEntity?>()

    // 获取表单项数据结果
    val dbFormItem = MutableLiveData<FormItem>()

    // 删除表单数据结果
    val dbDeleteForm = MutableLiveData<Boolean>()

    // 用户列表数据结果
    val userList = MutableLiveData<List<FormUserEntity>?>()

    // 提交表单数据结果
    val postFormStatus = MutableLiveData<FormVerifyUtils.Verify>()

    // 更新列表项值数据结果
    val updateValueStatus = MutableLiveData<Boolean>()

    // 插入Value到FormItem后数据结果
    val addValueToFormItem = MutableLiveData<Value?>()
}