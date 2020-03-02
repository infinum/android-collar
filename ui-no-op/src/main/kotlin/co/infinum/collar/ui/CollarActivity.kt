package co.infinum.collar.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class CollarActivity : AppCompatActivity(R.layout.collar_activity_collar) {

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, CollarActivity::class.java))
    }
}