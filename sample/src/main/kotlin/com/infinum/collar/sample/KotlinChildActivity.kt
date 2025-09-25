package com.infinum.collar.sample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.infinum.collar.sample.databinding.ActivityChildKotlinBinding

class KotlinChildActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val viewBinding = ActivityChildKotlinBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        supportFragmentManager.beginTransaction()
            .replace(viewBinding.fragmentContainerKotlin.id, KotlinChildFragment())
            .commit()
    }
}
