package qsos.lib.netservice.file

import qsos.lib.base.data.http.UDFileEntity

/**
 * @author : 华清松
 * @description : 七月
 */
object FileHelper  : IFileModel {

    val imlFileModel: IFileModel = FileRepository()

    override fun downloadFile(fileEntity: UDFileEntity) {
        imlFileModel.downloadFile(fileEntity)
    }

    override fun uploadFile(fileEntity: UDFileEntity) {
        imlFileModel.uploadFile(fileEntity)
    }

}