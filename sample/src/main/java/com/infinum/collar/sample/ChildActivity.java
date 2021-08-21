package com.infinum.collar.sample;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import com.infinum.collar.sample.databinding.ActivityChildBinding;

public class ChildActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityChildBinding viewBinding = ActivityChildBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        getFragmentManager().beginTransaction()
            .replace(viewBinding.fragmentContainer.getId(), new ChildFragment())
            .commit();
    }
}
