package org.alter.plugins.content.skills.slayer

import org.alter.rscm.RSCM
import org.junit.BeforeClass
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Verifies the shipped Slayer JSON files load, resolve through RSCM and produce
 * a valid repository (every master references an existing task and every task's
 * npc ids resolve).
 */
class SlayerServiceLoadTest {
    @Test
    fun `loads the shipped slayer task and master definitions`() {
        val service = SlayerService()
        service.load(
            tasksFile = Paths.get("../data/cfg/slayer/tasks.json"),
            mastersFile = Paths.get("../data/cfg/slayer/masters.json"),
        )

        assertTrue(service.repository.tasks.isNotEmpty(), "expected at least one task")
        assertTrue(service.repository.masters.isNotEmpty(), "expected at least one master")
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun loadRscm() {
            RSCM.init()
        }
    }
}
