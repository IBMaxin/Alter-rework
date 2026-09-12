package org.alter.plugins.content.npcs.slayer_tower

import org.alter.api.*
import org.alter.api.cfg.*
import org.alter.api.dsl.*
import org.alter.api.ext.*
import org.alter.game.*
import org.alter.game.model.*
import org.alter.game.model.attr.*
import org.alter.game.model.container.*
import org.alter.game.model.container.key.*
import org.alter.game.model.entity.*
import org.alter.game.model.item.*
import org.alter.game.model.queue.*
import org.alter.game.model.shop.*
import org.alter.game.model.timer.*
import org.alter.game.plugin.*

/**
 * Combat definitions for all Slayer Tower NPCs.
 *
 * Each NPC receives a slayerData block so the existing Combat.kt gate
 * enforces the Slayer level requirement before combat can begin.
 */
class SlayerTowerCombatPlugin(
    r: PluginRepository,
    world: World,
    server: Server,
) : KotlinPlugin(r, world, server) {

    init {
        setCombatDef("npc.crawling_hand_453", "npc.crawling_hand_454") {
            configs {
                attackSpeed = 3
                respawnDelay = 25
            }
            aggro {
                radius = 4
                searchDelay = 1
            }
            stats {
                hitpoints = 13
                attack = 10
                strength = 10
                defence = 10
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 0
                defenceCrush = 0
                defenceMagic = 0
                defenceRanged = 0
            }
            anims {
                attack = Animation.CRAWLING_HAND_ATTACK
                block = Animation.CRAWLING_HAND_HIT
                death = Animation.CRAWLING_HAND_DEATH
            }
            slayerData {
                levelRequirement = 5
                xp = 12.0
            }
        }

        setCombatDef("npc.banshee_414") {
            configs {
                attackSpeed = 4
                respawnDelay = 25
            }
            aggro {
                radius = 4
                searchDelay = 1
            }
            stats {
                hitpoints = 22
                attack = 30
                strength = 25
                defence = 25
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 0
                defenceCrush = 0
                defenceMagic = 10
                defenceRanged = 0
            }
            anims {
                attack = Animation.BANSHEE_ATTACK
                block = Animation.BANSHEE_HIT
                death = Animation.BANSHEE_DEATH
            }
            sound {
                attackSound = Sound.BANSHEE_ATTACK
                deathSound = Sound.BANSHEE_DEATH
                blockSound = Sound.BANSHEE_HIT
            }
            slayerData {
                levelRequirement = 15
                xp = 22.0
            }
        }

        setCombatDef(
            "npc.infernal_mage_443", "npc.infernal_mage_444", "npc.infernal_mage_445",
            "npc.infernal_mage_446", "npc.infernal_mage_447",
        ) {
            configs {
                attackSpeed = 4
                respawnDelay = 25
            }
            aggro {
                radius = 4
                searchDelay = 1
            }
            stats {
                hitpoints = 64
                attack = 68
                strength = 65
                defence = 60
                magic = 80
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 0
                defenceCrush = 0
                defenceMagic = 20
                defenceRanged = 0
            }
            anims {
                attack = Animation.SPECTRE_ATTACK
                block = Animation.SPECTRE_HIT
                death = Animation.SPECTRE_DEATH
            }
            sound {
                attackSound = Sound.SPECTRE_ATTACK
                deathSound = Sound.SPECTRE_DEATH
                blockSound = Sound.SPECTRE_HIT
            }
            slayerData {
                levelRequirement = 45
                xp = 60.0
            }
        }

        setCombatDef("npc.bloodveld_484") {
            configs {
                attackSpeed = 4
                respawnDelay = 25
            }
            aggro {
                radius = 4
                searchDelay = 1
            }
            stats {
                hitpoints = 124
                attack = 96
                strength = 110
                defence = 100
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 20
                defenceCrush = 0
                defenceMagic = 0
                defenceRanged = 0
            }
            anims {
                attack = Animation.BLOODVELD_ATTACK
                block = Animation.BLOODVELD_HIT
                death = Animation.BLOODVELD_DEATH
            }
            sound {
                attackSound = Sound.BLOODVELD_ATTACK
                deathSound = Sound.BLOODVELD_DEATH
                blockSound = Sound.BLOODVELD_HIT
            }
            slayerData {
                levelRequirement = 50
                xp = 120.0
            }
        }

        setCombatDef(
            "npc.aberrant_spectre_2", "npc.aberrant_spectre_3", "npc.aberrant_spectre_4",
            "npc.aberrant_spectre_5", "npc.aberrant_spectre_6", "npc.aberrant_spectre_7",
        ) {
            configs {
                attackSpeed = 4
                respawnDelay = 25
            }
            aggro {
                radius = 4
                searchDelay = 1
            }
            stats {
                hitpoints = 96
                attack = 86
                strength = 84
                defence = 80
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 0
                defenceSlash = 0
                defenceCrush = 0
                defenceMagic = 30
                defenceRanged = 0
            }
            anims {
                attack = Animation.SPECTRE_ATTACK
                block = Animation.SPECTRE_HIT
                death = Animation.SPECTRE_DEATH
            }
            sound {
                attackSound = Sound.SPECTRE_ATTACK
                deathSound = Sound.SPECTRE_DEATH
                blockSound = Sound.SPECTRE_HIT
            }
            slayerData {
                levelRequirement = 60
                xp = 90.0
            }
        }

        setCombatDef("npc.gargoyle_412", "npc.gargoyle_413") {
            configs {
                attackSpeed = 4
                respawnDelay = 25
            }
            aggro {
                radius = 4
                searchDelay = 1
            }
            stats {
                hitpoints = 115
                attack = 111
                strength = 118
                defence = 120
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 40
                defenceSlash = 30
                defenceCrush = 20
                defenceMagic = 0
                defenceRanged = 30
            }
            anims {
                attack = Animation.GARGOYLE_ATTACK
                block = Animation.GARGOYLE_HIT
                death = Animation.GARGOYLE_DEATH
            }
            sound {
                attackSound = Sound.GARGOYLE_ATTACK
                deathSound = Sound.GARGOYLE_DEATH
                blockSound = Sound.GARGOYLE_HIT
            }
            slayerData {
                levelRequirement = 75
                xp = 105.0
            }
        }

        setCombatDef("npc.nechryael_8", "npc.nechryael_11") {
            configs {
                attackSpeed = 4
                respawnDelay = 25
            }
            aggro {
                radius = 4
                searchDelay = 1
            }
            stats {
                hitpoints = 175
                attack = 140
                strength = 150
                defence = 140
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 20
                defenceSlash = 20
                defenceCrush = 20
                defenceMagic = 0
                defenceRanged = 20
            }
            anims {
                attack = Animation.NECHRYAEL_ATTACK
                block = Animation.NECHRYAEL_HIT
                death = Animation.NECHRYAEL_DEATH
            }
            slayerData {
                levelRequirement = 80
                xp = 105.0
            }
        }

        setCombatDef("npc.abyssal_demon_415", "npc.abyssal_demon_416") {
            configs {
                attackSpeed = 4
                respawnDelay = 25
            }
            aggro {
                radius = 4
                searchDelay = 1
            }
            stats {
                hitpoints = 150
                attack = 138
                strength = 135
                defence = 140
                magic = 1
                ranged = 1
            }
            bonuses {
                defenceStab = 30
                defenceSlash = 30
                defenceCrush = 30
                defenceMagic = 0
                defenceRanged = 30
            }
            anims {
                attack = Animation.ABYSSAL_DEMON_ATTACK
                block = Animation.ABYSSAL_DEMON_HIT
                death = Animation.ABYSSAL_DEMON_DEATH
            }
            sound {
                attackSound = Sound.ABYSSAL_ATTACK
                deathSound = Sound.ABYSSAL_DEATH
                blockSound = Sound.ABYSSAL_HIT
            }
            slayerData {
                levelRequirement = 85
                xp = 150.0
            }
        }
    }
}
