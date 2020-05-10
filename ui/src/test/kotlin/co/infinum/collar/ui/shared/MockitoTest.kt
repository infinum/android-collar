package co.infinum.collar.ui.shared

import org.junit.Rule
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

interface MockitoTest {

    @get:Rule
    val mockitoRule: MockitoRule
        get() = MockitoJUnit.rule()
}