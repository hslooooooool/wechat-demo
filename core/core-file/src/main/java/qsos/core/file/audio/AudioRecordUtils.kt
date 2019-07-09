package qsos.core.file.audio

import android.media.AudioFormat
import android.media.AudioRecord
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.utils.record.AudioFileUtils
import qsos.lib.base.utils.record.AudioUtils
import java.io.*

/**
 * @author : 华清松
 * @description : 录音操作
 */
object AudioRecordUtils {
    // 缓冲区字节大小
    private var bufferSizeInBytes = 0
    private var audioRecord: AudioRecord? = null
    private var mRecordListener: OnTListener<AudioRecordState>? = null
    private var mRecordThread: Thread? = null

    private var mAudioRecordState = AudioRecordState.FAIL

    fun startRecord(recordListener: OnTListener<AudioRecordState>) {
        this.mRecordListener = recordListener

        //判断是否有外部存储设备sdcard
        if (AudioFileUtils.isSdcardExit) {
            audioRecord ?: createAudioRecord()
            audioRecord?.startRecording()
            mRecordThread = Thread(AudioRecordThread())
            mRecordThread?.start()
            mAudioRecordState = AudioRecordState.RECORDING
        } else {
            mAudioRecordState = AudioRecordState.FAIL
        }
        mRecordListener?.getItem(mAudioRecordState)
    }

    fun stopRecord() {
        mAudioRecordState = AudioRecordState.STOP
        if (audioRecord != null) {
            try {
                audioRecord?.stop()
            } finally {
                audioRecord?.release()
                mRecordThread = null
                audioRecord = null
                mRecordListener = null
            }
        }
    }

    private fun createAudioRecord(): AudioRecord {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AudioFileUtils.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
        audioRecord = AudioRecord(AudioFileUtils.AUDIO_INPUT, AudioFileUtils.AUDIO_SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes)
        return audioRecord!!
    }

    class AudioRecordThread : Runnable {
        override fun run() {
            // 往文件中写入裸数据
            writeDateTOFile()
            // 给裸数据加上头文件
            copyWaveFile()
        }
    }

    /**
     * 这里将数据写入文件，但是并不能播放，因为AudioRecord获得的音频是原始的裸音频，
     * 如果需要播放就必须加入一些格式或者编码的头信息。但是这样的好处就是你可以对音频的 裸数据进行处理，比如你要做一个爱说话的TOM
     * 猫在这里就进行音频的处理，然后重新封装 所以说这样得到的音频比较容易做一些音频的处理。
     */
    private fun writeDateTOFile() {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        val audioData = ByteArray(bufferSizeInBytes)
        var fos: FileOutputStream? = null
        var readSize: Int
        try {
            val file = File(AudioFileUtils.rawFilePath)
            if (file.exists()) {
                file.delete()
            }
            fos = FileOutputStream(file)
        } catch (e: Exception) {
            e.printStackTrace()
            mAudioRecordState = AudioRecordState.FAIL
            mRecordListener?.getItem(mAudioRecordState)
        }
        while (mAudioRecordState == AudioRecordState.RECORDING) {
            readSize = audioRecord!!.read(audioData, 0, bufferSizeInBytes)
            if (AudioRecord.ERROR_INVALID_OPERATION != readSize && fos != null) {
                try {
                    fos.write(audioData)
                } catch (e: IOException) {
                    e.printStackTrace()
                    mAudioRecordState = AudioRecordState.FAIL
                    mRecordListener?.getItem(mAudioRecordState)
                }
            }
        }
        try {
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
            mAudioRecordState = AudioRecordState.FAIL
            mRecordListener?.getItem(mAudioRecordState)
        }
    }

    /**将原始录音文件转化格式*/
    private fun copyWaveFile() {
        val `in`: FileInputStream
        val out: FileOutputStream
        val totalAudioLen: Long
        val totalDataLen: Long
        val longSampleRate = AudioFileUtils.AUDIO_SAMPLE_RATE.toLong()
        val channels = 2
        val byteRate = (16 * AudioFileUtils.AUDIO_SAMPLE_RATE * channels / 8).toLong()
        val data = ByteArray(bufferSizeInBytes)
        try {
            `in` = FileInputStream(AudioFileUtils.rawFilePath)
            out = FileOutputStream(AudioUtils.audioPath)
            totalAudioLen = `in`.channel.size()
            totalDataLen = totalAudioLen + 36
            writeWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate)
            while (`in`.read(data) != -1) {
                out.write(data)
            }
            `in`.close()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            mAudioRecordState = AudioRecordState.FAIL
            mRecordListener?.getItem(mAudioRecordState)
        } catch (e: IOException) {
            e.printStackTrace()
            mAudioRecordState = AudioRecordState.FAIL
            mRecordListener?.getItem(mAudioRecordState)
        }
    }

    @Throws(IOException::class)
    private fun writeWaveFileHeader(
            out: FileOutputStream, totalAudioLen: Long,
            totalDataLen: Long, longSampleRate: Long,
            channels: Int, byteRate: Long
    ) {
        val header = ByteArray(44)
        header[0] = 'R'.toByte() // RIFF/WAVE header
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        header[12] = 'f'.toByte() // 'fmt ' chunk
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] = (2 * 16 / 8).toByte() // block align
        header[33] = 0
        header[34] = 16 // bits per sample
        header[35] = 0
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        out.write(header, 0, 44)
    }

}