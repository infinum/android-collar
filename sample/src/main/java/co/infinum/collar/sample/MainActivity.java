package co.infinum.collar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.UUID;

import co.infinum.collar.CollarScreenNames;
import co.infinum.collar.annotations.ScreenName;

@ScreenName(value = JavaScreenNames.MAIN_SCREEN)
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonProduceEvent1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CollarAnalyticsEvent.trackEvent(new AnalyticsEvent.Event1());
            }
        });

        findViewById(R.id.buttonShowChild).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showChildScreen();
            }
        });

        CollarUserProperty.trackProperty(new UserProperty.UserUUID(UUID.randomUUID().toString()));
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
}