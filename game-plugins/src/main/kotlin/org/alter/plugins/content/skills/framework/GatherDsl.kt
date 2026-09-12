package org.alter.plugins.content.skills.framework

import dev.openrune.cache.CacheManager.getItem
import dev.openrune.cache.CacheManager.getObject
import org.alter.api.Skills
import org.alter.api.ext.*
import org.alter.game.model.World
import org.alter.game.model.entity.DynamicObject
import org.alter.game.model.entity.GameObject
import org.alter.game.model.entity.Player
import org.alter.game.model.queue.QueueTask
import org.alter.game.plugin.KotlinPlugin

/**
 * The per-attempt parameters used by [gatherFromObject].
 *
 * @param actionTicks cycles between two gather rolls.
 * @param animation animation played while gathering; negative disables it.
 */
data class GatherAction(
    val actionTicks: Int,
    val animation: Int,
)

/**
 * Resolves the [GatherAction] for a player and node, or `null` when the player
 * cannot gather (for example, no usable tool). Implementations returning `null`
 * are responsible for sending any player feedback.
 */
fun interface GatherActionResolver {
    fun resolve(
        player: Player,
        node: ResolvedSkillNode,
    ): GatherAction?
}

/**
 * Binds every object id of [nodes] to a gather interaction on [option].
 *
 * Mirrors the thieving pattern: a content plugin loads its nodes through
 * [SkillingService] and passes them here inside `onWorldInit`. Only object
 * variants that actually expose [option] in the cache are bound, so a node's
 * alternate ids never fail the option lookup at boot.
 *
 * [actionResolver] lets a skill override the node's `actionTicks`/`animation`
 * per attempt (mining uses it to apply the equipped pickaxe's speed and
 * animation); by default the node's own values are used.
 */
fun KotlinPlugin.gatherFromObjects(
    skill: Int,
    option: String,
    nodes: Iterable<ResolvedSkillNode>,
    actionResolver: GatherActionResolver = DefaultGatherActionResolver,
) {
    nodes.forEach { node ->
        node.objectIds.forEach { objectId ->
            getObject(objectId).actions
                .filterNotNull()
                .filter { it.equals(option, ignoreCase = true) }
                .forEach { action ->
                    onObjOption(obj = objectId, option = action) {
                        val obj = player.getInteractingGameObj()
                        player.queue { gatherFromObject(this, player, obj, skill, node, actionResolver) }
                    }
                }
        }
    }
}

/** Uses the node's own `actionTicks` and `animation`. */
object DefaultGatherActionResolver : GatherActionResolver {
    override fun resolve(
        player: Player,
        node: ResolvedSkillNode,
    ): GatherAction = GatherAction(node.actionTicks, node.animation)
}

/**
 * The shared gather loop used by [gatherFromObjects].
 *
 * Repeats a single roll every [GatherAction.actionTicks] until the node is
 * depleted or the player stops interacting. A successful roll awards every
 * satisfied [SkillLoot] entry, grants xp and depletes the object; a failed roll
 * simply tries again.
 *
 * Clicking elsewhere or walking interrupts the queue through the engine, so no
 * explicit "player moved" check is required.
 */
suspend fun gatherFromObject(
    task: QueueTask,
    player: Player,
    obj: GameObject,
    skill: Int,
    node: ResolvedSkillNode,
    actionResolver: GatherActionResolver = DefaultGatherActionResolver,
) {
    val world = player.world
    val level = player.getSkills().getCurrentLevel(skill)
    val skillName = Skills.getSkillName(world, skill).lowercase()

    if (level < node.levelRequired) {
        player.message("You need a $skillName level of ${node.levelRequired} to do this.")
        return
    }

    if (!obj.isSpawned(world)) {
        return
    }

    val action = actionResolver.resolve(player, node) ?: return

    if (!canReceiveLoot(player, node)) {
        player.message("Your inventory is too full to hold any more.")
        return
    }

    player.faceTile(obj.tile)
    player.lock()
    try {
        if (action.animation >= 0) {
            player.animate(action.animation)
        }

        while (obj.isSpawned(world)) {
            task.wait(action.actionTicks)

            if (!obj.isSpawned(world)) {
                break
            }

            if (!GatherRolls.rollSuccess(node, level, world.randomDouble())) {
                continue
            }

            if (!canReceiveLoot(player, node)) {
                player.message("Your inventory is too full to hold any more.")
                break
            }

            GatherRolls.rollLoot(node) { world.randomDouble() }.forEach { (loot, amount) ->
                player.inventory.add(item = loot.itemId, amount = amount)
            }

            player.addXp(skill, node.xp)
            depleteObject(world, obj, node)
            break
        }
    } finally {
        player.unlock()
    }
}

/**
 * The pure, player-independent rolls behind the gather loop, exposed so they
 * can be unit tested without a cache or a live [World].
 */
object GatherRolls {

    /** `true` when [roll] (a value in `0.0..1.0`) succeeds against [node] at [level]. */
    fun rollSuccess(
        node: ResolvedSkillNode,
        level: Int,
        roll: Double,
    ): Boolean = roll < node.successChance(level)

    /**
     * Rolls every [ResolvedSkillNode.loot] entry independently. [random] must
     * return a value in `0.0..1.0`; the returned amount is inclusive of
     * `min..max`.
     */
    fun rollLoot(
        node: ResolvedSkillNode,
        random: () -> Double,
    ): List<Pair<ResolvedSkillLoot, Int>> {
        val results = mutableListOf<Pair<ResolvedSkillLoot, Int>>()
        node.loot.forEach { loot ->
            if (random() >= loot.spec.chance) {
                return@forEach
            }
            val min = loot.spec.min
            val max = loot.spec.max
            val amount =
                if (min == max) {
                    min
                } else {
                    (min + (random() * (max - min + 1)).toInt()).coerceAtMost(max)
                }
            results.add(loot to amount)
        }
        return results
    }
}

private fun canReceiveLoot(
    player: Player,
    node: ResolvedSkillNode,
): Boolean {
    if (!player.inventory.isFull) {
        return true
    }
    return node.loot.all { loot ->
        val def = getItem(loot.itemId)
        def.stackable && player.inventory.getItemCount(loot.itemId) > 0
    }
}

/**
 * Removes the mined object, optionally spawns its depleted variant, then
 * restores the original object after [ResolvedSkillNode.respawnTicks].
 */
private fun depleteObject(
    world: World,
    obj: GameObject,
    node: ResolvedSkillNode,
) {
    val tile = obj.tile
    val type = obj.type
    val rot = obj.rot
    val originalId = obj.id

    world.remove(obj)

    val depleted = node.depletedObjectId?.let { DynamicObject(id = it, type = type, rot = rot, tile = tile) }
    if (depleted != null) {
        world.spawn(depleted)
    }

    world.queue {
        wait(node.respawnTicks)
        if (depleted != null && world.isSpawned(depleted)) {
            world.remove(depleted)
        }
        world.spawn(DynamicObject(id = originalId, type = type, rot = rot, tile = tile))
    }
}
