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
- [ ] Fix combat skill levels not propagating to runtime stats

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

## Loot System — Content
- [x] **Add drops to CowPlugin** — moved to `drops { always { bones, cowhide, raw beef } }` in `setCombatDef`; manual `onNpcDeath` spawn block deleted to avoid double drops — 2026-09-12
- [ ] **Migrate BlackDemon drops to DSL** — currently manual `onNpcDeath` with a custom `Reward` class; move to `drops {}` and delete the manual spawn (do after engine hardening)
- [ ] **Uncomment KBD drops** — `KbdConfigsPlugin.kt:75-105` has a commented `drops {}` block using obsolete `Items.*` / old builder syntax; convert to RSCM string IDs and the current DSL
- [ ] **Add drops to Barrows brothers** — all 6 (Verac, Guthan, Torag, Dharok, Karil, Ahrim) have `setCombatDef` but no drops
- [ ] **Migrate Slayer Tower drops to DSL** — the 8 monsters spawn loot manually in `onNpcDeath`; convert each to `drops {}` once the engine supports their table shapes
- [ ] **Add rare drop table** — global table referenced by weight from individual NPC tables
- [ ] **Add herblore secondaries drop table**
- [ ] **Create `data/cfg/drops/` directory** — optional: move drop tables to JSON for a data-driven approach (like the thieving system)

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
