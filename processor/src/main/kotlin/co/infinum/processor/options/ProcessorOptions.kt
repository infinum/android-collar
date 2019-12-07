package co.infinum.processor.options

class ProcessorOptions(
    options: MutableMap<String, String>
) : Options {

    companion object {
        private const val OPTION_SCREEN_NAME_LENGTH = "screen_name_length"
        private const val OPTION_EVENTS_COUNT = "events_count"
        private const val OPTION_EVENT_PARAMETERS_COUNT = "event_parameters_count"
        private const val OPTION_EVENT_NAME_LENGTH = "event_name_length"
        private const val OPTION_RESERVED_PREFIXES = "reserved_prefixes"

        private const val DEFAULT_SIZE_SCREEN_NAME = 36
        private const val DEFAULT_COUNT_MAX_EVENTS = 500
        private const val DEFAULT_COUNT_MAX_EVENT_PARAMETERS = 25
        private const val DEFAULT_SIZE_EVENT_NAME = 40
        private val DEFAULT_RESERVED_PREFIXES = listOf("firebase_", "google_", "ga_")

        val SUPPORTED: Set<String> =
            setOf(
                OPTION_SCREEN_NAME_LENGTH,
                OPTION_EVENTS_COUNT,
                OPTION_EVENT_PARAMETERS_COUNT,
                OPTION_EVENT_NAME_LENGTH,
                OPTION_RESERVED_PREFIXES
            )
    }

    var maxScreenNameSize = DEFAULT_SIZE_SCREEN_NAME
    var maxEventsCount = DEFAULT_COUNT_MAX_EVENTS
    var maxEventParametersCount = DEFAULT_COUNT_MAX_EVENT_PARAMETERS
    var maxEventNameSize = DEFAULT_SIZE_EVENT_NAME
    var reservedPrefixes = DEFAULT_RESERVED_PREFIXES

    init {
        with(options) {
            this[OPTION_SCREEN_NAME_LENGTH]?.let { maxScreenNameSize = it.toInt() }
            this[OPTION_EVENTS_COUNT]?.let { maxEventsCount = it.toInt() }
            this[OPTION_EVENT_PARAMETERS_COUNT]?.let { maxEventParametersCount = it.toInt() }
            this[OPTION_EVENT_NAME_LENGTH]?.let { maxEventNameSize = it.toInt() }
            this[OPTION_RESERVED_PREFIXES]?.let { reservedPrefixes = it.split(",") }
        }
    }
}