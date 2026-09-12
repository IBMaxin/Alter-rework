package org.alter.plugins.content.npcs.kbd

import org.alter.game.model.Tile
import org.alter.game.model.World
import org.alter.game.model.combat.NpcCombatDef
import org.alter.game.model.entity.Player
import org.alter.game.model.weightedTableBuilder.LootTable
import org.alter.game.model.weightedTableBuilder.TableType
import org.alter.game.model.weightedTableBuilder.roll
import org.alter.plugins.testing.PluginIntegrationSupport
import org.alter.rscm.RSCM
import org.alter.rscm.RSCM.getRSCM
import org.junit.Before
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration coverage for the migrated King Black Dragon drop table.
 *
 * Instantiating [KbdConfigsPlugin] validates that the former commented-out
 * `Items.*` block now resolves through RSCM and produces well-formed loot
 * tables. The spawn itself lives in
 * [org.alter.plugins.content.areas.king_black_dragon_lair.spawns.KbdSpawnPlugin]
 * so this test does not need the NPC cache.
 */
class KbdDropsIntegrationTest {

    private lateinit var world: World
    private lateinit var player: Player
    private lateinit var def: NpcCombatDef

    @Before
    fun setUp() {
        world = PluginIntegrationSupport.newWorld("kbd-test")
        player = PluginIntegrationSupport.newPlayer(world)
        KbdConfigsPlugin(world.plugins, world, PluginIntegrationSupport.newServer())
        def = world.plugins.npcCombatDefs[getRSCM(KbdConfigsPlugin.NPC_ID)]
            ?: error("KbdConfigsPlugin did not register ${KbdConfigsPlugin.NPC_ID}")
    }

    private fun alwaysTable(tables: List<LootTable>): LootTable =
        tables.single { it.tableType == TableType.ALWAYS }

    private fun mainTable(tables: List<LootTable>): LootTable =
        tables.single { it.tableType == TableType.MAIN }

    @Test
    fun `dragon bones and black dragon leather always drop`() {
        val always = alwaysTable(def.LootTables!!)

        assertEquals(2, always.drops.size)
        assertEquals(
            setOf(getRSCM("item.dragon_bones"), getRSCM("item.black_dragon_leather")),
            always.drops.map { it.item }.toSet(),
        )
    }

    @Test
    fun `main table uses a 128 total with a 10 point nothing remainder`() {
        val main = mainTable(def.LootTables!!)

        assertEquals(128, main.tableWeight)
        assertEquals(118, main.drops.sumOf { it.weight ?: 0 })
    }

    @Test
    fun `main table entries match the configured weights and resolve through rscm`() {
        val main = mainTable(def.LootTables!!)
        val weightByItem = main.drops.associate { (it.item as Int) to (it.weight ?: 0) }

        val expected =
            mapOf(
                "item.rune_longsword" to 10,
                "item.adamant_platebody" to 9,
                "item.adamant_kiteshield" to 3,
                "item.dragon_med_helm" to 1,
                "item.fire_rune" to 5,
                "item.air_rune" to 10,
                "item.iron_arrow" to 10,
                "item.runite_bolts" to 10,
                "item.law_rune" to 5,
                "item.blood_rune" to 5,
                "item.yew_logs_noted" to 10,
                "item.adamantite_bar" to 5,
                "item.runite_bar" to 3,
                "item.gold_ore_noted" to 2,
                "item.amulet_of_power" to 7,
                "item.dragon_arrowtips" to 5,
                "item.dragon_dart_tip" to 5,
                "item.dragon_javelin_heads" to 5,
                "item.runite_limbs" to 4,
                "item.shark" to 4,
            ).mapKeys { (name, _) -> getRSCM(name) }

        assertEquals(expected, weightByItem)
    }

    @Test
    fun `rolling the table always drops the guaranteed pair and at most one main drop`() {
        val tables = def.LootTables!!
        val alwaysIds = alwaysTable(tables).drops.map { it.item as Int }.toSet()
        val mainIds = mainTable(tables).drops.map { it.item as Int }.toSet()
        val tile = Tile(2274, 4698, 0)

        var withMain = 0
        repeat(TRIALS) {
            val drops = roll(player, tables, tile)

            assertTrue(drops.map { it.item }.containsAll(alwaysIds), "guaranteed drops were missing")
            val main = drops.filterNot { it.item in alwaysIds }
            assertTrue(main.size <= 1, "at most one main drop may be awarded, got ${main.size}")
            main.forEach { assertTrue(it.item in mainIds, "unexpected main drop ${it.item}") }
            if (main.isNotEmpty()) {
                withMain++
            }
        }

        assertTrue(
            withMain in MIN_MAIN..MAX_MAIN,
            "main drop rate looked wrong: $withMain/$TRIALS (expected most kills to award one)",
        )
    }

    companion object {
        private const val TRIALS = 1000
        private const val MIN_MAIN = 850
        private const val MAX_MAIN = 995

        @BeforeClass
        @JvmStatic
        fun initRscm() {
            RSCM.init()
        }
    }
}
