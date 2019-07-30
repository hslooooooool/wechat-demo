package qsos.base.find.data

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import qsos.lib.base.BuildConfig
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.data.WeChatUserBeen
import qsos.lib.base.utils.RxTestUtils
import qsos.lib.base.utils.date.DateDeserializer
import qsos.lib.base.utils.date.DateSerializer
import qsos.lib.netservice.ApiEngine
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * @author : 华清松
 * @description : TweetRepositoryTest 重点测试接口是否连通且返回的数据格式是否正确
 */
class TweetRepositoryTest {

    private val mGSonBuilder: GsonBuilder = GsonBuilder()
    private lateinit var mGSonConverterFactory: GsonConverterFactory
    private lateinit var mClient: OkHttpClient.Builder
    private lateinit var mBuild: Retrofit.Builder

    @Before
    @Throws(Exception::class)
    fun setUp() {
        RxTestUtils.asyncToSync()
        mGSonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        mGSonBuilder.registerTypeAdapter(Date::class.java, DateSerializer()).setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        mGSonConverterFactory = GsonConverterFactory.create(mGSonBuilder.create())
        mClient = OkHttpClient.Builder()
        mBuild = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(mGSonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(mClient.build())
    }

    @Test
    fun getTweet() = runBlocking {
        launch {
            val tweets = ApiEngine.createService(ApiTweet::class.java).getTweet().await()

            val data = arrayListOf<WeChatTweetBeen>()
            tweets.forEach { tweet ->
                if (!tweet.images.isNullOrEmpty() && !TextUtils.isEmpty(tweet.content)) {
                    tweet.comments?.forEach { comment ->
                        if (TextUtils.isEmpty(comment.content)) {
                            tweet.comments!!.remove(comment)
                        }
                    }
                    data.add(tweet)
                }
            }

            assert(tweets.isNotEmpty())
            assert(data.isNotEmpty())

            System.out.println("数量：" + tweets.size)
            System.out.println(data.size)
        }
    }

    @Test
    fun getUserInfo() {
        mBuild.build()
                .create(ApiTweet::class.java)
                .getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    assert(it != null)
                    assert(it is WeChatUserBeen)
                    System.out.println(Gson().toJson(it))
                }
    }

    @Test
    fun getTweetList() {
        mBuild.build()
                .create(ApiTweet::class.java)
                .getTweetList()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    assert(it != null)
                    assert(it is List<WeChatTweetBeen>)
                    System.out.println(Gson().toJson(it))
                }
    }

    @Test
    fun testUserInfo() {
        val mUserInfo = WeChatUserBeen()
        mUserInfo.profileImage = "http://www.qsos.vip/resource/test.jpg"
        mUserInfo.avatar = "http://www.qsos.vip/resource/test.jpg"
        mUserInfo.nick = "那所有沸腾的年华"
        mUserInfo.username = "华清松"
        assert(mUserInfo.profileImage == "http://www.qsos.vip/resource/test.jpg")
        assert(mUserInfo.avatar == "http://www.qsos.vip/resource/test.jpg")
        assert(mUserInfo.nick == "那所有沸腾的年华")
        assert(mUserInfo.username == "华清松")
        System.out.println(Gson().toJson(mUserInfo))
    }

}
