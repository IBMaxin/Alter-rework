# TODO

## High Priority
- [ ] Fix `World.setNpcDefaults()` — copy `NpcCombatDef` attack/strength/defence/magic/ranged into `Npc.Stats` at spawn time
- [ ] Fix `defence {}` DSL block in `NpcCombatDsl.kt:103-107` — currently discards result
- [ ] Replace or remove "Broke" forceChat in `CombatPlugin.kt:128` — it's a debug message for pathfinding failures

## Slayer
- [x] Add missing Slayer Tower tasks (crawling hand, banshee, infernal mage, aberrant spectre) — 2026-09-12
- [x] Add Slayer Tower combat definitions for all 8 NPC types — 2026-09-12
- [x] Populate Slayer Tower with floor-by-floor NPC spawns — 2026-09-12
- [x] Add Slayer Tower staircase handlers (objects 2114, 2118-2122) — 2026-09-12
- [ ] Add more slayer masters (Vannaka, Chaeldar, Konar, Nieve, Duradel)
- [ ] Implement slayer points shop
- [ ] Add superior slayer monsters
- [ ] Add slayer unlockables (broad bolts, slayer ring, etc.)
- [ ] Add remaining slayer tasks (smoke devils, twisted jadinkos, etc.)
- [ ] Implement slayer helmet bonuses

## Black Demon / NPCs
- [x] Replace black_demon_1432 placeholders with OSRS-accurate Slayer Tower population — 2026-09-12
- [ ] Verify all black demon variant IDs serve different purposes before permanent removal
- [ ] Add remaining demon variants with proper combat defs if needed
- [ ] Fix combat skill levels not propagating to runtime stats

## Loot System — Critical Fixes
- [ ] **Wire up `roll()` in `NpcDeathAction.kt`** — `roll` is imported (line 16) but never called; `NpcCombatDef.LootTables` is populated but never read during death. Add a step after `executeNpcDeath(npc)` (line 69) that calls `roll(killer, npc.combatDef.LootTables)` and spawns the returned `GroundItem`s
- [ ] **Fix `handleToItem()` tile bug** — `LootTableBuilder.kt:118` uses `Tile(0, 0, 0)` as default; must pass the NPC's actual `tile` and the `killer` player so items spawn at the correct location with the correct owner
- [ ] **Fix offensive error messages** — `LootTableBuilder.kt:85` (`"fix ur code idiot"`) and line 50 (`"Why be so retarded?"`) need professional replacements
- [ ] **Remove dead `reroll` val** — `LootTableBuilder.kt:113` is a top-level `val reroll: Boolean = false`, never used
- [ ] **Fix `preRoll()` logic** — `LootTableBuilder.kt:90` uses `Random.nextInt(loot.weight)` which treats weight as an upper bound, not a probability; semantics are inverted vs `mainRoll()`

## Loot System — Content
- [ ] **Uncomment KBD drops** — `KbdConfigsPlugin.kt:75-105` has a full `drops {}` DSL block but it's commented out and uses old `Items.*` constants; convert to RSCM string IDs and uncomment
- [ ] **Add drops to CowPlugin** — has `setCombatDef` but no drops (bones, cowhide, raw beef)
- [ ] **Add drops to Barrows brothers** — all 6 (Verac, Guthan, Torag, Dharok, Karil, Ahrim) have `setCombatDef` but no drops
- [ ] **Migrate BlackDemon drops to DSL** — currently uses manual `onNpcDeath` with custom `Reward` class; once `roll()` is wired up, convert to `drops {}` DSL in `setCombatDef` for consistency
- [ ] **Add rare drop table** — global table referenced by weight from individual NPC tables
- [ ] **Add herblore secondaries drop table**
- [ ] **Implement `announce` and `description` fields** — defined on `Loot` data class but never consumed; should broadcast rare drops and show drop notifications
- [ ] **Create `data/cfg/drops/` directory** — optional: move drop tables to JSON for data-driven approach (like thieving system)

## Combat
- [x] Add Slayer Tower staircase handlers — fixed "I can't reach that!" on all 6 staircases — 2026-09-12
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
