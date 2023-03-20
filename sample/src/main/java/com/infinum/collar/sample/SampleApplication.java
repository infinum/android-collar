package com.infinum.collar.sample;

import android.app.Application;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import androidx.collection.ArraySet;

import com.infinum.collar.Collar;
import com.infinum.collar.Event;
import com.infinum.collar.Property;
import com.infinum.collar.Screen;
import com.infinum.collar.ui.Configuration;
import com.infinum.collar.ui.LiveCollector;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        attachCollar();
    }

    private void attachCollar() {
        Collar.attach(
                new LiveCollector(
                        new Configuration(
                                SampleConstants.analyticsCollectionEnabled,
                                SampleConstants.showSystemNotifications,
                                SampleConstants.showInAppNotifications,
                                SampleConstants.INSTANCE.getRedactedWords()
                        )
                ) {

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
