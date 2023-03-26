package com.infinum.collar.ui.presentation.shared.base

import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal abstract class BaseActivity<State, Event> : AppCompatActivity(), BaseView<State, Event> {

    abstract val binding: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        collectFlows(this)
    }
}
