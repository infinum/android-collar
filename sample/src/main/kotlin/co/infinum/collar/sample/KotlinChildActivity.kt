package co.infinum.collar.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.infinum.collar.sample.databinding.ActivityChildKotlinBinding

class KotlinChildActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding = ActivityChildKotlinBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        supportFragmentManager.beginTransaction()
            .replace(viewBinding.fragmentContainerKotlin.id, KotlinChildFragment())
            .commit()
    }
}
