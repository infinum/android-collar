package co.infinum.collar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import co.infinum.collar.CollarScreenNames;
import co.infinum.collar.annotations.ScreenName;

@ScreenName(value = "MainActivity")
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        CollarAnalyticsEvent.trackEvent(new AnalyticsEvent.OnCreate("Main"));

        findViewById(R.id.buttonFoo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doFoo();
                CollarAnalyticsEvent.trackEvent(new AnalyticsEvent.DoFoo());
            }
        });
        findViewById(R.id.buttonKotlin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showKotlin();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CollarScreenNames.trackScreen(this);
    }

    private void doFoo() {
        new Foo().trackFoo();
    }

    private void showKotlin() {
        startActivity(new Intent(this, KotlinMainActivity.class));
    }

    private void onItemSelected(int position) {
        CollarAnalyticsEvent.trackEvent(new AnalyticsEvent.OnItemSelected(position, true));
    }
}