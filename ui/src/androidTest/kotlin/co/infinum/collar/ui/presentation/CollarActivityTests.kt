package co.infinum.collar.ui.presentation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.infinum.collar.Collar
import co.infinum.collar.ui.CollarTestApplication
import co.infinum.collar.ui.Configuration
import co.infinum.collar.ui.LiveCollector
import co.infinum.collar.ui.R
import co.infinum.collar.ui.data.models.local.SettingsEntity
import co.infinum.collar.ui.domain.Domain
import co.infinum.collar.ui.domain.settings.SettingsRepository
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CollarActivityTests {

    companion object {

        private lateinit var context: Context

        @BeforeClass
        @JvmStatic
        fun setupTest() {
            context = ApplicationProvider.getApplicationContext<CollarTestApplication>()
                .applicationContext

            Domain.initialise(context)
            SettingsRepository.save(SettingsEntity())
        }
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun whenLaunchedWithoutCollector_thenShowEmptyState() {
        launchActivity<CollarActivity>()

        onView(withId(R.id.appBarLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.instructionsLabel)).check(matches(isDisplayed()))
        onView(withId(R.id.instructionsButton)).check(matches(isDisplayed()))
        onView(withId(R.id.instructionsLabel)).check(matches(withText(R.string.collar_empty)))
        onView(withId(R.id.instructionsButton)).check(matches(withText(R.string.collar_check_setup)))
    }

    @Test
    fun whenLaunchedWithCollector_thenShowEmptyState() {
        Collar.attach(LiveCollector(Configuration(showSystemNotifications = false, showInAppNotifications = false)))

        launchActivity<CollarActivity>()

        onView(withId(R.id.appBarLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.emptyLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.instructionsLabel)).check(matches(isDisplayed()))
        onView(withId(R.id.instructionsButton)).check(matches(isDisplayed()))
        onView(withId(R.id.instructionsLabel)).check(matches(withText(R.string.collar_empty)))
        onView(withId(R.id.instructionsButton)).check(matches(withText(R.string.collar_check_setup)))

//        onView(withId(R.id.search)).perform(click())

//        onView(withId(R.id.filter)).check(matches(not(isDisplayed())))
//        onView(withId(R.id.settings)).check(matches(not(isDisplayed())))
    }
}