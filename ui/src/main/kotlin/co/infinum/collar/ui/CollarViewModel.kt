package co.infinum.collar.ui

import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.infinum.collar.ui.entries.CollarEntry
import co.infinum.collar.ui.entries.EventEntry
import co.infinum.collar.ui.entries.PropertyEntry
import co.infinum.collar.ui.entries.ScreenEntry
import co.infinum.collar.ui.entries.UnknownEntry

class CollarViewModel : ViewModel() {

    val items: MutableLiveData<MutableList<CollarEntry>> = MutableLiveData(mutableListOf())

    init {
        items.value = mutableListOf(
            EventEntry(
                timestamp = 1582796031L,
                name = "test_event",
                parameters = bundleOf(
                    "test_event_key1" to "test_event_value1",
                    "test_event_key2" to "test_event_value2",
                    "test_event_key3" to "test_event_value3"
                )
            ),
            PropertyEntry(
                timestamp = 1582791031L,
                name = "test_property",
                value = "test_property_value"
            ),
            ScreenEntry(
                timestamp = 1581796031L,
                name = "test_screen"
            ),
            UnknownEntry(
                timestamp = 1582792031L,
                name = "test_unknown"
            )
        )
    }
}