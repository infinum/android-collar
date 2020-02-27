package co.infinum.collar.ui

data class ScreenEntry(
    override val timestamp: Long,
    override val color: Int,
    override val icon: Int,
    override val name: String
) : CollarEntry