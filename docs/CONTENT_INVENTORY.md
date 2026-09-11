# Content Inventory — Alter-rework

> Auto-generated from codebase exploration. Paths relative to `game-plugins/src/main/kotlin/org/alter/plugins/content/`.

## Skills

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| Thieving — Stalls | `skills/thieving/stall/` | Implemented | 12 stall types in `data/cfg/thieving/stalls.json` |
| Thieving — Pickpocket | `skills/thieving/pickpocket/` | Implemented | Data in `data/cfg/thieving/pickpockets.json` |
| Thieving — Chests | `skills/thieving/chest/` | Implemented | Data in `data/cfg/thieving/chests.json`, includes trap mechanic |

## NPCs

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| Cow | `npcs/CowPlugin.kt` | Implemented | Combat def, Moo timer |
| Banker | `npcs/banker/BankerPlugin.kt` | Implemented | |
| Barrows Brothers | `npcs/barrows/` | Implemented | Ahrim, Dharok, Guthan, Karil, Torag, Verac — 6 files |
| King Black Dragon | `npcs/kbd/` | Implemented | `KbdCombatPlugin.kt`, `KbdConfigsPlugin.kt` |

## Combat

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| Core combat loop | `combat/CombatPlugin.kt` | Implemented | Melee/ranged/magic cycle |
| Combat configs | `combat/CombatConfigs.kt` | Implemented | Attack style, animations, XP mode |
| Melee formula | `combat/formula/MeleeCombatFormula.kt` | Implemented | |
| Ranged formula | `combat/formula/RangedCombatFormula.kt` | Implemented | |
| Magic formula | `combat/formula/MagicCombatFormula.kt` | Implemented | |
| Dragonfire formula | `combat/formula/DragonfireFormula.kt` | Implemented | |
| Melee strategy | `combat/strategy/MeleeCombatStrategy.kt` | Implemented | |
| Ranged strategy | `combat/strategy/RangedCombatStrategy.kt` | Implemented | Includes ammo/ and weapon/ subdirs |
| Magic strategy | `combat/strategy/MagicCombatStrategy.kt` | Implemented | Includes CombatSpell, CombatSpellsPlugin |
| Special attacks | `combat/specialattack/` | Partial | Abyssal bludgeon, abyssal dagger, AGS, dragon dagger |
| Ranged ammo | `combat/strategy/ranged/ammo/` | Implemented | Arrows, Bolts, Darts, Javelins, Knives |
| Ranged weapons | `combat/strategy/ranged/weapon/` | Implemented | Bows, BowType, CrossbowType |

## Commands

| Category | Location | Count | Notes |
|----------|----------|-------|-------|
| Admin | `commands/commands/admin/` | 11 | Broadcast, food, item, NPC, teleport, transmog, etc. |
| All players | `commands/commands/all/` | 3 | ColGrid, Teleports, Yell |
| Developer | `commands/commands/developer/` | 46 | Anim, bank, clip, emotes, find, interface, master, max, noclip, obj, reboot, setlvl, varbit, varp, etc. |
| Player | `commands/commands/player/` | 1 | EmptyPlugin |
| Utility | `commands/commands/` | 2 | GetdistPlugin, MemoryTestPlugin |

## Mechanics

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| NPC Aggro | `mechanics/aggro/NpcAggroPlugin.kt` | Implemented | |
| Appearance | `mechanics/appearance/` | Implemented | AppearanceInterfacePlugin, LoginAppearancePlugin |
| Bank PIN | `mechanics/bankpin/BankPinPlugin.kt` | Implemented | |
| Equipment | `mechanics/equipment/EquipmentPlugin.kt` | Implemented | |
| Multi-combat | `mechanics/multi/MultiwayCombatPlugin.kt` | Implemented | |
| NPC Random Walk | `mechanics/npcwalk/NpcRandomWalkPlugin.kt` | Implemented | |
| Poison | `mechanics/poison/` | Implemented | Poison.kt, PawnExt.kt, PoisonPluginPlugin.kt |
| Prayer | `mechanics/prayer/` | Implemented | Prayer.kt, PrayerGroup.kt, Prayers.kt, PrayersPlugin.kt |
| Run Energy | `mechanics/run/` | Implemented | RunEnergy.kt, RunEnergyPlugin.kt |
| Shops | `mechanics/shops/` | Implemented | CoinCurrency, ItemCurrency, ShopsPlugin |
| Skull Removal | `mechanics/skullremoval/SkullRemovalPlugin.kt` | Implemented | |
| Starter Kit | `mechanics/starter/StarterKitPlugin.kt` | Implemented | |
| Trading | `mechanics/trading/` | Implemented | TradingPlugin, TradeExt, impl/ subdir |
| Water | `mechanics/water/` | Implemented | WaterContainers, WaterPlugin, Waters |

## Interfaces

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| Bank | `interfaces/bank/` | Implemented | Bank, BankPlugin, BankTabs, BankTabsPlugin, CommandsPlugin, Extensions |
| Gameframe tabs | `interfaces/gameframe/tabs/` | Implemented | 12 tabs: account_management, character_summary, combat_options, emotes, friends_list, inventory, logout, magic, prayer, settings, skills, worn_equipment |
| Chat | `interfaces/gameframe/chat/` | Exists | Subdirectory present |
| XP Drops | `interfaces/gameframe/xpdrops/` | Exists | Subdirectory present |
| World Map | `interfaces/gameframe/world_map/` | Exists | Subdirectory present |
| Item Sets | `interfaces/itemsets/` | Implemented | ItemSets.kt, ItemsetsPlugin.kt |
| Tournament Supplies | `interfaces/tournament_supplies/` | Implemented | Tournament_Supplies.kt, TournamentSuppliesPlugin.kt |

## Items

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| Food/Eating | `items/consumables/food/` | Implemented | EatingPlugin, Food, Foods |
| Prayer Scrolls | `items/consumables/prayerscrolls/` | Implemented | PrayerScrollsPlugin |
| Teleport Tabs | `items/consumables/teletabs/` | Implemented | TeleportTabPlugin |
| Amulet of Glory | `items/amuletofglory/` | Implemented | AmuletOfGloryPlugin |
| Essence Pouch | `items/essencepouch/` | Implemented | EssencePouch, EssencePouchPlugin |
| Ancient Wyvern Shield | `items/ancient_wyvern_shield/` | Exists | Subdirectory |
| Dragon Pickaxe | `items/DragonPickaxePlugin.kt` | Implemented | |
| Dwarven Rock Cake | `items/dwarven_rock_cake/` | Exists | Subdirectory |
| Elemental Shield | `items/elemental_shield/` | Exists | Subdirectory |
| Looting Bag | `items/lootingbag/` | Exists | Subdirectory |
| Mind Shield | `items/mind_shield/` | Exists | Subdirectory |
| Mystery Box | `items/mystery_box/` | Exists | Subdirectory |
| Ring of Wealth | `items/ringofwealth/` | Exists | Subdirectory |
| Shattered Cane | `items/shattered_cane/` | Exists | Subdirectory |
| Spade | `items/spade/` | Exists | Subdirectory |

## Magic

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| Magic Spells | `magic/MagicSpells.kt` | Implemented | |
| Spell Metadata | `magic/SpellMetadata.kt` | Implemented | |
| Teleport Type | `magic/TeleportType.kt` | Implemented | |
| Teleport Spells | `magic/teleports/TeleportSpell.kt` | Implemented | |
| Pawn Extensions | `magic/PawnExt.kt` | Implemented | |

## Objects

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| Bank Booth | `objects/bankbooth/` | Exists | Subdirectory |
| Bookcase | `objects/bookcase/` | Exists | Subdirectory |
| Cabbage | `objects/cabbage/` | Exists | Subdirectory |
| Crates | `objects/crates/` | Exists | Subdirectory |
| Deposit Box | `objects/depositbox/` | Exists | Subdirectory |
| Ditch | `objects/ditch/` | Exists | Subdirectory |
| Door | `objects/door/` | Exists | Subdirectory |
| Gates | `objects/gates/` | Exists | Subdirectory |
| Hay | `objects/hay/` | Exists | Subdirectory |
| Ladder | `objects/ladder/` | Exists | Subdirectory |
| Sacks | `objects/sacks/` | Exists | Subdirectory |

## Areas

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| Lumbridge | `areas/lumbridge/` | Implemented | NPCs (11 + stores), objects (Alkharid gate), spawns |
| Thieving Test | `areas/thieving-test/` | Exists | Test spawn area |

## Weapons

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| Osmumten's Fang | `weapons/OsmumtensFangPlugin.kt` | Implemented | |

## Services (game-plugins)

| Feature | Location | Status | Notes |
|---------|----------|--------|-------|
| Market Value | `service/marketvalue/ItemMarketValueService.kt` | Exists | |
| REST API | `service/restapi/` | Exists | Auth, controllers, routes |
| World List | `service/worldlist/` | Exists | IO, model, WorldListService |
