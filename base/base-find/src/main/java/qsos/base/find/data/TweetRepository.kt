package qsos.base.find.data

import android.text.TextUtils
import kotlinx.coroutines.CoroutineScope
import qsos.core.lib.data.chat.WeChatTweetBeen
import qsos.core.lib.data.chat.WeChatUserBeen
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.data.HttpLiveData
import qsos.lib.netservice.expand.retrofit
import qsos.lib.netservice.expand.retrofitWithFunction
import qsos.lib.netservice.expand.retrofitWithLiveData
import vip.qsos.exception.GlobalException
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 聊天数据获取
 * TweetRepository 内部中 MutableLiveData 的对象必须为 val 不可变对象，防止外部篡改，外部仅观察数据。
 */
class TweetRepository(
        private val mCoroutineContext: CoroutineContext
) : ITweetRepo {

    val mDataTweetList: HttpLiveData<List<WeChatTweetBeen>> = HttpLiveData()

    val mDataUserInfo: HttpLiveData<WeChatUserBeen> = HttpLiveData()

    override fun postForm(success: () -> Unit) {
        CoroutineScope(mCoroutineContext).retrofit<WeChatUserBeen> {
            api = ApiEngine.createService(ApiTweet::class.java).getUser()
            onSuccess {
                success()
            }
            onFailed { code, error ->
                throw GlobalException.ServerException(code, error)
            }
        }
    }

    override fun getUserInfo() {
        CoroutineScope(mCoroutineContext).retrofitWithLiveData<WeChatUserBeen> {
            api = ApiEngine.createService(ApiTweet::class.java).getUser()
            data = mDataUserInfo
        }
    }

    override fun getTweetList() {
        val temValue = mDataTweetList.value
        if (!temValue.isNullOrEmpty()) {
            if (temValue.size > 5) {
                // 异步加载5条数据到列表刷新
                mDataTweetList.postValue(temValue.subList(0, 5))
            } else {
                // 不足5条全部加载
                mDataTweetList.postValue(temValue)
            }
        } else {
            CoroutineScope(mCoroutineContext).retrofitWithFunction<List<WeChatTweetBeen>> {
                api = ApiEngine.createService(ApiTweet::class.java).getTweet()
                data = mDataTweetList
                onSuccess {
                    val data = arrayListOf<WeChatTweetBeen>()
                    it!!.forEach { tweet ->
                        if (!tweet.images.isNullOrEmpty() && !TextUtils.isEmpty(tweet.content)) {
                            tweet.comments?.forEach { comment ->
                                if (TextUtils.isEmpty(comment.content)) {
                                    tweet.comments!!.remove(comment)
                                }
                            }
                            data.add(tweet)
                        }
                    }
                    mDataTweetList.postValue(it)
                }
            }
        }
    }

}