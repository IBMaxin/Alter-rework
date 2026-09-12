# Changelog

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
