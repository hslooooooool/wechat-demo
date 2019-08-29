package qsos.lib.netservice.file

import qsos.lib.netservice.data.HttpFileEntity

/**
 * @author : 华清松
 * 七月
 */
object FileHelper  : IFileModel {

    val imlFileModel: IFileModel = FileRepository()

    override fun downloadFile(fileEntity: HttpFileEntity) {
        imlFileModel.downloadFile(fileEntity)
    }

    override fun uploadFile(fileEntity: HttpFileEntity) {
        imlFileModel.uploadFile(fileEntity)
    }

}