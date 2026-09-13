package org.alter.plugins.content.skills.slayer

import org.alter.api.Skills
import org.alter.api.ext.*
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.model.entity.Player
import org.alter.game.model.queue.QueueTask
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Binds Slayer master conversations: assigning a new task when the player has
 * none, and reporting progress on an existing one.
 */
class SlayerPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {
    init {
        loadService(SlayerService())

        spawnNpc("npc.duradel_13622", 3222, 3217)

        onWorldInit {
            val service = world.getService(SlayerService::class.java) ?: return@onWorldInit
            service.repository.masters.forEach { master ->
                onNpcOption(master.npc, option = "talk-to") {
                    player.queue { dialog(player, master) }
                }
            }
        }
    }

    private suspend fun QueueTask.dialog(
        player: Player,
        master: SlayerMasterEntry,
    ) {
        val service = player.world.getService(SlayerService::class.java) ?: return
        val assigned = player.attr[SLAYER_TASK_ATTR]?.let { service.getTaskById(it) }

        if (assigned != null) {
            val remaining = player.attr[SLAYER_REMAINING_ATTR] ?: 0
            chatNpc(player, "You're still hunting ${assigned.displayName}; you have $remaining to go.")
            if (options(player, "Got any tips?", "Goodbye.") == 1) {
                chatNpc(player, "Kill $remaining more ${assigned.displayName} and return to me.")
            }
            return
        }

        chatNpc(player, "Hello, would you like a Slayer task?")
        when (options(player, "Yes please.", "No thanks.")) {
            1 -> assignTask(player, service, master)
            2 -> chatPlayer(player, "No thanks.")
        }
    }

    private fun assignTask(
        player: Player,
        service: SlayerService,
        master: SlayerMasterEntry,
    ) {
        val level = player.getSkills().getBaseLevel(Skills.SLAYER)
        val assignment =
            SlayerMechanics.assign(
                master = master,
                repository = service.repository,
                slayerLevel = level,
                taskRoll = player.world.randomDouble(),
                amountRoll = player.world.randomDouble(),
            )

        if (assignment == null) {
            player.message("You don't have any Slayer tasks available to you right now.")
            return
        }

        player.attr[SLAYER_TASK_ATTR] = assignment.task.categoryId
        player.attr[SLAYER_REMAINING_ATTR] = assignment.amount
        player.attr[SLAYER_MASTER_ATTR] = master.npcId
        player.message("Your new task is to kill ${assignment.amount} ${assignment.task.displayName}.")
    }
}
