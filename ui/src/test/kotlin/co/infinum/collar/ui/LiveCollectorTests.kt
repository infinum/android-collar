package co.infinum.collar.ui

import co.infinum.collar.Collar
import co.infinum.collar.Event
import co.infinum.collar.Property
import co.infinum.collar.Screen
import co.infinum.collar.ui.shared.MockitoTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class LiveCollectorTests : MockitoTest {

    @Mock
    private lateinit var collector: LiveCollector

    @Mock
    private lateinit var screen: Screen

    @Mock
    private lateinit var event: Event

    @Mock
    private lateinit var property: Property

    @Before
    fun attachCollarWithCollector() {
        Collar.attach(collector)
    }

    @Test
    fun whenScreenTracked_thenVerifyOnScreenCalledOnce() {
        Collar.trackScreen(screen)

        verify(collector, times(1)).onScreen(screen)
    }

    @Test
    fun whenEventTracked_thenVerifyOnEventCalledOnce() {
        Collar.trackEvent(event)

        verify(collector, times(1)).onEvent(event)
    }

    @Test
    fun whenPropertyTracked_thenVerifyOnPropertyCalledOnce() {
        Collar.trackProperty(property)

        verify(collector, times(1)).onProperty(property)
    }
}