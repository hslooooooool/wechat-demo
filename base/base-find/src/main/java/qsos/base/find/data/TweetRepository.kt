package qsos.base.find.data

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.data.WeChatUserBeen
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.ObservableService
import qsos.lib.netservice.file.BaseRepository

/**
 * @author : 华清松
 * @description : 聊天数据获取
 */
@SuppressLint("CheckResult")
class TweetRepository : ITweetRepo, BaseRepository() {
    /**推特列表数据*/
    val dataWeChatTweetList = MutableLiveData<List<WeChatTweetBeen>>()
    /**用户数据*/
    val dataWeChatUserInfo = MutableLiveData<WeChatUserBeen>()

    override fun getUserInfo() {
        ObservableService.setObservable(
                ApiEngine.createService(ApiTweet::class.java).getUserInfo()
        ).subscribe(
                {
                    dataWeChatUserInfo.postValue(it)
                },
                {
                    dataWeChatUserInfo.postValue(WeChatUserBeen())
                }
        )
    }

    override fun getTweetList() {
        val temValue = dataWeChatTweetList.value
        // TODO Load all tweets in memory at first time, and get 5 of them each time from memory asynchronously
        if (!temValue.isNullOrEmpty()) {
            if (temValue.size > 5) {
                // 异步加载5条数据到列表刷新
                dataWeChatTweetList.postValue(temValue.subList(0, 5))
            } else {
                // 不足5条全部加载
                dataWeChatTweetList.postValue(temValue)
            }
        } else {
            ObservableService.setObservable(
                    ApiEngine.createService(ApiTweet::class.java).getTweetList()
            ).subscribe(
                    {
                        // NOTICE 数据校验 ignore the tweet which does not contain a content and images
                        val data = arrayListOf<WeChatTweetBeen>()
                        it.forEach { tweet ->
                            if (!tweet.images.isNullOrEmpty() && !TextUtils.isEmpty(tweet.content)) {
                                tweet.comments?.forEach { comment ->
                                    if (TextUtils.isEmpty(comment.content)) {
                                        tweet.comments!!.remove(comment)
                                    }
                                }
                                data.add(tweet)
                            }
                        }
                        dataWeChatTweetList.postValue(data)
                    },
                    {
                        dataWeChatTweetList.postValue(arrayListOf())
                    }
            )
        }

    }

}