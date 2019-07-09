package qsos.base.find.view.activity

import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.find_activity_tweet_list.*
import qsos.base.find.R
import qsos.base.find.data.TweetModelIml
import qsos.base.find.data.TweetRepository
import qsos.base.find.view.adapter.TweetAdapter
import qsos.core.lib.view.BaseModuleActivity
import qsos.lib.base.data.WeChatBeen
import qsos.lib.base.data.WeChatType
import qsos.lib.base.data.WeChatUserBeen
import qsos.lib.base.routepath.FindPath

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
    private val mList = arrayListOf<WeChatBeen>()
    private var mRefresh = true

    override fun initData(savedInstanceState: Bundle?) {
        mList.add(WeChatBeen(WeChatType.HEAD, WeChatUserBeen()))
        mTweetModel = ViewModelProviders.of(this).get(TweetModelIml::class.java)
    }

    override fun initView() {
        super.initView()

        mTweetAdapter = TweetAdapter(mList)
        tweet_list_rv.adapter = mTweetAdapter
        tweet_list_rv.layoutManager = LinearLayoutManager(this)

        tweet_list_srl.setOnLoadMoreListener {
            mRefresh = false
            TweetRepository.getUserInfo()
            TweetRepository.getTweetList()
        }.setOnRefreshListener {
            mRefresh = true
            TweetRepository.getTweetList()
        }

        /**观测用户数据更新*/
        mTweetModel.dataUserInfo().observe(this, Observer { userBeen ->
            tweet_list_srl.finishRefresh()
            tweet_list_srl.finishLoadMore()
            if (mList.isNotEmpty() && mList[0].dataType == WeChatType.HEAD) {
                mList[0].data = userBeen
            } else {
                mList.add(0, WeChatBeen(WeChatType.HEAD, userBeen))
            }
            mTweetAdapter.notifyItemChanged(0)
        })
        /**观测推特数据更新*/
        mTweetModel.dataTweetList().observe(this, Observer { tweets ->
            tweet_list_srl.finishRefresh()
            tweet_list_srl.finishLoadMore()
            if (mRefresh) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mList.removeIf {
                        it.dataType == WeChatType.ITEM
                    }
                } else {
                    val head = mList[0].copy()
                    mList.clear()
                    mList.add(head)
                }
            }
            tweets.forEach {
                mList.add(WeChatBeen(WeChatType.ITEM, it))
            }
            mTweetAdapter.notifyDataSetChanged()
        })

    }

    override fun getData() {}

}
