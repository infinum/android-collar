package co.infinum.collar.ui

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import co.infinum.collar.ui.presentation.Presentation

internal class CollarContentProvider : ContentProvider() {

    companion object {
        private const val DEFAULT_PACKAGE = "co.infinum.collar.ui"
    }

    override fun attachInfo(context: Context?, info: ProviderInfo?) {
        info?.let {
            if ("$DEFAULT_PACKAGE.${CollarContentProvider::class.java.simpleName}" == it.authority) {
                throw IllegalStateException(
                    "Incorrect provider authority. " +
                        "Most likely due to missing applicationId variable in module build.gradle."
                )
            }
        } ?: throw IllegalStateException("This component cannot work with null ProviderInfo.")

        super.attachInfo(context, info)
    }

    override fun onCreate(): Boolean =
        context?.let {
            Presentation.initialise(it.applicationContext)
            true
        } ?: false

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun getType(uri: Uri): String? = null
}
