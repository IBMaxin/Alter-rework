package org.alter.plugins.content.loot

import org.alter.game.DevContext
import org.alter.game.GameContext
import org.alter.game.model.PlayerUID
import org.alter.game.model.Tile
import org.alter.game.model.World
import org.alter.game.model.entity.Player
import org.alter.game.model.weightedTableBuilder.Loot
import org.alter.game.model.weightedTableBuilder.LootTable
import org.alter.game.model.weightedTableBuilder.TableType
import org.alter.game.model.weightedTableBuilder.handleToItem
import org.alter.game.model.weightedTableBuilder.mainRoll
import org.alter.game.model.weightedTableBuilder.preRoll
import org.alter.game.model.weightedTableBuilder.roll
import org.alter.game.model.weightedTableBuilder.tertiaryRoll
import org.alter.game.saving.formats.SaveFormatType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LootTableBuilderTest {
    private fun newPlayer(): Player {
        val gameContext =
            GameContext(
                initialLaunch = false,
                name = "loot-test",
                revision = 228,
                saveFormat = SaveFormatType.JSON,
                cycleTime = 600,
                playerLimit = 10,
                home = Tile(0, 0, 0),
                skillCount = 25,
                npcStatCount = 10,
                runEnergy = false,
                gItemPublicDelay = 100,
                gItemDespawnDelay = 300,
                preloadMaps = false,
            )
        val devContext =
            DevContext(
                debugExamines = false,
                debugObjects = false,
                debugButtons = false,
                debugItemActions = false,
                debugMagicSpells = false,
                debugPackets = false,
            )
        val world = World(gameContext, devContext)
        return Player(world).apply { uid = PlayerUID("loot-test-player") }
    }

    @Test
    fun `handleToItem spawns on the supplied tile and assigns the killer as owner`() {
        val player = newPlayer()
        val tile = Tile(3200, 3200, 0)
        val loot = Loot(item = 1234, min = 2, max = 2)

        val drops = loot.handleToItem(player, tile)

        assertEquals(1, drops.size)
        val drop = drops.single()
        assertEquals(tile, drop.tile)
        assertEquals(1234, drop.item)
        assertEquals(2, drop.amount)
        assertTrue(drop.isOwnedBy(player))
    }

    @Test
    fun `handleToItem threads the tile and owner through a nested loot table`() {
        val player = newPlayer()
        val tile = Tile(3092, 3245, 0)
        val nested = LootTable(TableType.ALWAYS, tableWeight = 0, drops = mutableSetOf(Loot(item = 2132, min = 1, max = 1)))

        val drops = Loot(item = nested, min = 1, max = 1).handleToItem(player, tile)

        assertEquals(1, drops.size)
        val drop = drops.single()
        assertEquals(tile, drop.tile)
        assertEquals(2132, drop.item)
        assertTrue(drop.isOwnedBy(player))
    }

    @Test
    fun `roll propagates the tile and owner through an always table`() {
        val player = newPlayer()
        val tile = Tile(2650, 3300, 0)
        val loot = Loot(item = 995, min = 1, max = 1)
        val table = LootTable(TableType.ALWAYS, tableWeight = 0, drops = mutableSetOf(loot))

        val drops = roll(player, setOf(table), tile)

        assertEquals(1, drops.size)
        val drop = drops.single()
        assertEquals(tile, drop.tile)
        assertEquals(995, drop.item)
        assertTrue(drop.isOwnedBy(player))
    }

    @Test
    fun `main roll with a single entry always selects it`() {
        val loot = Loot(item = 1, min = 1, max = 1, weight = 1)
        val table = LootTable(TableType.MAIN, tableWeight = 1, drops = mutableSetOf(loot))

        assertEquals(loot, table.mainRoll())
    }

    @Test
    fun `pre roll weight zero never passes`() {
        val never = Loot(item = 1, min = 1, max = 1, weight = 0)
        val table = LootTable(TableType.PRE_ROLL, tableWeight = null, drops = mutableSetOf(never))

        repeat(128) { assertNull(table.preRoll()) }
    }

    @Test
    fun `pre roll weight one hundred and twenty eight always passes`() {
        val always = Loot(item = 2, min = 1, max = 1, weight = 128)
        val table = LootTable(TableType.PRE_ROLL, tableWeight = null, drops = mutableSetOf(always))

        repeat(128) { assertEquals(always, table.preRoll()) }
    }

    @Test
    fun `pre roll treats a missing weight as zero and never passes`() {
        val missing = Loot(item = 3, min = 1, max = 1, weight = null)
        val table = LootTable(TableType.PRE_ROLL, tableWeight = null, drops = mutableSetOf(missing))

        repeat(128) { assertNull(table.preRoll()) }
    }

    @Test
    fun `pre roll rejects a negative weight`() {
        val invalid = Loot(item = 4, min = 1, max = 1, weight = -1)
        val table = LootTable(TableType.PRE_ROLL, tableWeight = null, drops = mutableSetOf(invalid))

        val error = assertFailsWith<IllegalArgumentException> { table.preRoll() }
        assertTrue(error.message!!.contains("Invalid pre-roll weight -1"))
    }

    @Test
    fun `pre roll rejects a weight above one hundred and twenty eight`() {
        val invalid = Loot(item = 5, min = 1, max = 1, weight = 129)
        val table = LootTable(TableType.PRE_ROLL, tableWeight = null, drops = mutableSetOf(invalid))

        val error = assertFailsWith<IllegalArgumentException> { table.preRoll() }
        assertTrue(error.message!!.contains("Invalid pre-roll weight 129"))
    }

    @Test
    fun `main roll on an empty table returns null without crashing`() {
        val table = LootTable(TableType.MAIN, tableWeight = 0, drops = mutableSetOf())

        assertNull(table.mainRoll())
    }

    @Test
    fun `main roll on a table of only zero weights returns null without crashing`() {
        val zero = Loot(item = 1, min = 1, max = 1, weight = 0)
        val table = LootTable(TableType.MAIN, tableWeight = 0, drops = mutableSetOf(zero))

        assertNull(table.mainRoll())
    }

    @Test
    fun `main roll without a configured total returns null without crashing`() {
        val loot = Loot(item = 1, min = 1, max = 1, weight = 5)
        val table = LootTable(TableType.MAIN, tableWeight = null, drops = mutableSetOf(loot))

        assertNull(table.mainRoll())
    }

    @Test
    fun `main roll rejects a configured total below the accumulated item weight`() {
        val loot = Loot(item = 1, min = 1, max = 1, weight = 5)

        val error = assertFailsWith<IllegalArgumentException> {
            LootTable(TableType.MAIN, tableWeight = 1, drops = mutableSetOf(loot))
        }

        assertTrue(error.message!!.contains("configured total weight 1"))
        assertTrue(error.message!!.contains("accumulated item weight 5"))
        assertTrue(error.message!!.contains("1 entries"))
    }

    @Test
    fun `main roll only selects among configured entries for a valid table`() {
        val first = Loot(item = 1, min = 1, max = 1, weight = 1)
        val second = Loot(item = 2, min = 1, max = 1, weight = 1)
        val third = Loot(item = 3, min = 1, max = 1, weight = 1)
        val table = LootTable(TableType.MAIN, tableWeight = 3, drops = mutableSetOf(first, second, third))
        val configured = setOf(first, second, third)

        repeat(256) { assertTrue(table.mainRoll()!! in configured) }
    }

    @Test
    fun `always table accepts a missing or zero table weight`() {
        val loot = Loot(item = 1, min = 1, max = 1)

        LootTable(TableType.ALWAYS, tableWeight = null, drops = mutableSetOf(loot))
        LootTable(TableType.ALWAYS, tableWeight = 0, drops = mutableSetOf(loot))
    }

    @Test
    fun `always table rejects a positive table weight`() {
        val loot = Loot(item = 1, min = 1, max = 1)

        val error = assertFailsWith<IllegalArgumentException> {
            LootTable(TableType.ALWAYS, tableWeight = 5, drops = mutableSetOf(loot))
        }

        assertTrue(error.message!!.contains("ALWAYS loot table must not declare a table weight"))
        assertTrue(error.message!!.contains("5"))
    }

    @Test
    fun `weighted pre roll table constructs with a zero table weight`() {
        val loot = Loot(item = 2001, min = 1, max = 1, weight = 128)

        val table = LootTable(TableType.PRE_ROLL, tableWeight = 0, drops = mutableSetOf(loot))

        assertEquals(0, table.tableWeight)
        repeat(128) { assertEquals(loot, table.preRoll()) }
    }

    @Test
    fun `weighted tertiary table constructs with a zero table weight`() {
        val loot = Loot(item = 2002, min = 1, max = 1, weight = 1)

        val table = LootTable(TableType.TERTIARY, tableWeight = 0, drops = mutableSetOf(loot))

        assertEquals(0, table.tableWeight)
        repeat(64) { assertEquals(listOf(loot), table.tertiaryRoll()) }
    }

    @Test
    fun `valid main table constructs with its configured total`() {
        val first = Loot(item = 1, min = 1, max = 1, weight = 2)
        val second = Loot(item = 2, min = 1, max = 1, weight = 1)

        val table = LootTable(TableType.MAIN, tableWeight = 3, drops = mutableSetOf(first, second))

        assertEquals(3, table.tableWeight)
        assertTrue(table.mainRoll()!! in setOf(first, second))
    }

    @Test
    fun `main table rejects a configured total below the combined entry weights`() {
        val first = Loot(item = 1, min = 1, max = 1, weight = 2)
        val second = Loot(item = 2, min = 1, max = 1, weight = 2)

        val error = assertFailsWith<IllegalArgumentException> {
            LootTable(TableType.MAIN, tableWeight = 3, drops = mutableSetOf(first, second))
        }

        assertTrue(error.message!!.contains("configured total weight 3"))
        assertTrue(error.message!!.contains("accumulated item weight 4"))
    }
}
