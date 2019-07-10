package qsos.core.lib.view.widget.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import qsos.lib.base.utils.image.GlideApp
import qsos.lib.base.utils.image.ImageLoaderUtils
import qsos.lib.lib.R

/**
 * @author : 华清松
 * @description : 九宫格图片布局
 */
class NineGridLayout : AbsNineGridLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun displayOneImage(imageView: RatioImageView, url: String, parentWidth: Int): Boolean {
        GlideApp.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .dontAnimate()
                .placeholder(ContextCompat.getDrawable(context, R.drawable.bg_grey_white))
                .error(ContextCompat.getDrawable(context, R.drawable.bg_grey_white))
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        val w = resource.intrinsicWidth
                        val h = resource.intrinsicHeight
                        val newW: Int
                        val newH: Int
                        when {
                            h > w * MAX_RATIO -> {
                                //h:w = 5:3
                                newW = parentWidth / 2
                                newH = newW * 5 / 3
                            }
                            h < w -> {
                                //h:w = 2:3
                                newW = parentWidth * 2 / 3
                                newH = newW * 2 / 3
                            }
                            else -> {
                                //newH:h = newW :w
                                newW = parentWidth / 2
                                newH = h * newW / w
                            }
                        }
                        setOneImageLayoutParams(imageView, newW, newH)
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                })
                .into(imageView)
        return false
    }

    override fun displayImage(imageView: RatioImageView, url: String) {
        ImageLoaderUtils.display(context, imageView, url)
    }

    override fun onClickImage(position: Int, url: String, urlList: List<String>) {

    }

    companion object {
        /**最大列数*/
        private const val MAX_RATIO = 3
    }
}
