package co.infinum.processor.options

class UserPropertiesOptions(
    options: MutableMap<String, String>
) : Options {

    companion object {
        private const val OPTION_PROPERTIES_COUNT = "properties_count"
        private const val OPTION_RESERVED_PROPERTIES = "reserved_properties"

        private const val DEFAULT_COUNT_MAX_PROPERTIES = 25
        private val DEFAULT_RESERVED_PROPERTIES = listOf("Age", "Gender", "Country")

        val SUPPORTED: Set<String> =
            setOf(
                OPTION_PROPERTIES_COUNT,
                OPTION_RESERVED_PROPERTIES
            )
    }

    private var maxPropertiesCount = DEFAULT_COUNT_MAX_PROPERTIES
    private var reservedProperties = DEFAULT_RESERVED_PROPERTIES

    init {
        with(options) {
            this[OPTION_PROPERTIES_COUNT]?.let { maxPropertiesCount = it.toInt() }
            this[OPTION_RESERVED_PROPERTIES]?.let { reservedProperties = it.split(",") }
        }
    }

    override fun maxCount(): Int = maxPropertiesCount

    override fun reserved(): List<String> = reservedProperties
}