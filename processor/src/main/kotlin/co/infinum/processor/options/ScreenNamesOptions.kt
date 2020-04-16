package co.infinum.processor.options

class ScreenNamesOptions(
    options: MutableMap<String, String>
) : Options {

    companion object {
        private const val OPTION_SCREEN_NAME_LENGTH = "screen_name_length"

        private const val DEFAULT_SIZE_SCREEN_NAME = 36

        val SUPPORTED: Set<String> =
            setOf(
                OPTION_SCREEN_NAME_LENGTH
            )
    }

    private var maxScreenNameSize = DEFAULT_SIZE_SCREEN_NAME

    init {
        with(options) {
            this[OPTION_SCREEN_NAME_LENGTH]?.let { maxScreenNameSize = it.toInt() }
        }
    }

    override fun maxNameSize(): Int = maxScreenNameSize
}
