package org.alter.plugins.content.skills.mining

import org.alter.api.Skills
import org.alter.api.ext.*
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository
import org.alter.plugins.content.skills.framework.SkillingService
import org.alter.plugins.content.skills.framework.gatherFromObjects

/**
 * Binds every rock in `data/cfg/skilling/mining.json` to the Mining skill via
 * the shared [gatherFromObjects] DSL.
 *
 * Mining is the pilot for the data-driven skilling framework. Node data was
 * sourced from the OSRS wiki; entries in `mining.json` carry a
 * `needsHumanVerification` list for values that could not be confirmed against
 * the cache (e.g. depleted rock ids, pickaxe-tier roll speed and animations).
 */
class MiningPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {

    init {
        loadService(SkillingService())

        onWorldInit {
            val service = world.getService(SkillingService::class.java) ?: return@onWorldInit
            gatherFromObjects(Skills.MINING, "mine", service.nodes("mining"))
        }
    }
}
