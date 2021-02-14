package com.infinum.collar.ui.shared

import org.junit.Rule
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

public interface MockitoTest {

    @get:Rule
    public val mockitoRule: MockitoRule
        get() = MockitoJUnit.rule()
}
