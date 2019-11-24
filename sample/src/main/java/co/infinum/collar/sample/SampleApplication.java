package co.infinum.collar.sample;

import android.app.Application;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import co.infinum.collar.Collar;
import co.infinum.collar.Event;
import co.infinum.collar.Collector;
import co.infinum.collar.Screen;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        attachCollar();
    }

    private void attachCollar() {
        Collar.attach(new Collector() {

            @Override
            public void onScreen(@NotNull Screen screen) {
                // Send your screen views to Firebase, Amplitude, Fabric, Mixpanel, ...
                Log.d("onScreen", screen.toString());
            }

            @Override
            public void onEvent(@NotNull Event event) {
                // Send your events to Firebase, Amplitude, Fabric, Mixpanel, ...
                Log.d("onEvent", event.toString());
            }
        });
    }
}
