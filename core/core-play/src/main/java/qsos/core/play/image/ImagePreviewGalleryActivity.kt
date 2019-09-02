package qsos.core.play.image

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.play_activity_image_gallery.*
import kotlinx.android.synthetic.main.play_item_image.view.*
import qsos.core.lib.utils.image.BitmapUtils
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.core.play.PlayPath
import qsos.core.play.R
import qsos.lib.base.base.activity.BaseActivity
import qsos.lib.base.base.adapter.BaseNormalAdapter

/**
 * @author : 华清松
 * 图片预览-画廊模式
 */
@SuppressLint("CheckResult", "SetTextI18n")
@Route(group = PlayPath.GROUP, path = PlayPath.IMAGE_PREVIEW_GALLERY)
class ImagePreviewGalleryActivity(
        override val layoutId: Int? = R.layout.play_activity_image_gallery,
        override val reload: Boolean = false
) : BaseActivity(), ScalableCardHelper.OnPageChangeListener {

    @Autowired(name = PlayPath.IMAGE_LIST)
    @JvmField
    var imageListDataString: String? = null

    private var imageListData: FileListData? = null

    private var mAdapter: BaseNormalAdapter<FileData>? = null
    private var errorDrawable: Drawable? = null

    override fun initData(savedInstanceState: Bundle?) {
        imageListData = Gson().fromJson(imageListDataString, FileListData::class.java)

        errorDrawable = ContextCompat.getDrawable(this, R.drawable.ic_launcher)
    }

    override fun initView() {
        val fadeIn = AlphaAnimation(0.5f, 1f)
        fadeIn.duration = 500
        val fadeOut = AlphaAnimation(0.5f, 0f)
        fadeOut.duration = 500
        play_gallery_is.inAnimation = fadeIn
        play_gallery_is.outAnimation = fadeOut
        play_gallery_is.setFactory {
            val imageView = ImageView(this)
            imageView.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT)
            imageView.scaleType = ImageView.ScaleType.FIT_XY

            imageView
        }

        play_gallery_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mAdapter = BaseNormalAdapter(R.layout.play_item_image, imageListData!!.imageList,
                setHolder = { holder, data, _ ->
                    ImageLoaderUtils.display(holder.itemView.context, holder.itemView.play_image_iv, data.url)
                }
        )
        play_gallery_rv.adapter = mAdapter

        val cardHelper = ScalableCardHelper(this)
        cardHelper.attachToRecyclerView(play_gallery_rv)

        play_gallery_rv.scrollToPosition(imageListData!!.position)

    }

    override fun getData() {}

    private var mBlurBitmap = arrayOf<Bitmap?>()

    /**翻页监听*/
    override fun onPageSelected(position: Int) {
        val size = imageListData!!.imageList.size

        play_gallery_position?.text = " ${position + 1} / $size "
        play_gallery_name?.text = imageListData!!.imageList[position].name

        if (mBlurBitmap.isEmpty()) {
            mBlurBitmap = arrayOfNulls(imageListData!!.imageList.size)
        }

        var bitmap = mBlurBitmap[position]
        if (bitmap == null) {
            Observable.create<Bitmap> {
                bitmap = Glide.with(applicationContext).asBitmap()
                        .load(imageListData!!.imageList[position].url)
                        .error(R.drawable.ic_launcher)
                        .submit()
                        .get()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    bitmap = BitmapUtils.blurBitmap(this, bitmap!!, 25f)
                }
                it.onNext(bitmap!!)
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                mBlurBitmap[position] = bitmap
                                play_gallery_is.setImageDrawable(BitmapDrawable(this.resources, it))
                            },
                            {
                                mBlurBitmap[position] = null
                                play_gallery_is.setImageDrawable(errorDrawable)
                            })
        } else {
            play_gallery_is.setImageDrawable(BitmapDrawable(this.resources, bitmap))
        }
    }

    override fun finish() {
        mAdapter?.release(play_gallery_rv)
        super.finish()
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
    }
}