package qsos.lib.netservice.file

import qsos.lib.netservice.data.UDFileEntity

/**
 * @author : 华清松
 * 文件数据接口
 */
interface IFileModel {

    /**下载文件*/
    fun downloadFile(fileEntity: UDFileEntity)

    /**上传文件*/
    fun uploadFile(fileEntity: UDFileEntity)
}