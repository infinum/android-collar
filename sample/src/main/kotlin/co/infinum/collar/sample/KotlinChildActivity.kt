package co.infinum.collar.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class KotlinChildActivity : AppCompatActivity(R.layout.activity_child_kotlin) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerKotlin, KotlinChildFragment())
            .commit()
    }
}
