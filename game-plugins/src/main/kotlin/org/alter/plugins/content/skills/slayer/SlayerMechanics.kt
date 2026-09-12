package org.alter.plugins.content.skills.slayer

import kotlin.math.floor

/**
 * The outcome of rolling a new task for a player.
 */
data class SlayerAssignment(
    val task: SlayerTaskEntry,
    val amount: Int,
)

/**
 * Pure Slayer assignment rules. Randomness is injected as a `[0.0, 1.0)` roll so
 * the logic can be tested without a server, world or RNG.
 */
object SlayerMechanics {
    /**
     * Tasks that [master] can hand out and that the player is high enough Slayer
     * level to receive. Unresolvable task names are ignored.
     */
    fun eligibleTasks(
        master: SlayerMasterEntry,
        repository: SlayerRepository,
        slayerLevel: Int,
    ): List<SlayerTaskEntry> =
        master.tasks
            .mapNotNull { repository.getTask(it) }
            .filter { it.requiredLevel <= slayerLevel }

    /**
     * Weighted selection over [eligible]. [roll] must be in `[0.0, 1.0)`.
     */
    fun chooseTask(
        eligible: List<SlayerTaskEntry>,
        roll: Double,
    ): SlayerTaskEntry? {
        if (eligible.isEmpty()) {
            return null
        }
        val total = eligible.sumOf { it.weight }
        if (total <= 0.0) {
            return eligible.first()
        }
        val target = roll * total
        var cumulative = 0.0
        eligible.forEach { task ->
            cumulative += task.weight
            if (target < cumulative) {
                return task
            }
        }
        return eligible.last()
    }

    /**
     * Rolls the number of kills for [task]. [roll] must be in `[0.0, 1.0)`.
     */
    fun chooseAmount(
        task: SlayerTaskEntry,
        roll: Double,
    ): Int {
        if (task.amountMax <= task.amountMin) {
            return task.amountMin
        }
        val span = task.amountMax - task.amountMin + 1
        val offset = floor(roll * span).toInt().coerceIn(0, span - 1)
        return task.amountMin + offset
    }

    /**
     * Full assignment roll. Returns `null` if no task is eligible.
     */
    fun assign(
        master: SlayerMasterEntry,
        repository: SlayerRepository,
        slayerLevel: Int,
        taskRoll: Double,
        amountRoll: Double,
    ): SlayerAssignment? {
        val task = chooseTask(eligibleTasks(master, repository, slayerLevel), taskRoll) ?: return null
        return SlayerAssignment(task, chooseAmount(task, amountRoll))
    }
}
