package com.infinum.collar.processor.validators

import com.infinum.collar.processor.models.ComposeScreenHolder

internal class ComposeScreenNamesValidator : Validator<ComposeScreenHolder> {

    override fun validate(elements: Set<ComposeScreenHolder>): Set<ComposeScreenHolder> =
        elements.filter { it.enabled }.toSet()

}
