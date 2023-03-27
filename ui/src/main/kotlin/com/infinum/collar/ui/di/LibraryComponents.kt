package com.infinum.collar.ui.di

import android.annotation.SuppressLint
import android.content.Context
import com.infinum.collar.ui.di.component.DataComponent
import com.infinum.collar.ui.di.component.DomainComponent
import com.infinum.collar.ui.di.component.PresentationComponent
import com.infinum.collar.ui.di.component.ViewModelComponent
import com.infinum.collar.ui.di.component.create

@SuppressLint("StaticFieldLeak")
internal object LibraryComponents {

    private lateinit var viewModelComponent: ViewModelComponent
    private lateinit var presentationComponent: PresentationComponent

    fun initialize(context: Context) {
        val dataComponent = DataComponent::class.create(context)
        val domainComponent = DomainComponent::class.create(dataComponent)
        viewModelComponent = ViewModelComponent::class.create(domainComponent)
        presentationComponent = PresentationComponent::class.create(
            context,
            viewModelComponent
        )
    }

    fun presentation(): PresentationComponent = presentationComponent

    fun viewModels(): ViewModelComponent = viewModelComponent
}
