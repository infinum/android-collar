package co.infinum.collar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.UUID;

import co.infinum.collar.CollarScreenNames;
import co.infinum.collar.annotations.ScreenName;
import co.infinum.collar.sample.databinding.ActivityMainBinding;

@ScreenName(value = KotlinScreenNames.MAIN_SCREEN_JAVA)
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityMainBinding viewBinding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        viewBinding.buttonProduceEvent1.setOnClickListener(v ->
            CollarAnalyticsEvent.trackEvent(new AnalyticsEvent.Event1(
                "awesome",
                true,
                Byte.MAX_VALUE,
                Character.MAX_VALUE,
                7.11,
                31.5f,
                18,
                2L,
                Short.MAX_VALUE,
                buildBundle()
            ))
        );

        viewBinding.buttonShowChild.setOnClickListener(v -> showChildScreen());

        CollarUserProperty.trackProperty(new UserProperty.MyUUID(UUID.randomUUID().toString()));
        CollarUserProperty.trackProperty(new UserProperty.LanguageType("Java"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        CollarScreenNames.trackScreen(this);
    }

    private void showChildScreen() {
        startActivity(new Intent(this, ChildActivity.class));
    }

    private Bundle buildBundle() {
        final Bundle bundle = new Bundle();
        bundle.putInt("4", 4);
        bundle.putInt("5", 5);
        bundle.putInt("6", 6);
        return bundle;
    }
}