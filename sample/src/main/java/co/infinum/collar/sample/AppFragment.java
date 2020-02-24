package co.infinum.collar.sample;

import android.app.Fragment;

import co.infinum.collar.CollarScreenNames;
import co.infinum.collar.annotations.ScreenName;

@ScreenName("AppFragment")
public class AppFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        CollarScreenNames.trackScreen(this);
    }
}
