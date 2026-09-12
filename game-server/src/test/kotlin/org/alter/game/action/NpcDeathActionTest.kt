package org.alter.game.action

import org.alter.game.DevContext
import org.alter.game.GameContext
import org.alter.game.model.PlayerUID
import org.alter.game.model.Tile
import org.alter.game.model.World
import org.alter.game.model.entity.Player
import org.alter.game.model.weightedTableBuilder.Loot
import org.alter.game.model.weightedTableBuilder.LootTable
import org.alter.game.model.weightedTableBuilder.TableType
import org.alter.game.saving.formats.SaveFormatType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NpcDeathActionTest {
    private fun newWorld(): World {
        val gameContext =
            GameContext(
                initialLaunch = false,
                name = "death-test",
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
        return World(gameContext, devContext)
    }

    private fun newPlayer(world: World): Player = Player(world).apply { uid = PlayerUID("death-test-player") }

    private fun populatedTables(): List<LootTable> =
        listOf(
            LootTable(
                TableType.MAIN,
                tableWeight = 1,
                drops = mutableSetOf(Loot(item = 995, min = 1, max = 1, weight = 1)),
            ),
        )

    @Test
    fun `no credited killer never resolves loot`() {
        assertNull(NpcDeathAction.resolveLootContext(null, populatedTables()))
    }

    @Test
    fun `null loot tables never resolve loot`() {
        assertNull(NpcDeathAction.resolveLootContext(newPlayer(newWorld()), null))
    }

    @Test
    fun `empty loot tables never resolve loot`() {
        assertNull(NpcDeathAction.resolveLootContext(newPlayer(newWorld()), emptyList()))
    }

    @Test
    fun `credited killer with populated tables resolves the roll context`() {
        val world = newWorld()
        val killer = newPlayer(world)
        val tables = populatedTables()

        val resolved = NpcDeathAction.resolveLootContext(killer, tables)

        assertNotNull(resolved)
        assertEquals(killer, resolved.first)
        assertEquals(tables, resolved.second)
    }
}
