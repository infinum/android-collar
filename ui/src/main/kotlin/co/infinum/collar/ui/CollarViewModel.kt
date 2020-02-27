package co.infinum.collar.ui

import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CollarViewModel : ViewModel() {

    val items: MutableLiveData<MutableList<CollarEntry>> = MutableLiveData(mutableListOf())

    init {
        items.value = mutableListOf(
            EventEntry(
                timestamp = 1L,
                color = R.color.collar_color_event,
                icon = R.drawable.ic_event,
                name = "test_event",
                parameters = bundleOf()
            ),
            PropertyEntry(
                timestamp = 2L,
                color = R.color.collar_color_property,
                icon = R.drawable.ic_property,
                name = "test_property",
                value = "test_property_value"
            ),
            ScreenEntry(
                timestamp = 3L,
                color = R.color.collar_color_screen,
                icon = R.drawable.ic_screen,
                name = "test_screen"
            ),
            UnknownEntry(
                timestamp = 4L,
                color = R.color.collar_color_unknown,
                icon = R.drawable.ic_unknown,
                name = "test_unknown"
            )
        )
    }
}