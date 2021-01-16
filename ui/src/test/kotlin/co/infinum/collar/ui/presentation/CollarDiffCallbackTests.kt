package co.infinum.collar.ui.presentation

import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.shared.MockitoTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`

public class CollarDiffCallbackTests : MockitoTest {

    @Mock
    private lateinit var entity1: CollarEntity

    @Mock
    private lateinit var entity2: CollarEntity

    @Test
    public fun whenListsAreEmpty_thenTheyAreTheSame() {
        val oldList: List<CollarEntity> = listOf()
        val newList: List<CollarEntity> = listOf()

        val diffCallback = CollarDiffCallback(oldList, newList)

        assertEquals(oldList, newList)
        assertEquals(diffCallback.oldListSize, diffCallback.newListSize)
    }

    @Test
    public fun whenOldListIsEmpty_thenTheyAreNotTheSame() {
        val oldList: List<CollarEntity> = listOf()
        val newList: List<CollarEntity> = listOf(entity1)

        val diffCallback = CollarDiffCallback(oldList, newList)

        assertNotEquals(oldList, newList)
        assertNotEquals(diffCallback.oldListSize, diffCallback.newListSize)
    }

    @Test
    public fun whenNewListIsEmpty_thenTheyAreNotTheSame() {
        val oldList: List<CollarEntity> = listOf(entity1)
        val newList: List<CollarEntity> = listOf()

        val diffCallback = CollarDiffCallback(oldList, newList)

        assertNotEquals(oldList, newList)
        assertNotEquals(diffCallback.oldListSize, diffCallback.newListSize)
    }

    @Test
    public fun whenListsHaveSameItems_thenTheyAreTheSame() {
        val oldList: List<CollarEntity> = listOf(entity1, entity2)
        val newList: List<CollarEntity> = listOf(entity1, entity2)

        `when`(entity1.id).thenReturn(0)
        `when`(entity2.id).thenReturn(1)

        val diffCallback = CollarDiffCallback(oldList, newList)

        assertEquals(oldList, newList)
        assertEquals(diffCallback.oldListSize, diffCallback.newListSize)
        assertTrue(diffCallback.areContentsTheSame(0, 0))
        assertTrue(diffCallback.areContentsTheSame(1, 1))
        assertTrue(diffCallback.areItemsTheSame(0, 0))
        assertTrue(diffCallback.areItemsTheSame(1, 1))
    }

    @Test
    public fun whenListsHaveDifferentItems_thenTheyAreNotTheSame() {
        val oldList: List<CollarEntity> = listOf(entity1, entity2)
        val newList: List<CollarEntity> = listOf(entity2, entity1)

        `when`(entity1.id).thenReturn(0)
        `when`(entity2.id).thenReturn(1)

        val diffCallback = CollarDiffCallback(oldList, newList)

        assertNotEquals(oldList, newList)
        assertEquals(diffCallback.oldListSize, diffCallback.newListSize)
        assertFalse(diffCallback.areContentsTheSame(0, 0))
        assertFalse(diffCallback.areContentsTheSame(1, 1))
        assertFalse(diffCallback.areItemsTheSame(0, 0))
        assertFalse(diffCallback.areItemsTheSame(1, 1))
    }
}
