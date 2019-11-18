package co.infinum.collar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import co.infinum.collar.Trackable;
import co.infinum.collar.annotations.Attribute;
import co.infinum.collar.annotations.ConstantAttribute;
import co.infinum.collar.annotations.ConvertAttribute;
import co.infinum.collar.annotations.ConvertAttributes;
import co.infinum.collar.annotations.ScreenName;
import co.infinum.collar.annotations.TrackAttribute;
import co.infinum.collar.annotations.AnalyticsEvent;

@ScreenName(value = "MainScreen")
public class MainActivity extends Activity implements Trackable {

    @AnalyticsEvent("onCreate")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @AnalyticsEvent("buttonClick")
            @ConstantAttribute(key = "buttonName", value = "showLogin")
            @Override
            public void onClick(View v) {
                showLogin();
            }
        });
    }

    /**
     * For each event this attributes will be used, same as screen_name
     */
    @Override
    public Map<String, Object> trackableAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userId", "7110");
        return attributes;
    }

    @AnalyticsEvent("showLogin")
    private void showLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @AnalyticsEvent("onLoggedIn")
    private void onLoggedIn(@TrackAttribute User user, @Attribute("id") String id) {
        // do something cool indeed
    }

    @AnalyticsEvent("onItemSelected")
    @ConvertAttributes(
        keys = {1, 2},
        values = {"finished", "accepted"}
    )
    private void onItemSelected(@ConvertAttribute("status") int position) {
        // do something cool with selected item
    }

    @AnalyticsEvent("someEvent")
    @Attribute("userId") // This attribute will use return value as attribute value.
    private String userId() {
        return "7110";
    }
}