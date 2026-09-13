package org.alter.plugins.content.skills.smithing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Deterministic, cache-free tests for the bronze anvil-forging transaction.
 *
 * The [SmeltInventory] seam lets the tests drive the same [AnvilService.smith]
 * implementation used at runtime with an in-memory inventory, so no cache is
 * required.
 */
class BronzeAnvilRecipeTest {

    @Test
    fun `produces one bronze dagger from one bar`() {
        val inventory = FakeInventory(mapOf(BAR to 1, HAMMER to 1), freeSlots = 26)

        val result = AnvilService().smith(inventory, bronzeRecipe(), smithingLevel = 1)

        val success = assertIs<AnvilResult.Success>(result)
        assertEquals(12.5, success.recipe.experience)
        assertEquals(0, inventory.count(BAR))
        assertEquals(1, inventory.count(DAGGER))
    }

    @Test
    fun `does not consume the hammer`() {
        val inventory = FakeInventory(mapOf(BAR to 1, HAMMER to 2), freeSlots = 26)

        val result = AnvilService().smith(inventory, bronzeRecipe(), smithingLevel = 1)

        assertIs<AnvilResult.Success>(result)
        assertEquals(2, inventory.count(HAMMER))
    }

    @Test
    fun `requires a bronze bar`() {
        val inventory = FakeInventory(mapOf(HAMMER to 1), freeSlots = 27)

        val result = AnvilService().smith(inventory, bronzeRecipe(), smithingLevel = 1)

        assertEquals(AnvilResult.MissingInput, result)
        assertEquals(0, inventory.count(BAR))
        assertEquals(0, inventory.count(DAGGER))
        assertEquals(1, inventory.count(HAMMER))
        assertEquals(27, inventory.freeSlots())
    }

    @Test
    fun `requires a hammer`() {
        val inventory = FakeInventory(mapOf(BAR to 1), freeSlots = 27)

        val result = AnvilService().smith(inventory, bronzeRecipe(), smithingLevel = 1)

        assertEquals(AnvilResult.MissingTool, result)
        assertEquals(1, inventory.count(BAR))
        assertEquals(0, inventory.count(DAGGER))
        assertEquals(0, inventory.count(HAMMER))
        assertEquals(27, inventory.freeSlots())
    }

    @Test
    fun `requires smithing level 1`() {
        val inventory = FakeInventory(mapOf(BAR to 1, HAMMER to 1), freeSlots = 26)

        val result = AnvilService().smith(inventory, bronzeRecipe(), smithingLevel = 0)

        assertEquals(AnvilResult.InsufficientLevel, result)
        assertEquals(1, inventory.count(BAR))
        assertEquals(0, inventory.count(DAGGER))
        assertEquals(1, inventory.count(HAMMER))
        assertEquals(26, inventory.freeSlots())
    }

    @Test
    fun `consumes nothing when the output does not fit`() {
        val inventory = FakeInventory(mapOf(BAR to 1, HAMMER to 1), freeSlots = 0)

        val result = AnvilService().smith(inventory, bronzeRecipe(), smithingLevel = 1)

        assertEquals(AnvilResult.NotEnoughSpace, result)
        assertEquals(1, inventory.count(BAR))
        assertEquals(0, inventory.count(DAGGER))
        assertEquals(1, inventory.count(HAMMER))
        assertEquals(0, inventory.freeSlots())
    }

    @Test
    fun `restores the bar when the output cannot be added`() {
        val inventory = FakeInventory(mapOf(BAR to 1, HAMMER to 1), freeSlots = 26)
        inventory.failAddsOf = DAGGER

        val result = AnvilService().smith(inventory, bronzeRecipe(), smithingLevel = 1)

        assertEquals(AnvilResult.Failed, result)
        assertEquals(1, inventory.count(BAR))
        assertEquals(0, inventory.count(DAGGER))
        assertEquals(1, inventory.count(HAMMER))
        assertEquals(26, inventory.freeSlots())
    }

    private fun bronzeRecipe(): AnvilRecipe =
        AnvilRecipe(
            name = "bronze dagger",
            objectKey = "object.anvil",
            input = "item.bronze_bar",
            inputAmount = 1,
            tool = "item.hammer",
            toolAmount = 1,
            output = "item.bronze_dagger",
            outputAmount = 1,
            level = 1,
            experience = 12.5,
        ).apply {
            objectIds = intArrayOf(ANVIL, RUSTED_ANVIL)
            inputItemId = BAR
            toolItemId = HAMMER
            outputItemId = DAGGER
        }

    /**
     * In-memory [SmeltInventory] modelling a non-stackable inventory: removing
     * the last unit of an item frees a slot, adding an item consumes one.
     */
    private class FakeInventory(
        items: Map<Int, Int>,
        freeSlots: Int,
    ) : SmeltInventory {
        private val items = items.toMutableMap()
        private var slots = freeSlots

        var failAddsOf: Int? = null

        override fun count(itemId: Int): Int = items[itemId] ?: 0

        override fun freeSlots(): Int = slots

        override fun remove(
            itemId: Int,
            amount: Int,
        ): Boolean {
            val count = items[itemId] ?: return false
            if (count < amount) {
                return false
            }
            if (count == amount) {
                items.remove(itemId)
                slots++
            } else {
                items[itemId] = count - amount
            }
            return true
        }

        override fun add(
            itemId: Int,
            amount: Int,
        ): Boolean {
            if (itemId == failAddsOf || slots < 1) {
                return false
            }
            items[itemId] = (items[itemId] ?: 0) + amount
            slots--
            return true
        }
    }

    private companion object {
        const val ANVIL = 2031
        const val RUSTED_ANVIL = 39620
        const val BAR = 2349
        const val DAGGER = 1205
        const val HAMMER = 2347
    }
}
