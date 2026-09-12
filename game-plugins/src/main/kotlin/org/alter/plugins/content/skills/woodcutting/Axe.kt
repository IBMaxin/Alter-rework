package org.alter.plugins.content.skills.woodcutting

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
 * A woodcutting axe tier.
 *
 * @param tier lowercase name prefix of the axe item, e.g. `"rune"`.
 * @param woodcuttingLevel Woodcutting level required to use the axe.
 * @param successMultiplier multiplier applied to a tree's success parameters.
 * @param animation woodcutting animation played while chopping with this axe.
 */
data class Axe(
    val tier: String,
    val woodcuttingLevel: Int,
    val successMultiplier: Double,
    val animation: Int,
)

/**
 * The standard axe tiers, ordered best first.
 *
 * Level requirements and the relative log chance multipliers come from the OSRS
 * wiki "Axe" page; the per-tree success chart confirms the multipliers scale the
 * tree's `low`/`high` parameters (e.g. oak: bronze 32/100, iron 48/150, rune
 * 112/350, dragon 120/375).
 *
 * Gilded/3rd age/infernal/crystal values are not shown on the tree charts and
 * are estimated from the wiki relative-chance table; see the woodcutting data
 * file's `needsHumanVerification` entries.
 */
object Axes {

    const val AXE_CATEGORY = 35

    val ALL: List<Axe> =
        listOf(
            Axe("crystal", 71, 4.0, Animation.WOODCUTTING_CRYSTAL_AXE),
            Axe("infernal", 61, 3.75, Animation.WOODCUTTING_INFERNAL_AXE),
            Axe("3rd age", 61, 3.75, Animation.WOODCUTTING_THIRDAGE_AXE),
            Axe("dragon", 61, 3.75, Animation.WOODCUTTING_DRAGON_AXE),
            Axe("gilded", 41, 3.5, Animation.WOODCUTTING_RUNE_AXE),
            Axe("rune", 41, 3.5, Animation.WOODCUTTING_RUNE_AXE),
            Axe("adamant", 31, 3.0, Animation.WOODCUTTING_ADAMANT_AXE),
            Axe("mithril", 21, 2.5, Animation.WOODCUTTING_MITHRIL_AXE),
            Axe("black", 11, 2.25, Animation.WOODCUTTING_BLACK_AXE),
            Axe("steel", 6, 2.0, Animation.WOODCUTTING_STEEL_AXE),
            Axe("iron", 1, 1.5, Animation.WOODCUTTING_IRON_AXE),
            Axe("bronze", 1, 1.0, Animation.WOODCUTTING_BRONZE_AXE),
        )

    /** The tier whose name prefix matches [name], or `null`. */
    fun forName(name: String): Axe? = ALL.firstOrNull { name.startsWith(it.tier, ignoreCase = true) }

    /** The best tier among [names] that [woodcuttingLevel] is high enough to use. */
    fun best(
        woodcuttingLevel: Int,
        names: Iterable<String>,
    ): Axe? {
        val present = names.mapNotNull(::forName).toHashSet()
        return ALL.firstOrNull { it in present && it.woodcuttingLevel <= woodcuttingLevel }
    }
}

/**
 * Resolves the axe a player is chopping with, using the best axe in the weapon
 * slot or inventory that the player has the Woodcutting level to use.
 *
 * Matching uses the cache item category (35) plus the weapon equip slot so every
 * axe variant is handled; the tier is then matched from the cache item name.
 */
class WoodcuttingActionResolver : GatherActionResolver {

    override fun resolve(
        player: Player,
        node: ResolvedSkillNode,
    ): GatherAction? {
        val level = player.getSkills().getCurrentLevel(Skills.WOODCUTTING)
        val names = axeNames(player)
        val axe = Axes.best(level, names)

        if (axe == null) {
            player.message(
                if (names.isEmpty()) {
                    "You need an axe to chop down this tree."
                } else {
                    "You do not have an axe which you have the Woodcutting level to use."
                },
            )
            return null
        }

        return GatherAction(
            actionTicks = 4,
            animation = axe.animation,
            successMultiplier = axe.successMultiplier,
        )
    }

    private fun axeNames(player: Player): List<String> {
        val names = mutableListOf<String>()
        player.getEquipment(EquipmentType.WEAPON)?.let { item -> axeName(item)?.let { names += it } }
        player.inventory.filterNotNull().forEach { item -> axeName(item)?.let { names += it } }
        return names
    }

    private fun axeName(item: Item): String? {
        val def = getItem(item.id)
        if (def.category != Axes.AXE_CATEGORY || def.equipSlot != EquipmentType.WEAPON.id) {
            return null
        }
        if (def.noted || def.isPlaceholder) {
            return null
        }
        return def.name
    }
}
