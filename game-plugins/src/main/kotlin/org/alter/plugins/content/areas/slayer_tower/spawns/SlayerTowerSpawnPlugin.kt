package org.alter.plugins.content.areas.slayer_tower.spawns

import org.alter.game.Server
import org.alter.game.model.Direction
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Slayer Tower NPC spawns — floor-by-floor OSRS-accurate placement.
 *
 * Tower located at ~x=3428, z=3538 (Canifis region).
 * Height 0 = ground floor, height 1 = first floor, height 2 = second floor.
 *
 * Floor layout (OSRS Wiki):
 * - Ground: Crawling hands (west wing), Banshees (east wing)
 * - 1st: Bloodvelds (NW), Infernal mages (NE), Aberrant spectres (S)
 * - 2nd: Gargoyles (SE), Nechryael (NE), Abyssal demons (NW)
 */
class SlayerTowerSpawnPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {
    init {
        // ── Ground floor (height 0) ──────────────────────────────────────
        // Crawling hands — west wing
        spawnNpc(npc = "npc.crawling_hand_448", x = 3422, z = 3540, height = 0, walkRadius = 4, direction = Direction.SOUTH)
        spawnNpc(npc = "npc.crawling_hand_449", x = 3420, z = 3542, height = 0, walkRadius = 4, direction = Direction.WEST)
        spawnNpc(npc = "npc.crawling_hand_450", x = 3424, z = 3543, height = 0, walkRadius = 4, direction = Direction.NORTH)
        spawnNpc(npc = "npc.crawling_hand_451", x = 3421, z = 3545, height = 0, walkRadius = 4, direction = Direction.EAST)
        spawnNpc(npc = "npc.crawling_hand_452", x = 3423, z = 3541, height = 0, walkRadius = 4, direction = Direction.SOUTH)

        // Banshees — east wing
        spawnNpc(npc = "npc.banshee_414", x = 3435, z = 3540, height = 0, walkRadius = 4, direction = Direction.SOUTH)
        spawnNpc(npc = "npc.banshee_414", x = 3437, z = 3542, height = 0, walkRadius = 4, direction = Direction.WEST)
        spawnNpc(npc = "npc.banshee_414", x = 3436, z = 3544, height = 0, walkRadius = 4, direction = Direction.NORTH)

        // ── First floor (height 1) ───────────────────────────────────────
        // Bloodvelds — northwest wing
        spawnNpc(npc = "npc.bloodveld_484", x = 3422, z = 3535, height = 1, walkRadius = 4, direction = Direction.SOUTH)
        spawnNpc(npc = "npc.bloodveld_485", x = 3420, z = 3533, height = 1, walkRadius = 4, direction = Direction.EAST)
        spawnNpc(npc = "npc.bloodveld_486", x = 3424, z = 3534, height = 1, walkRadius = 4, direction = Direction.WEST)
        spawnNpc(npc = "npc.bloodveld_487", x = 3421, z = 3536, height = 1, walkRadius = 4, direction = Direction.NORTH)

        // Infernal mages — northeast wing
        spawnNpc(npc = "npc.infernal_mage_443", x = 3435, z = 3535, height = 1, walkRadius = 4, direction = Direction.SOUTH)
        spawnNpc(npc = "npc.infernal_mage_444", x = 3437, z = 3533, height = 1, walkRadius = 4, direction = Direction.WEST)
        spawnNpc(npc = "npc.infernal_mage_445", x = 3436, z = 3536, height = 1, walkRadius = 4, direction = Direction.NORTH)
        spawnNpc(npc = "npc.infernal_mage_446", x = 3434, z = 3534, height = 1, walkRadius = 4, direction = Direction.EAST)
        spawnNpc(npc = "npc.infernal_mage_447", x = 3438, z = 3535, height = 1, walkRadius = 4, direction = Direction.SOUTH)

        // Aberrant spectres — south wing
        spawnNpc(npc = "npc.aberrant_spectre_2", x = 3428, z = 3543, height = 1, walkRadius = 4, direction = Direction.SOUTH)
        spawnNpc(npc = "npc.aberrant_spectre_3", x = 3430, z = 3544, height = 1, walkRadius = 4, direction = Direction.WEST)
        spawnNpc(npc = "npc.aberrant_spectre_4", x = 3426, z = 3542, height = 1, walkRadius = 4, direction = Direction.NORTH)
        spawnNpc(npc = "npc.aberrant_spectre_5", x = 3429, z = 3545, height = 1, walkRadius = 4, direction = Direction.EAST)
        spawnNpc(npc = "npc.aberrant_spectre_6", x = 3427, z = 3543, height = 1, walkRadius = 4, direction = Direction.SOUTH)
        spawnNpc(npc = "npc.aberrant_spectre_7", x = 3431, z = 3542, height = 1, walkRadius = 4, direction = Direction.WEST)

        // ── Second floor (height 2) ──────────────────────────────────────
        // Gargoyles — southeast wing
        spawnNpc(npc = "npc.gargoyle_412", x = 3435, z = 3543, height = 2, walkRadius = 4, direction = Direction.SOUTH)
        spawnNpc(npc = "npc.gargoyle_413", x = 3437, z = 3544, height = 2, walkRadius = 4, direction = Direction.WEST)
        spawnNpc(npc = "npc.gargoyle_412", x = 3436, z = 3542, height = 2, walkRadius = 4, direction = Direction.NORTH)
        spawnNpc(npc = "npc.gargoyle_413", x = 3434, z = 3545, height = 2, walkRadius = 4, direction = Direction.EAST)
        spawnNpc(npc = "npc.gargoyle_412", x = 3438, z = 3543, height = 2, walkRadius = 4, direction = Direction.SOUTH)

        // Nechryael — northeast wing
        spawnNpc(npc = "npc.nechryael_8", x = 3435, z = 3535, height = 2, walkRadius = 4, direction = Direction.SOUTH)
        spawnNpc(npc = "npc.nechryael_11", x = 3437, z = 3533, height = 2, walkRadius = 4, direction = Direction.WEST)
        spawnNpc(npc = "npc.nechryael_8", x = 3436, z = 3536, height = 2, walkRadius = 4, direction = Direction.NORTH)
        spawnNpc(npc = "npc.nechryael_11", x = 3434, z = 3534, height = 2, walkRadius = 4, direction = Direction.EAST)

        // Abyssal demons — northwest wing
        spawnNpc(npc = "npc.abyssal_demon_415", x = 3422, z = 3535, height = 2, walkRadius = 4, direction = Direction.SOUTH)
        spawnNpc(npc = "npc.abyssal_demon_416", x = 3420, z = 3533, height = 2, walkRadius = 4, direction = Direction.EAST)
        spawnNpc(npc = "npc.abyssal_demon_415", x = 3424, z = 3534, height = 2, walkRadius = 4, direction = Direction.WEST)
        spawnNpc(npc = "npc.abyssal_demon_416", x = 3421, z = 3536, height = 2, walkRadius = 4, direction = Direction.NORTH)
        spawnNpc(npc = "npc.abyssal_demon_415", x = 3423, z = 3535, height = 2, walkRadius = 4, direction = Direction.SOUTH)
    }

    companion object {
        val SPAWNED_NPC_IDS = listOf(
            "npc.crawling_hand_448", "npc.crawling_hand_449", "npc.crawling_hand_450",
            "npc.crawling_hand_451", "npc.crawling_hand_452",
            "npc.banshee_414",
            "npc.bloodveld_484", "npc.bloodveld_485", "npc.bloodveld_486", "npc.bloodveld_487",
            "npc.infernal_mage_443", "npc.infernal_mage_444", "npc.infernal_mage_445",
            "npc.infernal_mage_446", "npc.infernal_mage_447",
            "npc.aberrant_spectre_2", "npc.aberrant_spectre_3", "npc.aberrant_spectre_4",
            "npc.aberrant_spectre_5", "npc.aberrant_spectre_6", "npc.aberrant_spectre_7",
            "npc.gargoyle_412", "npc.gargoyle_413",
            "npc.nechryael_8", "npc.nechryael_11",
            "npc.abyssal_demon_415", "npc.abyssal_demon_416",
        )
    }
}
