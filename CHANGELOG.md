# Changelog

## [Unreleased] - 2026-09-12

### Added
- **Slayer Tower drops** — per-monster drop tables for all 8 NPC types (crawling hand, banshee, infernal mage, bloodveld, aberrant spectre, gargoyle, nechryael, abyssal demon), authored with the `drops {}` DSL
- **`SlayerTowerDropsIntegrationTest`** — 9 cache-free tests validating registration, table shape, nested weighted tables (aberrant spectre herbs/seeds, nechryael seeds), and roll behaviour for the migrated Slayer Tower definitions
- **`SlayerTowerRegistryTest`** — cross-references every spawned NPC ID against the per-monster plugin registrations and resolves them through RSCM
- **Cow drops** — bones, cowhide, and raw beef, all guaranteed on every kill via the `drops {}` DSL, matching OSRS behaviour

### Changed
- **Slayer Tower loot migrated to the `drops {}` DSL** — the manual `onNpcDeath` loot blocks for all 8 monsters were replaced by declarative tables; each independent roll group is preserved as its own MAIN table and nested weighted tables cover the aberrant spectre herb/seed and nechryael seed rolls
- **Slayer Tower content split per monster** — `SlayerTowerCombatPlugin.kt` and `SlayerTowerDropsPlugin.kt` replaced by one self-contained plugin per monster under `content/npcs/slayer_tower/<monster>/`, each owning its combat definition, drop table, and `NPC_IDS` list
- **Bloodveld registration gap fixed** — bloodvelds `484-487` now all receive the Slayer 50 combat definition (previously only `484` was registered)
- `SlayerTowerSpawnPlugin` now exposes `SPAWNED_NPC_IDS` for cross-reference testing

## [0.0.5] - 2026-09-10

### Added
- **Slayer skill system** — full implementation with Duradel master, task assignment, kill tracking, streak/points, and task completion
- **Black demon combat definition** — NPC 1432 with enhanced loot table (noted drops, 50% bonus chance, sharks, prayer potions)
- **Slayer Tower spawns** — 5x `npc.black_demon_1432` in correct positions
- **XP rate modifier plugin** — configurable XP multiplier for all skills
- **`::slayer` teleport** — teleports to Slayer Tower
- **DumpSlayerCategories tool** — cache dumper for NPC slayer category data

### Changed
- Slayer Tower spawns unified to NPC ID 1432 (removed 4 legacy variants)
- Slayer task `black_demons` NPC pool reduced to single entry (`npc.black_demon_1432`)
- Black demon loot table: increased bonus chance to 50%, added noted sharks/prayer potions, increased quantities across all tiers

### Known Issues
- `World.setNpcDefaults()` does not propagate `NpcCombatDef.attack/strength/defence/magic/ranged` into `Npc.Stats` — combat skill levels remain 1 at runtime despite definition. HP, attack speed, species, bonuses, and sounds work correctly.
- `defence {}` DSL block in `NpcCombatDsl.kt:103-107` discards results — use `bonuses {}` instead
- NPC "Broke" debug message in `CombatPlugin.kt:128` fires when pathfinding fails (pre-existing)

## [0.0.5] - 2026-09-12

### Added
- **4 missing Slayer Tower tasks** — crawling hand (lvl 5), banshee (lvl 15), infernal mage (lvl 45), aberrant spectre (lvl 60) added to `tasks.json` and Duradel's task list
- **Slayer Tower combat definitions** — `SlayerTowerCombatPlugin.kt` with `slayerData{}` blocks for all 8 NPC types (crawling hand, banshee, infernal mage, bloodveld, aberrant spectre, gargoyle, nechryael, abyssal demon)
- **Floor-by-floor NPC spawns** — 37 spawns across 3 floors replacing 5x black_demon placeholders. Ground: crawling hands + banshees. 1st: bloodvelds + infernal mages + aberrant spectres. 2nd: gargoyles + nechryael + abyssal demons
- **Slayer Tower staircase handlers** — 6 object handlers (2114, 2118-2122) in `LadderPlugin.kt` for climb-up/climb-down between all floors
- **Test suite** — 30 new tests across 3 files: `SlayerTowerTasksTest` (8), `SlayerTowerCombatDefTest` (10), `SlayerTowerSpawnTest` (12)

### Changed
- Slayer Tower now fully populated with OSRS-accurate NPC distribution
- Staircase objects in Slayer Tower are now interactive (were previously "I can't reach that!")
