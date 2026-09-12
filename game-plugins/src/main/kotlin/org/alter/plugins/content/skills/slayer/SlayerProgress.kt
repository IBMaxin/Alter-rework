package org.alter.plugins.content.skills.slayer

/**
 * The outcome of crediting a Slayer kill toward a player's active task.
 */
data class SlayerKillResult(
    val xp: Double,
    val bonusXp: Double,
    val remaining: Int,
    val pointsAwarded: Int,
    val completed: Boolean,
)

/**
 * Pure kill-credit rules. The plugin layer is responsible for applying the
 * returned values to the player (xp, persisted attributes, messages).
 */
object SlayerProgress {
    /**
     * Resolves the credit for killing an NPC that maps to [killTask] while
     * [assigned] task is active with [remaining] kills left.
     *
     * Returns `null` when no credit is due: no active task, the killed NPC is
     * not part of the task, or the task is already complete.
     */
    fun onKill(
        assigned: SlayerTaskEntry?,
        killTask: SlayerTaskEntry?,
        remaining: Int,
        pointsPerTask: Int,
    ): SlayerKillResult? {
        if (assigned == null || killTask == null) {
            return null
        }
        if (assigned.categoryId != killTask.categoryId) {
            return null
        }
        if (remaining <= 0) {
            return null
        }

        val newRemaining = remaining - 1
        val completed = newRemaining == 0
        return SlayerKillResult(
            xp = assigned.xp,
            bonusXp = if (completed) assigned.bonusXp else 0.0,
            remaining = newRemaining,
            pointsAwarded = if (completed) pointsPerTask else 0,
            completed = completed,
        )
    }
}
