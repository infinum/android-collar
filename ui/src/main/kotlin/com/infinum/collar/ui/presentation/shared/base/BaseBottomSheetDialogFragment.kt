package com.infinum.collar.ui.presentation.shared.base

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.RestrictTo
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinum.collar.ui.R
import com.infinum.collar.ui.di.LibraryKoinComponent

@RestrictTo(RestrictTo.Scope.LIBRARY)
internal abstract class BaseBottomSheetDialogFragment<State, Event>(
    @LayoutRes private val contentLayoutId: Int
) : BottomSheetDialogFragment(), BaseView<State, Event>, LibraryKoinComponent {

    abstract val binding: ViewBinding

    @CallSuper
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window?.let {
                    it.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    it.attributes.blurBehindRadius = resources.getDimensionPixelSize(R.dimen.collar_blur_radius)
                }
            }
        }

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(contentLayoutId, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        collectFlows(viewLifecycleOwner)
    }
}
