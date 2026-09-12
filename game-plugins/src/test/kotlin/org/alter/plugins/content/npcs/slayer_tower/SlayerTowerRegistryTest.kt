package org.alter.plugins.content.npcs.slayer_tower

import org.alter.plugins.content.areas.slayer_tower.spawns.SlayerTowerSpawnPlugin
import org.alter.plugins.content.npcs.slayer_tower.banshee.BansheePlugin
import org.alter.plugins.content.npcs.slayer_tower.crawling_hand.CrawlingHandPlugin
import org.alter.plugins.content.npcs.slayer_tower.infernal_mage.InfernalMagePlugin
import org.alter.plugins.content.npcs.slayer_tower.bloodveld.BloodveldPlugin
import org.alter.plugins.content.npcs.slayer_tower.aberrant_spectre.AberrantSpectrePlugin
import org.alter.plugins.content.npcs.slayer_tower.gargoyle.GargoylePlugin
import org.alter.plugins.content.npcs.slayer_tower.nechryael.NechryaelPlugin
import org.alter.plugins.content.npcs.slayer_tower.abyssal_demon.AbyssalDemonPlugin
import org.alter.rscm.RSCM
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Cross-references Slayer Tower spawns against the per-monster plugins that
 * register their combat definitions and drop handlers.
 */
class SlayerTowerRegistryTest {

    private val registeredNpcIds: List<String>
        get() = CrawlingHandPlugin.NPC_IDS + BansheePlugin.NPC_IDS + InfernalMagePlugin.NPC_IDS + BloodveldPlugin.NPC_IDS +
            AberrantSpectrePlugin.NPC_IDS + GargoylePlugin.NPC_IDS + NechryaelPlugin.NPC_IDS + AbyssalDemonPlugin.NPC_IDS

    private fun assertSpawnedCovered(prefix: String, pluginNpcIds: List<String>) {
        val spawned = SlayerTowerSpawnPlugin.SPAWNED_NPC_IDS
            .filter { it.startsWith(prefix) }
            .toSet()
        assertTrue(spawned.isNotEmpty(), "expected spawned '$prefix' NPCs")
        val missing = spawned - pluginNpcIds
        assertTrue(missing.isEmpty(), "Spawned '$prefix' NPCs missing a plugin: $missing")
    }

    @Test
    fun `spawned crawling hands are registered by CrawlingHandPlugin`() {
        assertSpawnedCovered("npc.crawling_hand_", CrawlingHandPlugin.NPC_IDS)
    }

    @Test
    fun `spawned banshees are registered by BansheePlugin`() {
        assertSpawnedCovered("npc.banshee_", BansheePlugin.NPC_IDS)
    }

    @Test
    fun `spawned infernal mages are registered by InfernalMagePlugin`() {
        assertSpawnedCovered("npc.infernal_mage_", InfernalMagePlugin.NPC_IDS)
    }

    @Test
    fun `spawned bloodvelds are registered by BloodveldPlugin`() {
        assertSpawnedCovered("npc.bloodveld_", BloodveldPlugin.NPC_IDS)
    }

    @Test
    fun `spawned aberrant spectres are registered by AberrantSpectrePlugin`() {
        assertSpawnedCovered("npc.aberrant_spectre_", AberrantSpectrePlugin.NPC_IDS)
    }

    @Test
    fun `spawned gargoyles are registered by GargoylePlugin`() {
        assertSpawnedCovered("npc.gargoyle_", GargoylePlugin.NPC_IDS)
    }

    @Test
    fun `spawned nechryaels are registered by NechryaelPlugin`() {
        assertSpawnedCovered("npc.nechryael_", NechryaelPlugin.NPC_IDS)
    }

    @Test
    fun `spawned abyssal demons are registered by AbyssalDemonPlugin`() {
        assertSpawnedCovered("npc.abyssal_demon_", AbyssalDemonPlugin.NPC_IDS)
    }

    @Test
    fun `every spawned tower npc is covered by a plugin`() {
        val spawned = SlayerTowerSpawnPlugin.SPAWNED_NPC_IDS.toSet()
        val missing = spawned - registeredNpcIds.toSet()
        assertTrue(missing.isEmpty(), "Spawned NPCs missing a plugin: $missing")
    }

    @Test
    fun `spawn list has no duplicate npc ids`() {
        val ids = SlayerTowerSpawnPlugin.SPAWNED_NPC_IDS
        assertEquals(ids.size, ids.toSet().size, "duplicate spawned npc ids: $ids")
    }

    @Test
    fun `every registered npc id resolves through rscm`() {
        registeredNpcIds.forEach { RSCM.getRSCM(it) }
    }

    @Test
    fun `registered npc ids are unique across plugins`() {
        assertEquals(
            registeredNpcIds.size,
            registeredNpcIds.toSet().size,
            "duplicate npc ids registered: $registeredNpcIds",
        )
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun initRscm() {
            RSCM.init()
        }
    }
}
