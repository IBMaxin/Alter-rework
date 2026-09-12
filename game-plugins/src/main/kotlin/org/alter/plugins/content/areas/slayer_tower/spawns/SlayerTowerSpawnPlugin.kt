package org.alter.plugins.content.areas.slayer_tower.spawns

import org.alter.game.Server
import org.alter.game.model.Direction
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

class SlayerTowerSpawnPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {
    init {
        spawnNpc(npc = "npc.black_demon_1432", x = 3500, z = 3500, walkRadius = 5, direction = Direction.SOUTH)
        spawnNpc(npc = "npc.black_demon_1432", x = 3503, z = 3500, walkRadius = 5, direction = Direction.WEST)
        spawnNpc(npc = "npc.black_demon_1432", x = 3505, z = 3503, walkRadius = 5, direction = Direction.NORTH)
        spawnNpc(npc = "npc.black_demon_1432", x = 3502, z = 3505, walkRadius = 5, direction = Direction.EAST)
        spawnNpc(npc = "npc.black_demon_1432", x = 3498, z = 3502, walkRadius = 5, direction = Direction.SOUTH)
    }
}