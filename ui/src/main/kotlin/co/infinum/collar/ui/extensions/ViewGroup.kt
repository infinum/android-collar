package co.infinum.collar.ui.extensions

import android.view.LayoutInflater
import android.view.ViewGroup

internal val ViewGroup.inflater: LayoutInflater
    get() = lazyOf(LayoutInflater.from(this.context)).value
