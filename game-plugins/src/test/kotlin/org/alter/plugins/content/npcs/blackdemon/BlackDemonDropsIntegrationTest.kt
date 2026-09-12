package org.alter.plugins.content.npcs.blackdemon

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
 * Integration coverage for the migrated [BlackDemonPlugin] drop table.
 *
 * The real plugin is instantiated against a bare [World], so this exercises the
 * `drops {}` DSL wiring, RSCM resolution, and the nested tier tables rather than
 * a hand-built copy of the definition.
 */
class BlackDemonDropsIntegrationTest {

    private lateinit var world: World
    private lateinit var player: Player
    private lateinit var def: NpcCombatDef

    @Before
    fun setUp() {
        world = PluginIntegrationSupport.newWorld("blackdemon-test")
        player = PluginIntegrationSupport.newPlayer(world)
        BlackDemonPlugin(world.plugins, world, PluginIntegrationSupport.newServer())
        def = world.plugins.npcCombatDefs[getRSCM(BlackDemonPlugin.NPC_ID)]
            ?: error("BlackDemonPlugin did not register ${BlackDemonPlugin.NPC_ID}")
    }

    private val ashesId get() = getRSCM("item.malicious_ashes_noted")

    private fun alwaysTable(tables: List<LootTable>): LootTable =
        tables.single { it.tableType == TableType.ALWAYS }

    private fun mainTable(tables: List<LootTable>): LootTable =
        tables.single { it.tableType == TableType.MAIN }

    @Test
    fun `plugin registers a combat definition for the black demon npc`() {
        assertTrue(world.plugins.npcCombatDefs.containsKey(getRSCM(BlackDemonPlugin.NPC_ID)))
    }

    @Test
    fun `malicious ashes are an unconditional always drop`() {
        val always = alwaysTable(def.LootTables!!)

        assertEquals(1, always.drops.size)
        assertEquals(ashesId, always.drops.single().item)
    }

    @Test
    fun `bonus roll exposes one main table with the four tier weights`() {
        val main = mainTable(def.LootTables!!)

        assertEquals(200, main.tableWeight)
        assertEquals(setOf(50, 25, 15, 10), main.drops.mapNotNull { it.weight }.toSet())
    }

    @Test
    fun `each tier is a uniform nested pool of the expected size`() {
        val main = mainTable(def.LootTables!!)
        val poolSizeByWeight =
            main.drops.associate { loot ->
                val pool = loot.item as? LootTable ?: error("tier entry is not a nested table: $loot")
                (loot.weight ?: 0) to pool
            }

        assertEquals(setOf(50, 25, 15, 10), poolSizeByWeight.keys)
        assertEquals(6, poolSizeByWeight.getValue(50).drops.size)
        assertEquals(8, poolSizeByWeight.getValue(25).drops.size)
        assertEquals(5, poolSizeByWeight.getValue(15).drops.size)
        assertEquals(5, poolSizeByWeight.getValue(10).drops.size)

        poolSizeByWeight.values.forEach { pool ->
            assertEquals(TableType.MAIN, pool.tableType)
            assertEquals(pool.drops.size, pool.tableWeight)
            assertTrue(pool.drops.all { it.weight == 1 }, "tier pool entries must be uniformly weighted")
        }
    }

    @Test
    fun `every configured drop item resolves through rscm`() {
        val itemIds = def.LootTables.orEmpty().flatMap { table -> table.drops.map { it.item } }
        val intIds = itemIds.filterIsInstance<Int>()
        assertTrue(intIds.isNotEmpty())
        assertTrue(intIds.all { it > 0 }, "unresolved item ids: $itemIds")
    }

    @Test
    fun `rolling the table always drops ashes and rarely more than one bonus`() {
        val tables = def.LootTables!!
        val bonusIds =
            mainTable(tables).drops
                .flatMap { (it.item as LootTable).drops }
                .map { it.item as Int }
                .toSet()
        val tile = Tile(2270, 4690, 0)

        var withBonus = 0
        repeat(TRIALS) {
            val drops = roll(player, tables, tile)

            assertTrue(drops.any { it.item == ashesId }, "ashes were not dropped")
            val bonus = drops.filterNot { it.item == ashesId }
            assertTrue(bonus.size <= 1, "at most one bonus reward may drop per kill, got ${bonus.size}")
            bonus.forEach { assertTrue(it.item in bonusIds, "unexpected bonus drop ${it.item}") }
            if (bonus.isNotEmpty()) {
                withBonus++
            }
        }

        assertTrue(
            withBonus in MIN_BONUS..MAX_BONUS,
            "bonus drop rate looked wrong: $withBonus/$TRIALS (expected roughly half)",
        )
    }

    companion object {
        private const val TRIALS = 1000
        private const val MIN_BONUS = 400
        private const val MAX_BONUS = 600

        @BeforeClass
        @JvmStatic
        fun initRscm() {
            RSCM.init()
        }
    }
}
