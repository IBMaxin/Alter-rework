package org.alter.plugins.content.skills.slayer

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Indexing and integrity rules for [SlayerRepository], independent of the cache
 * and server bootstrap so the lookup behaviour can be verified in isolation.
 */
class SlayerRepositoryTest {
    private val npcIds =
        mapOf(
            "npc.goblin_1" to 100,
            "npc.goblin_2" to 101,
            "npc.rat" to 200,
            "npc.turael" to 900,
        )

    private fun repository(
        tasks: List<SlayerTaskEntry> = listOf(goblins()),
        masters: List<SlayerMasterEntry> = listOf(turael()),
    ) = SlayerRepository(tasks, masters) { npcIds.getValue(it) }

    private fun goblins() =
        SlayerTaskEntry(
            taskName = "goblins",
            categoryId = 2,
            npcIds = listOf("npc.goblin_1", "npc.goblin_2"),
            requiredLevel = 1,
            xp = 5.0,
            amountMin = 10,
            amountMax = 25,
            masters = listOf("npc.turael"),
        )

    private fun turael() =
        SlayerMasterEntry(
            npc = "npc.turael",
            level = 1,
            pointsPerTask = 0,
            tasks = listOf("goblins"),
        )

    @Test
    fun `looks a task up by name`() {
        assertEquals("goblins", repository().getTask("goblins")?.taskName)
    }

    @Test
    fun `returns null for an unknown task name`() {
        assertNull(repository().getTask("dragons"))
    }

    @Test
    fun `resolves task npc ids`() {
        val task = repository().getTask("goblins")!!
        assertEquals(listOf(100, 101), task.resolvedNpcIds.toList())
    }

    @Test
    fun `looks a task up by npc id`() {
        assertEquals("goblins", repository().getTaskForNpc(100)?.taskName)
        assertEquals("goblins", repository().getTaskForNpc(101)?.taskName)
    }

    @Test
    fun `looks a task up by category id`() {
        assertEquals("goblins", repository().getTaskById(2)?.taskName)
    }

    @Test
    fun `returns null for an unknown category id`() {
        assertNull(repository().getTaskById(999))
    }

    @Test
    fun `returns null for an npc with no task`() {
        assertNull(repository().getTaskForNpc(200))
    }

    @Test
    fun `resolves masters by npc id`() {
        val master = repository().getMaster(900)
        assertEquals("npc.turael", master?.npc)
        assertEquals(900, master?.npcId)
    }

    @Test
    fun `returns null for an unknown master npc`() {
        assertNull(repository().getMaster(1))
    }

    @Test
    fun `rejects a master that references an unknown task`() {
        assertFailsWith<IllegalArgumentException> {
            repository(masters = listOf(turael().copy(tasks = listOf("goblins", "dragons"))))
        }
    }

    @Test
    fun `rejects duplicate task names`() {
        assertFailsWith<IllegalArgumentException> {
            repository(tasks = listOf(goblins(), goblins().copy(categoryId = 3)))
        }
    }

    @Test
    fun `rejects duplicate category ids`() {
        assertFailsWith<IllegalArgumentException> {
            repository(tasks = listOf(goblins(), goblins().copy(taskName = "goblins_2")))
        }
    }

    @Test
    fun `rejects an invalid task even when constructed outside of init`() {
        val json =
            """
            [{"taskName":"goblins","categoryId":2,"npcIds":["npc.goblin_1"],"requiredLevel":1,
              "xp":5.0,"amountMin":100,"amountMax":25,"weight":1.0}]
            """.trimIndent()
        assertFailsWith<IllegalArgumentException> {
            val tasks: List<SlayerTaskEntry> =
                Gson().fromJson(json, object : TypeToken<List<SlayerTaskEntry>>() {}.type)
            repository(tasks = tasks)
        }
    }
}
