package com.infinum.collar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.infinum.collar.Collar;
import com.infinum.collar.CollarScreenNames;
import com.infinum.collar.annotations.ScreenName;
import com.infinum.collar.sample.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ScreenName(value = KotlinScreenNames.MAIN_SCREEN_JAVA)
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityMainBinding viewBinding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        viewBinding.analyticsCollectionSwitch.setChecked(SampleConstants.analyticsCollectionEnabled);
        viewBinding.analyticsCollectionSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> Collar.setAnalyticsCollectionStatus(isChecked)

        );

        viewBinding.buttonProduceEvent1.setOnClickListener(
                v -> CollarAnalyticsEvent.trackEvent(new AnalyticsEvent.Event1(
                        "awesome",
                        true,
                        Byte.MAX_VALUE,
                        Character.MAX_VALUE,
                        7.11,
                        31.5f,
                        18,
                        2L,
                        Short.MAX_VALUE,
                        buildMap(),
                        "awesome",
                        true,
                        Byte.MAX_VALUE,
                        Character.MAX_VALUE,
                        7.11,
                        31.5f,
                        18,
                        2L,
                        Short.MAX_VALUE,
                        "awesome",
                        true,
                        Byte.MAX_VALUE,
                        Character.MAX_VALUE,
                        7.11,
                        31.5f,
                        18,
                        2L,
                        Short.MAX_VALUE,
                        "awesome",
                        true,
                        Byte.MAX_VALUE,
                        Character.MAX_VALUE,
                        7.11,
                        31.5f,
                        18,
                        2L,
                        Short.MAX_VALUE,
                        buildMap(),
                        "awesome",
                        true,
                        Byte.MAX_VALUE,
                        Character.MAX_VALUE,
                        7.11,
                        31.5f,
                        18,
                        2L,
                        Short.MAX_VALUE,
                        "awesome",
                        true,
                        Byte.MAX_VALUE,
                        Character.MAX_VALUE,
                        7.11,
                        31.5f,
                        18,
                        2L,
                        Short.MAX_VALUE
                ))
        );

        viewBinding.buttonShowChild.setOnClickListener(v -> showChildScreen());

        CollarUserProperty.trackProperty(new UserProperty.MyUUID(UUID.randomUUID().toString()));
        CollarUserProperty.trackProperty(new UserProperty.LanguageType("Java", new HashMap<>()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        CollarScreenNames.trackScreen(this);
    }

    private void showChildScreen() {
        startActivity(new Intent(this, ChildActivity.class));
    }

    private Map<String, Integer> buildMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("4", 4);
        map.put("5", 5);
        map.put("6", 6);
        return map;
    }
}