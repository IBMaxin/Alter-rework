package org.alter.plugins.content.skills.smithing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Deterministic, cache-free tests for the bronze smelting transaction.
 *
 * The [SmeltInventory] seam lets the tests drive the same
 * [SmeltingService.smelt] implementation used at runtime with an in-memory
 * inventory, so no cache is required.
 */
class BronzeSmeltingRecipeTest {

    @Test
    fun `produces one bronze bar from copper and tin`() {
        val inventory = FakeInventory(mapOf(COPPER to 1, TIN to 1), freeSlots = 26)

        val result = SmeltingService().smelt(inventory, bronzeRecipe(), smithingLevel = 1)

        val success = assertIs<SmeltResult.Success>(result)
        assertEquals(6.2, success.recipe.experience)
        assertEquals(0, inventory.count(COPPER))
        assertEquals(0, inventory.count(TIN))
        assertEquals(1, inventory.count(BRONZE))
    }

    @Test
    fun `requires both ores`() {
        val inventory = FakeInventory(mapOf(COPPER to 1), freeSlots = 27)

        val result = SmeltingService().smelt(inventory, bronzeRecipe(), smithingLevel = 1)

        assertEquals(SmeltResult.MissingInputs, result)
        assertEquals(1, inventory.count(COPPER))
        assertEquals(0, inventory.count(TIN))
        assertEquals(0, inventory.count(BRONZE))
        assertEquals(27, inventory.freeSlots())
    }

    @Test
    fun `requires smithing level 1`() {
        val inventory = FakeInventory(mapOf(COPPER to 1, TIN to 1), freeSlots = 26)

        val result = SmeltingService().smelt(inventory, bronzeRecipe(), smithingLevel = 0)

        assertEquals(SmeltResult.InsufficientLevel, result)
        assertEquals(1, inventory.count(COPPER))
        assertEquals(1, inventory.count(TIN))
        assertEquals(0, inventory.count(BRONZE))
        assertEquals(26, inventory.freeSlots())
    }

    @Test
    fun `consumes nothing when the output does not fit`() {
        val inventory = FakeInventory(mapOf(COPPER to 1, TIN to 1), freeSlots = 0)

        val result = SmeltingService().smelt(inventory, bronzeRecipe(), smithingLevel = 1)

        assertEquals(SmeltResult.NotEnoughSpace, result)
        assertEquals(1, inventory.count(COPPER))
        assertEquals(1, inventory.count(TIN))
        assertEquals(0, inventory.count(BRONZE))
        assertEquals(0, inventory.freeSlots())
    }

    @Test
    fun `restores inputs when the output cannot be added`() {
        val inventory = FakeInventory(mapOf(COPPER to 1, TIN to 1), freeSlots = 26)
        inventory.failAddsOf = BRONZE

        val result = SmeltingService().smelt(inventory, bronzeRecipe(), smithingLevel = 1)

        assertEquals(SmeltResult.Failed, result)
        assertEquals(1, inventory.count(COPPER))
        assertEquals(1, inventory.count(TIN))
        assertEquals(0, inventory.count(BRONZE))
        assertEquals(26, inventory.freeSlots())
    }

    private fun bronzeRecipe(): SmeltingRecipe =
        SmeltingRecipe(
            name = "bronze bar",
            inputs = listOf(SmeltingInput("item.copper_ore"), SmeltingInput("item.tin_ore")),
            output = "item.bronze_bar",
            outputAmount = 1,
            level = 1,
            experience = 6.2,
        ).apply {
            outputItemId = BRONZE
            inputs[0].itemId = COPPER
            inputs[1].itemId = TIN
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
        const val COPPER = 436
        const val TIN = 438
        const val BRONZE = 2349
    }
}
