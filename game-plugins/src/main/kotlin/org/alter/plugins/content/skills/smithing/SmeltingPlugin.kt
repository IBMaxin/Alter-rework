package org.alter.plugins.content.skills.smithing

import org.alter.api.Skills
import org.alter.api.cfg.Animation
import org.alter.api.ext.*
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.model.container.ItemContainer
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.Plugin
import org.alter.game.plugin.PluginRepository

/**
 * Binds the data-driven smelting recipes in `data/cfg/smithing/smelting.json` to
 * the supported furnaces using item-on-object interactions.
 *
 * This is the first Smithing vertical slice: a player uses copper ore or tin
 * ore on a furnace and, when both ores are held and the Smithing level is high
 * enough, one bronze bar is produced. The action is immediate; there is no
 * queue, delay, repeat loop, interface or Make-X selection.
 */
class SmeltingPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {

    init {
        loadService(SmeltingService())

        onWorldInit {
            val service = world.getService(SmeltingService::class.java) ?: return@onWorldInit
            bindFurnace(service)
        }
    }

    private fun bindFurnace(service: SmeltingService) {
        val bound = mutableSetOf<Pair<String, Int>>()
        FURNACES.forEach { furnace ->
            service.recipes.forEach { recipe ->
                recipe.inputs.forEach { input ->
                    // Guard against a duplicate (object, item) binding.
                    if (!bound.add(furnace to input.itemId)) {
                        return@forEach
                    }
                    onItemOnObj(obj = furnace, item = input.item) {
                        smelt(service, input.itemId)
                    }
                }
            }
        }
    }

    private fun Plugin.smelt(
        service: SmeltingService,
        usedItemId: Int,
    ) {
        val recipe = service.recipeForInput(usedItemId) ?: return
        val level = player.getSkills().getCurrentLevel(Skills.SMITHING)
        val inventory = ItemContainerSmeltInventory(player.inventory)

        when (val result = service.smelt(inventory, recipe, level)) {
            is SmeltResult.Success -> {
                player.animate(Animation.SMITHING_SMELT)
                player.addXp(Skills.SMITHING, result.recipe.experience)
                player.message("You smelt a ${result.recipe.name}.")
            }
            SmeltResult.MissingInputs ->
                player.message("You need the required ores to smelt a ${recipe.name}.")
            SmeltResult.InsufficientLevel ->
                player.message("You need a Smithing level of ${recipe.level} to smelt a ${recipe.name}.")
            SmeltResult.NotEnoughSpace ->
                player.message("You need some inventory space to smelt a ${recipe.name}.")
            SmeltResult.Failed ->
                player.message("You were unable to smelt a ${recipe.name}.")
        }
    }

    private companion object {
        val FURNACES = listOf("object.furnace", "object.furnace_16469")
    }
}

/**
 * Production [SmeltInventory] backed by a player's [ItemContainer].
 *
 * Removal and addition use the existing [ItemContainer] transaction methods so
 * their capacity and assurance semantics are preserved.
 */
class ItemContainerSmeltInventory(
    private val container: ItemContainer,
) : SmeltInventory {
    override fun count(itemId: Int): Int = container.getItemCount(itemId)

    override fun freeSlots(): Int = container.freeSlotCount

    override fun remove(
        itemId: Int,
        amount: Int,
    ): Boolean = container.remove(itemId, amount, assureFullRemoval = true).hasSucceeded()

    override fun add(
        itemId: Int,
        amount: Int,
    ): Boolean = container.add(itemId, amount, assureFullInsertion = true).hasSucceeded()
}
