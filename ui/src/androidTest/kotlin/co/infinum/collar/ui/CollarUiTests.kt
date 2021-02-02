package co.infinum.collar.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.infinum.collar.ui.presentation.CollarActivity
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CollarUiTests {

    companion object {

        private lateinit var context: Context

        @BeforeClass
        @JvmStatic
        fun setupTest() {
            context = ApplicationProvider.getApplicationContext<CollarTestApplication>()
                .applicationContext
        }
    }

    @Test
    fun launchIntent() {
        Intents.init()

        context.startActivity(CollarUi.launchIntent())

        intended(hasComponent(CollarActivity::class.java.name), Intents.times(1))

        Intents.release()
    }

    @Test
    fun show() {
        Intents.init()

        CollarUi.show()

        intended(hasComponent(CollarActivity::class.java.name), Intents.times(1))

        Intents.release()
    }
}
