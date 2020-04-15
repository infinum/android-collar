package co.infinum.collar.sample;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import co.infinum.collar.CollarScreenNames;
import co.infinum.collar.annotations.ScreenName;
import co.infinum.collar.sample.databinding.FragmentChildBinding;

@SuppressWarnings("deprecation")
@ScreenName(value = KotlinScreenNames.CHILD_SCREEN_JAVA)
public class ChildFragment extends Fragment {

    private FragmentChildBinding viewBinding = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        viewBinding = FragmentChildBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewBinding.buttonProduceEvent2.setOnClickListener(v ->
            CollarAnalyticsEvent.trackEvent(new AnalyticsEvent.Event2("bla", 0))
        );

        viewBinding.buttonShowKotlinMain.setOnClickListener(v -> showKotlinMainScreen());
    }

    @Override
    public void onResume() {
        super.onResume();
        CollarScreenNames.trackScreen(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewBinding = null;
    }

    private void showKotlinMainScreen() {
        startActivity(new Intent(getActivity(), KotlinMainActivity.class));
    }
}
