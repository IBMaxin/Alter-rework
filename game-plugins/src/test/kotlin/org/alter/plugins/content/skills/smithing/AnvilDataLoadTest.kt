package org.alter.plugins.content.skills.smithing

import org.alter.rscm.RSCM
import org.alter.rscm.RSCM.getRSCM
import org.junit.BeforeClass
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies the shipped `data/cfg/smithing/anvil.json` loads and that every RSCM
 * key resolves. `getRSCM` throws on an unknown name, so a successful load also
 * proves the data file references only real object/item ids.
 */
class AnvilDataLoadTest {

    @Test
    fun `loads the bronze dagger anvil recipe`() {
        val recipe = loadService().recipes.single()

        assertEquals("bronze dagger", recipe.name)
        assertEquals("object.anvil", recipe.objectKey)
        assertEquals(listOf("object.rusted_anvil"), recipe.objectVariants)
        assertEquals(1, recipe.level)
        assertEquals(12.5, recipe.experience)
        assertEquals(1, recipe.inputAmount)
        assertEquals(1, recipe.toolAmount)
        assertEquals(1, recipe.outputAmount)
        assertEquals(getRSCM("item.bronze_dagger"), recipe.outputItemId)
    }

    @Test
    fun `resolves the bronze bar, hammer and anvil ids`() {
        val recipe = loadService().recipes.single()

        assertEquals("item.bronze_bar", recipe.input)
        assertEquals("item.hammer", recipe.tool)
        assertEquals(getRSCM("item.bronze_bar"), recipe.inputItemId)
        assertEquals(getRSCM("item.hammer"), recipe.toolItemId)
        assertEquals(
            listOf(getRSCM("object.anvil"), getRSCM("object.rusted_anvil")),
            recipe.objectIds.toList(),
        )
    }

    @Test
    fun `indexes the bronze bar input to the recipe`() {
        val service = loadService()
        val recipe = service.recipes.single()

        assertEquals(recipe, service.recipeForInput(getRSCM("item.bronze_bar")))
    }

    private fun loadService(): AnvilService =
        AnvilService().apply { load(Paths.get("../data/cfg/smithing/anvil.json")) }

    companion object {
        @BeforeClass
        @JvmStatic
        fun loadRscm() {
            RSCM.init()
        }
    }
}
