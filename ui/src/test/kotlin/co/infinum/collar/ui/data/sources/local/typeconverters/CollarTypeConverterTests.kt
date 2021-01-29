package co.infinum.collar.ui.data.sources.local.typeconverters

import co.infinum.collar.ui.data.models.local.EntityType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

public class CollarTypeConverterTests {

    private val typeConverter = CollarTypeConverter()

    @Test
    public fun whenScreenEntitySaved_thenConvertToNameScreen() {
        val entity = EntityType.SCREEN

        val expectedData = EntityType.SCREEN.name

        val actualData = typeConverter.fromEnum(entity)

        assertNotNull(actualData)
        assertEquals(expectedData, actualData)
    }

    @Test
    public fun whenEventEntitySaved_thenConvertToNameEvent() {
        val entity = EntityType.EVENT

        val expectedData = EntityType.EVENT.name

        val actualData = typeConverter.fromEnum(entity)

        assertNotNull(actualData)
        assertEquals(expectedData, actualData)
    }

    @Test
    public fun whenPropertyEntitySaved_thenConvertToNameProperty() {
        val entity = EntityType.PROPERTY

        val expectedData = EntityType.PROPERTY.name

        val actualData = typeConverter.fromEnum(entity)

        assertNotNull(actualData)
        assertEquals(expectedData, actualData)
    }

    @Test
    public fun whenScreenEntityLoaded_thenConvertToScreenType() {
        val entity = EntityType.SCREEN

        val expectedData = EntityType.SCREEN

        val actualData = typeConverter.toEnum(entity.name)

        assertNotNull(actualData)
        assertEquals(expectedData, actualData)
    }

    @Test
    public fun whenEventEntityLoaded_thenConvertToEventType() {
        val entity = EntityType.EVENT

        val expectedData = EntityType.EVENT

        val actualData = typeConverter.toEnum(entity.name)

        assertNotNull(actualData)
        assertEquals(expectedData, actualData)
    }

    @Test
    public fun whenPropertyEntityLoaded_thenConvertToPropertyType() {
        val entity = EntityType.PROPERTY

        val expectedData = EntityType.PROPERTY

        val actualData = typeConverter.toEnum(entity.name)

        assertNotNull(actualData)
        assertEquals(expectedData, actualData)
    }
}