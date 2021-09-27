package com.infinum.collar.processor.validators

import com.infinum.collar.processor.models.ScreenHolder

internal class ScreenNamesValidator : Validator<ScreenHolder> {

    override fun validate(elements: Set<ScreenHolder>): Set<ScreenHolder> =
        elements.filter { it.enabled }.toSet()
}
