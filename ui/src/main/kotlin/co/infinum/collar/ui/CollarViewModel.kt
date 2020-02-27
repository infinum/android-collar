package co.infinum.collar.ui

import androidx.lifecycle.ViewModel
import co.infinum.collar.ui.data.room.repository.EntityRepository

class CollarViewModel : ViewModel() {

    fun screens() = EntityRepository.loadScreens()

    fun events() = EntityRepository.loadEvents()

    fun properties() = EntityRepository.loadProperties()
}