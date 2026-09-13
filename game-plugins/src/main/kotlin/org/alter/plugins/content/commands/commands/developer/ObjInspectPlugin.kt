package org.alter.plugins.content.commands.commands.developer

import dev.openrune.cache.CacheManager
import dev.openrune.cache.CacheManager.getObject
import dev.openrune.cache.CacheManager.getObjectOrDefault
import org.alter.api.ext.getCommandArgs
import org.alter.api.ext.message
import org.alter.api.ext.player
import org.alter.game.Server
import org.alter.game.model.EntityType
import org.alter.game.model.World
import org.alter.game.model.entity.GameObject
import org.alter.game.model.entity.Player
import org.alter.game.model.priv.Privilege
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * TEMPORARY DIAGNOSTIC — REMOVE AFTER STAGE 3
 *
 * Inspects object definitions at the player's tile and surrounding area.
 * Usage: ::objinspect [id1] [id2] ...
 */
class ObjInspectPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        onCommand("objinspect", Privilege.DEV_POWER, description = "Inspect object definitions (temporary diagnostic)") {
            val values = player.getCommandArgs()

            if (values.isEmpty()) {
                inspectTile(player)
            } else {
                for (idStr in values) {
                    val id = idStr.toIntOrNull()
                    if (id != null) {
                        printDef(player, id)
                    } else {
                        player.message("Invalid ID: $idStr")
                    }
                }
            }
        }
    }

    private fun inspectTile(p: Player) {
        val chunk = world.chunks.getOrCreate(p.tile)
        val objects = chunk.getEntities<GameObject>(
            p.tile,
            EntityType.STATIC_OBJECT,
            EntityType.DYNAMIC_OBJECT
        )

        if (objects.isEmpty()) {
            p.message("No objects found at your tile.")
            return
        }

        p.message("=== Objects at (${p.tile.x}, ${p.tile.z}, h${p.tile.height}) ===")
        for (obj in objects) {
            printObj(p, obj)
        }

        for (dx in -2..2) {
            for (dz in -2..2) {
                if (dx == 0 && dz == 0) continue
                val nearTile = p.tile.transform(dx, dz)
                val nearChunk = world.chunks.getOrCreate(nearTile)
                val nearObjs = nearChunk.getEntities<GameObject>(
                    nearTile,
                    EntityType.STATIC_OBJECT,
                    EntityType.DYNAMIC_OBJECT
                )
                for (obj in nearObjs) {
                    p.message("  NEAR (${nearTile.x}, ${nearTile.z}): [id=${obj.id}, type=${obj.type}, rot=${obj.rot}]")
                }
            }
        }
    }

    private fun printObj(p: Player, obj: GameObject) {
        val def = getObjectOrDefault(obj.id)
        val actions = def.actions.filterNotNull().filter { it.isNotBlank() }
        p.message("[id=${obj.id}, type=${obj.type}, rot=${obj.rot}] name=\"${def.name}\" size=${def.sizeX}x${def.sizeY}")
        p.message("  actions=$actions clipMask=${def.clipMask} impenetrable=${def.impenetrable} solid=${def.solid}")
        p.message("  obstructive=${def.obstructive} clipType=${def.clipType} varbitId=${def.varbitId} varpId=${def.varpId}")

        if (def.transforms != null && def.transforms!!.isNotEmpty()) {
            val transforms = def.transforms!!.filter { it != -1 }
            p.message("  transforms=$transforms")
            if (def.varbitId != -1) {
                try {
                    val varbitDef = CacheManager.getVarbit(def.varbitId)
                    p.message("  varbit: varp=${varbitDef.varp}, startBit=${varbitDef.startBit}, endBit=${varbitDef.endBit}")
                } catch (e: Exception) {
                    p.message("  varbit: could not resolve varbit ${def.varbitId}")
                }
            }
            if (def.varpId != -1) {
                p.message("  varp: ${def.varpId}")
            }
        }
    }

    private fun printDef(p: Player, id: Int) {
        try {
            val def = getObject(id)
            val actions = def.actions.filterNotNull().filter { it.isNotBlank() }
            p.message("=== Object $id ===")
            p.message("name=\"${def.name}\" size=${def.sizeX}x${def.sizeY}")
            p.message("actions=$actions")
            p.message("clipMask=${def.clipMask} impenetrable=${def.impenetrable} solid=${def.solid}")
            p.message("obstructive=${def.obstructive} clipType=${def.clipType}")
            p.message("varbitId=${def.varbitId} varpId=${def.varpId}")

            if (def.transforms != null && def.transforms!!.isNotEmpty()) {
                val transforms = def.transforms!!.filter { it != -1 }
                p.message("transforms=$transforms")
                if (def.varbitId != -1) {
                    try {
                        val varbitDef = CacheManager.getVarbit(def.varbitId)
                        p.message("varbit: varp=${varbitDef.varp}, startBit=${varbitDef.startBit}, endBit=${varbitDef.endBit}")
                    } catch (e: Exception) {
                        p.message("varbit: could not resolve varbit ${def.varbitId}")
                    }
                }
            }

            for (delta in listOf(-2, -1, 1, 2)) {
                val neighborId = id + delta
                val neighborDef = getObjectOrDefault(neighborId)
                if (neighborDef.name != null && neighborDef.name!!.contains("door", ignoreCase = true)) {
                    val neighborActions = neighborDef.actions.filterNotNull().filter { it.isNotBlank() }
                    p.message("  neighbor $neighborId: name=\"${neighborDef.name}\" actions=$neighborActions")
                }
            }
        } catch (e: Exception) {
            p.message("Object $id not found in cache.")
        }
    }
}
