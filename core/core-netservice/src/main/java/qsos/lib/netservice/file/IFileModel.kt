package qsos.lib.netservice.file

/**
 * @author : 华清松
 * 文件数据接口
 */
interface IFileModel {

    /**下载文件*/
    fun downloadFile(fileEntity: HttpFileEntity)

    /**上传文件*/
    fun uploadFile(fileEntity: HttpFileEntity)
}