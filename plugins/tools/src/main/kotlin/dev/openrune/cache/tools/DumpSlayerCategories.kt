package dev.openrune.cache.tools

import dev.openrune.cache.CacheManager
import dev.openrune.cache.filestore.definition.data.ParamMapper
import java.nio.file.Files
import java.nio.file.Path

private const val CACHE_REVISION = 228

fun main(args: Array<String>) {
    val cachePath = Path.of(args.getOrElse(0) { "data/cache" })
    val npcRscm = Path.of(args.getOrElse(1) { "data/cfg/rscm/npc.rscm" })

    CacheManager.init(cachePath, CACHE_REVISION)

    val namesById =
        Files.newBufferedReader(npcRscm).useLines { lines ->
            lines
                .mapNotNull { line ->
                    val parts = line.split(":")
                    if (parts.size == 2) parts[1].trim().toInt() to parts[0].trim() else null
                }.toMap()
        }

    CacheManager.getNpcs().entries
        .mapNotNull { (id, npc) ->
            val category = (npc.params?.get(ParamMapper.npc.PRIMARY_SLAYER_CATEGORY) as? Number)?.toInt() ?: return@mapNotNull null
            val name = namesById[id] ?: return@mapNotNull null
            SlayerNpc(category, "npc.$name", npc.combatLevel, npc.name)
        }.sortedWith(compareBy({ it.category }, { it.combatLevel }, { it.rscm }))
        .forEach { println("${it.category}\t${it.rscm}\t${it.combatLevel}\t${it.cacheName}") }
}

private data class SlayerNpc(
    val category: Int,
    val rscm: String,
    val combatLevel: Int,
    val cacheName: String,
)
