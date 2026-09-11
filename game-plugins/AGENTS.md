# game-plugins/AGENTS.md

## Plugin Loading Model

Plugins are plain Kotlin classes extending `KotlinPlugin(pluginRepository, world, server)`. At boot, `PluginRepository.scanPackageForPlugins` uses ClassGraph to find all direct subclasses on the classpath and instantiates each via its `(PluginRepository, World, Server)` constructor. The `init {}` block in each plugin registers all bindings (object clicks, NPC interactions, commands, timers, login hooks, etc.). There is no plugin manifest or annotation — classpath presence is sufficient.

## Reference Pattern: Thieving

The thieving system (`content/skills/thieving/`) is the canonical example for data-driven content:

- **`stall/StallThievingData.kt`** — immutable data classes (`StallEntry`, `StallLoot`) with validation `init` blocks
- **`stall/StallThievingService.kt`** — implements `Service`, loads JSON from `data/cfg/thieving/stalls.json`, resolves RSCM string IDs to ints
- **`stall/StallThievingPlugin.kt`** — extends `KotlinPlugin`, calls `loadService()`, `onWorldInit {}` binds object options

Identical pattern used for `pickpocket/` and `chest/`.

## Content Authoring Rules

1. **Data-driven where possible**: Define game data in `data/cfg/` as JSON, load via a `Service` class
2. **One plugin per file**: Each `*Plugin.kt` handles one feature or concept
3. **Follow existing naming**: `*Plugin.kt`, `*Service.kt`, `*Data.kt` or `*Entry.kt`
4. **Use RSCM string IDs**: `"object.veg_stall"`, `"npc.man_3106"`, `"item.potato"` — never raw ints
5. **Register in `init {}`**: Call `loadService()` then `onWorldInit {}` to bind interactions
6. **Standard imports**: See existing plugins for the canonical import block

## Content Inventory

See `docs/CONTENT_INVENTORY.md` for the full table of implemented features, their locations, and completeness status.
