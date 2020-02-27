package co.infinum.collar.ui.entries

import android.os.Bundle
import androidx.core.os.bundleOf
import co.infinum.collar.ui.entries.CollarEntry

data class EventEntry(
    override val timestamp: Long,
    override val name: String,
    val parameters: Bundle = bundleOf()
) : CollarEntry