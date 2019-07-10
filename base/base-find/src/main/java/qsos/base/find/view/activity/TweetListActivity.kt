package qsos.base.find.view.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import kotlinx.android.synthetic.main.find_activity_tweet_list.*
import qsos.base.find.R
import qsos.base.find.data.TweetModelIml
import qsos.base.find.data.TweetRepository
import qsos.base.find.view.adapter.TweetAdapter
import qsos.core.lib.view.BaseModuleActivity
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.routepath.FindPath
import qsos.lib.base.utils.BaseUtils
import qsos.lib.base.utils.StatusBarUtil
import qsos.lib.base.utils.image.ImageLoaderUtils

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
    private var mRefresh = true
    private var mOffset = 0
    private var mScrollY = 0
    private lateinit var mToolbarBack: Drawable

    override fun initData(savedInstanceState: Bundle?) {
        mTweetModel = ViewModelProviders.of(this).get(TweetModelIml::class.java)
    }

    override fun initView() {
        super.initView()

        StatusBarUtil.immersive(this)
        mToolbarBack = ContextCompat.getDrawable(mContext!!, R.drawable.bg_wx)!!

        tweet_list_head_tb.setBackgroundColor(0)

        mTweetAdapter = TweetAdapter(mList)
        mLinearLayoutManager = LinearLayoutManager(this)
        tweet_list_rv.adapter = mTweetAdapter
        tweet_list_rv.layoutManager = mLinearLayoutManager
        tweet_list_rv.isNestedScrollingEnabled = false
        tweet_list_rv.setHasFixedSize(true)
        tweet_list_nsv.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            private var lastScrollY = 0
            private val defaultHeight = BaseUtils.dip2px(mContext!!, 170f)
            override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                mScrollY = Math.min(defaultHeight, scrollY)
                val scrollColor = 255 * mScrollY / defaultHeight
                item_tweet_head_profile_iv.translationY = (mOffset - mScrollY).toFloat()
                changeToolbar(scrollColor)
                lastScrollY = scrollY
            }
        })

        /**刷新监听,重新获取数据*/
        tweet_list_srl.setOnRefreshListener {
            mRefresh = true
            TweetRepository.getUserInfo()
            TweetRepository.getTweetList()
        }.setOnLoadMoreListener {
            mRefresh = false
            TweetRepository.getTweetList()
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

            if (mRefresh) {
                mList.clear()
            }
            val oldSize = mList.size
            val addSize = tweets.size
            mList.addAll(tweets)
            if (mRefresh) {
                mTweetAdapter.notifyDataSetChanged()
            } else {
                if (addSize > 0) {
                    if (oldSize == 0) {
                        mTweetAdapter.notifyItemRangeInserted(0, addSize)
                    } else {
                        mTweetAdapter.notifyItemChanged(oldSize)
                        mTweetAdapter.notifyItemRangeInserted(oldSize, addSize)
                    }
                }
            }
        })
    }

    override fun getData() {}

    /**修改状态栏样式*/
    private fun changeToolbar(color: Int) {

        when (color) {
            in 240..255 -> {
                mToolbarBack.alpha = color
                tweet_list_head_tb.background = mToolbarBack
                tweet_list_head_tb.navigationIcon = ContextCompat.getDrawable(mContext!!, R.drawable.icon_back_black)
                tweet_list_camera_iv.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.icon_take_photo_black))
            }
            else -> {
                mToolbarBack.alpha = 0
                tweet_list_head_tb.background = mToolbarBack
                tweet_list_head_tb.navigationIcon = ContextCompat.getDrawable(mContext!!, R.drawable.icon_back)
                tweet_list_camera_iv.setImageDrawable(ContextCompat.getDrawable(mContext!!, R.drawable.icon_take_photo))
            }
        }
    }
}
