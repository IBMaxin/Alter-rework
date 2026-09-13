package org.alter.plugins.content.skills.smithing

import org.alter.plugins.content.skills.framework.MAX_SKILL_LEVEL

/**
 * A single item consumed when a [SmeltingRecipe] is performed.
 *
 * [item] is an RSCM string id (for example `"item.copper_ore"`); [itemId] is
 * populated by [SmeltingService] once the definition file has been loaded and
 * the RSCM name has been resolved.
 */
data class SmeltingInput(
    val item: String,
    val amount: Int = 1,
) {
    @Transient
    var itemId: Int = -1

    init {
        validate()
    }

    /**
     * Re-runs the configuration checks. Gson can instantiate this class without
     * invoking the primary constructor (and therefore the [init] block), so
     * callers must re-validate after deserialising.
     */
    fun validate() {
        require(item.isNotBlank()) { "Smelting input item id must not be blank." }
        require(amount >= 1) { "Smelting input amount must be at least 1, but was $amount." }
    }
}

/**
 * A smelting recipe loaded from `data/cfg/smithing/smelting.json`.
 *
 * [inputs] and [output] are RSCM string ids; [outputItemId] and every
 * [SmeltingInput.itemId] are populated by [SmeltingService] after loading.
 */
data class SmeltingRecipe(
    val name: String,
    val inputs: List<SmeltingInput>,
    val output: String,
    val outputAmount: Int = 1,
    val level: Int,
    val experience: Double,
) {
    @Transient
    var outputItemId: Int = -1

    init {
        validate()
    }

    /**
     * Re-runs the configuration checks. Gson can instantiate this class without
     * invoking the primary constructor (and therefore the [init] block), so
     * callers must re-validate after deserialising.
     */
    fun validate() {
        require(name.isNotBlank()) { "Smelting recipe name must not be blank." }
        require(inputs.isNotEmpty()) { "Smelting recipe must define at least one input." }
        require(output.isNotBlank()) { "Smelting recipe output item id must not be blank." }
        require(outputAmount >= 1) { "Smelting recipe output amount must be at least 1, but was $outputAmount." }
        require(level in 1..MAX_SKILL_LEVEL) {
            "Smelting recipe level must be between 1 and $MAX_SKILL_LEVEL, but was $level."
        }
        require(experience >= 0.0) { "Smelting recipe experience must not be negative, but was $experience." }
        inputs.forEach { it.validate() }
    }

    /**
     * Resolves [output] and every input [SmeltingInput.item] through [resolve].
     */
    fun resolve(resolve: (String) -> Int) {
        outputItemId = resolve(output)
        inputs.forEach { it.itemId = resolve(it.item) }
    }
}
