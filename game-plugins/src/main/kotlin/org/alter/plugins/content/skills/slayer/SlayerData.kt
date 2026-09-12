package org.alter.plugins.content.skills.slayer

/**
 * Immutable configuration for a single Slayer task, loaded from
 * [data/cfg/slayer/tasks.json].
 *
 * A task maps one OSRS Slayer category (as exposed by the cache param
 * `ParamMapper.npc.PRIMARY_SLAYER_CATEGORY`) to the NPCs that satisfy it. The
 * [categoryId] is the stable identity stored on a player's active assignment.
 */
data class SlayerTaskEntry(
    val taskName: String,
    val categoryId: Int,
    val npcIds: List<String>,
    val requiredLevel: Int,
    val xp: Double,
    val bonusXp: Double = 0.0,
    val amountMin: Int,
    val amountMax: Int,
    val masters: List<String> = emptyList(),
    val weight: Double = 1.0,
) {
    @Transient
    var resolvedNpcIds: IntArray = intArrayOf()

    init {
        validate()
    }

    /**
     * Re-runs the configuration checks. Gson can instantiate this class without
     * invoking the primary constructor (and therefore the [init] block), so the
     * repository calls this after deserialising.
     */
    fun validate() {
        require(taskName.isNotBlank()) { "Slayer task name cannot be blank." }
        require(categoryId >= 0) { "Slayer task category id cannot be negative." }
        require(npcIds.isNotEmpty()) { "Slayer task must define at least one npc id." }
        require(requiredLevel >= 1) { "Slayer task level requirement must be >= 1." }
        require(xp >= 0.0) { "Slayer task experience cannot be negative." }
        require(bonusXp >= 0.0) { "Slayer task bonus experience cannot be negative." }
        require(amountMin >= 1) { "Slayer task minimum amount must be >= 1." }
        require(amountMax >= amountMin) { "Slayer task maximum amount cannot be less than the minimum." }
        require(weight > 0.0) { "Slayer task weight must be greater than 0." }
    }
}

/**
 * Immutable configuration for a Slayer master, loaded from
 * [data/cfg/slayer/masters.json].
 */
data class SlayerMasterEntry(
    val npc: String,
    val level: Int,
    val pointsPerTask: Int,
    val tasks: List<String>,
) {
    @Transient
    var npcId: Int = -1

    init {
        validate()
    }

    /**
     * Re-runs the configuration checks. See [SlayerTaskEntry.validate].
     */
    fun validate() {
        require(npc.isNotBlank()) { "Slayer master npc cannot be blank." }
        require(level >= 1) { "Slayer master level requirement must be >= 1." }
        require(pointsPerTask >= 0) { "Slayer master points per task cannot be negative." }
        require(tasks.isNotEmpty()) { "Slayer master must define at least one task." }
    }
}
