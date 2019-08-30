package qsos.lib.netservice.file

import android.annotation.SuppressLint
import android.text.TextUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import qsos.core.lib.utils.file.FileUtils
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.data.HttpLiveData

/**
 * @author : 华清松
 * 文件服务
 */
@SuppressLint("CheckResult")
class FileRepository : IFileModel {

    override fun downloadFile(fileEntity: HttpFileEntity) {
        if (TextUtils.isEmpty(fileEntity.url)) {
            fileEntity.progress = -1
            dataDownloadFile.postValue(fileEntity)
        } else {
            val saveName = fileEntity.filename ?: FileUtils.getFileNameByUrl(fileEntity.url)
            val savePath = "${FileUtils.DOWNLOAD_PATH}/$saveName"

            ApiEngine.createService(ApiDownloadFile::class.java).downloadFile(fileEntity.url!!)
                    .subscribeOn(Schedulers.io())
                    .map {
                        FileUtils.writeBodyToFile(savePath, it)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                fileEntity.progress = 100
                                fileEntity.loadSuccess = true
                                dataDownloadFile.postValue(fileEntity)
                            },
                            {
                                fileEntity.progress = -1
                                dataDownloadFile.postValue(fileEntity)
                            }
                    )
        }
    }

    override fun uploadFile(fileEntity: HttpFileEntity) {
        dataUploadFile.postValue(fileEntity)
        if (TextUtils.isEmpty(fileEntity.path)) {
            fileEntity.progress = -1
            dataUploadFile.postValue(fileEntity)
        } else {
            val uploadFile = FileUtils.getFile(fileEntity.path!!)
            if (uploadFile == null) {
                fileEntity.progress = -1
                dataUploadFile.postValue(fileEntity)
            } else {
                val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile)
                val uploadBody = UploadBody(requestBody, object : ProgressListener {
                    override fun progress(progress: Int, length: Long, success: Boolean) {
                        fileEntity.progress = progress
                        fileEntity.loadSuccess = false
                        dataUploadFile.postValue(fileEntity)
                    }
                })
                val part = MultipartBody.Part.createFormData("file", uploadFile.name, uploadBody)
                ApiEngine.createService(ApiUploadFile::class.java).uploadFile(part)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(
                                {
                                    it.data!!.progress = 100
                                    it.data.adjoin = fileEntity.adjoin
                                    it.data.loadSuccess = true
                                    dataUploadFile.postValue(it.data)
                                },
                                {
                                    fileEntity.url = null
                                    fileEntity.progress = -1
                                    fileEntity.loadSuccess = false
                                    dataUploadFile.postValue(fileEntity)
                                })

            }
        }
    }

    val dataUploadFile = HttpLiveData<HttpFileEntity>()
    val dataDownloadFile = HttpLiveData<HttpFileEntity>()

}