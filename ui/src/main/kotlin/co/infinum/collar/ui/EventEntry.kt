package co.infinum.collar.ui

import android.os.Bundle
import androidx.core.os.bundleOf

data class EventEntry(
    override val timestamp: Long,
    override val color: Int,
    override val icon: Int,
    override val name: String,
    val parameters: Bundle = bundleOf()
) : CollarEntry