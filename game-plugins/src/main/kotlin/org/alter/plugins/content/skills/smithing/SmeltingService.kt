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
 * The outcome of a smelting attempt.
 */
sealed interface SmeltResult {
    /**
     * The recipe completed: inputs were consumed and the output was added.
     */
    data class Success(val recipe: SmeltingRecipe) : SmeltResult

    /**
     * The player does not hold every input required by the recipe.
     */
    object MissingInputs : SmeltResult

    /**
     * The player's Smithing level is below the recipe requirement.
     */
    object InsufficientLevel : SmeltResult

    /**
     * The output could not be placed in the inventory. This is checked before
     * any input is consumed.
     */
    object NotEnoughSpace : SmeltResult

    /**
     * A container operation failed unexpectedly after inputs had been removed.
     * The consumed inputs have been restored.
     */
    object Failed : SmeltResult
}

/**
 * The minimal inventory view required by [SmeltingService.smelt].
 *
 * This is a deliberately small seam so the recipe transaction can be unit
 * tested without a cache: [ItemContainerSmeltInventory] backs it with the
 * player's real inventory at runtime, while tests can supply a fake. The
 * production behaviour (stacking, `assureFullRemoval`, etc.) still comes from
 * the existing [org.alter.game.model.container.ItemContainer] transactions.
 */
interface SmeltInventory {
    /** The total amount of [itemId] held, across all stacks. */
    fun count(itemId: Int): Int

    /** The number of empty slots available. */
    fun freeSlots(): Int

    /**
     * Removes exactly [amount] of [itemId].
     *
     * @return true only when the full [amount] was removed; implementations must
     * not partially remove.
     */
    fun remove(itemId: Int, amount: Int): Boolean

    /**
     * Adds exactly [amount] of [itemId].
     *
     * @return true only when the full [amount] was added.
     */
    fun add(itemId: Int, amount: Int): Boolean
}

/**
 * Loads the smithing recipes from `data/cfg/smithing/smelting.json`.
 *
 * The data file is intentionally separate from `data/cfg/skilling/`: the
 * skilling node framework is not involved in Smithing.
 */
class SmeltingService : Service {

    private val gson = Gson()

    val recipes: ObjectArrayList<SmeltingRecipe> = ObjectArrayList()

    private val recipesByInput: Int2ObjectOpenHashMap<SmeltingRecipe> = Int2ObjectOpenHashMap()

    override fun init(
        server: Server,
        world: World,
        serviceProperties: ServerProperties,
    ) {
        val file = Paths.get(serviceProperties.get("smelting") ?: "../data/cfg/smithing/smelting.json")
        load(file)

        Server.logger.info { "Loaded ${recipes.size} smelting recipe(s) from $file." }
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
            val type = object : TypeToken<List<SmeltingRecipe>>() {}.type
            val loaded: List<SmeltingRecipe> = gson.fromJson(reader, type) ?: emptyList()
            loaded.forEach { recipe ->
                recipe.validate()
                recipe.resolve(resolve)
                recipe.inputs.forEach { input ->
                    val previous = recipesByInput.put(input.itemId, recipe)
                    require(previous == null) { "Duplicate smelting input item id ${input.itemId}." }
                }
                recipes.add(recipe)
            }
        }
    }

    /** The recipe that consumes [itemId], or `null` when unknown. */
    fun recipeForInput(itemId: Int): SmeltingRecipe? = recipesByInput[itemId]

    /**
     * Performs [recipe] against [inventory].
     *
     * Inputs are validated and output capacity is verified before anything is
     * consumed. If an input removal or the output addition fails unexpectedly,
     * the inputs consumed so far are restored and [SmeltResult.Failed] is
     * returned.
     *
     * Capacity is checked against the current free slots before consumption, so
     * a completely full inventory is rejected even when consuming the inputs
     * would free a slot. This conservative check matches the existing
     * inventory-space checks used by other skills and never risks item loss.
     */
    fun smelt(
        inventory: SmeltInventory,
        recipe: SmeltingRecipe,
        smithingLevel: Int,
    ): SmeltResult {
        if (smithingLevel < recipe.level) {
            return SmeltResult.InsufficientLevel
        }

        if (recipe.inputs.any { inventory.count(it.itemId) < it.amount }) {
            return SmeltResult.MissingInputs
        }

        if (inventory.freeSlots() < recipe.outputAmount) {
            return SmeltResult.NotEnoughSpace
        }

        val removed = mutableListOf<SmeltingInput>()
        recipe.inputs.forEach { input ->
            if (!inventory.remove(input.itemId, input.amount)) {
                restore(inventory, removed)
                return SmeltResult.Failed
            }
            removed.add(input)
        }

        if (!inventory.add(recipe.outputItemId, recipe.outputAmount)) {
            restore(inventory, removed)
            return SmeltResult.Failed
        }

        return SmeltResult.Success(recipe)
    }

    private fun restore(
        inventory: SmeltInventory,
        removed: List<SmeltingInput>,
    ) {
        removed.asReversed().forEach { input -> inventory.add(input.itemId, input.amount) }
    }
}
