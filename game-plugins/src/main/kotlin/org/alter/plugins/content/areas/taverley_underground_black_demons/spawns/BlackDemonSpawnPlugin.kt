package org.alter.plugins.content.areas.taverley_underground_black_demons.spawns

import org.alter.game.Server
import org.alter.game.model.Direction
import org.alter.game.model.Tile
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Black Demon spawns in the Taverley Underground Black Demon area.
 */
class BlackDemonSpawnPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {

    init {
        SPAWN_TILES.forEach { tile ->
            spawnNpc(
                npc = NPC_ID,
                tile = tile,
                walkRadius = WALK_RADIUS,
                direction = DIRECTION,
            )
        }
    }

    companion object {
        const val NPC_ID = "npc.black_demon_1432"

        val SPAWN_TILES =
            listOf(
                Tile(x = 2861, z = 9778, height = 0),
                Tile(x = 2866, z = 9778, height = 0),
                Tile(x = 2862, z = 9780, height = 0),
                Tile(x = 2868, z = 9778, height = 0),
                Tile(x = 2870, z = 9778, height = 0),
            )

        val SPAWNED_NPC_IDS = List(SPAWN_TILES.size) { NPC_ID }

        const val WALK_RADIUS = 0

        val DIRECTION = Direction.SOUTH
    }
}
