# Architecture — Alter-rework

## Overview

Alter-rework is a Kotlin OSRS private server (RSMod fork) targeting cache revision 228. The server boots, loads the OSRS cache, discovers content plugins via classpath scanning, registers all bindings, then listens for client connections on a configurable port (default 43594).

## Module Dependency Graph

```
game-server/  ←── game-api/
    ↑              ↑
    │              │
game-plugins/ ─────┘
    ↑
plugins/ (filestore, rscm, tools)
util/ (shared utilities)
```

- `game-server` depends on `util`, `plugins/filestore`, `plugins/rscm`, `plugins/tools`
- `game-api` depends on `game-server`, `util`, `plugins/filestore`, `plugins/rscm`
- `game-plugins` depends on `game-server`, `game-api`, `util`, `plugins/filestore`, `plugins/rscm`

## game-server/ — Engine Core

**Purpose**: Server lifecycle, world simulation, plugin system, networking, player/NPC models.

**Key classes** (all under `org.alter.game`):

| Class | File | Role |
|-------|------|------|
| `Launcher` | `Launcher.kt` | JVM entry point. Constructs `Server`, calls `startServer` then `startGame` |
| `Server` | `Server.kt` | Boot sequence: load config → cache → services → plugins → network |
| `GameContext` | `GameContext.kt` | Immutable config: name, revision, home tile, cycle time, skill count |
| `DevContext` | `DevContext.kt` | Debug flags: debug-objects, debug-packets, etc. |
| `World` | `model/World.kt` | Game world: entities, chunks, services, plugins, collision, queue |
| `PluginRepository` | `plugin/PluginRepository.kt` | Central plugin registry: discovers plugins, stores bindings, dispatches events |
| `KotlinPlugin` | `plugin/KotlinPlugin.kt` | Plugin author API: `onObjOption`, `onNpcOption`, `onCommand`, `loadService`, etc. |
| `Service` | `service/Service.kt` | Interface for boot-time services: `init`, `postLoad`, `bindNet`, `terminate` |
| `GameService` | `service/GameService.kt` | Likely the 600ms tick loop (NEEDS HUMAN VERIFICATION) |

**Boot sequence** (from `Server.startGame`):
1. Load `game.yml` + `dev-settings.yml` into `ServerProperties`
2. Build `GameContext` from game.yml values
3. `PlayerDetails.init` + `PlayerSaving.init`
4. `CacheManager.init(filestore, revision)` — loads OSRS cache
5. Construct `World(gameContext, devContext)`
6. `ObjectExamineHolder.load()`
7. `world.loadServices()` — instantiate YAML-declared services
8. Build network: `CacheJs5GroupProvider`, `NetworkServiceFactory`
9. `world.init()`
10. `RSCM.init()` — string→int ID resolver
11. `world.privileges.load()`
12. `world.plugins.init()` — classpath scan → plugin instantiation → binding registration
13. `world.postLoad()`
14. `world.bindServices()` + `world.network.start()`

## game-api/ — Generated Definitions

**Purpose**: Entity definitions auto-generated from cache data, plus API types and extensions.

Key contents: `Skills.kt`, `EquipmentType.kt`, `WeaponType.kt`, `ChatMessageType.kt`, `InterfaceDestination.kt`, `cfg/` package (Animation, Varp, Varbit, Sound constants), `dsl/` (builder helpers), `ext/` (extension functions on Player/Npc).

## game-plugins/ — Content

**Purpose**: All game content. Organized by feature category.

### Content Categories

| Category | Path | Contents |
|----------|------|----------|
| Skills | `content/skills/thieving/` | Stall, pickpocket, chest thieving |
| NPCs | `content/npcs/` | Cow, banker, barrows brothers, KBD |
| Combat | `content/combat/` | CombatPlugin, CombatConfigs, formulas (melee/ranged/magic), special attacks, strategy classes |
| Commands | `content/commands/commands/` | admin/ (11), all/ (3), developer/ (46), player/ (1) |
| Mechanics | `content/mechanics/` | aggro, appearance, bankpin, equipment, multi-combat, npc walk, poison, prayer, run energy, shops, skull removal, starter kit, trading, water |
| Interfaces | `content/interfaces/` | bank, gameframe tabs (12), item sets, tournament supplies |
| Items | `content/items/` | consumables (food, prayer scrolls, teleport tabs), amulet of glory, essence pouch, looting bag, mystery box, etc. |
| Magic | `content/magic/` | MagicSpells, teleports, spell metadata |
| Objects | `content/objects/` | bank booth, bookcase, cabbage, crates, deposit box, ditch, door, gates, hay, ladder, sacks |
| Areas | `content/areas/` | lumbridge (NPCs, objects, spawns), thieving-test |
| Weapons | `content/weapons/` | Osmumten's Fang |

### Plugin Registration Flow

1. Plugin extends `KotlinPlugin(r, world, server)`
2. `init {}` block calls registration helpers:
   - `loadService(MyService())` — queue service for init
   - `onWorldInit { ... }` — deferred binding after world loads
   - `onObjOption(obj, option) { ... }` — bind object click
   - `onNpcOption(npc, option) { ... }` — bind NPC click
   - `onCommand(cmd) { ... }` — bind chat command
   - `onTimer(key) { ... }` — bind timer expiry
   - `onLogin { ... }` / `onLogout { ... }` — player lifecycle hooks
   - `setCombatDef(npc) { ... }` — NPC combat definition
   - `createShop(name) { ... }` — shop creation
   - `spawnNpc(npc, x, z)` — NPC spawn

## plugins/ — Dev Tooling

**Purpose**: Separate Gradle modules for development utilities.

- `plugins/filestore/` — OSRS cache filestore tools (used by game-server and game-api)
- `plugins/rscm/` — RSCM string→int ID resolver
- `plugins/tools/` — Additional development tools

## util/ — Shared Utilities

**Purpose**: Utility classes shared across modules (`gg.rsmod.util.*`). Includes `ServerProperties` (YAML config loader), `Stopwatch`, etc. Planned to eventually merge into game-server.

## Key Systems

### Data-Driven Content Pattern

For data-heavy features (thieving, shops, etc.):
1. Define JSON schema in `data/cfg/`
2. Create data classes in `*Data.kt` with validation
3. Create `*Service.kt` implementing `Service` to load and index data
4. Create `*Plugin.kt` extending `KotlinPlugin` to bind and execute
5. Load the service in `init {}` via `loadService()`

### String ID System (RSCM)

All game entity references use RSCM string format (`"object.veg_stall"`, `"npc.man_3106"`, `"item.potato"`). At runtime, `RSCM.getRSCM(string)` resolves to the integer cache ID. Name mappings live in `data/cfg/rscm/*.rscm`.

### Player Queue System

Plugin logic uses `player.queue { ... }` to create coroutines that can `wait(ticks)` for delays. This is how animations, delays, and multi-step interactions are implemented. `world.queue { ... }` provides world-level queues (e.g., stall respawn timers).

### Configuration

- `game.yml` — server name, port, revision, home coords, privileges, services list
- `dev-settings.yml` — debug flags (objects, buttons, packets, etc.)
- Both copied from `.example.yml` files during `gradlew install`
