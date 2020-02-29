package co.infinum.collar.ui

import androidx.lifecycle.ViewModel
import co.infinum.collar.ui.data.room.repository.EntityRepository

class CollarViewModel : ViewModel() {

    fun entities() = EntityRepository.loadAll()

    fun clearAll() = EntityRepository.clearAll()
}