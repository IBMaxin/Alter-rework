package org.alter.plugins.content.skills.slayer

import org.alter.api.Skills
import org.alter.api.ext.message
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.model.attr.KILLER_ATTR
import org.alter.game.model.entity.Npc
import org.alter.game.model.entity.Player
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Credits NPC kills toward a player's active Slayer task: awards Slayer
 * experience, decrements the remaining count and completes the task for points.
 */
class SlayerKillPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {
    init {
        onAnyNpcDeath {
            val npc = ctx as? Npc ?: return@onAnyNpcDeath
            val killer = npc.attr[KILLER_ATTR]?.get() as? Player ?: return@onAnyNpcDeath
            val service = world.getService(SlayerService::class.java) ?: return@onAnyNpcDeath

            val assigned = killer.attr[SLAYER_TASK_ATTR]?.let { service.getTaskById(it) } ?: return@onAnyNpcDeath
            val killTask = service.getTaskForNpc(npc.id) ?: return@onAnyNpcDeath
            val remaining = killer.attr[SLAYER_REMAINING_ATTR] ?: 0
            val pointsPerTask =
                killer.attr[SLAYER_MASTER_ATTR]?.let { service.getMaster(it)?.pointsPerTask } ?: 0

            val result = SlayerProgress.onKill(assigned, killTask, remaining, pointsPerTask) ?: return@onAnyNpcDeath
            killer.addXp(Skills.SLAYER, result.xp + result.bonusXp)

            if (result.completed) {
                killer.attr.remove(SLAYER_TASK_ATTR)
                killer.attr.remove(SLAYER_REMAINING_ATTR)
                killer.attr.remove(SLAYER_MASTER_ATTR)
                if (result.pointsAwarded > 0) {
                    killer.attr[SLAYER_POINTS_ATTR] = (killer.attr[SLAYER_POINTS_ATTR] ?: 0) + result.pointsAwarded
                }
                killer.message("You have completed your Slayer task.")
            } else {
                killer.attr[SLAYER_REMAINING_ATTR] = result.remaining
            }
        }
    }
}
