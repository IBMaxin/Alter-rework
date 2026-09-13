package org.alter.plugins.content.skills.smithing

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gg.rsmod.util.ServerProperties
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.service.Service
import org.alter.rscm.RSCM.getRSCM
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * The outcome of an anvil-forging attempt.
 */
sealed interface AnvilResult {
    /**
     * The recipe completed: the input was consumed and the output was added.
     */
    data class Success(val recipe: AnvilRecipe) : AnvilResult

    /**
     * The player does not hold the required input item.
     */
    object MissingInput : AnvilResult

    /**
     * The player does not hold the required tool.
     */
    object MissingTool : AnvilResult

    /**
     * The player's Smithing level is below the recipe requirement.
     */
    object InsufficientLevel : AnvilResult

    /**
     * The output could not be placed in the inventory. This is checked before
     * any input is consumed.
     */
    object NotEnoughSpace : AnvilResult

    /**
     * A container operation failed unexpectedly after the input had been
     * removed. The consumed input has been restored.
     */
    object Failed : AnvilResult
}

/**
 * Loads the anvil-forging recipes from `data/cfg/smithing/anvil.json`.
 *
 * The transaction semantics mirror [SmeltingService] and reuse its
 * [SmeltInventory] seam, so the same `assureFullRemoval`/`assureFullInsertion`
 * guarantees apply and the recipe can be unit tested without a cache.
 */
class AnvilService : Service {

    private val gson = Gson()

    val recipes: ObjectArrayList<AnvilRecipe> = ObjectArrayList()

    private val recipesByInput: Int2ObjectOpenHashMap<AnvilRecipe> = Int2ObjectOpenHashMap()

    override fun init(
        server: Server,
        world: World,
        serviceProperties: ServerProperties,
    ) {
        val file = Paths.get(serviceProperties.get("anvil") ?: "../data/cfg/smithing/anvil.json")
        load(file)

        Server.logger.info { "Loaded ${recipes.size} anvil recipe(s) from $file." }
    }

    /**
     * Reads and indexes every recipe in [file]. Exposed for tests; [resolve]
     * defaults to the production [getRSCM] lookup.
     */
    fun load(
        file: Path,
        resolve: (String) -> Int = { getRSCM(it) },
    ) {
        recipes.clear()
        recipesByInput.clear()

        Files.newBufferedReader(file).use { reader ->
            val type = object : TypeToken<List<AnvilRecipe>>() {}.type
            val loaded: List<AnvilRecipe> = gson.fromJson(reader, type) ?: emptyList()
            loaded.forEach { recipe ->
                recipe.validate()
                recipe.resolve(resolve)
                val previous = recipesByInput.put(recipe.inputItemId, recipe)
                require(previous == null) { "Duplicate anvil input item id ${recipe.inputItemId}." }
                recipes.add(recipe)
            }
        }
    }

    /** The recipe that consumes [itemId], or `null` when unknown. */
    fun recipeForInput(itemId: Int): AnvilRecipe? = recipesByInput[itemId]

    /**
     * Performs [recipe] against [inventory].
     *
     * The tool is validated but never consumed. The input and output capacity
     * are validated before anything is consumed. If the input removal or the
     * output addition fails unexpectedly, the consumed input is restored and
     * [AnvilResult.Failed] is returned.
     *
     * Capacity is checked against the current free slots before consumption, so
     * a completely full inventory is rejected even when consuming the input
     * would free a slot. This conservative check matches [SmeltingService].
     */
    fun smith(
        inventory: SmeltInventory,
        recipe: AnvilRecipe,
        smithingLevel: Int,
    ): AnvilResult {
        if (smithingLevel < recipe.level) {
            return AnvilResult.InsufficientLevel
        }

        if (inventory.count(recipe.inputItemId) < recipe.inputAmount) {
            return AnvilResult.MissingInput
        }

        if (inventory.count(recipe.toolItemId) < recipe.toolAmount) {
            return AnvilResult.MissingTool
        }

        if (inventory.freeSlots() < recipe.outputAmount) {
            return AnvilResult.NotEnoughSpace
        }

        if (!inventory.remove(recipe.inputItemId, recipe.inputAmount)) {
            return AnvilResult.Failed
        }

        if (!inventory.add(recipe.outputItemId, recipe.outputAmount)) {
            inventory.add(recipe.inputItemId, recipe.inputAmount)
            return AnvilResult.Failed
        }

        return AnvilResult.Success(recipe)
    }
}
