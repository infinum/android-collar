package co.infinum.processor.options

internal class AnalyticsEventsOptions(
    options: MutableMap<String, String>
) : Options {

    companion object {
        private const val OPTION_EVENTS_COUNT = "events_count"
        private const val OPTION_EVENT_PARAMETERS_COUNT = "event_parameters_count"
        private const val OPTION_EVENT_NAME_LENGTH = "event_name_length"
        private const val OPTION_RESERVED_PREFIXES = "reserved_prefixes"
        private const val OPTION_RESERVED_EVENTS = "reserved_events"

        private const val DEFAULT_COUNT_MAX_EVENTS = 500
        private const val DEFAULT_COUNT_MAX_EVENT_PARAMETERS = 25
        private const val DEFAULT_SIZE_EVENT_NAME = 40
        private val DEFAULT_RESERVED_PREFIXES = listOf("firebase_", "google_", "ga_")
        private val DEFAULT_RESERVED_EVENTS = listOf(
            "ad_activeview",
            "ad_click",
            "ad_exposure",
            "ad_impression",
            "ad_query",
            "adunit_exposure",
            "app_clear_data",
            "app_uninstall",
            "app_update",
            "error",
            "first_open",
            "first_visit",
            "in_app_purchase",
            "notification_dismiss",
            "notification_foreground",
            "notification_open",
            "notification_receive",
            "os_update",
            "screen_view",
            "session_start",
            "user_engagement"
        )

        val SUPPORTED: Set<String> =
            setOf(
                OPTION_EVENTS_COUNT,
                OPTION_EVENT_PARAMETERS_COUNT,
                OPTION_EVENT_NAME_LENGTH,
                OPTION_RESERVED_PREFIXES,
                OPTION_RESERVED_EVENTS
            )
    }

    private var maxEventsCount = DEFAULT_COUNT_MAX_EVENTS
    private var maxEventNameSize = DEFAULT_SIZE_EVENT_NAME
    private var maxEventParametersCount = DEFAULT_COUNT_MAX_EVENT_PARAMETERS
    private var reservedPrefixes = DEFAULT_RESERVED_PREFIXES
    private var reservedEvents = DEFAULT_RESERVED_EVENTS

    init {
        with(options) {
            this[OPTION_EVENTS_COUNT]?.let { maxEventsCount = it.toInt() }
            this[OPTION_EVENT_PARAMETERS_COUNT]?.let { maxEventParametersCount = it.toInt() }
            this[OPTION_EVENT_NAME_LENGTH]?.let { maxEventNameSize = it.toInt() }
            this[OPTION_RESERVED_PREFIXES]?.let { reservedPrefixes = it.split(",") }
            this[OPTION_RESERVED_EVENTS]?.let { reservedEvents = it.split(",") }
        }
    }

    override fun maxCount(): Int = maxEventsCount

    override fun maxNameSize(): Int = maxEventNameSize

    override fun maxParametersCount(): Int = maxEventParametersCount

    override fun reservedPrefixes(): List<String> = reservedPrefixes

    override fun reserved(): List<String> = reservedEvents
}
