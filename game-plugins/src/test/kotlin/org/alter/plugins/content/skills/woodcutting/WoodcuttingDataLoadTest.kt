package org.alter.plugins.content.skills.woodcutting

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
 * Verifies the shipped `data/cfg/skilling/woodcutting.json` loads, that every
 * RSCM object/item id resolves and that the tree table matches the wiki-sourced
 * levels, xp, lifetimes and respawns. `getRSCM` throws on an unknown name, so a
 * successful load also proves the data file references only real ids.
 */
class WoodcuttingDataLoadTest {

    @Test
    fun `loads every tree`() {
        val service = load()

        assertEquals(8, service.nodes("woodcutting").size)
    }

    @Test
    fun `binds the primary and variant tree ids`() {
        val service = load()

        listOf(
            "object.tree_1276",
            "object.oak_tree_4533",
            "object.oak_tree_42831",
            "object.willow_tree_4534",
            "object.teak_tree_15062",
            "object.maple_tree_40754",
            "object.mahogany_tree_30417",
            "object.yew_tree_42391",
            "object.magic_tree_10834",
        ).forEach { name ->
            assertNotNull(service.nodeForObject(getRSCM(name)), "no node bound to $name")
        }
    }

    @Test
    fun `matches the wiki sourced tree table`() {
        val service = load()

        val normal = service.nodes("woodcutting").single { it.node.key == "object.tree_1276" }
        assertEquals(1, normal.levelRequired)
        assertEquals(25.0, normal.xp)
        assertEquals(0, normal.lifetimeTicks)
        assertEquals(getRSCM("item.logs"), normal.loot.single().itemId)

        val oak = service.nodes("woodcutting").single { it.node.key == "object.oak_tree_8467" }
        assertEquals(15, oak.levelRequired)
        assertEquals(37.5, oak.xp)
        assertEquals(45, oak.lifetimeTicks)
        assertEquals(14, oak.respawnTicks)
        assertEquals(getRSCM("item.oak_logs"), oak.loot.single().itemId)
        assertEquals(getRSCM("object.tree_stump"), oak.depletedObjectId)

        val magic = service.nodes("woodcutting").single { it.node.key == "object.magic_tree_8409" }
        assertEquals(75, magic.levelRequired)
        assertEquals(250.0, magic.xp)
        assertEquals(390, magic.lifetimeTicks)
        assertEquals(199, magic.respawnTicks)
        assertEquals(getRSCM("item.magic_logs"), magic.loot.single().itemId)
        assertEquals(getRSCM("object.magic_tree_stump"), magic.depletedObjectId)
    }

    @Test
    fun `every tree drops a resolvable item`() {
        val service = load()

        service.nodes("woodcutting").forEach { node ->
            assertTrue(node.objectIds.isNotEmpty(), "${node.node.key} has no object ids")
            assertTrue(node.loot.isNotEmpty(), "${node.node.key} has no loot")
            node.loot.forEach { loot ->
                assertTrue(loot.itemId > 0, "${node.node.key} loot did not resolve")
            }
            assertTrue(node.actionTicks >= 1, "${node.node.key} has an invalid action interval")
        }
    }

    private fun load(): SkillingService {
        val service = SkillingService()
        service.load(Paths.get("../data/cfg/skilling"))
        return service
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun loadRscm() {
            RSCM.init()
        }
    }
}
