package org.alter.plugins.content.skills.framework

const val MAX_SKILL_LEVEL = 99

/**
 * A single item entry that can be produced by a skilling node.
 *
 * @param item RSCM id of the produced item, e.g. `"item.copper_ore"`.
 * @param chance independent probability (0.0..1.0) that this entry is produced.
 */
data class SkillLoot(
    val item: String,
    val min: Int = 1,
    val max: Int = 1,
    val chance: Double = 1.0,
) {
    init {
        validate()
    }

    /**
     * Re-runs the configuration checks. Gson can instantiate this class without
     * invoking the primary constructor (and therefore the [init] block), so
     * callers must re-validate after deserialising.
     */
    fun validate() {
        require(item.isNotBlank()) { "Skill loot item id must not be blank." }
        require(min >= 1) { "Skill loot minimum amount must be at least 1, but was $min." }
        require(max >= min) { "Skill loot maximum amount ($max) must be at least the minimum ($min)." }
        require(chance in 0.0..1.0) { "Skill loot chance must be between 0 and 1, but was $chance." }
    }
}

/**
 * A gatherable/production node shared by skilling plugins. Data is intentionally
 * presentation-agnostic: a node may be bound to an object or an npc.
 *
 * A node may be reachable through several cache object ids (OSRS rocks have
 * many variants that share the same behaviour), so [key] is the primary RSCM id
 * and [variants] lists any additional RSCM ids that should behave identically.
 *
 * @param key RSCM id of the node, e.g. `"object.copper_rock"`.
 * @param successLow the OSRS "low" success parameter; success chance when the player is level 1.
 * @param successHigh the OSRS "high" success parameter; success chance when the player is level 99.
 * @param respawnTicks cycles before a depleted node restores; 0 disables depletion.
 * @param lifetimeTicks cycles a node survives after gathering begins before it
 * depletes; 0 depletes on the first successful roll (e.g. mining). Woodcutting
 * trees use their despawn time so several logs can be gathered per tree.
 * @param depletedKey optional RSCM id of the depleted object variant.
 * @param variants additional RSCM ids that resolve to the same node.
 */
data class SkillNode(
    val key: String,
    val levelRequired: Int,
    val xp: Double,
    val actionTicks: Int = 4,
    val animation: Int = -1,
    val successLow: Int = 256,
    val successHigh: Int = 256,
    val respawnTicks: Int = 0,
    val lifetimeTicks: Int = 0,
    val depletedKey: String? = null,
    val variants: List<String> = emptyList(),
    val loot: List<SkillLoot>,
) {
    init {
        validate()
    }

    /**
     * Re-runs the configuration checks. Gson can instantiate this class without
     * invoking the primary constructor (and therefore the [init] block), so
     * callers must re-validate after deserialising.
     */
    fun validate() {
        require(key.isNotBlank()) { "Skill node key must not be blank." }
        require(levelRequired in 1..MAX_SKILL_LEVEL) {
            "Skill node level requirement must be between 1 and $MAX_SKILL_LEVEL, but was $levelRequired."
        }
        require(xp >= 0.0) { "Skill node xp must not be negative, but was $xp." }
        require(actionTicks >= 1) { "Skill node action ticks must be at least 1, but was $actionTicks." }
        require(successLow >= 0) { "Skill node low success parameter must not be negative, but was $successLow." }
        require(successHigh >= 0) { "Skill node high success parameter must not be negative, but was $successHigh." }
        require(respawnTicks >= 0) { "Skill node respawn ticks must not be negative, but was $respawnTicks." }
        require(lifetimeTicks >= 0) { "Skill node lifetime ticks must not be negative, but was $lifetimeTicks." }
        require(depletedKey == null || depletedKey.isNotBlank()) { "Skill node depleted key must not be blank." }
        require(variants.all { it.isNotBlank() }) { "Skill node variants must not contain blank ids." }
        require(loot.isNotEmpty()) { "Skill node must define at least one loot entry." }
        loot.forEach { it.validate() }
    }

    /**
     * The OSRS skilling success chance, per
     * https://oldschool.runescape.wiki/w/Skilling_success_rate:
     *
     * `P(level) = (1 + floor(low * (99 - level) / 98 + high * (level - 1) / 98 + 0.5)) / 256`
     *
     * clamped to `0.0..1.0`. Levels outside `1..99` are clamped before evaluation.
     *
     * [low] and [high] default to this node's values but can be overridden so a
     * tool (for example a woodcutting axe) can scale the parameters for a roll.
     */
    fun successChance(
        level: Int,
        low: Int = successLow,
        high: Int = successHigh,
    ): Double {
        val clampLevel = level.coerceIn(1, MAX_SKILL_LEVEL)
        val scaled =
            low * (MAX_SKILL_LEVEL - clampLevel) / (MAX_SKILL_LEVEL - 1).toDouble() +
                high * (clampLevel - 1) / (MAX_SKILL_LEVEL - 1).toDouble()
        val numerator = 1 + Math.floor(scaled + 0.5).toInt()
        return (numerator / 256.0).coerceIn(0.0, 1.0)
    }
}
