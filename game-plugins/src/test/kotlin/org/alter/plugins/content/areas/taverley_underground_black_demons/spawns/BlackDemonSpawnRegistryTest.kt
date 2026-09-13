package org.alter.plugins.content.areas.taverley_underground_black_demons.spawns

import org.alter.game.model.Direction
import org.alter.game.model.Tile
import org.alter.plugins.content.npcs.blackdemon.BlackDemonPlugin
import org.alter.plugins.testing.PluginIntegrationSupport
import org.alter.rscm.RSCM
import org.alter.rscm.RSCM.getRSCM
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Cross-references the Taverley Underground Black Demon spawns against the
 * plugin that registers the Black Demon combat definition.
 */
class BlackDemonSpawnRegistryTest {

    @Test
    fun `spawn plugin targets the existing black demon npc`() {
        assertEquals(BlackDemonPlugin.NPC_ID, BlackDemonSpawnPlugin.NPC_ID)
        assertEquals("npc.black_demon_1432", BlackDemonSpawnPlugin.NPC_ID)
    }

    @Test
    fun `exactly five spawns are registered`() {
        assertEquals(5, BlackDemonSpawnPlugin.SPAWN_TILES.size)
        assertEquals(5, BlackDemonSpawnPlugin.SPAWNED_NPC_IDS.size)
    }

    @Test
    fun `every spawned npc id is the black demon`() {
        assertTrue(BlackDemonSpawnPlugin.SPAWNED_NPC_IDS.all { it == BlackDemonSpawnPlugin.NPC_ID })
    }

    @Test
    fun `every spawned npc id resolves through rscm`() {
        BlackDemonSpawnPlugin.SPAWNED_NPC_IDS.forEach { RSCM.getRSCM(it) }
    }

    @Test
    fun `every spawned npc has a registered combat definition`() {
        val world = PluginIntegrationSupport.newWorld("black-demon-registry")
        BlackDemonPlugin(world.plugins, world, PluginIntegrationSupport.newServer())

        val missing =
            BlackDemonSpawnPlugin.SPAWNED_NPC_IDS.filterNot {
                world.plugins.npcCombatDefs.containsKey(getRSCM(it))
            }

        assertTrue(missing.isEmpty(), "spawned NPCs missing a combat definition: $missing")
        assertFalse(world.plugins.npcCombatDefs.isEmpty())
    }

    @Test
    fun `all spawn tiles are unique`() {
        assertEquals(
            BlackDemonSpawnPlugin.SPAWN_TILES.size,
            BlackDemonSpawnPlugin.SPAWN_TILES.toSet().size,
        )
    }

    @Test
    fun `no spawn tile equals the landing tile`() {
        val landingTile = Tile(x = 2863, z = 9777, height = 0)
        assertTrue(BlackDemonSpawnPlugin.SPAWN_TILES.none { it == landingTile })
    }

    @Test
    fun `spawns use south direction and zero walk radius`() {
        assertEquals(Direction.SOUTH, BlackDemonSpawnPlugin.DIRECTION)
        assertEquals(0, BlackDemonSpawnPlugin.WALK_RADIUS)
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun initRscm() {
            RSCM.init()
        }
    }
}
