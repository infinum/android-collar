package co.infinum.processor.options

interface Options {

    fun maxCount(): Int = throw UnsupportedOperationException()

    fun maxNameSize(): Int = throw UnsupportedOperationException()

    fun maxParametersCount(): Int = throw UnsupportedOperationException()

    fun reserved(): List<String> = throw UnsupportedOperationException()
}