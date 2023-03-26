package com.infinum.collar.ui.di.component

import androidx.lifecycle.ViewModel
import com.infinum.collar.ui.presentation.CollarViewModel
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides

@Component
internal abstract class ViewModelComponent(
    @Component val domainComponent: DomainComponent
) {

    abstract val viewModelMap: Map<Class<*>, ViewModel>

    abstract val collar: CollarViewModel

    @IntoMap
    @Provides
    fun collar(): Pair<Class<*>, ViewModel> =
        CollarViewModel::class.java to collar
}
