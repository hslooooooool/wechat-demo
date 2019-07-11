package qsos.base.find.view.activity

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.facade.annotation.Route
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import kotlinx.android.synthetic.main.find_activity_tweet_list.*
import qsos.base.find.R
import qsos.base.find.data.TweetModelIml
import qsos.base.find.view.adapter.TweetAdapter
import qsos.core.lib.view.BaseModuleActivity
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.routepath.FindPath
import qsos.lib.base.utils.BaseUtils
import qsos.lib.base.utils.StatusBarUtil
import qsos.lib.base.utils.image.ImageLoaderUtils
import kotlin.math.min

/**
 * @author : 华轻松
 * @description : 朋友圈界面
 */
@Route(group = FindPath.GROUP, path = FindPath.TWEET_LIST)
class TweetListActivity(
        override val layoutId: Int? = R.layout.find_activity_tweet_list,
        override val reload: Boolean = false
) : BaseModuleActivity() {

    private lateinit var mTweetModel: TweetModelIml
    private lateinit var mTweetAdapter: TweetAdapter
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private val mList = arrayListOf<WeChatTweetBeen>()

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
        mSofia.statusBarLightFont()
                .invasionStatusBar()
                .navigationBarBackground(ContextCompat.getColor(this, R.color.black_light))
                .statusBarBackground(Color.TRANSPARENT)

        mToolbarBack = ContextCompat.getDrawable(mContext!!, R.drawable.bg_wx)!!

        /**默认Toolbar背景透明*/
        tweet_list_head_tb.setBackgroundColor(0)

        mTweetAdapter = TweetAdapter(mList)
        mLinearLayoutManager = LinearLayoutManager(this)
        mLinearLayoutManager.isSmoothScrollbarEnabled = true
        tweet_list_rv.adapter = mTweetAdapter
        tweet_list_rv.layoutManager = mLinearLayoutManager
        tweet_list_rv.isNestedScrollingEnabled = false
        tweet_list_rv.setHasFixedSize(false)
        (tweet_list_rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        /**滚动视图监听*/
        tweet_list_nsv.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            private val defaultHeight = BaseUtils.dip2px(mContext!!, 170f)
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                mScrollY = min(defaultHeight, scrollY)
                item_tweet_head_profile_iv.translationY = (mOffset - scrollY).toFloat()
                changeToolbar(255 * mScrollY / defaultHeight)
            }
        })

        /**刷新监听,重新获取数据*/
        tweet_list_srl.setOnRefreshListener {
            mDataLoadType = 1
            mTweetModel.mTweetRepository.getUserInfo()
            mTweetModel.mTweetRepository.getTweetList()
        }.setOnLoadMoreListener {
            mDataLoadType = 2
            mTweetModel.mTweetRepository.getTweetList()
        }

        /**刷新滚动监听,设置状态栏及背景图动效*/
        tweet_list_srl.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            override fun onHeaderMoving(header: RefreshHeader?, isDragging: Boolean, percent: Float, offset: Int, headerHeight: Int, maxDragHeight: Int) {
                mOffset = offset
                item_tweet_head_profile_iv.translationY = (offset - mScrollY).toFloat()
            }
        })

        /**观测用户数据更新*/
        mTweetModel.dataUserInfo().observe(this, Observer { userBeen ->
            tweet_list_srl.finishRefresh()
            // 加载头像
            ImageLoaderUtils.displayRounded(mContext!!, tweet_list_head_avatar_iv, userBeen.avatar)
            // 加载封面,设置站位与错误图
            ImageLoaderUtils.displayWithErrorAndPlace(mContext!!,
                    item_tweet_head_profile_iv, userBeen.profileImage,
                    R.drawable.bg_cadet_blue, R.drawable.bg_cadet_blue)
            tweet_list_head_name_tv.text = userBeen.nick
        })
        /**观测推特数据更新*/
        mTweetModel.dataTweetList().observe(this, Observer { tweets ->
            tweet_list_srl.finishLoadMore()

            if (mDataLoadType < 2) {
                mList.clear()
            }
            val oldSize = mList.size
            val addSize = tweets.size
            mList.addAll(tweets)
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

        registerForContextMenu(tweet_list_camera_iv)

        tweet_list_camera_iv.setOnClickListener { showToast("TAKE PHOTO") }
    }

    override fun getData() {}

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu?.add(0, 1, 0, "LIVE")
        menu?.add(0, 2, 0, "VLOG")
        menu?.setGroupEnabled(0, true)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        showToast("${item.title}")
        return super.onContextItemSelected(item)
    }

    /**修改状态栏样式*/
    private fun changeToolbar(color: Int) {
        when (color) {
            in 250..255 -> {
                mToolbarBack.alpha = color
                tweet_list_head_tb.background = mToolbarBack
                tweet_list_head_tb.navigationIcon = ContextCompat.getDrawable(mContext!!, R.drawable.icon_back_black)
                tweet_list_camera_iv.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.icon_take_photo_black))
                tweet_list_head_tb.title = "朋友圈"
                mSofia.statusBarDarkFont()
                        .invasionStatusBar()
                        .navigationBarBackground(ContextCompat.getColor(this, R.color.black_light))
                        .statusBarBackground(Color.TRANSPARENT)
            }
            else -> {
                mToolbarBack.alpha = 0
                tweet_list_head_tb.background = mToolbarBack
                tweet_list_head_tb.navigationIcon = ContextCompat.getDrawable(mContext!!, R.drawable.icon_back)
                tweet_list_camera_iv.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.icon_take_photo))
                tweet_list_head_tb.title = ""
                mSofia.statusBarLightFont()
                        .invasionStatusBar()
                        .navigationBarBackground(ContextCompat.getColor(this, R.color.black_light))
                        .statusBarBackground(Color.TRANSPARENT)
            }
        }
    }
}
