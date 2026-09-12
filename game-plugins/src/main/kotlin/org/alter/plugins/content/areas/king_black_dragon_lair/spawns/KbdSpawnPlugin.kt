package org.alter.plugins.content.areas.king_black_dragon_lair.spawns

import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * King Black Dragon lair spawns.
 */
class KbdSpawnPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        spawnNpc(npc = NPC_ID, x = 2274, z = 4698, walkRadius = 5)
    }

    companion object {
        const val NPC_ID = "npc.king_black_dragon"
        val SPAWNED_NPC_IDS = listOf(NPC_ID)
    }
}
