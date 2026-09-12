package org.alter.plugins.content.skills.woodcutting

import org.alter.api.Skills
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository
import org.alter.plugins.content.skills.framework.SkillingService
import org.alter.plugins.content.skills.framework.gatherFromObjects

/**
 * Binds every tree in `data/cfg/skilling/woodcutting.json` to the Woodcutting
 * skill via the shared [gatherFromObjects] DSL.
 *
 * Axes are resolved per attempt by [WoodcuttingActionResolver]; trees roll for a
 * log every 4 ticks and, unless they are felled after a single log (normal
 * trees), persist for their despawn lifetime before depleting into a stump.
 */
class WoodcuttingPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {

    init {
        loadService(SkillingService())

        onWorldInit {
            val service = world.getService(SkillingService::class.java) ?: return@onWorldInit
            gatherFromObjects(Skills.WOODCUTTING, "chop down", service.nodes("woodcutting"), WoodcuttingActionResolver())
        }
    }
}
