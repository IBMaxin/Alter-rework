package org.alter.plugins.content.skills.woodcutting

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Pure unit tests for the axe tier table and selection logic. These do not touch
 * the cache; [Axes.forName]/[Axes.best] operate only on names.
 */
class AxeTest {

    @Test
    fun `resolves a tier from the item name prefix`() {
        assertEquals("bronze", Axes.forName("Bronze axe")?.tier)
        assertEquals("iron", Axes.forName("Iron felling axe")?.tier)
        assertEquals("rune", Axes.forName("Rune axe")?.tier)
        assertEquals("dragon", Axes.forName("Dragon axe (or)")?.tier)
        assertEquals("3rd age", Axes.forName("3rd age axe")?.tier)
        assertEquals("crystal", Axes.forName("Crystal axe (inactive)")?.tier)
    }

    @Test
    fun `returns null for unknown names`() {
        assertNull(Axes.forName("Abyssal whip"))
        assertNull(Axes.forName("Blessed axe"))
        assertNull(Axes.forName(""))
    }

    @Test
    fun `only selects axes the player can use`() {
        assertNull(Axes.best(woodcuttingLevel = 1, names = listOf("Dragon axe")))
        assertEquals("iron", Axes.best(woodcuttingLevel = 1, names = listOf("Iron axe"))?.tier)
    }

    @Test
    fun `selects the best usable axe`() {
        val best =
            Axes.best(
                woodcuttingLevel = 41,
                names = listOf("Bronze axe", "Rune axe", "Mithril axe"),
            )

        assertEquals("rune", best?.tier)
        assertEquals(3.5, best?.successMultiplier)
    }

    @Test
    fun `ties are broken by table order`() {
        val best =
            Axes.best(
                woodcuttingLevel = 61,
                names = listOf("Dragon axe", "Infernal axe"),
            )

        assertEquals("infernal", best?.tier)
    }

    @Test
    fun `the tier table is ordered best first`() {
        assertEquals(Axes.ALL.sortedByDescending { it.successMultiplier }, Axes.ALL)
        assertEquals(listOf("bronze", "iron"), Axes.ALL.takeLast(2).map { it.tier }.reversed())
    }
}
