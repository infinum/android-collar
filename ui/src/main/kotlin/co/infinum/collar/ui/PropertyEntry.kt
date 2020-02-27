package co.infinum.collar.ui

data class PropertyEntry(
    override val timestamp: Long,
    override val color: Int,
    override val icon: Int,
    override val name: String,
    val value: String?
) : CollarEntry