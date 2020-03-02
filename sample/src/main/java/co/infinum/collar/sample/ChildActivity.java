package co.infinum.collar.sample;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class ChildActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_child);

        getFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, new ChildFragment())
            .commit();
    }
}
