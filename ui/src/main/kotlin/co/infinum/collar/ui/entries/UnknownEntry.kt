package co.infinum.collar.ui.entries

import co.infinum.collar.ui.entries.CollarEntry

data class UnknownEntry(
    override val timestamp: Long,
    override val name: String
) : CollarEntry