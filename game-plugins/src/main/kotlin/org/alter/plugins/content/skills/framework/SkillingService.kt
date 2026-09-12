package org.alter.plugins.content.skills.framework

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gg.rsmod.util.ServerProperties
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.service.Service
import org.alter.rscm.RSCM.getRSCM
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * A [SkillLoot] whose string item id has been resolved to a cache item id.
 */
data class ResolvedSkillLoot(
    val itemId: Int,
    val spec: SkillLoot,
)

/**
 * A [SkillNode] with every RSCM string id resolved to a cache id.
 *
 * @param objectIds every object id that should trigger this node (the primary
 * [SkillNode.key] plus all [SkillNode.variants]).
 * @param depletedObjectId resolved [SkillNode.depletedKey], or `null` when the
 * object should simply be removed and respawned.
 */
class ResolvedSkillNode(
    val node: SkillNode,
    val objectIds: IntArray,
    val depletedObjectId: Int?,
    val loot: List<ResolvedSkillLoot>,
) {
    val levelRequired: Int get() = node.levelRequired
    val xp: Double get() = node.xp
    val actionTicks: Int get() = node.actionTicks
    val animation: Int get() = node.animation
    val respawnTicks: Int get() = node.respawnTicks
    val lifetimeTicks: Int get() = node.lifetimeTicks

    fun successChance(
        level: Int,
        low: Int = node.successLow,
        high: Int = node.successHigh,
    ): Double = node.successChance(level, low, high)
}

/**
 * Resolved, indexed view over the raw skilling JSON definitions.
 *
 * The repository re-validates every node (Gson can bypass the primary
 * constructor/`init` block), resolves string ids through the supplied `resolve`
 * function and builds the object-id lookup used by [gatherFromObjects].
 */
class SkillingRepository(
    definitions: Map<String, List<SkillNode>>,
    resolve: (String) -> Int,
) {
    private val byDefinition = LinkedHashMap<String, List<ResolvedSkillNode>>()
    private val byObjectId = Int2ObjectOpenHashMap<ResolvedSkillNode>()

    init {
        definitions.forEach { (definition, nodes) ->
            val resolved =
                nodes.map { node ->
                    node.validate()

                    val objectIds = (listOf(node.key) + node.variants).map(resolve).toIntArray()
                    val depletedObjectId = node.depletedKey?.let(resolve)
                    val loot = node.loot.map { ResolvedSkillLoot(resolve(it.item), it) }

                    ResolvedSkillNode(
                        node = node,
                        objectIds = objectIds,
                        depletedObjectId = depletedObjectId,
                        loot = loot,
                    ).also { resolvedNode ->
                        objectIds.forEach { objectId ->
                            val previous = byObjectId.put(objectId, resolvedNode)
                            require(previous == null) {
                                "Duplicate skilling object id $objectId across definitions."
                            }
                        }
                    }
                }
            byDefinition[definition] = resolved
        }
    }

    /** All loaded definition names (one per JSON file, without the extension). */
    val definitionNames: Set<String> get() = byDefinition.keys

    /** The resolved nodes for [definition], or an empty list when unknown. */
    fun nodes(definition: String): List<ResolvedSkillNode> = byDefinition[definition] ?: emptyList()

    /** The node bound to [objectId], or `null`. */
    fun nodeForObject(objectId: Int): ResolvedSkillNode? = byObjectId[objectId]
}

/**
 * Loads skilling node definitions from `data/cfg/skilling/`.
 *
 * Every `*.json` file in the directory is treated as one definition set, keyed
 * by its file name (without the `.json` extension); the mining pilot therefore
 * lives in `data/cfg/skilling/mining.json` and is retrieved with
 * `service.nodes("mining")`.
 */
class SkillingService : Service {

    private val gson = Gson()

    lateinit var repository: SkillingRepository
        private set

    override fun init(
        server: Server,
        world: World,
        serviceProperties: ServerProperties,
    ) {
        val directory = Paths.get(serviceProperties.get("skillingDirectory") ?: "../data/cfg/skilling")
        load(directory)

        Server.logger.info {
            "Loaded ${repository.definitionNames.size} skilling definition file(s) from $directory."
        }
    }

    /**
     * Reads and indexes every `*.json` file in [directory]. Exposed for tests;
     * [resolve] defaults to the production [getRSCM] lookup.
     */
    fun load(
        directory: Path,
        resolve: (String) -> Int = { getRSCM(it) },
    ) {
        val definitions = LinkedHashMap<String, List<SkillNode>>()

        if (Files.isDirectory(directory)) {
            Files.newDirectoryStream(directory, "*.json").use { files ->
                files
                    .sortedBy { it.fileName.toString() }
                    .forEach { file ->
                        val name = file.fileName.toString().removeSuffix(".json")
                        Files.newBufferedReader(file).use { reader ->
                            val type = object : TypeToken<List<SkillNode>>() {}.type
                            definitions[name] = gson.fromJson<List<SkillNode>>(reader, type) ?: emptyList()
                        }
                    }
            }
        }

        repository = SkillingRepository(definitions, resolve)
    }

    fun nodes(definition: String): List<ResolvedSkillNode> = repository.nodes(definition)

    fun nodeForObject(objectId: Int): ResolvedSkillNode? = repository.nodeForObject(objectId)
}
