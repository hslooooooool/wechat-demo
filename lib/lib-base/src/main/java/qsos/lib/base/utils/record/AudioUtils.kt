package qsos.lib.base.utils.record

import androidx.lifecycle.MutableLiveData
import qsos.lib.base.utils.LogUtil
import java.util.*

/**
 * @author : 华清松
 * @description : 音频录制工具类
 */
object AudioUtils {

    enum class RecordType { WAV, AMR }

    /**录音文件大小*/
    private var mRecordSize: Long = 0L
    /**录制时长 秒*/
    private var mRecordTime: Int = 0
    /**录制方式*/
    private var mRecordType: RecordType = RecordType.AMR
    /**录制状态*/
    private var mRecordStateEnum: RecordStateEnum = RecordStateEnum.NORMAL

    /**录音文件路径*/
    var audioPath: String = ""

    /**
     * 录制状态实体
     * @param time 录制时间
     * @param size 录制大小
     * @param path 录制路径
     * @param state 录制状态
     */
    data class RecordState(
            var time: Int = 0,
            var size: Long = 0L,
            var path: String? = "",
            var state: RecordStateEnum = RecordStateEnum.FAIL
    )

    private var mRecordTimer: Timer? = null

    private var mRecordState: MutableLiveData<RecordState>? = null
    /**
     * 开始录音
     * @param type 录制类型
     * @param recordState 录制数据观察者
     */
    fun record(type: RecordType, recordState: MutableLiveData<RecordState>) {
        mRecordStateEnum = RecordStateEnum.NORMAL
        this.mRecordState = recordState
        /**录音未准备好,录制失败*/
        setRecordState(RecordStateEnum.NORMAL)

        when (type) {
            RecordType.WAV -> {
                audioPath = AudioFileUtils.wavFilePath
                mRecordStateEnum = AudioRecordFunc.startRecordAndFile()
            }
            RecordType.AMR -> {
                audioPath = AudioFileUtils.amrFilePath
                mRecordStateEnum = MediaRecordFunc.startRecordAndFile()
            }
        }
        /**判断启动无错误,开始录音,计时开始,每秒进行一次时间更新*/
        if (mRecordStateEnum == RecordStateEnum.START) {
            if (mRecordTimer == null) {
                mRecordTimer = Timer()
                mRecordTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        setRecordState(RecordStateEnum.DOING)
                    }
                }, 0, 1000)
            }
            setRecordState(RecordStateEnum.START)
        } else {
            setRecordState(RecordStateEnum.FAIL)
        }
    }

    fun setRecordState(recordStateEnum: RecordStateEnum) {
        mRecordSize = when (mRecordType) {
            RecordType.WAV -> AudioRecordFunc.recordFileSize
            RecordType.AMR -> MediaRecordFunc.recordFileSize
        }
        when (recordStateEnum) {
            RecordStateEnum.NORMAL -> {
                mRecordState?.postValue(RecordState(mRecordTime, 0, audioPath, recordStateEnum))
                LogUtil.i("录音准备")
            }
            RecordStateEnum.START -> {
                mRecordState?.postValue(RecordState(mRecordTime, 0, audioPath, recordStateEnum))
                LogUtil.i("开始录音，已录制：$mRecordTime s")
            }
            RecordStateEnum.DOING -> {
                mRecordState?.postValue(RecordState(mRecordTime++, 0, audioPath, recordStateEnum))
                LogUtil.i("正在录音中，已录制：$mRecordTime s")
            }
            RecordStateEnum.FAIL -> {
                mRecordTimer?.cancel()
                mRecordTimer = null
                mRecordState?.postValue(RecordState(mRecordTime, 0, audioPath, recordStateEnum))
                LogUtil.i("录音失败")
            }
            RecordStateEnum.SUCCESS, RecordStateEnum.STOP, RecordStateEnum.CANCEL_YES -> {
                when (mRecordType) {
                    RecordType.WAV -> AudioRecordFunc.stopRecordAndFile()
                    RecordType.AMR -> MediaRecordFunc.stopRecordAndFile()
                }

                mRecordTimer?.cancel()
                mRecordTimer = null
                mRecordState?.postValue(RecordState(mRecordTime, mRecordSize, audioPath, recordStateEnum))
                LogUtil.i("录音已完成.录音文件:$audioPath\n文件大小：$mRecordSize")
            }
        }
    }

}
