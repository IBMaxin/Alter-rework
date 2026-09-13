package org.alter.plugins.content.skills.smithing

import org.alter.plugins.content.skills.framework.MAX_SKILL_LEVEL

/**
 * An anvil-forging recipe loaded from `data/cfg/smithing/anvil.json`.
 *
 * [objectKey], [objectVariants], [input], [tool] and [output] are RSCM string
 * ids; their resolved cache ids are populated by [AnvilService] after the
 * definition file has been loaded.
 *
 * [objectVariants] lists additional anvil object ids that share this recipe
 * (for example `object.rusted_anvil`); every id in `[objectKey] + variants` is
 * bound to the same recipe.
 */
data class AnvilRecipe(
    val name: String,
    val objectKey: String,
    val objectVariants: List<String> = emptyList(),
    val input: String,
    val inputAmount: Int = 1,
    val tool: String,
    val toolAmount: Int = 1,
    val output: String,
    val outputAmount: Int = 1,
    val level: Int,
    val experience: Double,
) {
    @Transient
    var objectIds: IntArray = IntArray(0)

    @Transient
    var inputItemId: Int = -1

    @Transient
    var toolItemId: Int = -1

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
        require(name.isNotBlank()) { "Anvil recipe name must not be blank." }
        require(objectKey.isNotBlank()) { "Anvil recipe object id must not be blank." }
        require(objectVariants.none { it.isBlank() }) { "Anvil recipe object variant ids must not be blank." }
        require(input.isNotBlank()) { "Anvil recipe input item id must not be blank." }
        require(inputAmount >= 1) { "Anvil recipe input amount must be at least 1, but was $inputAmount." }
        require(tool.isNotBlank()) { "Anvil recipe tool item id must not be blank." }
        require(toolAmount >= 1) { "Anvil recipe tool amount must be at least 1, but was $toolAmount." }
        require(output.isNotBlank()) { "Anvil recipe output item id must not be blank." }
        require(outputAmount >= 1) { "Anvil recipe output amount must be at least 1, but was $outputAmount." }
        require(level in 1..MAX_SKILL_LEVEL) {
            "Anvil recipe level must be between 1 and $MAX_SKILL_LEVEL, but was $level."
        }
        require(experience >= 0.0) { "Anvil recipe experience must not be negative, but was $experience." }
    }

    /**
     * Resolves [objectKey] and every [objectVariants] entry, plus [input],
     * [tool] and [output], through [resolve].
     */
    fun resolve(resolve: (String) -> Int) {
        objectIds = (listOf(objectKey) + objectVariants).map(resolve).toIntArray()
        inputItemId = resolve(input)
        toolItemId = resolve(tool)
        outputItemId = resolve(output)
    }
}
