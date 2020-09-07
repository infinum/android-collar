package co.infinum.collar.sample;

import android.app.Application;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import androidx.collection.ArraySet;
import co.infinum.collar.Collar;
import co.infinum.collar.Event;
import co.infinum.collar.Property;
import co.infinum.collar.Screen;
import co.infinum.collar.ui.Configuration;
import co.infinum.collar.ui.LiveCollector;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        attachCollar();
    }

    private void attachCollar() {
        final Set<String> redactedWords = new ArraySet<>(1);
        redactedWords.add("Java");
        Collar.attach(new LiveCollector(new Configuration(true, true, redactedWords)) {

            @Override
            public void onScreen(@NotNull Screen screen) {
                super.onScreen(screen);
                // Send your screen views to Firebase, Amplitude, Mixpanel, ...
                Log.d("onScreen", screen.toString());
            }

            @Override
            public void onEvent(@NotNull Event event) {
                super.onEvent(event);
                // Send your events to Firebase, Amplitude, Mixpanel, ...
                Log.d("onEvent", event.toString());
            }

            @Override
            public void onProperty(@NotNull Property property) {
                super.onProperty(property);
                // Send your user properties to Firebase, Amplitude, Mixpanel, ...
                Log.d("onProperty", property.toString());
            }
        });
    }
}
