package org.alter.game.model.queue

import kotlinx.coroutines.Dispatchers
import org.alter.game.DevContext
import org.alter.game.GameContext
import org.alter.game.model.PlayerUID
import org.alter.game.model.Tile
import org.alter.game.model.World
import org.alter.game.model.entity.Player
import org.alter.game.saving.formats.SaveFormatType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Regression tests for [QueueTask] termination while the task is suspended.
 *
 * When a suspended task is terminated, its captured continuation is resumed
 * with an expected private termination signal. Kotlin then runs any `finally`
 * blocks (so cleanup such as releasing a pawn `FULL` lock happens) but does not
 * execute ordinary code after the suspension point.
 *
 * The tests are fully deterministic: no sleeping, no threads, no randomness,
 * no cache, no client/network. The queue is pumped manually with
 * `Player.queues.cycle()` and interruption uses the normal
 * `Pawn.interruptQueues()` path.
 */
class QueueTaskTerminationTest {
    private fun newWorld(): World {
        val gameContext =
            GameContext(
                initialLaunch = false,
                name = "queue-termination-test",
                revision = 228,
                saveFormat = SaveFormatType.JSON,
                cycleTime = 600,
                playerLimit = 10,
                home = Tile(0, 0, 0),
                skillCount = 25,
                npcStatCount = 10,
                runEnergy = false,
                gItemPublicDelay = 100,
                gItemDespawnDelay = 300,
                preloadMaps = false,
            )
        val devContext =
            DevContext(
                debugExamines = false,
                debugObjects = false,
                debugButtons = false,
                debugItemActions = false,
                debugMagicSpells = false,
                debugPackets = false,
            )
        return World(gameContext, devContext)
    }

    private fun newPlayer(world: World): Player {
        // Pawn.queue builds a CoroutineScope from the world dispatcher. The
        // queue body under test never dispatches, so an unconfined dispatcher
        // keeps execution on the calling thread and the test deterministic.
        world.coroutineDispatcher = Dispatchers.Unconfined
        return Player(world).apply { uid = PlayerUID("queue-termination-test-player") }
    }

    @Test
    fun `terminated suspended task runs finally, releases lock, and skips post-suspension code`() {
        val world = newWorld()
        val player = newPlayer(world)

        var finallyRan = false
        var postSuspensionRan = false

        player.queue {
            player.lock()
            try {
                // Suspend far beyond the single pumped cycle so the task stays
                // suspended until it is explicitly interrupted.
                wait(1_000_000)
                postSuspensionRan = true
            } finally {
                finallyRan = true
                player.unlock()
            }
        }

        // One cycle starts the task and leaves it suspended on the wait.
        player.queues.cycle()

        assertTrue(player.isLocked(), "precondition: suspended task holds the FULL lock")
        assertFalse(finallyRan, "precondition: suspension alone must not run the finally block")

        // Normal queue interruption path, e.g. Pawn.attack -> interruptQueues.
        player.interruptQueues()

        assertTrue(finallyRan, "finally must run when a suspended task is terminated")
        assertFalse(player.isLocked(), "finally must release the FULL lock held by the terminated task")
        assertFalse(postSuspensionRan, "ordinary code after the suspension point must not execute")
        assertEquals(0, player.queues.size, "terminated task should be removed from the queue")
    }

    @Test
    fun `terminated suspended task without finally does not resume post-suspension code`() {
        val world = newWorld()
        val player = newPlayer(world)

        var postSuspensionRan = false

        player.queue {
            player.lock()
            wait(1_000_000)
            postSuspensionRan = true
            player.unlock()
        }

        player.queues.cycle()

        assertTrue(player.isLocked(), "precondition: suspended task holds the FULL lock")

        player.interruptQueues()

        assertFalse(
            postSuspensionRan,
            "a terminated suspended task must not execute code after its suspension point",
        )
        assertTrue(player.isLocked(), "without a finally block the lock is never released")
        assertEquals(0, player.queues.size, "terminated task should be removed from the queue")
    }

    @Test
    fun `bulk termination keeps replacement task queued from terminated task cleanup`() {
        val world = newWorld()
        val player = newPlayer(world)

        var finallyRan = false
        var replacementRan = false

        player.queue {
            try {
                wait(1_000_000)
            } finally {
                finallyRan = true
                // Replacement work queued from cleanup must survive the
                // in-progress bulk termination.
                player.queue {
                    replacementRan = true
                }
            }
        }

        player.queues.cycle()
        assertEquals(1, player.queues.size, "precondition: one suspended task is queued")

        player.interruptQueues()

        assertTrue(finallyRan, "cleanup must run during bulk termination")
        assertEquals(1, player.queues.size, "replacement work queued from cleanup must not be cleared")

        player.queues.cycle()
        assertTrue(replacementRan, "replacement work must be runnable after bulk termination")
        assertEquals(0, player.queues.size, "replacement work should complete and be removed")
    }

    @Test
    fun `terminating a task twice runs finally exactly once`() {
        val world = newWorld()
        val player = newPlayer(world)

        var finallyRuns = 0
        var task: QueueTask? = null

        player.queue {
            task = this
            try {
                wait(1_000_000)
            } finally {
                finallyRuns++
            }
        }

        player.queues.cycle()
        assertTrue(task!!.suspended(), "precondition: task is suspended")

        task.terminate()
        task.terminate()

        assertEquals(1, finallyRuns, "finally must run exactly once for a terminated suspended task")

        player.queues.cycle()
        assertEquals(0, player.queues.size, "terminated task should be removed from the queue")
    }

    @Test
    fun `terminateAction runs once and its failure does not prevent finally cleanup`() {
        val world = newWorld()
        val player = newPlayer(world)

        var finallyRan = false
        var terminateActions = 0
        var task: QueueTask? = null

        player.queue {
            task = this
            terminateAction = {
                terminateActions++
                error("terminate action failure")
            }
            try {
                wait(1_000_000)
            } finally {
                finallyRan = true
            }
        }

        player.queues.cycle()
        assertTrue(task!!.suspended(), "precondition: task is suspended")

        task.terminate()
        task.terminate()

        assertTrue(finallyRan, "finally cleanup must run even when terminateAction fails")
        assertEquals(1, terminateActions, "terminateAction must run at most once")
    }
}
