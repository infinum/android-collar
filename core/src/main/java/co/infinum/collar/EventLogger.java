package co.infinum.collar;

/**
 * Subscribe to the pre-formatted log message stream.
 * Once there is a subscriber, log message will be sent through this interface
 */
public interface EventLogger {

    void log(String message);
}
