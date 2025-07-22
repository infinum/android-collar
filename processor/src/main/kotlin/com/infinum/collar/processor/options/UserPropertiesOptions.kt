package com.infinum.collar.processor.options

internal class UserPropertiesOptions(
    options: MutableMap<String, String>
) : Options {

    companion object {
        private const val OPTION_PROPERTIES_COUNT = "properties_count"
        private const val OPTION_PROPERTY_NAME_LENGTH = "property_name_length"
        private const val OPTION_PROPERTY_NAME_REGEX = "property_name_regex"
        private const val OPTION_RESERVED_PREFIXES = "reserved_prefixes"
        private const val OPTION_RESERVED_PROPERTIES = "reserved_properties"

        private const val DEFAULT_COUNT_MAX_PROPERTIES = 25
        private const val DEFAULT_COUNT_MAX_PROPERTY_PARAMETERS = 2
        private const val DEFAULT_SIZE_PROPERTY_NAME = 24
        const val DEFAULT_REGEX_PROPERTY_NAME = "^[a-zA-Z0-9_]*$"
        private val DEFAULT_RESERVED_PREFIXES = listOf("firebase_", "google_", "ga_")
        private val DEFAULT_RESERVED_PROPERTIES = listOf(
            "first_open_time",
            "first_visit_time",
            "last_deep_link_referrer",
            "user_id",
            "first_open_after_install"
        )

        val SUPPORTED: Set<String> =
            setOf(
                OPTION_PROPERTIES_COUNT,
                OPTION_PROPERTY_NAME_LENGTH,
                OPTION_PROPERTY_NAME_REGEX,
                OPTION_RESERVED_PREFIXES,
                OPTION_RESERVED_PROPERTIES
            )
    }

    private var maxPropertiesCount = DEFAULT_COUNT_MAX_PROPERTIES
    private var maxPropertyNameSize = DEFAULT_SIZE_PROPERTY_NAME
    private var propertyNameRegex = DEFAULT_REGEX_PROPERTY_NAME
    private val maxPropertyParametersCount = DEFAULT_COUNT_MAX_PROPERTY_PARAMETERS
    private var reservedPrefixes = DEFAULT_RESERVED_PREFIXES
    private var reservedProperties = DEFAULT_RESERVED_PROPERTIES

    init {
        with(options) {
            this[OPTION_PROPERTIES_COUNT]?.let { maxPropertiesCount = it.toInt() }
            this[OPTION_PROPERTY_NAME_LENGTH]?.let { maxPropertyNameSize = it.toInt() }
            this[OPTION_PROPERTY_NAME_REGEX]?.let { propertyNameRegex = it }
            this[OPTION_RESERVED_PREFIXES]?.let { reservedPrefixes = it.split(",") }
            this[OPTION_RESERVED_PROPERTIES]?.let { reservedProperties = it.split(",") }
        }
    }

    override fun maxCount(): Int = maxPropertiesCount

    override fun maxNameSize(): Int = maxPropertyNameSize

    override fun nameRegex(): String = propertyNameRegex

    override fun maxParametersCount(): Int = maxPropertyParametersCount

    override fun reservedPrefixes(): List<String> = reservedPrefixes

    override fun reserved(): List<String> = reservedProperties
}
