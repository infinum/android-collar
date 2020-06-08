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
import co.infinum.collar.sample.analytics.CollarTrackingPlanEvents;
import co.infinum.collar.sample.analytics.TrackingPlanEvents;
import co.infinum.collar.sample.analytics.TrackingPlanScreens;

@SuppressWarnings("deprecation")
@ScreenName(value = TrackingPlanScreens.HOME)
public class ChildFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_child, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.buttonProduceEvent2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CollarTrackingPlanEvents.trackEvent(new TrackingPlanEvents.MachineRemove());
            }
        });

        view.findViewById(R.id.buttonShowKotlinMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKotlinMainScreen();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        CollarScreenNames.trackScreen(this);
    }

    private void showKotlinMainScreen() {
        startActivity(new Intent(getActivity(), KotlinMainActivity.class));
    }
}
