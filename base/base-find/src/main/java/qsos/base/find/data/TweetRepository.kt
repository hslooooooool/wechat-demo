package qsos.base.find.data

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import qsos.lib.base.data.HttpLiveData
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.data.WeChatUserBeen
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.ObservableService

/**
 * @author : 华清松
 * @description : 聊天数据获取
 * TweetRepository 内部中 MutableLiveData 的对象必须为 val 不可变对象，防止外部篡改，外部仅观察数据。
 */
@SuppressLint("CheckResult")
class TweetRepository : ITweetRepo {
    /**推特列表数据*/
    val dataTweetList = HttpLiveData<List<WeChatTweetBeen>>()

    /**用户数据*/
    val dataUserInfo = HttpLiveData<WeChatUserBeen>()

    override fun getUserInfo() {
        ObservableService.setObservable(
                ApiEngine.createService(ApiTweet::class.java).getUserInfo()
        ).subscribe(
                {
                    dataUserInfo.postValue(it)
                },
                {
                    dataUserInfo.postValue(WeChatUserBeen())
                }
        )
    }

    override fun getTweetList() {
        val temValue = dataTweetList.value
        if (!temValue.isNullOrEmpty()) {
            if (temValue.size > 5) {
                // 异步加载5条数据到列表刷新
                dataTweetList.postValue(temValue.subList(0, 5))
            } else {
                // 不足5条全部加载
                dataTweetList.postValue(temValue)
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
                        dataTweetList.postValue(data)
                    },
                    {
                        dataTweetList.postValue(arrayListOf())
                    }
            )
        }

    }

}