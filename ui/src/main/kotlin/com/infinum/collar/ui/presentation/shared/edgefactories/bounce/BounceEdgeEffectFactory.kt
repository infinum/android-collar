package com.infinum.collar.ui.presentation.shared.edgefactories.bounce

import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

internal class BounceEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {

    override fun createEdgeEffect(recyclerView: RecyclerView, direction: Int): EdgeEffect =
        BounceEdgeEffect(recyclerView, direction)
}
