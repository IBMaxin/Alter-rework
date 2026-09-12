package org.alter.plugins.content.skills.slayer

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
 * Resolved, indexed view over the raw Slayer JSON entries.
 *
 * The repository re-validates every entry (Gson can bypass the primary
 * constructor/`init` block), resolves RSCM string ids through [resolveNpc] and
 * builds the lookups used by the assignment and kill-credit logic.
 */
class SlayerRepository(
    val tasks: List<SlayerTaskEntry>,
    val masters: List<SlayerMasterEntry>,
    resolveNpc: (String) -> Int,
) {
    private val tasksByName = LinkedHashMap<String, SlayerTaskEntry>()
    private val tasksByCategoryId = Int2ObjectOpenHashMap<SlayerTaskEntry>()
    private val tasksByNpcId = Int2ObjectOpenHashMap<SlayerTaskEntry>()
    private val mastersByNpcId = Int2ObjectOpenHashMap<SlayerMasterEntry>()

    init {
        tasks.forEach { task ->
            task.validate()
            require(tasksByName.put(task.taskName, task) == null) { "Duplicate slayer task: ${task.taskName}." }
            require(tasksByCategoryId.put(task.categoryId, task) == null) {
                "Duplicate slayer task category id: ${task.categoryId}."
            }

            task.resolvedNpcIds = task.npcIds.map(resolveNpc).toIntArray()
            task.resolvedNpcIds.forEach { npcId -> tasksByNpcId[npcId] = task }
        }

        masters.forEach { master ->
            master.validate()
            master.tasks.forEach { taskName ->
                require(tasksByName.containsKey(taskName)) {
                    "Slayer master ${master.npc} references unknown task: $taskName."
                }
            }
            master.npcId = resolveNpc(master.npc)
            mastersByNpcId[master.npcId] = master
        }
    }

    fun getTask(name: String): SlayerTaskEntry? = tasksByName[name]

    fun getTaskById(categoryId: Int): SlayerTaskEntry? = tasksByCategoryId[categoryId]

    fun getTaskForNpc(npcId: Int): SlayerTaskEntry? = tasksByNpcId[npcId]

    fun getMaster(npcId: Int): SlayerMasterEntry? = mastersByNpcId[npcId]
}

/**
 * Loads [data/cfg/slayer/tasks.json] and [data/cfg/slayer/masters.json] so that
 * Slayer definitions can be retrieved at runtime.
 */
class SlayerService : Service {
    private val gson = Gson()

    lateinit var repository: SlayerRepository
        private set

    override fun init(
        server: Server,
        world: World,
        serviceProperties: ServerProperties,
    ) {
        val tasksFile = Paths.get(serviceProperties.get("slayerTasks") ?: "../data/cfg/slayer/tasks.json")
        val mastersFile = Paths.get(serviceProperties.get("slayerMasters") ?: "../data/cfg/slayer/masters.json")
        load(tasksFile, mastersFile)

        Server.logger.info {
            "Loaded ${repository.tasks.size} slayer task definitions and ${repository.masters.size} slayer masters."
        }
    }

    /**
     * Reads and indexes the Slayer definitions from [tasksFile] and
     * [mastersFile]. Exposed for tests.
     */
    fun load(
        tasksFile: Path,
        mastersFile: Path,
    ) {
        val tasks =
            Files.newBufferedReader(tasksFile).use { reader ->
                val type = object : TypeToken<List<SlayerTaskEntry>>() {}.type
                gson.fromJson<List<SlayerTaskEntry>>(reader, type)
            }
        val masters =
            Files.newBufferedReader(mastersFile).use { reader ->
                val type = object : TypeToken<List<SlayerMasterEntry>>() {}.type
                gson.fromJson<List<SlayerMasterEntry>>(reader, type)
            }

        repository = SlayerRepository(tasks, masters) { getRSCM(it) }
    }

    fun getTask(name: String): SlayerTaskEntry? = repository.getTask(name)

    fun getTaskById(categoryId: Int): SlayerTaskEntry? = repository.getTaskById(categoryId)

    fun getTaskForNpc(npcId: Int): SlayerTaskEntry? = repository.getTaskForNpc(npcId)

    fun getMaster(npcId: Int): SlayerMasterEntry? = repository.getMaster(npcId)
}
