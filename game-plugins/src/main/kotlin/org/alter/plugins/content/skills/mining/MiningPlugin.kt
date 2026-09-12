package org.alter.plugins.content.skills.mining

import dev.openrune.cache.CacheManager.getObject
import org.alter.api.Skills
import org.alter.api.ext.*
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository
import org.alter.plugins.content.skills.framework.SkillingService
import org.alter.plugins.content.skills.framework.gatherFromObjects
import org.alter.rscm.RSCM.getRSCM

/**
 * Binds every rock in `data/cfg/skilling/mining.json` to the Mining skill via
 * the shared [gatherFromObjects] DSL.
 *
 * Mining is the pilot for the data-driven skilling framework. Node data was
 * sourced from the OSRS wiki; entries in `mining.json` carry a
 * `needsHumanVerification` list for values that could not be confirmed against
 * the cache (e.g. Mining Guild respawns, per-ore depleted variants).
 *
 * The pickaxe is resolved per attempt by [MiningActionResolver]; the depleted
 * rocks (object ids 11390-11392 and their Prifddinas/Varlamore counterparts)
 * are bound to the "no ore" message.
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
            gatherFromObjects(Skills.MINING, "mine", service.nodes("mining"), MiningActionResolver())
            bindDepletedRocks()
        }
    }

    private fun bindDepletedRocks() {
        DEPLETED_ROCKS.forEach { depleted ->
            val objectId = getRSCM(depleted)
            getObject(objectId).actions
                .filterNotNull()
                .filter { it.equals("mine", ignoreCase = true) }
                .forEach { option ->
                    onObjOption(obj = objectId, option = option) {
                        player.message("There is currently no ore available in this rock.")
                    }
                }
        }
    }

    private companion object {
        /**
         * The shared depleted/empty rock objects, confirmed from the cache as
         * category 227 "Rocks" (RuneLite `ROCKS1/2/3`, `PRIF_MINE_ROCKS1_EMPTY`
         * and the Varlamore empties).
         */
        val DEPLETED_ROCKS =
            listOf(
                "object.rocks_11390",
                "object.rocks_11391",
                "object.rocks_11392",
                "object.rocks_36202",
                "object.rocks_41549",
                "object.rocks_41550",
            )
    }
}
