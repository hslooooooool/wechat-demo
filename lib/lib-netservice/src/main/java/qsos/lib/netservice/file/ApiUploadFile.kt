package qsos.lib.netservice.file

import io.reactivex.Flowable
import okhttp3.MultipartBody
import vip.qsos.lib_data.data.BaseHttpResult
import vip.qsos.lib_data.data.http.UDFileEntity
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * @author : 华清松
 * @description : 文件上传接口
 */
interface ApiUploadFile {

    /**
     * 文件上传
     * @param part 待上传的文件
     * @return BaseHttpResult<String> 文件在服务器的路径，因设计为去除host后的路径，
     * 建议：保证后期服务器地址更换带来的数据不一致问题，如访问路径是 http://www.baidu.com/file/20190502/head.png
     * 则返回的路径应是 file/20190502/head.png ，host由协商确定
     */
    @Multipart
    @POST("/kenary/common/sysFile/upload")
    fun uploadFile(
            @Part part: MultipartBody.Part
    ): Flowable<BaseHttpResult<UDFileEntity>>

}