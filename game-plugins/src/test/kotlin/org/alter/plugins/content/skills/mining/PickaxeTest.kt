package org.alter.plugins.content.skills.mining

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Pure unit tests for the pickaxe tier table and selection logic. These do not
 * touch the cache; [Pickaxes.forName]/[Pickaxes.best] operate only on names.
 */
class PickaxeTest {

    @Test
    fun `resolves a tier from the item name prefix`() {
        assertEquals("bronze", Pickaxes.forName("Bronze pickaxe")?.tier)
        assertEquals("rune", Pickaxes.forName("Rune pickaxe")?.tier)
        assertEquals("dragon", Pickaxes.forName("Dragon pickaxe (or)")?.tier)
        assertEquals("3rd age", Pickaxes.forName("3rd age pickaxe")?.tier)
        assertEquals("crystal", Pickaxes.forName("Crystal pickaxe (i)")?.tier)
    }

    @Test
    fun `returns null for unknown names`() {
        assertNull(Pickaxes.forName("Abyssal whip"))
        assertNull(Pickaxes.forName(""))
    }

    @Test
    fun `only selects pickaxes the player can use`() {
        assertNull(Pickaxes.best(miningLevel = 1, names = listOf("Dragon pickaxe")))
        assertEquals("iron", Pickaxes.best(miningLevel = 1, names = listOf("Iron pickaxe"))?.tier)
    }

    @Test
    fun `selects the fastest usable pickaxe`() {
        val best =
            Pickaxes.best(
                miningLevel = 41,
                names = listOf("Bronze pickaxe", "Rune pickaxe", "Mithril pickaxe"),
            )

        assertEquals("rune", best?.tier)
        assertEquals(3, best?.actionTicks)
    }

    @Test
    fun `ties are broken by table order`() {
        val best =
            Pickaxes.best(
                miningLevel = 71,
                names = listOf("Dragon pickaxe", "Crystal pickaxe"),
            )

        assertEquals("crystal", best?.tier)
    }

    @Test
    fun `the tier table is ordered fastest first`() {
        assertEquals(Pickaxes.ALL.sortedBy { it.actionTicks }, Pickaxes.ALL)
        assertEquals(listOf("bronze", "iron"), Pickaxes.ALL.takeLast(2).map { it.tier }.reversed())
    }
}
