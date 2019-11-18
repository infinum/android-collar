package co.infinum.collar

/**
 * Subscribe to the pre-formatted onEventLogged message stream.
 * Once there is a subscriber, onEventLogged message will be sent through this interface
 */
interface EventLogger {

    fun onEventLogged(message: String)
}