package org.alter.plugins.content.npcs.slayer_tower

import org.alter.game.model.Tile
import org.alter.game.model.World
import org.alter.game.model.combat.NpcCombatDef
import org.alter.game.model.entity.Player
import org.alter.game.model.weightedTableBuilder.LootTable
import org.alter.game.model.weightedTableBuilder.TableType
import org.alter.game.model.weightedTableBuilder.roll
import org.alter.plugins.content.npcs.slayer_tower.aberrant_spectre.AberrantSpectrePlugin
import org.alter.plugins.content.npcs.slayer_tower.abyssal_demon.AbyssalDemonPlugin
import org.alter.plugins.content.npcs.slayer_tower.banshee.BansheePlugin
import org.alter.plugins.content.npcs.slayer_tower.bloodveld.BloodveldPlugin
import org.alter.plugins.content.npcs.slayer_tower.crawling_hand.CrawlingHandPlugin
import org.alter.plugins.content.npcs.slayer_tower.gargoyle.GargoylePlugin
import org.alter.plugins.content.npcs.slayer_tower.infernal_mage.InfernalMagePlugin
import org.alter.plugins.content.npcs.slayer_tower.nechryael.NechryaelPlugin
import org.alter.plugins.testing.PluginIntegrationSupport
import org.alter.rscm.RSCM
import org.alter.rscm.RSCM.getRSCM
import org.junit.Before
import org.junit.BeforeClass
import kotlin.reflect.KFunction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration coverage for the eight Slayer Tower monsters migrated from the
 * manual `onNpcDeath` loot blocks to the `drops {}` DSL.
 *
 * Each real plugin is instantiated against a bare [World], so this exercises the
 * DSL wiring, RSCM resolution, and nested weighted tables rather than a
 * hand-built copy of the definitions. The expected table counts lock in the
 * behaviour of the original hand-rolled roll groups: every independent
 * `world.random(1..N)` block must survive as its own table.
 */
class SlayerTowerDropsIntegrationTest {

    private lateinit var world: World
    private lateinit var player: Player

    @Before
    fun setUp() {
        world = PluginIntegrationSupport.newWorld("slayer-tower-drops")
        player = PluginIntegrationSupport.newPlayer(world)
        val server = PluginIntegrationSupport.newServer()
        CrawlingHandPlugin(world.plugins, world, server)
        BansheePlugin(world.plugins, world, server)
        InfernalMagePlugin(world.plugins, world, server)
        BloodveldPlugin(world.plugins, world, server)
        AberrantSpectrePlugin(world.plugins, world, server)
        GargoylePlugin(world.plugins, world, server)
        NechryaelPlugin(world.plugins, world, server)
        AbyssalDemonPlugin(world.plugins, world, server)
    }

    private fun def(npcId: String): NpcCombatDef =
        world.plugins.npcCombatDefs[getRSCM(npcId)]
            ?: error("no combat definition registered for $npcId")

    private fun allTowerNpcIds(): List<String> =
        CrawlingHandPlugin.NPC_IDS +
            BansheePlugin.NPC_IDS +
            InfernalMagePlugin.NPC_IDS +
            BloodveldPlugin.NPC_IDS +
            AberrantSpectrePlugin.NPC_IDS +
            GargoylePlugin.NPC_IDS +
            NechryaelPlugin.NPC_IDS +
            AbyssalDemonPlugin.NPC_IDS

    private fun nestedTables(table: LootTable): List<LootTable> =
        table.drops
            .mapNotNull { it.item as? LootTable }
            .flatMap { listOf(it) + nestedTables(it) }

    private fun itemIds(table: LootTable): List<Int> =
        table.drops.flatMap { loot ->
            when (val item = loot.item) {
                is Int -> listOf(item)
                is LootTable -> itemIds(item)
                else -> emptyList()
            }
        }

    private fun validate(table: LootTable, path: String) {
        assertTrue(table.drops.isNotEmpty(), "$path has no entries")
        when (table.tableType) {
            TableType.ALWAYS ->
                assertEquals(0, table.tableWeight ?: 0, "$path: ALWAYS tables must not declare a weight")
            TableType.MAIN -> {
                val accumulated = table.drops.sumOf { it.weight ?: 0 }
                assertTrue(
                    accumulated <= (table.tableWeight ?: 0),
                    "$path: item weight $accumulated exceeds table weight ${table.tableWeight}",
                )
            }
            TableType.PRE_ROLL, TableType.TERTIARY -> Unit
        }
        table.drops.forEach { loot ->
            when (val item = loot.item) {
                is Int -> assertTrue(item > 0, "$path: unresolved item id $item")
                is LootTable -> validate(item, "$path -> nested")
                is KFunction<*> -> Unit
                else -> error("$path: unsupported loot item type ${item?.javaClass}")
            }
        }
    }

    @Test
    fun `every spawned tower npc id has a combat definition with loot tables`() {
        allTowerNpcIds().forEach { id ->
            val tables = def(id).LootTables
            assertTrue(!tables.isNullOrEmpty(), "$id has no loot tables")
        }
    }

    @Test
    fun `every registered table is well formed`() {
        allTowerNpcIds().distinct().forEach { id ->
            def(id).LootTables!!.forEach { validate(it, id) }
        }
    }

    @Test
    fun `every drop resolves to a positive item id`() {
        allTowerNpcIds().distinct().forEach { id ->
            val ids = def(id).LootTables!!.flatMap { itemIds(it) }
            assertTrue(ids.isNotEmpty(), "$id has no concrete item drops")
            assertTrue(ids.all { it > 0 }, "$id has unresolved item ids: $ids")
        }
    }

    @Test
    fun `guaranteed ash and bone drops are registered as always tables`() {
        val expectations = mapOf(
            "npc.crawling_hand_448" to "item.bones",
            "npc.infernal_mage_443" to "item.bones",
            "npc.bloodveld_484" to "item.vile_ashes",
            "npc.nechryael_8" to "item.malicious_ashes",
            "npc.abyssal_demon_415" to "item.abyssal_ashes",
        )

        expectations.forEach { (npc, item) ->
            val always = def(npc).LootTables!!.filter { it.tableType == TableType.ALWAYS }
            assertEquals(1, always.size, "$npc should have exactly one ALWAYS table")
            assertEquals(getRSCM(item), always.single().drops.single().item, "$npc ALWAYS drop")
        }
    }

    @Test
    fun `monsters without guaranteed drops register no always tables`() {
        listOf("npc.banshee_414", "npc.aberrant_spectre_2", "npc.gargoyle_412").forEach { npc ->
            val always = def(npc).LootTables!!.filter { it.tableType == TableType.ALWAYS }
            assertTrue(always.isEmpty(), "$npc should not have an ALWAYS table")
        }
    }

    @Test
    fun `each monster keeps its expected number of independent roll groups`() {
        val expectedMainTables = mapOf(
            "npc.crawling_hand_448" to 3,
            "npc.banshee_414" to 4,
            "npc.infernal_mage_443" to 5,
            "npc.bloodveld_484" to 5,
            "npc.aberrant_spectre_2" to 6,
            "npc.gargoyle_412" to 6,
            "npc.nechryael_8" to 7,
            "npc.abyssal_demon_415" to 6,
        )

        expectedMainTables.forEach { (npc, expected) ->
            val actual = def(npc).LootTables!!.count { it.tableType == TableType.MAIN }
            assertEquals(expected, actual, "$npc MAIN table count")
        }
    }

    @Test
    fun `aberrant spectre herb and seed rolls are nested weighted tables`() {
        val tables = def("npc.aberrant_spectre_2").LootTables!!
        val nested = tables.flatMap { nestedTables(it) }

        val herb = nested.single { it.drops.any { loot -> loot.item == getRSCM("item.grimy_guam_leaf") } }
        assertEquals(26, herb.tableWeight)
        assertEquals(11, herb.drops.size)
        assertEquals(26, herb.drops.sumOf { it.weight ?: 0 })

        val seeds = nested.single { it.drops.any { loot -> loot.item == getRSCM("item.toadflax_seed") } }
        assertEquals(14, seeds.tableWeight)
        assertEquals(14, seeds.drops.size)
        assertEquals(14, seeds.drops.sumOf { it.weight ?: 0 })
    }

    @Test
    fun `nechryael registers two independent nested seed rolls`() {
        val tables = def("npc.nechryael_8").LootTables!!
        val seedTables = tables
            .filter { it.tableType == TableType.MAIN && it.drops.size == 1 }
            .mapNotNull { it.drops.single().item as? LootTable }
            .filter { nested -> nested.drops.any { it.item == getRSCM("item.limpwurt_seed") } }

        assertEquals(2, seedTables.size, "expected two independent seed tables")
        seedTables.forEach { seed ->
            assertEquals(100, seed.tableWeight)
            assertEquals(13, seed.drops.size)
            assertEquals(100, seed.drops.sumOf { it.weight ?: 0 })
        }
    }

    @Test
    fun `rolling an abyssal demon kill always yields abyssal ashes`() {
        val tables = def("npc.abyssal_demon_415").LootTables!!
        val ashes = getRSCM("item.abyssal_ashes")
        val allowed = tables.flatMap { itemIds(it) }.toSet()
        val maxDrops = tables.count { it.tableType == TableType.MAIN } + 1

        repeat(TRIALS) {
            val drops = roll(player, tables, Tile(0, 0, 0))
            assertTrue(drops.any { it.item == ashes }, "abyssal ashes were not dropped")
            assertTrue(drops.size <= maxDrops, "dropped ${drops.size} items, expected at most $maxDrops")
            drops.forEach { assertTrue(it.item in allowed, "unexpected drop ${it.item}") }
        }
    }

    companion object {
        private const val TRIALS = 300

        @BeforeClass
        @JvmStatic
        fun initRscm() {
            RSCM.init()
        }
    }
}
