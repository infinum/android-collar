package co.infinum.collar.ui.presentation.shared.logger

import timber.log.Timber

internal class Stump : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) = Unit
}
