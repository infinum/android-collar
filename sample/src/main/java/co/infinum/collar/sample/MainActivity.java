package co.infinum.collar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import co.infinum.collar.annotations.ScreenName;

@ScreenName(value = "MainScreen")
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Collar.trackEvent(new AnalyticsEvent.OnCreate("Main"));

        findViewById(R.id.buttonFoo).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doFoo();
                Collar.trackEvent(new AnalyticsEvent.DoFoo());
            }
        });
        findViewById(R.id.buttonKotlin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showKotlin();
            }
        });
    }

    private void doFoo() {
        new Foo().trackFoo();
    }

    private void showKotlin() {
        startActivity(new Intent(this, KotlinMainActivity.class));
    }

    private void onItemSelected(int position) {
        Collar.trackEvent(new AnalyticsEvent.OnItemSelected(position, true));
    }
}