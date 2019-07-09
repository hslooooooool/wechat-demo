package qsos.core.form.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.form_activity_main.*
import qsos.core.file.IFileHelper
import qsos.core.file.audio.*
import qsos.core.form.R
import qsos.core.form.data.FormModelIml
import qsos.core.form.data.FormRepository
import qsos.core.form.utils.FormVerifyUtils
import qsos.core.form.view.adapter.FormAdapter
import qsos.core.form.view.other.FormItemDecoration
import qsos.lib.base.data.app.FileTypeEnum
import qsos.lib.base.data.form.FormEntity
import qsos.lib.base.data.form.FormItem
import qsos.lib.base.data.form.FormType
import qsos.lib.base.data.form.Value
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.data.http.UDFileEntity
import qsos.lib.base.routepath.FormPath
import qsos.lib.base.utils.LogUtil
import qsos.lib.base.utils.activity.ActivityUtils
import qsos.lib.base.utils.file.FileUtils
import qsos.lib.base.utils.record.AudioDialogUtil
import qsos.lib.netservice.file.HttpResult
import java.io.File

/**
 * @author : 华清松
 * @description : 表单界面
 */
@Route(group = FormPath.FORM, path = FormPath.MAIN)
class FormActivity(
        @JvmField @Autowired(name = FormPath.FORM_CONNECT_ID) var connectId: String? = "",
        @JvmField @Autowired(name = FormPath.FORM_TYPE) var formType: String? = "",
        @JvmField @Autowired(name = FormPath.FORM_EDIT) var formEdit: Boolean = true,
        @JvmField @Autowired(name = FormPath.FORM_POST_TYPE) var formPostType: Int = 0
) : AbsFormActivity(), IRespondView {

    private lateinit var mAdapter: FormAdapter
    private lateinit var mRecordDialog: AudioDialogUtil
    private lateinit var mAudioRecordHelper: AudioRecordHelper
    private var canRecord = true
    /**录音按键上下移动距离*/
    private var moveY = 0F

    /**表单数据实现类*/
    private lateinit var mModel: FormModelIml
    private var type: FormType? = null
    private val mFormList = ArrayList<FormItem>()
    private var mFileType: FileTypeEnum? = null
    private var mForm: FormEntity? = null
    /**待上传的表单项ID*/
    private var uploadFileFormItemId: Long = 0L
    private var mAddPosition: Int? = null

    override val layoutId: Int = R.layout.form_activity_main
    override val reload: Boolean = true

    override fun initData(savedInstanceState: Bundle?) {
        mModel = FormModelIml(FormRepository(mContext!!))
        mAudioRecordHelper = AudioRecordHelper(this, AudioRecordConfig.Builder()
                .setLimitMinTime(1)
                .setLimitMaxTime(10)
                .setAudioFormat(AudioFormat.AMR)
                .build(), this)
    }

    @SuppressLint("ClickableViewAccessibility", "CheckResult")
    override fun initView() {
        formType = formType ?: ""
        type = FormType.getEnum(formType)
        if (type == null) {
            showToast("没有此表单")
            finish()
            return
        }

        super.initView()

        RxPermissions(this).request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE
        )!!.subscribe {
            if (!it) {
                showToast("请前往设置开启权限")
                finishThis()
            }
        }

        form_main_abv.setTitle(type?.title ?: "表单")

        form_main_rv.layoutManager = LinearLayoutManager(mContext)

        mAdapter = FormAdapter(connectId, mFormList)
        // 添加装饰类
        form_main_rv.addItemDecoration(FormItemDecoration())
        // 设置列表容器
        form_main_rv.adapter = mAdapter

        btn_form?.visibility = if (formEdit) View.VISIBLE else View.GONE

        btn_form?.setOnClickListener {
            btn_form?.isClickable = false
            btn_form?.background = ContextCompat.getDrawable(mContext!!, R.drawable.bg_rectangle_grey)
            if (mForm != null) {
                mModel.formRepo.postFormStatus.postValue(FormVerifyUtils.verify(mForm!!))
            } else {
                showToast("提交失败！数据已丢失。")
            }
        }

        mModel.formRepo.dbFormEntity.observe(this, Observer {

            mForm = it

            if (it == null) {
                mHttpNetCode.postValue(HttpResult(HttpCode.RESULT_NULL, "获取表单数据失败"))
                showToast("获取表单数据失败")
            } else {
                mHttpNetCode.postValue(HttpResult(HttpCode.SUCCESS, "获取表单数据成功"))

                btn_form?.text = mForm!!.submit_name ?: "提交"

                mFormList.clear()
                val formItemList = arrayListOf<FormItem>()
                for (item in mForm!!.form_item!!) {
                    if (item.form_visible) formItemList.add(item)
                }
                mFormList.addAll(formItemList)
                mAdapter.notifyDataSetChanged()
            }
        })

        mModel.formRepo.postFormStatus.observe(this, Observer {
            if (it.pass) {
                when (formPostType) {
                    0 -> {
                        // 提交并清数据库
                        mModel.postForm(type!!.name, connectId, mForm!!.id!!)
                    }
                    1 -> {
                        val intent = Intent()
                        intent.putExtra(FormPath.FORM_ID, mForm!!.id ?: -1L)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            } else {
                showToast(it.message)
                btn_form?.isClickable = true
                btn_form?.background = ContextCompat.getDrawable(mContext!!, R.drawable.bg_rectangle_primary)
            }
        })

        mModel.formRepo.dbDeleteForm.observe(this, Observer {
            finish()
        })

        // 表单内操作文件监听
        mAdapter.setOnFileListener(object : FormAdapter.OnFileListener {
            @SuppressLint("SetTextI18n")
            override fun getFile(type: FileTypeEnum, position: Int) {
                mFileType = type
                mAddPosition = position
                uploadFileFormItemId = mFormList[position].id!!
                when (type) {
                    FileTypeEnum.PHOTO -> {
                        takeCamera(Consumer {
                            uploadImageBeforeZip(FileUtils.getFile(mContext!!, it.uri), null)
                        })
                    }
                    FileTypeEnum.IMAGE -> {
                        takeGallery(Consumer {
                            uploadImageBeforeZip(FileUtils.getFile(mContext!!, it.uri), null)
                        })
                    }
                    FileTypeEnum.VIDEO -> {
                        takeVideo()
                    }
                    FileTypeEnum.AUDIO -> {
                        initAudioRecordView()
                    }
                    else -> {
                    }
                }
            }
        })

        // NOTICE 录音操作
        mRecordDialog = AudioDialogUtil(this)
        mRecordDialog.mIcon?.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                /**按下手指*/
                MotionEvent.ACTION_DOWN -> {
                    moveY = motionEvent.y
                    /**开始录音*/
                    mAudioRecordHelper.start()
                }
                /**抬起手指*/
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    mAudioRecordHelper.stop()
                }
                /**移动手指*/
                MotionEvent.ACTION_MOVE -> {
                    if ((moveY - motionEvent.y) > 200) {
                        mAudioRecordHelper.cancelWant()
                    } else {
                        mAudioRecordHelper.cancelRefuse()
                    }
                }
            }
            true // NOTICE 继续往下分发
        }
        mAudioRecordHelper.observe(this, Observer {
            if (it.recordState == AudioRecordState.SUCCESS && canRecord) {
                canRecord = false
                val file = File(it.audioPath)
                val udFile = UDFileEntity("", file.path, file.name, 0)
                val value = Value()
                value.form_item_id = uploadFileFormItemId
                value.file_type = FileTypeEnum.AUDIO.name
                value.file_name = file.name
                value.file_url = file.path
                udFile.adjoint = value
                mModel.fileRepo.uploadFile(udFile)
            }
        })

        // NOTICE 文件处理操作
        /**文件本地处理后回调,进行提交操作*/
        setOnSendFileListener(object : IFileHelper.OnSendFileListener<FileTypeEnum> {
            override fun send(file: File, type: FileTypeEnum) {
                val udFile = UDFileEntity("", file.path, file.name, 0)
                val value = Value()
                value.form_item_id = uploadFileFormItemId
                value.file_type = type.name
                value.file_name = file.name
                value.file_url = file.path
                udFile.adjoint = value
                mModel.fileRepo.uploadFile(udFile)
            }
        })

        /**文件上传结果监听*/
        mModel.fileRepo.dataUploadFile.observe(this, Observer {
            val value = it.adjoint as Value
            if (it.loadSuccess) {
                value.file_id = it.id
                mModel.addValueToFormItem(value)
            } else {
                LogUtil.d("上传中 ${it.progress}")
                if (it.progress == -1) {
                    showToast("添加附件失败")
                }
            }
        })

        mModel.formRepo.addValueToFormItem.observe(this, Observer {
            if (it == null) {
                showToast("添加失败")
            } else {
                mFormList[mAddPosition!!].form_item_value!!.values!!.add(it)
                mAdapter.notifyItemChanged(mAddPosition!!)
            }
        })
    }

    override fun getData() {
        if (mForm != null) {
            mModel.getFormByDB(mForm!!.id!!)
        } else {
            mModel.getForm(type!!, connectId)
        }
    }

    override fun onBackPressed() {
        if (formEdit) {
            finishThis()
        } else {
            mModel.deleteForm(mForm!!)
        }
    }

    override fun finishThis() {
        if (mForm != null) {
            MaterialDialog(this)
                    .title(null, "表单提示")
                    .message(null, "要放弃填写么？离开将不会保存您的数据。")
                    .show {
                        positiveButton(text = "放弃填写") {
                            mModel.deleteForm(mForm!!)
                        }
                        negativeButton(text = "取消")
                    }
        } else {
            finish()
        }
    }

    override fun vPrepare() {
        LogUtil.d("V准备")
    }

    override fun vRecording(recordTime: Int) {
        LogUtil.d("V录制中 $recordTime s")
        mRecordDialog.updateTime(recordTime)
    }

    override fun vCancelWant() {
        LogUtil.d("V意向取消")
        mRecordDialog.wantToCancel()
    }

    override fun vCancelRefuse() {
        LogUtil.d("V放弃取消")
        mRecordDialog.noWantToCancel()
    }

    override fun vCancel() {
        LogUtil.d("V取消")
        mRecordDialog.dismissDialog()
        form_main_rv.isFocusable = true
        canRecord = true
    }

    override fun vFail(msg: String) {
        LogUtil.d("V失败")
        mRecordDialog.dismissDialog()
        form_main_rv.isFocusable = true
        canRecord = true
        showToast(msg)
    }

    override fun vStop() {
        LogUtil.d("V停止")
        mRecordDialog.dismissDialog()
        form_main_rv.isFocusable = true
        canRecord = true
    }

    fun initAudioRecordView() {
        form_main_rv.isFocusable = false
        mRecordDialog.show()
    }
}
