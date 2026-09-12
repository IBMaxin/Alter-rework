package org.alter.plugins.content.skills.mining

import dev.openrune.cache.CacheManager.getItem
import org.alter.api.EquipmentType
import org.alter.api.Skills
import org.alter.api.cfg.Animation
import org.alter.api.ext.*
import org.alter.game.model.entity.Player
import org.alter.game.model.item.Item
import org.alter.plugins.content.skills.framework.GatherAction
import org.alter.plugins.content.skills.framework.GatherActionResolver
import org.alter.plugins.content.skills.framework.ResolvedSkillNode

/**
 * A pickaxe tier as used for Mining.
 *
 * @param tier lowercase name prefix of the pickaxe item, e.g. `"rune"`.
 * @param miningLevel Mining level required to use the pickaxe.
 * @param actionTicks default cycles between two mining rolls.
 * @param animation mining animation played while wielding this pickaxe.
 */
data class Pickaxe(
    val tier: String,
    val miningLevel: Int,
    val actionTicks: Int,
    val animation: Int,
)

/**
 * The standard pickaxe tiers, ordered fastest first. Values come from the OSRS
 * wiki "Pickaxe" table (ticks between rolls and Mining level to use).
 *
 * The dragon/3rd age/infernal (2.83) and crystal (2.75) pickaxes roll 3 ticks
 * by default with a chance of a 2-tick roll; that random acceleration is not
 * modelled and the default 3 ticks is used.
 */
object Pickaxes {

    const val PICKAXE_CATEGORY = 67

    val ALL: List<Pickaxe> =
        listOf(
            Pickaxe("crystal", 71, 3, Animation.MINING_CRYSTAL_PICKAXE),
            Pickaxe("infernal", 61, 3, Animation.MINING_INFERNAL_PICKAXE),
            Pickaxe("3rd age", 61, 3, Animation.MINING_THIRDAGE_PICKAXE),
            Pickaxe("dragon", 61, 3, Animation.MINING_DRAGON_PICKAXE),
            Pickaxe("gilded", 41, 3, Animation.GILDED_PICKAXE_MINE),
            Pickaxe("rune", 41, 3, Animation.MINING_RUNE_PICKAXE),
            Pickaxe("adamant", 31, 4, Animation.MINING_ADAMANT_PICKAXE),
            Pickaxe("mithril", 21, 5, Animation.MINING_MITHRIL_PICKAXE),
            Pickaxe("black", 11, 5, Animation.MINING_BLACK_PICKAXE),
            Pickaxe("steel", 6, 6, Animation.MINING_STEEL_PICKAXE),
            Pickaxe("iron", 1, 7, Animation.MINING_IRON_PICKAXE),
            Pickaxe("bronze", 1, 8, Animation.MINING_BRONZE_PICKAXE),
        )

    /** The tier whose name prefix matches [name], or `null`. */
    fun forName(name: String): Pickaxe? = ALL.firstOrNull { name.startsWith(it.tier, ignoreCase = true) }

    /** The fastest tier among [names] that [miningLevel] is high enough to use. */
    fun best(
        miningLevel: Int,
        names: Iterable<String>,
    ): Pickaxe? {
        val present = names.mapNotNull(::forName).toHashSet()
        return ALL.firstOrNull { it in present && it.miningLevel <= miningLevel }
    }
}

/**
 * Resolves the pickaxe a player is mining with, using the best pickaxe in the
 * weapon slot or inventory that the player has the Mining level to use.
 *
 * Pickaxe detection uses the cache item category (67) rather than a name list so
 * that every variant (`or`, `uncharged`, ...) is handled; the tier itself is
 * matched from the cache item name.
 */
class MiningActionResolver : GatherActionResolver {

    override fun resolve(
        player: Player,
        node: ResolvedSkillNode,
    ): GatherAction? {
        val miningLevel = player.getSkills().getCurrentLevel(Skills.MINING)
        val names = pickaxeNames(player)
        val pickaxe = Pickaxes.best(miningLevel, names)

        if (pickaxe == null) {
            player.message(
                if (names.isEmpty()) {
                    "You need a pickaxe to mine this rock."
                } else {
                    "You do not have a pickaxe which you have the Mining level to use."
                },
            )
            return null
        }

        return GatherAction(pickaxe.actionTicks, pickaxe.animation)
    }

    private fun pickaxeNames(player: Player): List<String> {
        val names = mutableListOf<String>()
        player.getEquipment(EquipmentType.WEAPON)?.let { item -> pickaxeName(item)?.let { names += it } }
        player.inventory.filterNotNull().forEach { item -> pickaxeName(item)?.let { names += it } }
        return names
    }

    private fun pickaxeName(item: Item): String? {
        val def = getItem(item.id)
        if (def.category != Pickaxes.PICKAXE_CATEGORY || def.equipSlot != EquipmentType.WEAPON.id) {
            return null
        }
        if (def.noted || def.isPlaceholder) {
            return null
        }
        return def.name
    }
}
