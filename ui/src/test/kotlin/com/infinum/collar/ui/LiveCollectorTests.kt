package com.infinum.collar.ui

import com.infinum.collar.Collar
import com.infinum.collar.Event
import com.infinum.collar.Property
import com.infinum.collar.Screen
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

public class LiveCollectorTests {

    @Mock
    private lateinit var collector: LiveCollector

    @Mock
    private lateinit var screen: Screen

    @Mock
    private lateinit var event: Event

    @Mock
    private lateinit var property: Property

    @Before
    public fun attachCollarWithCollector() {
        MockitoAnnotations.openMocks(this)
        Collar.attach(collector)
    }

    @Test
    public fun whenScreenTracked_thenVerifyOnScreenCalledOnce() {
        Collar.trackScreen(screen)

        verify(collector, times(1)).onScreen(screen)
    }

    @Test
    public fun whenEventTracked_thenVerifyOnEventCalledOnce() {
        Collar.trackEvent(event)

        verify(collector, times(1)).onEvent(event)
    }

    @Test
    public fun whenPropertyTracked_thenVerifyOnPropertyCalledOnce() {
        Collar.trackProperty(property)

        verify(collector, times(1)).onProperty(property)
    }
}
