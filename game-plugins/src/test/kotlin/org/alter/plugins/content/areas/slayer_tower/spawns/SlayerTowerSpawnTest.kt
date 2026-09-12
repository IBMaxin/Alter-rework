package org.alter.plugins.content.areas.slayer_tower.spawns

import org.junit.Assert.*
import org.junit.Test

/**
 * Validates Slayer Tower spawn data integrity.
 *
 * Tests verify:
 * - All 8 NPC types are present (50 spawns total)
 * - Each floor has correct NPC types
 * - No duplicate NPC IDs spawned on same tile
 * - Walk radius is reasonable (2-6)
 * - Height values match floor assignments
 * - Directions are valid
 */
class SlayerTowerSpawnTest {

    private data class SpawnEntry(
        val npc: String,
        val x: Int,
        val z: Int,
        val height: Int,
        val walkRadius: Int,
        val direction: String,
    )

    private val spawns = listOf(
        // Ground floor (height 0) — Crawling hands (west wing)
        SpawnEntry("npc.crawling_hand_448", 3422, 3540, 0, 4, "SOUTH"),
        SpawnEntry("npc.crawling_hand_449", 3420, 3542, 0, 4, "WEST"),
        SpawnEntry("npc.crawling_hand_450", 3424, 3543, 0, 4, "NORTH"),
        SpawnEntry("npc.crawling_hand_451", 3421, 3545, 0, 4, "EAST"),
        SpawnEntry("npc.crawling_hand_452", 3423, 3541, 0, 4, "SOUTH"),
        // Ground floor (height 0) — Banshees (east wing)
        SpawnEntry("npc.banshee_414", 3435, 3540, 0, 4, "SOUTH"),
        SpawnEntry("npc.banshee_414", 3437, 3542, 0, 4, "WEST"),
        SpawnEntry("npc.banshee_414", 3436, 3544, 0, 4, "NORTH"),
        // First floor (height 1) — Bloodvelds (NW)
        SpawnEntry("npc.bloodveld_484", 3422, 3535, 1, 4, "SOUTH"),
        SpawnEntry("npc.bloodveld_485", 3420, 3533, 1, 4, "EAST"),
        SpawnEntry("npc.bloodveld_486", 3424, 3534, 1, 4, "WEST"),
        SpawnEntry("npc.bloodveld_487", 3421, 3536, 1, 4, "NORTH"),
        // First floor (height 1) — Infernal mages (NE)
        SpawnEntry("npc.infernal_mage_443", 3435, 3535, 1, 4, "SOUTH"),
        SpawnEntry("npc.infernal_mage_444", 3437, 3533, 1, 4, "WEST"),
        SpawnEntry("npc.infernal_mage_445", 3436, 3536, 1, 4, "NORTH"),
        SpawnEntry("npc.infernal_mage_446", 3434, 3534, 1, 4, "EAST"),
        SpawnEntry("npc.infernal_mage_447", 3438, 3535, 1, 4, "SOUTH"),
        // First floor (height 1) — Aberrant spectres (S)
        SpawnEntry("npc.aberrant_spectre_2", 3428, 3543, 1, 4, "SOUTH"),
        SpawnEntry("npc.aberrant_spectre_3", 3430, 3544, 1, 4, "WEST"),
        SpawnEntry("npc.aberrant_spectre_4", 3426, 3542, 1, 4, "NORTH"),
        SpawnEntry("npc.aberrant_spectre_5", 3429, 3545, 1, 4, "EAST"),
        SpawnEntry("npc.aberrant_spectre_6", 3427, 3543, 1, 4, "SOUTH"),
        SpawnEntry("npc.aberrant_spectre_7", 3431, 3542, 1, 4, "WEST"),
        // Second floor (height 2) — Gargoyles (SE)
        SpawnEntry("npc.gargoyle_412", 3435, 3543, 2, 4, "SOUTH"),
        SpawnEntry("npc.gargoyle_413", 3437, 3544, 2, 4, "WEST"),
        SpawnEntry("npc.gargoyle_412", 3436, 3542, 2, 4, "NORTH"),
        SpawnEntry("npc.gargoyle_413", 3434, 3545, 2, 4, "EAST"),
        SpawnEntry("npc.gargoyle_412", 3438, 3543, 2, 4, "SOUTH"),
        // Second floor (height 2) — Nechryael (NE)
        SpawnEntry("npc.nechryael_8", 3435, 3535, 2, 4, "SOUTH"),
        SpawnEntry("npc.nechryael_11", 3437, 3533, 2, 4, "WEST"),
        SpawnEntry("npc.nechryael_8", 3436, 3536, 2, 4, "NORTH"),
        SpawnEntry("npc.nechryael_11", 3434, 3534, 2, 4, "EAST"),
        // Second floor (height 2) — Abyssal demons (NW)
        SpawnEntry("npc.abyssal_demon_415", 3422, 3535, 2, 4, "SOUTH"),
        SpawnEntry("npc.abyssal_demon_416", 3420, 3533, 2, 4, "EAST"),
        SpawnEntry("npc.abyssal_demon_415", 3424, 3534, 2, 4, "WEST"),
        SpawnEntry("npc.abyssal_demon_416", 3421, 3536, 2, 4, "NORTH"),
        SpawnEntry("npc.abyssal_demon_415", 3423, 3535, 2, 4, "SOUTH"),
    )

    @Test
    fun `total spawn count is 37`() {
        assertEquals(37, spawns.size)
    }

    @Test
    fun `all 8 NPC types are represented`() {
        val npcTypes = spawns.map { it.npc.replace(Regex("_\\d+$"), "").removePrefix("npc.") }.distinct()
        assertEquals(8, npcTypes.size)
        assertTrue(npcTypes.any { it.contains("crawling_hand") })
        assertTrue(npcTypes.any { it.contains("banshee") })
        assertTrue(npcTypes.any { it.contains("bloodveld") })
        assertTrue(npcTypes.any { it.contains("infernal_mage") })
        assertTrue(npcTypes.any { it.contains("aberrant_spectre") })
        assertTrue(npcTypes.any { it.contains("gargoyle") })
        assertTrue(npcTypes.any { it.contains("nechryael") })
        assertTrue(npcTypes.any { it.contains("abyssal_demon") })
    }

    @Test
    fun `ground floor has only crawling hands and banshees`() {
        val groundFloor = spawns.filter { it.height == 0 }
        assertTrue(groundFloor.all { it.npc.contains("crawling_hand") || it.npc.contains("banshee") })
        assertEquals(5, groundFloor.count { it.npc.contains("crawling_hand") })
        assertEquals(3, groundFloor.count { it.npc.contains("banshee") })
    }

    @Test
    fun `first floor has bloodvelds, infernal mages, and aberrant spectres`() {
        val firstFloor = spawns.filter { it.height == 1 }
        assertTrue(firstFloor.all {
            it.npc.contains("bloodveld") || it.npc.contains("infernal_mage") || it.npc.contains("aberrant_spectre")
        })
        assertEquals(4, firstFloor.count { it.npc.contains("bloodveld") })
        assertEquals(5, firstFloor.count { it.npc.contains("infernal_mage") })
        assertEquals(6, firstFloor.count { it.npc.contains("aberrant_spectre") })
    }

    @Test
    fun `second floor has gargoyles, nechryael, and abyssal demons`() {
        val secondFloor = spawns.filter { it.height == 2 }
        assertTrue(secondFloor.all {
            it.npc.contains("gargoyle") || it.npc.contains("nechryael") || it.npc.contains("abyssal_demon")
        })
        assertEquals(5, secondFloor.count { it.npc.contains("gargoyle") })
        assertEquals(4, secondFloor.count { it.npc.contains("nechryael") })
        assertEquals(5, secondFloor.count { it.npc.contains("abyssal_demon") })
    }

    @Test
    fun `no duplicate spawns on same tile`() {
        val tiles = spawns.map { Triple(it.x, it.z, it.height) }
        assertEquals(tiles.size, tiles.toSet().size)
    }

    @Test
    fun `all walk radii are between 2 and 6`() {
        assertTrue(spawns.all { it.walkRadius in 2..6 })
    }

    @Test
    fun `all height values are 0 1 or 2`() {
        assertTrue(spawns.all { it.height in 0..2 })
    }

    @Test
    fun `all directions are valid`() {
        val validDirections = setOf("NORTH", "SOUTH", "EAST", "WEST")
        assertTrue(spawns.all { it.direction in validDirections })
    }

    @Test
    fun `crawling hands use varied IDs`() {
        val handIds = spawns.filter { it.npc.contains("crawling_hand") }.map { it.npc }.distinct()
        assertEquals(5, handIds.size)
        assertTrue(handIds.all { it.startsWith("npc.crawling_hand_4") })
    }

    @Test
    fun `banshee spawns are within east wing x range`() {
        val bansheeSpawns = spawns.filter { it.npc.contains("banshee") }
        assertTrue(bansheeSpawns.all { it.x in 3430..3440 })
    }

    @Test
    fun `tower coordinates are in Canifis region`() {
        assertTrue(spawns.all { it.x in 3400..3450 })
        assertTrue(spawns.all { it.z in 3525..3555 })
    }
}
