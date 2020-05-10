package co.infinum.collar.processor.models

import com.squareup.kotlinpoet.ClassName

internal data class ScreenHolder(
    val enabled: Boolean,
    val superClassName: ClassName?,
    val className: ClassName,
    val screenName: String
) : Holder
