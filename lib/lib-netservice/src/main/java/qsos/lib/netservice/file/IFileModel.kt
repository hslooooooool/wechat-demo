package qsos.lib.netservice.file

import vip.qsos.lib_data.data.http.UDFileEntity

/**
 * @author : 华清松
 * @description : 文件数据接口
 */
interface IFileModel {

    /**下载文件*/
    fun downloadFile(fileEntity: UDFileEntity)

    /**上传文件*/
    fun uploadFile(fileEntity: UDFileEntity)
}