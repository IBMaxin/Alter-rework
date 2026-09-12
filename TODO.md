# TODO

## High Priority
- [ ] Fix `World.setNpcDefaults()` — copy `NpcCombatDef` attack/strength/defence/magic/ranged into `Npc.Stats` at spawn time
- [ ] Fix `defence {}` DSL block in `NpcCombatDsl.kt:103-107` — currently discards result
- [ ] Replace or remove "Broke" forceChat in `CombatPlugin.kt:128` — it's a debug message for pathfinding failures

## Slayer
- [ ] Add more slayer masters (Vannaka, Chaeldar, Konar, Nieve, Duradel)
- [ ] Implement slayer points shop
- [ ] Add superior slayer monsters
- [ ] Add slayer unlockables (broad bolts, slayer ring, etc.)
- [ ] Add remaining slayer tasks (aberrant spectres, smoke devils, twisted jadinkos, etc.)
- [ ] Implement slayer helmet bonuses

## Black Demon / NPCs
- [ ] Verify all black demon variant IDs serve different purposes before permanent removal
- [ ] Add remaining demon variants with proper combat defs if needed
- [ ] Fix combat skill levels not propagating to runtime stats

## Loot System
- [ ] Migrate loot tables from inline to `data/cfg/` JSON (data-driven approach)
- [ ] Add rare drop table integration
- [ ] Add herblore secondaries drop table
- [ ] Add dragon chainbody/dragon platebody to rare table

## Combat
- [ ] Review pathfinding in Slayer Tower — demons getting stuck
- [ ] Add demon-specific combat animations (currently using generic `DEMON_DEATH=67`)
- [ ] Implement demon weakness (slash attacks in OSRS)

## Dev Tooling
- [ ] Add more cache dump tools (items, objects, animations)
- [ ] Auto-generate RSCM mappings from cache

## Polish
- [ ] Add examine text for black demons
- [ ] Add slayer task jingle on completion
- [ ] Add slayer XP drop styling
