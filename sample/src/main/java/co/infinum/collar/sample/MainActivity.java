package co.infinum.collar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.UUID;

import co.infinum.collar.CollarScreenNames;
import co.infinum.collar.annotations.ScreenName;
import co.infinum.collar.sample.analytics.AnalyticsEvent;
import co.infinum.collar.sample.analytics.AnalyticsScreens;
import co.infinum.collar.sample.analytics.CollarAnalyticsEvent;
import co.infinum.collar.sample.analytics.CollarUserProperty;
import co.infinum.collar.sample.analytics.UserProperty;

import static co.infinum.collar.sample.analytics.UserProperty.GenderGo;

@ScreenName(value = AnalyticsScreens.MAIN_SCREEN)
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonProduceEvent1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CollarAnalyticsEvent.trackEvent(new AnalyticsEvent.LoginUser(AnalyticsEvent.LoginUser.LanguageTypeEnum.JAVA.toString()));
            }
        });

        findViewById(R.id.buttonShowChild).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showChildScreen();
            }
        });

        CollarUserProperty.trackProperty(new UserProperty.UserId(UUID.randomUUID().toString()));
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