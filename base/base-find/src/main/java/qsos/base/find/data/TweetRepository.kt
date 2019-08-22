package qsos.base.find.data

import android.text.TextUtils
import kotlinx.coroutines.CoroutineScope
import qsos.lib.base.data.HttpLiveData
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.data.WeChatUserBeen
import qsos.lib.base.data.http.BaseDataState
import qsos.lib.base.data.http.DataState
import qsos.lib.base.data.http.DataStateEntity
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.expand.retrofit
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * @description : 聊天数据获取
 * TweetRepository 内部中 MutableLiveData 的对象必须为 val 不可变对象，防止外部篡改，外部仅观察数据。
 */
class TweetRepository(
        private val mCoroutineContext: CoroutineContext,
        val mDataTweetList: HttpLiveData<List<WeChatTweetBeen>> = HttpLiveData(),
        val mDataUserInfo: HttpLiveData<WeChatUserBeen> = HttpLiveData()
) : ITweetRepo {

    override fun getUserInfo() {
        CoroutineScope(mCoroutineContext).retrofit<WeChatUserBeen> {
            api = ApiEngine.createService(ApiTweet::class.java).getUser()

            onStart {
                mDataUserInfo.httpState.postValue(DataStateEntity(DataState.LOADING))
            }
            onSuccess {
                mDataUserInfo.httpState.postValue(DataStateEntity(DataState.SUCCESS))
                mDataUserInfo.postValue(it)
            }
            onFailed { code, msg ->
                mDataUserInfo.httpState.postValue(DataStateEntity(BaseDataState(code, msg)))
            }
            onComplete {

            }
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
            CoroutineScope(mCoroutineContext).retrofit<List<WeChatTweetBeen>> {
                api = ApiEngine.createService(ApiTweet::class.java).getTweet()

                onStart {
                    mDataTweetList.httpState.postValue(DataStateEntity(DataState.LOADING))
                }
                onSuccess {
                    mDataTweetList.httpState.postValue(DataStateEntity(DataState.SUCCESS))
                    // NOTICE 数据校验 ignore the tweet which does not contain a content and images
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
                onFailed { code, msg ->
                    mDataTweetList.httpState.postValue(DataStateEntity(BaseDataState(code, msg)))
                }
            }
        }

    }

}