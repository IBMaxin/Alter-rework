package org.alter.plugins.content.skills.smithing

import org.alter.api.Skills
import org.alter.api.cfg.Animation
import org.alter.api.ext.*
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.Plugin
import org.alter.game.plugin.PluginRepository

/**
 * Binds the data-driven anvil-forging recipe in `data/cfg/smithing/anvil.json`
 * to the supported anvil using an item-on-object interaction.
 *
 * This is the first anvil-smithing vertical slice: a player uses a bronze bar
 * on an anvil and, when a hammer is carried and the Smithing level is high
 * enough, one bronze dagger is produced. The action is immediate; there is no
 * queue, delay, repeat loop or interface.
 *
 * Inventory mutation is delegated to [AnvilService] through the existing
 * [SmeltInventory] seam so the same safe transaction semantics used by
 * [SmeltingService] are reused rather than duplicated.
 */
class AnvilPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {

    init {
        loadService(AnvilService())

        onWorldInit {
            val service = world.getService(AnvilService::class.java) ?: return@onWorldInit
            bindAnvil(service)
        }
    }

    private fun bindAnvil(service: AnvilService) {
        val bound = mutableSetOf<Pair<String, Int>>()
        for (recipe in service.recipes) {
            for (anvilKey in listOf(recipe.objectKey) + recipe.objectVariants) {
                // Guard against a duplicate (object, item) binding.
                if (!bound.add(anvilKey to recipe.inputItemId)) {
                    continue
                }
                onItemOnObj(obj = anvilKey, item = recipe.input) {
                    smith(service, recipe)
                }
            }
        }
    }

    private fun Plugin.smith(
        service: AnvilService,
        recipe: AnvilRecipe,
    ) {
        val level = player.getSkills().getCurrentLevel(Skills.SMITHING)
        val inventory = ItemContainerSmeltInventory(player.inventory)

        when (val result = service.smith(inventory, recipe, level)) {
            is AnvilResult.Success -> {
                player.animate(Animation.ANVIL_SMITH)
                player.addXp(Skills.SMITHING, result.recipe.experience)
                player.message("You smith a ${result.recipe.name}.")
            }
            AnvilResult.MissingInput ->
                player.message("You need a bronze bar to smith a ${recipe.name}.")
            AnvilResult.MissingTool ->
                player.message("You need a hammer to smith a ${recipe.name}.")
            AnvilResult.InsufficientLevel ->
                player.message("You need a Smithing level of ${recipe.level} to smith a ${recipe.name}.")
            AnvilResult.NotEnoughSpace ->
                player.message("You need some inventory space to smith a ${recipe.name}.")
            AnvilResult.Failed ->
                player.message("You were unable to smith a ${recipe.name}.")
        }
    }
}
