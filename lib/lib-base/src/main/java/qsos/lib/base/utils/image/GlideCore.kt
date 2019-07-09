package qsos.lib.base.utils.image

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import qsos.lib.base.data.Constants
import qsos.lib.base.utils.file.FileUtils

@GlideModule
class GlideCore : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes().toLong())).setLogLevel(Log.ERROR)
        // 根据SD卡是否可用,选择是在内部缓存还是SD卡缓存
        if (FileUtils.checkSDStatus()) {
            builder.setDiskCache(ExternalPreferredCacheDiskCacheFactory(context, diskCacheFolderName(context), memoryCacheSizeBytes().toLong()))
        } else {
            builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheFolderName(context), memoryCacheSizeBytes().toLong()))
        }
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    private fun memoryCacheSizeBytes(): Int {
        // 50 MB
        return 1024 * 1024 * 50
    }

    private fun diskCacheSizeBytes(): Int {
        // 512 MB
        return 1024 * 1024 * 512
    }

    private fun diskCacheFolderName(context: Context): String {
        return ContextCompat.getCodeCacheDir(context).path + "/${Constants.appName}"
    }
}
