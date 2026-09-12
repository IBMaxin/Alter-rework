package org.alter.plugins.content.skills.mining

import org.alter.plugins.content.skills.framework.SkillingService
import org.alter.rscm.RSCM
import org.alter.rscm.RSCM.getRSCM
import org.junit.BeforeClass
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Verifies the shipped `data/cfg/skilling/mining.json` loads, that every RSCM
 * object/item id resolves and that the node table matches the wiki-sourced
 * levels, xp and respawns. `getRSCM` throws on an unknown name, so a successful
 * load also proves the data file references only real object/item ids.
 */
class MiningDataLoadTest {

    @Test
    fun `loads every mining rock`() {
        val service = SkillingService()
        service.load(Paths.get("../data/cfg/skilling"))

        assertEquals(setOf("mining", "woodcutting"), service.repository.definitionNames)
        assertEquals(12, service.nodes("mining").size)
    }

    @Test
    fun `binds the primary and variant rock ids`() {
        val service = SkillingService()
        service.load(Paths.get("../data/cfg/skilling"))

        listOf(
            "object.clay_rocks",
            "object.clay_rocks_11363",
            "object.copper_rocks_37944",
            "object.tin_rocks_37945",
            "object.blurite_rocks",
            "object.iron_rocks_36203",
            "object.iron_rocks_42833",
            "object.silver_rocks_36205",
            "object.coal_rocks_36204",
            "object.gold_rocks_36206",
            "object.mithril_rocks_36207",
            "object.adamantite_rocks_36208",
            "object.runite_rocks_11376",
            "object.runite_rocks_36209",
        ).forEach { name ->
            assertNotNull(service.nodeForObject(getRSCM(name)), "no node bound to $name")
        }
    }

    @Test
    fun `matches the wiki sourced ore table`() {
        val service = SkillingService()
        service.load(Paths.get("../data/cfg/skilling"))

        val clay = service.nodes("mining").single { it.node.key == "object.clay_rocks" }
        assertEquals(1, clay.levelRequired)
        assertEquals(5.0, clay.xp)
        assertEquals(2, clay.respawnTicks)
        assertEquals(getRSCM("item.clay"), clay.loot.single().itemId)

        val runite = service.nodes("mining").single { it.node.key == "object.runite_rocks_11376" }
        assertEquals(85, runite.levelRequired)
        assertEquals(125.0, runite.xp)
        assertEquals(1200, runite.respawnTicks)
        assertEquals(getRSCM("item.runite_ore"), runite.loot.single().itemId)

        val theNode = service.nodes("mining").single { it.node.key == "object.iron_rocks_42833" }
        assertEquals(15, theNode.levelRequired)
        assertEquals(0.0, theNode.xp)
        assertEquals(getRSCM("item.iron_ore"), theNode.loot.single().itemId)
    }

    @Test
    fun `every node references a depleted object`() {
        val service = SkillingService()
        service.load(Paths.get("../data/cfg/skilling"))

        service.nodes("mining").forEach { node ->
            assertEquals(
                getRSCM("object.rocks_11390"),
                node.depletedObjectId,
                "${node.node.key} does not resolve its depleted rock",
            )
        }
    }

    @Test
    fun `every node drops a resolvable item`() {
        val service = SkillingService()
        service.load(Paths.get("../data/cfg/skilling"))

        service.nodes("mining").forEach { node ->
            assertTrue(node.objectIds.isNotEmpty(), "${node.node.key} has no object ids")
            assertTrue(node.loot.isNotEmpty(), "${node.node.key} has no loot")
            node.loot.forEach { loot ->
                assertTrue(loot.itemId > 0, "${node.node.key} loot did not resolve")
            }
        }
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun loadRscm() {
            RSCM.init()
        }
    }
}
