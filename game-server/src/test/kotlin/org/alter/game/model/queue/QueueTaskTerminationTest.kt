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
 * Characterization tests for the current (pre-fix) behavior of [QueueTask]
 * termination while the task is suspended.
 *
 * These tests intentionally pin the *existing defect*: a suspended queue body
 * that holds a pawn `FULL` lock and relies on a `finally` block to release it
 * will not run that `finally` block when the task is terminated, because
 * [QueueTask.terminate] clears the captured suspension continuation without
 * resuming it. The pawn is therefore left permanently locked.
 *
 * Do not "fix" these expectations here. They exist so the eventual engine fix
 * produces a visible, reviewable behavior change.
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
    fun `terminated suspended task holding FULL lock never runs finally so pawn stays locked`() {
        val world = newWorld()
        val player = newPlayer(world)

        var finallyRan = false

        player.queue {
            player.lock()
            try {
                // Suspend far beyond the single pumped cycle so the task stays
                // suspended until it is explicitly interrupted.
                wait(1_000_000)
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

        // Pump only what is needed to process the interruption.
        player.queues.cycle()

        // Current behavior: termination abandons the suspended continuation
        // without resuming it, so the finally-based lock release never executes.
        assertTrue(player.isLocked(), "current behavior: pawn remains locked after task termination")
        assertFalse(finallyRan, "current behavior: finally block never runs for a terminated suspended task")
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
        player.queues.cycle()

        assertFalse(
            postSuspensionRan,
            "current behavior: a terminated suspended task must not execute code after its suspension point",
        )
        assertTrue(player.isLocked(), "current behavior: lock held at suspension is never released")
        assertEquals(0, player.queues.size, "terminated task should be removed from the queue")
    }
}
