package org.alter.plugins.content.skills.smithing

import org.alter.rscm.RSCM
import org.alter.rscm.RSCM.getRSCM
import org.junit.BeforeClass
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies the shipped `data/cfg/smithing/smelting.json` loads and that every
 * RSCM key resolves. `getRSCM` throws on an unknown name, so a successful load
 * also proves the data file references only real object/item ids.
 */
class SmeltingDataLoadTest {

    @Test
    fun `loads the bronze smelting recipe`() {
        val recipe = loadService().recipes.single()

        assertEquals("bronze bar", recipe.name)
        assertEquals(1, recipe.level)
        assertEquals(6.2, recipe.experience)
        assertEquals(1, recipe.outputAmount)
        assertEquals(getRSCM("item.bronze_bar"), recipe.outputItemId)
    }

    @Test
    fun `resolves the copper and tin inputs`() {
        val recipe = loadService().recipes.single()

        assertEquals(2, recipe.inputs.size)
        val copper = recipe.inputs.single { it.item == "item.copper_ore" }
        val tin = recipe.inputs.single { it.item == "item.tin_ore" }
        assertEquals(getRSCM("item.copper_ore"), copper.itemId)
        assertEquals(1, copper.amount)
        assertEquals(getRSCM("item.tin_ore"), tin.itemId)
        assertEquals(1, tin.amount)
    }

    @Test
    fun `indexes every input to the recipe`() {
        val service = loadService()
        val recipe = service.recipes.single()

        assertEquals(recipe, service.recipeForInput(getRSCM("item.copper_ore")))
        assertEquals(recipe, service.recipeForInput(getRSCM("item.tin_ore")))
    }

    private fun loadService(): SmeltingService =
        SmeltingService().apply { load(Paths.get("../data/cfg/smithing/smelting.json")) }

    companion object {
        @BeforeClass
        @JvmStatic
        fun loadRscm() {
            RSCM.init()
        }
    }
}
