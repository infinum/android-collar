package co.infinum.collar.ui.entries

import co.infinum.collar.ui.entries.CollarEntry

data class PropertyEntry(
    override val timestamp: Long,
    override val name: String,
    val value: String?
) : CollarEntry