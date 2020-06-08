package co.infinum.collar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import co.infinum.collar.CollarScreenNames;
import co.infinum.collar.annotations.ScreenName;
import co.infinum.collar.sample.analytics.CollarTrackingPlanEvents;
import co.infinum.collar.sample.analytics.CollarTrackingPlanUserProperties;
import co.infinum.collar.sample.analytics.TrackingPlanEvents;
import co.infinum.collar.sample.analytics.TrackingPlanScreens;
import co.infinum.collar.sample.analytics.TrackingPlanUserProperties;

@ScreenName(value = TrackingPlanScreens.HOME)
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonProduceEvent1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CollarTrackingPlanEvents.trackEvent(new TrackingPlanEvents.UserLogIn(TrackingPlanEvents.UserLogIn.TypeEnum.FACEBOOK.toString(),
                    TrackingPlanEvents.UserLogIn.SourceEnum.ONBOARDING.toString()));
            }
        });

        findViewById(R.id.buttonShowChild).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showChildScreen();
            }
        });

        CollarTrackingPlanUserProperties.trackProperty(new TrackingPlanUserProperties.MachineModel(
            TrackingPlanUserProperties.AnalyticsConsent.AnalyticsConsentEnum.OPT_IN.toString()));
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