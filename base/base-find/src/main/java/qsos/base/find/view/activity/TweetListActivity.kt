package qsos.base.find.view.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import kotlinx.android.synthetic.main.find_activity_tweet_list.*
import kotlinx.android.synthetic.main.find_item_tweet.view.*
import qsos.base.core.view.BaseModuleActivity
import qsos.base.core.widget.image.NineGridLayout
import qsos.base.find.R
import qsos.base.find.data.TweetModelIml
import qsos.base.find.data.EmployeeBeen
import qsos.core.lib.router.FindPath
import qsos.core.lib.router.PlayPath
import qsos.core.lib.utils.StatusBarUtil
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.core.play.image.FileData
import qsos.core.play.image.FileListData
import qsos.lib.base.base.adapter.BaseLifeCycleAdapter
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.utils.BaseUtils
import qsos.lib.base.utils.ToastUtils
import kotlin.math.min

/**
 * @author : 华轻松
 * 朋友圈界面
 */
@Route(group = FindPath.GROUP, path = FindPath.TWEET_LIST)
class TweetListActivity(
        override val layoutId: Int? = R.layout.find_activity_tweet_list,
        override val reload: Boolean = false
) : BaseModuleActivity() {

    private lateinit var mTweetModel: TweetModelIml
    private lateinit var mTweetAdapter: BaseLifeCycleAdapter<EmployeeBeen>
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private val mList = arrayListOf<EmployeeBeen>()

    /**数据加载模式，0加载所有推特，仅第一次加载有效，1刷新数据仅显示前五条数据，2往后追加5条数据*/
    private var mDataLoadType: Int = 0
    private var mCanLoadMore = true
    private var mOffset = 0
    private var mScrollY = 0
    private lateinit var mToolbarBack: Drawable

    override fun initData(savedInstanceState: Bundle?) {
        mTweetModel = ViewModelProviders.of(this).get(TweetModelIml::class.java)
    }

    override fun initView() {
        super.initView()

        StatusBarUtil.immersive(this)
        StatusBarUtil.setMargin(this, tweet_list_ch)
        StatusBarUtil.setPaddingSmart(this, tweet_list_head_tb)

        mToolbarBack = ContextCompat.getDrawable(mContext, R.drawable.bg_wx)!!

        /**默认Toolbar背景透明*/
        tweet_list_head_tb.setBackgroundColor(0)

        mTweetAdapter = BaseLifeCycleAdapter(lifecycle, R.layout.find_item_tweet, mList,
                setHolder = { holder, data, _ ->
                    setHolder(holder, data)
                }
        )

        mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.isSmoothScrollbarEnabled = true
        tweet_list_rv.adapter = mTweetAdapter
        tweet_list_rv.layoutManager = mLinearLayoutManager
        tweet_list_rv.isNestedScrollingEnabled = false
        tweet_list_rv.setHasFixedSize(false)
        (tweet_list_rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        registerForContextMenu(tweet_list_camera_iv)

        tweet_list_camera_iv.setOnClickListener { ToastUtils.showToast(this, "TAKE PHOTO") }

        /**滚动视图监听*/
        tweet_list_nsv.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            private val defaultHeight = BaseUtils.dip2px(mContext, 170f)
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                mScrollY = min(defaultHeight, scrollY)
                item_tweet_head_profile_iv.translationY = (mOffset - scrollY).toFloat()
                changeToolbar(255 * mScrollY / defaultHeight)
            }
        })

        /**刷新监听,重新获取数据*/
        tweet_list_srl.setOnRefreshListener {
            mDataLoadType = 1
            mTweetModel.getOne()
            mTweetModel.getList()
        }.setOnLoadMoreListener {
            mDataLoadType = 2
            mTweetModel.getList()
        }

        /**刷新滚动监听,设置状态栏及背景图动效*/
        tweet_list_srl.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            override fun onHeaderMoving(header: RefreshHeader?, isDragging: Boolean, percent: Float, offset: Int, headerHeight: Int, maxDragHeight: Int) {
                mOffset = offset
                item_tweet_head_profile_iv.translationY = (offset - mScrollY).toFloat()
            }
        })

        /**观测用户数据更新*/
        mTweetModel.mOne().observe(this, Observer {
            tweet_list_srl.finishRefresh()
            // 加载头像
            ImageLoaderUtils.displayRounded(mContext, tweet_list_head_avatar_iv, it.data?.head)
            // 加载封面,设置站位与错误图
            ImageLoaderUtils.displayWithErrorAndPlace(mContext,
                    item_tweet_head_profile_iv, it.data?.head,
                    R.drawable.bg_cadet_blue, R.drawable.bg_cadet_blue)
            tweet_list_head_name_tv.text = it.data?.name
        })

        /**观测推特数据更新*/
        mTweetModel.mList().observe(this, Observer {
            tweet_list_srl.finishLoadMore()

            if (mDataLoadType < 2) {
                mList.clear()
            }
            val oldSize = mList.size
            val addSize = it.data!!.size
            mList.addAll(it.data!!)
            if (mDataLoadType < 2) {
                mTweetAdapter.notifyDataSetChanged()
            } else {
                if (addSize > 0) {
                    if (oldSize == 0) {
                        mTweetAdapter.notifyItemRangeInserted(0, addSize)
                    } else {
                        mTweetAdapter.notifyItemRangeInserted(oldSize, addSize)
                    }
                }
            }
            mCanLoadMore = true
        })

        mTweetModel.mList().httpState.observe(this, Observer {
            tweet_list_srl.closeHeaderOrFooter()
        })

        getData()
    }

    override fun getData() {
        mTweetModel.getOne()
        mTweetModel.getList()

        mTweetModel.addOne(
                {
                    getUserInfoSuccess()
                },
                {
                    ToastUtils.showToast(this, it)
                }
        )
    }

    private fun getUserInfoSuccess() {
        ToastUtils.showToast(this, "提交成功")
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu?.add(0, 1, 0, "LIVE")
        menu?.add(0, 2, 0, "VLOG")
        menu?.setGroupEnabled(0, true)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        ToastUtils.showToast(this, "${item.title}")
        return super.onContextItemSelected(item)
    }

    /**修改状态栏样式*/
    private fun changeToolbar(color: Int) {
        when (color) {
            in 250..255 -> {
                mToolbarBack.alpha = color
                tweet_list_head_tb.background = mToolbarBack
                tweet_list_head_tb.navigationIcon = ContextCompat.getDrawable(mContext, R.drawable.icon_back_black)
                tweet_list_camera_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_take_photo_black))
                tweet_list_head_tb.title = "朋友圈"
            }
            else -> {
                mToolbarBack.alpha = 0
                tweet_list_head_tb.background = mToolbarBack
                tweet_list_head_tb.navigationIcon = ContextCompat.getDrawable(mContext, R.drawable.icon_back)
                tweet_list_camera_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_take_photo))
                tweet_list_head_tb.title = ""
            }
        }
    }

    private fun setHolder(holder: BaseHolder<EmployeeBeen>, data: EmployeeBeen) {
        // 加载头像
        ImageLoaderUtils.displayRounded(holder.itemView.context, holder.itemView.item_tweet_head_iv, data.head)

        holder.itemView.item_tweet_nick_tv.text = "${data.managerId}"
        holder.itemView.item_tweet_content_tv.text = data.name

        holder.itemView.item_tweet_image_ngl.setUrlList(arrayListOf(data.head ?: ""))

        holder.itemView.item_tweet_image_ngl.setOnClickListener(object : NineGridLayout.OnImageClickListener {
            override fun onClickImage(view: View, position: Int, urls: List<String>) {
                val fileDataList = arrayListOf<FileData>()
                urls.forEach {
                    fileDataList.add(FileData(it, it))
                }
                val fileListData = FileListData(position, fileDataList)
                val optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view, view.width / 2, view.height / 2, 0, 0)
                ARouter.getInstance().build(PlayPath.IMAGE_PREVIEW)
                        .withString(PlayPath.IMAGE_LIST, Gson().toJson(fileListData))
                        .withOptionsCompat(optionsCompat)
                        .navigation()
            }

            override fun onLongClickImage(view: View, position: Int, url: String, index: Int) {
                ToastUtils.showToast(holder.itemView.context, "菜单$index")
            }
        })
    }

}
