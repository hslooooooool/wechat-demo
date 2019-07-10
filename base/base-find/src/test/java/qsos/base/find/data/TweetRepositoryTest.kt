package qsos.base.find.data

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import qsos.base.find.BuildConfig
import qsos.lib.base.data.WeChatUserBeen
import qsos.lib.base.utils.RxTestUtils
import qsos.lib.netservice.ApiEngine

/**
 * @author : 华清松
 * @description : 七月
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [23])
class TweetRepositoryTest {

    @Before
    fun setUp() {
        RxTestUtils.asyncToSync()
    }

    @Test
    fun testGetUserInfo() {
        ApiEngine.createService(ApiTweet::class.java).getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    assert(it != null)
                    assert(it is WeChatUserBeen)
                }
    }
}
