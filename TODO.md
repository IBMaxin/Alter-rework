# TODO

## High Priority
- [x] Fix `World.setNpcDefaults()` — copies `NpcCombatDef` attack/strength/defence/magic/ranged into `Npc.Stats` via `Npc.Stats.applyCombatStats` — 2026-09-12
- [x] Fix `defence {}` DSL block in `NpcCombatDsl.kt` — dead builders and unused defence data types removed; use `bonuses {}` — 2026-09-12
- [x] Replace or remove "Broke" forceChat in `CombatPlugin.kt` — debug message removed — 2026-09-12

## Slayer
- [x] Add missing Slayer Tower tasks (crawling hand, banshee, infernal mage, aberrant spectre) — 2026-09-12
- [x] Add Slayer Tower combat definitions for all 8 NPC types — 2026-09-12
- [x] Populate Slayer Tower with floor-by-floor NPC spawns — 2026-09-12
- [x] Add Slayer Tower staircase handlers (objects 2114, 2118-2122) — 2026-09-12
- [x] Add Slayer Tower drops for all 8 NPC types — 2026-09-12
- [x] Split Slayer Tower combat/drops into one self-contained plugin per monster — 2026-09-12
- [x] Register bloodvelds 484-487 (previously only 484 had a combat def) — 2026-09-12
- [ ] **Fix staircase route-finding failure** — objects 2114/2118-2122 trigger "I can't reach that!" due to engine-level `ObjectPathAction.walkTo()` route-finding failure (`route.success=false`). Cache properties (`clipMask`/`clipType`/`impenetrable`) likely block pathfinding to the object tile. Needs diagnostic dump of `ObjectType` properties to confirm root cause — may require engine or cache override fix
- [ ] **Add spikey chain handler** — object 16537 (`spikey_chain`, has RSCM mapping) gets "Nothing interesting happens". Route-finding succeeds but no plugin bound. Add `onObjOption("object.spikey_chain", "climb-up")` handler
- [ ] **Add Slayer Tower door handlers** — objects 2111/2112/2113 need opened-variant IDs discovered at runtime before implementation
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
- [x] Fix combat skill levels not propagating to runtime stats — covered by `Npc.Stats.applyCombatStats` regression test — 2026-09-12

## Loot System — Critical Fixes
- [x] **Wire up `roll()` in `NpcDeathAction.kt`** — `roll` is called after `executeNpcDeath` and spawns `GroundItem`s with public/despawn delays and ownership — 2026-09-12
- [x] **Fix `handleToItem()` tile bug** — passes the NPC `tile` and `killer`; the `Tile(0, 0, 0)` default is gone — 2026-09-12
- [x] **Fix offensive error messages** — `LootTableBuilder.kt` messages rewritten professionally — 2026-09-12
- [x] **Remove dead `reroll` val** — removed — 2026-09-12
- [x] **Fix `preRoll()` logic** — weight is now a numerator out of 128 (`0` never succeeds, `128` always succeeds); validated at boot and roll time — 2026-09-12
- [x] **Implement `announce` and `description` fields** — consumed by `rareDropMessage`/`announceRareDrop`, gated by `game.yml` rare-drop announcements — 2026-09-12

## Loot System — Engine Hardening
- [x] **Support multiple tables per `TableType`** — each `always`/`main`/`preroll`/`tertiary` block now creates its own table and `roll()` resolves every table of a type; `LootTables` is now a `List` instead of a `Set` — 2026-09-12
- [x] **PRE_ROLL no longer replaces MAIN** — `roll()` rolls every PRE_ROLL table independently and always evaluates MAIN; a successful pre-roll no longer suppresses the main drop — 2026-09-12
- [x] **Nested / independent drop tables** — multiple same-type slots plus nested `LootTable`/`KFunction` entries now cover OSRS multi-slot layouts — 2026-09-12
- [x] **Fix `mainRoll()` inclusive bounds** — `mainRoll(rng)` now draws `1..tableWeight` (`rng.nextInt(tableWeight) + 1`); previously `random(tableWeight)` drew `0..tableWeight`, inflating the first entry's chance. Uniformity is locked by distribution tests — 2026-09-12

## Loot System — Content
- [x] **Add drops to CowPlugin** — moved to `drops { always { bones, cowhide, raw beef } }` in `setCombatDef`; manual `onNpcDeath` spawn block deleted to avoid double drops — 2026-09-12
- [x] **Migrate BlackDemon drops to DSL** — `BlackDemonPlugin` now uses `drops { always { … } main { … } }` with nested per-tier tables; manual `onNpcDeath`/`Reward` block deleted — 2026-09-12
- [x] **Uncomment KBD drops** — converted the commented block to RSCM string IDs and the current `drops {}` DSL in `KbdConfigsPlugin.kt` — 2026-09-12
- [x] **Barrows brothers have no individual drops (N/A)** — verified against the OSRS wiki: brothers drop nothing on death except a tertiary Brimstone key (Konar task only). All Barrows equipment comes from the Barrows chest reward system, which is not implemented. 2026-09-12
- [x] **Migrate Slayer Tower drops to DSL** — all 8 monsters converted from manual `onNpcDeath` loot blocks to `drops {}`; each independent roll group is preserved as its own MAIN table, with nested weighted tables for aberrant spectre herbs/seeds and nechryael seeds — 2026-09-12
- [x] **Drop-DSL migration complete** — Cow, Black Demon, KBD, and all 8 Slayer Tower monsters use `drops {}`; no manual `onNpcDeath` loot blocks remain in `game-plugins/` — 2026-09-12
- [ ] **Add rare drop table** — global table referenced by weight from individual NPC tables
- [ ] **Add herblore secondaries drop table**
- [ ] **Create `data/cfg/drops/` directory** — optional: move drop tables to JSON for a data-driven approach (like the thieving system)

## Combat
- [ ] Review pathfinding in Slayer Tower — demons getting stuck
- [ ] Add demon-specific combat animations (currently using generic `DEMON_DEATH=67`)
- [ ] Implement demon weakness (slash attacks in OSRS)

## Skilling Framework
- [x] Add `SkillNode`/`SkillLoot` data model + OSRS success formula (`SkillNodeData.kt`) — 2026-09-12
- [x] Add `SkillingService`/`SkillingRepository` — loads one JSON file per skill from `data/cfg/skilling/`, resolves RSCM ids, indexes by object id — 2026-09-12
- [x] Add `gatherFromObjects(skill, option, nodes)` DSL + generic gather loop with depletion/respawn — 2026-09-12
- [x] Add tests: `SkillNodeDataTest`, `SkillingServiceTest`, `GatherRollsTest`, `MiningDataLoadTest` (31 passing) — 2026-09-12
- [x] Mining pilot: `data/cfg/skilling/mining.json` (11 rocks) + `MiningPlugin` — 2026-09-12
- [ ] **NEEDS HUMAN VERIFICATION (Mining)** — each entry in `mining.json` carries a `needsHumanVerification` list:
  - depleted rock object ids are unverified (`depletedKey: null`; rock is removed and respawned instead)
  - `actionTicks` is fixed at 4 for every rock; OSRS roll speed varies by pickaxe tier (8 bronze .. ~2.75 crystal) and is not implemented
  - `animation` is fixed at the bronze pickaxe swing (625); per-pickaxe animation is not implemented
  - Mining Guild accelerated respawns (iron/adamantite/runite) are not modelled
  - `object.iron_rocks_42833` ("The Node", 0 XP past level 3) is intentionally excluded; `object.coal_rocks` (4676) binding needs confirmation
- [ ] Add remaining gathering skills (woodcutting, fishing, etc.) as JSON + plugin using the same framework

## Dev Tooling
- [ ] Add more cache dump tools (items, objects, animations)
- [ ] Auto-generate RSCM mappings from cache

## Polish
- [ ] Add examine text for black demons
- [ ] Add slayer task jingle on completion
- [ ] Add slayer XP drop styling
