package qsos.core.file.audio

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.utils.LogUtil
import qsos.lib.base.utils.record.AudioFileUtils
import qsos.lib.base.utils.record.AudioUtils
import java.util.*

/**
 * @author : 华清松
 * @description : 录音工具，所需权限
 * Manifest.permission.WRITE_EXTERNAL_STORAGE,
 * Manifest.permission.READ_EXTERNAL_STORAGE,
 * Manifest.permission.RECORD_AUDIO,
 * Manifest.permission.READ_PHONE_STATE
 */
class AudioRecordHelper(
        lifecycle: LifecycleOwner,
        private val config: AudioRecordConfig,
        private val respondView: IRespondView
) : MutableLiveData<AudioRecordData>(), IControlView {

    private var mRecordTimer: Timer? = null
    private var mRecordTimerTask: TimerTask? = null

    /**最总录制结果 -1 失败 0 取消 1 成功*/
    private var resultState: Int = -1

    init {
        // 仅做视图操作，不做数据操作，成功才操作数据，停止后取消与成功都不做视图操作
        this.observe(lifecycle, Observer {
            when (it.recordState) {
                AudioRecordState.PREPARE -> {
                    respondView.vPrepare()
                }
                AudioRecordState.RECORDING -> {
                    respondView.vRecording(it.recordTime)
                }
                AudioRecordState.CANCEL_WANT -> {
                    respondView.vCancelWant()
                }
                AudioRecordState.CANCEL_REFUSE -> {
                    respondView.vCancelRefuse()
                }
                AudioRecordState.STOP -> {
                    respondView.vStop()
                    release()
                    when (resultState) {
                        -1 -> {
                            respondView.vFail("")
                            this@AudioRecordHelper.value!!.recordState = AudioRecordState.FAIL
                            this@AudioRecordHelper.postValue(this@AudioRecordHelper.value)
                        }
                        0 -> {
                            respondView.vCancel()
                            this@AudioRecordHelper.value!!.recordState = AudioRecordState.CANCEL
                            this@AudioRecordHelper.postValue(this@AudioRecordHelper.value)
                        }
                        1 -> {
                            if (this@AudioRecordHelper.value!!.recordState != AudioRecordState.SUCCESS) {
                                this@AudioRecordHelper.value!!.recordState = AudioRecordState.SUCCESS
                                this@AudioRecordHelper.postValue(this@AudioRecordHelper.value)
                            }
                        }
                    }
                }
                else -> {
                    LogUtil.i("录制结果回执 ${it.recordState}")
                }
            }
        })
    }

    @SuppressLint("CheckResult")
    override fun start() {
        AudioUtils.audioPath = when (config.audioFormat) {
            AudioFormat.WAV -> AudioFileUtils.wavFilePath
            AudioFormat.AMR -> AudioFileUtils.amrFilePath
        }

        this.value = AudioRecordData(0, AudioRecordState.PREPARE, AudioUtils.audioPath)
        AudioRecordUtils.startRecord(object : OnTListener<AudioRecordState> {
            override fun getItem(item: AudioRecordState) {
                when (item) {
                    AudioRecordState.FAIL -> {
                        AudioRecordUtils.stopRecord()
                        resultState = -1
                        this@AudioRecordHelper.value!!.recordState = AudioRecordState.STOP
                        this@AudioRecordHelper.postValue(this@AudioRecordHelper.value)
                    }
                    AudioRecordState.RECORDING -> {
                        mRecordTimer = Timer()
                        mRecordTimerTask = object : TimerTask() {
                            override fun run() {
                                this@AudioRecordHelper.value!!.recordState = AudioRecordState.RECORDING
                                this@AudioRecordHelper.value!!.recordTime++
                                this@AudioRecordHelper.postValue(this@AudioRecordHelper.value)
                            }
                        }
                        mRecordTimer!!.schedule(mRecordTimerTask!!, 0, 1000)
                    }
                    AudioRecordState.SUCCESS -> {
                        LogUtil.d("完成文件拷贝，可以获取文件了")
                    }
                }
            }
        })
    }

    override fun cancelWant() {
        resultState = 0
        this.value!!.recordState = AudioRecordState.CANCEL_WANT
        this@AudioRecordHelper.postValue(this.value)
    }

    override fun cancelRefuse() {
        resultState = 1
        this.value!!.recordState = AudioRecordState.CANCEL_REFUSE
        this@AudioRecordHelper.postValue(this.value)
    }

    override fun stop() {
        AudioRecordUtils.stopRecord()
        this.value!!.recordState = AudioRecordState.STOP
        this@AudioRecordHelper.postValue(this.value)
    }

    private fun release() {
        mRecordTimerTask?.cancel()
        mRecordTimer?.cancel()
        mRecordTimerTask = null
        mRecordTimer = null
    }
}