package co.infinum.collar.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import co.infinum.collar.Collar;
import co.infinum.collar.Event;
import co.infinum.collar.EventCollector;
import co.infinum.collar.EventLogger;
import co.infinum.collar.Trackable;
import co.infinum.collar.annotations.Attribute;
import co.infinum.collar.annotations.ConstantAttribute;
import co.infinum.collar.annotations.ConvertAttribute;
import co.infinum.collar.annotations.ConvertAttributes;
import co.infinum.collar.annotations.ScreenName;
import co.infinum.collar.annotations.TrackAttribute;
import co.infinum.collar.annotations.TrackEvent;

@ScreenName(value = "Login")
public class MainActivity extends Activity implements Trackable {

    @TrackEvent("on_create")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Collar.attach(new EventCollector() {
            @Override
            public void onEventTracked(Event event) {
                // Send your events to Firebase, Amplitude, Fabric, Mixpanel, ...
            }
        }).setEventLogger(new EventLogger() {
            @Override
            public void log(String message) {
                // Set your logger here. ie: Log or Timber
                Log.d("Tracker", message);
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @TrackEvent("button_click")
            @ConstantAttribute(key = "button_name", value = "Login")
            @Override
            public void onClick(View v) {
                // do something cool
            }
        });
    }

    @TrackEvent("login")
    private void onLoggedIn(@TrackAttribute User user, @Attribute("id") String id) {
        // do something cool indeed
    }

    @TrackEvent("transform")
    @ConvertAttributes(
        keys = {1, 2},
        values = {"finished", "accepted"}
    )
    private void onItemSelected(@ConvertAttribute("status") int position) {
        // do something cool with selected item
    }

    @TrackEvent("another_event")
    @Attribute("user_id") // This attribute will use return value as attribute value.
    private String userId() {
        return "7110";
    }

    /**
     * For each event this attributes will be used, same as screen_name
     */
    @Override
    public Map<String, Object> getTrackableAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user_id", "7110");
        return attributes;
    }
}
