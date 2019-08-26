package qsos.base.find

import org.junit.runner.RunWith
import org.junit.runners.Suite
import qsos.base.find.data.MockitoTest

@RunWith(value = Suite::class)
@Suite.SuiteClasses(value = [MockitoTest::class, TweetRepositoryTest::class])
class TestFind