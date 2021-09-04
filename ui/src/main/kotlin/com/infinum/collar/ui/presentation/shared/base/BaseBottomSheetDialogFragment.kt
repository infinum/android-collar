package com.infinum.collar.ui.presentation.shared.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.RestrictTo
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinum.collar.ui.di.LibraryKoinComponent

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal abstract class BaseBottomSheetDialogFragment<State, Event>(
    @LayoutRes private val contentLayoutId: Int
) : BottomSheetDialogFragment(), BaseView<State, Event>, LibraryKoinComponent {

    abstract val binding: ViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(contentLayoutId, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        collectFlows(viewLifecycleOwner.lifecycleScope)
    }
}
