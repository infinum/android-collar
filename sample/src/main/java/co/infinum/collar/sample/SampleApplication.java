package co.infinum.collar.sample;

import android.app.Application;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import co.infinum.collar.Collar;
import co.infinum.collar.Event;
import co.infinum.collar.EventCollector;
import co.infinum.collar.EventLogger;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        attachCollar();
    }

    private void attachCollar() {
        Collar.attach(new EventCollector() {
            @Override
            public void onEventCollected(Event event) {
                // Send your events to Firebase, Amplitude, Fabric, Mixpanel, ...
                Log.d("onEventCollected", event.getName());
            }
        }).setEventLogger(new EventLogger() {
            @Override
            public void onEventLogged(@NotNull String message) {
                // Set your logger here, like Log, Timber, ...
                Log.d("onEventLogged", message);
            }
        });
    }
}
