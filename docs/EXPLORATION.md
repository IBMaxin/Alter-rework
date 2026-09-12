# Exploration Notes

Repo: Alter-rework (RSMod fork), Kotlin 2.0.20 / Java 17.
Every claim below cites a file actually read. Anchors are class/function names, not line numbers.

## System: Server boot & service instantiation
- What it does: Brings the game world online from a cold start — loads config, cache, definitions, services, plugins, network.
- Key files:
  - `game-server/.../org/alter/game/Launcher.kt` — `object Launcher.main` is the JVM entry point. Constructs a `Server`, calls `server.startServer(apiProps = ../data/api.yml)` then `server.startGame(filestore = ../data/cache, gameProps = ../game.yml, devProps = ../dev-settings.yml)`.
  - `game-server/.../org/alter/game/Server.kt` — `Server.startGame` is the real boot sequence, in this order:
    1. Loads `game.yml` + `dev-settings.yml` into `ServerProperties`.
    2. Builds `GameContext` from game.yml keys: `name`, `revision`, `saveFormat` (default `JSON`), `cycle-time` (default 600 = the tick length), `max-players` (default 2048), `home` Tile from `home-x`/`home-z`/`home-height`, `skill-count`, `run-energy`, ground-item delays, `preload-maps`.
    3. Builds `DevContext` from dev-settings (`debug-objects`, `debug-packets`, etc.).
    4. `PlayerDetails.init` + `PlayerSaving.init` (save subsystem primed with GameContext).
    5. `CacheManager.init(filestore, revision)` — loads the OpenRune cache (`dev.openrune.cache.CacheManager`).
    6. `val world = World(gameContext, devContext)`.
    7. `ObjectExamineHolder.load()` — loads examine/equipment-menu defs.
    8. `world.loadServices(this, gameProperties)` — instantiates the YAML-declared services (see next system).
    9. Builds the network: `CacheJs5GroupProvider`, `NetworkServiceFactory(groupProvider, world, [port], [DESKTOP])`, `world.network = network.build()`; port from `game-port` (default 43594).
    10. `world.init()`.
    11. `RSCM.init()` — the string-id → int-id name resolver (`org.alter.rscm.RSCM`).
    12. `world.privileges.load(gameProperties)` — loads the privilege ladder from game.yml.
    13. `world.plugins.init(server, world, jarPluginsDirectory = getOrDefault("plugin-packed-path", "../plugins"))` — loads ALL content plugins (see Plugin loading).
    14. `world.postLoad()`.
    15. `world.bindServices(this)` then `world.network.start()` — begins listening.
- How game.yml services are instantiated: game.yml has a `services:` list, each entry a `class:` FQN (e.g. `org.alter.game.service.xtea.XteaKeyService`, `ItemMetadataService`, `NpcMetadataService`, `ObjectMetadataService`, `RsaService`, `GameService`, `login.LoginService`). `Server.startGame` calls `world.loadServices(this, gameProperties)` — the reflective instantiation lives in `World.loadServices` (NOT YET READ; UNKNOWN — needs human check for exact reflection code). Note these YAML services are a SEPARATE set from plugin-requested services (those go through `PluginRepository.loadServices`).
- How it connects to other systems: `GameContext.cycleTime` feeds the tick loop; `world.plugins.init` triggers Plugin loading; `saveFormat` feeds player persistence.
- Open questions:
  - Exact reflection logic in `World.loadServices` — UNKNOWN, needs read of `World.kt`.
  - Where the 600ms tick loop actually runs (likely `GameService` from game.yml) — UNKNOWN, needs read of `org.alter.game.service.GameService`.
  - `world.init()` / `world.postLoad()` internals — UNKNOWN.

## System: Plugin loading & discovery
- What it does: Discovers every content plugin at startup, instantiates it, lets each register its bindings (object/npc/item options, commands, timers, login hooks, services, spawns), then loads plugin services and spawns plugin-declared entities.
- Key files:
  - `game-server/.../org/alter/game/plugin/PluginRepository.kt` — the central registry AND dispatcher.
    - `PluginRepository.init(server, world, jarPluginsDirectory)` calls in order: `loadPlugins` → `loadServices` → `spawnEntities`.
    - `loadPlugins` → `scanPackageForPlugins` (the jar-directory scan `scanJarDirectoryForPlugins` is present but COMMENTED OUT / disabled — the call is commented in `loadPlugins`, and `scanJarForPlugins` body is fully commented). So plugins are found ONLY by classpath scan, not from `../plugins` jars, despite the passed directory.
    - `scanPackageForPlugins` uses `io.github.classgraph.ClassGraph().enableAllInfo().scan()`, gets `getSubclasses(KotlinPlugin::class.java.name).directOnly()`, then for each: `Class.forName(p.name)`, gets the `(PluginRepository, World, Server)` constructor, and `constructor.newInstance(this, world, server)`. Construction IS registration — a plugin's `init {}` block runs its bindings immediately. Failures are caught per-plugin and printed ("Failed to load: <name> plugin").
    - `loadServices` iterates `services` (populated by plugins via `loadService`), calls `service.init(server, world, ServerProperties())` and adds to `world.services`, then a second pass calls `service.postLoad(server, world)`.
    - `spawnEntities` spawns everything plugins queued into `npcSpawns` / `objSpawns` / `itemSpawns` via `world.spawn`.
  - `game-server/.../org/alter/game/plugin/KotlinPlugin.kt` — `abstract class KotlinPlugin(r: PluginRepository, world: World, server: Server)`. This is the plugin author API. Every `onX`/`spawnX`/`createShop`/`loadService`/`setCombatDef` helper just forwards to a `r.bindX(...)` call on the repository. Object/npc/item ids are resolved from string names via `org.alter.rscm.RSCM.getRSCM`, and option strings are resolved to option index by scanning the cache def's `actions`/`interfaceOptions` (via `dev.openrune.cache.CacheManager.getObject/getNpc/getItem`), then `+1`'d (1-based option slots).
- Binding model: `PluginRepository` holds one map/list per interaction type (e.g. `objectPlugins: Int2ObjectOpenHashMap<Int2ObjectOpenHashMap<Plugin.() -> Unit>>` keyed by objId→opt; `commandPlugins`, `buttonPlugins`, `npcPlugins`, `timerPlugins`, `loginPlugins`, `eventPlugins`, etc.). Each has a `bindX` (register, usually throws `IllegalStateException` on duplicate) and an `executeX` (lookup + `pawn.executePlugin(logic)`, returning false if unbound). This `executeX`-returns-Boolean pattern is how the engine asks "did any plugin handle this?".
- How it connects to other systems: `Server.startGame` step 13 calls `plugins.init`. The engine's interaction handlers call `world.plugins.executeX(...)` to route events (see Event routing). Plugin-requested services join the same `world.services` list as YAML services.
- Open questions:
  - `Plugin` class + `Pawn.executePlugin` / `World.executePlugin` internals (the coroutine/receiver mechanism) — UNKNOWN, needs read of `plugin/Plugin.kt` and `entity/Pawn.kt`.
  - Whether classpath scan ordering is deterministic (affects duplicate-bind errors) — UNKNOWN.

## System: Event routing — object click → thieving stall (canonical trace)
Concrete action: player clicks "Steal-from" on a stall object. Verified end-to-end (both caller and callee read at every step).

Flow:
1. Registration (boot): `StallThievingPlugin` (`game-plugins/.../content/skills/thieving/stall/StallThievingPlugin.kt`) `init {}` calls `loadService(StallThievingService())` and `onWorldInit { ... }`. On world init it fetches the service, and for every stall object id (whose cache def `actions` contain "steal-from"/"steal from") calls `onObjOption(obj = objId, option = option) { ... }`.
2. Binding: `KotlinPlugin.onObjOption(obj, option, ...)` (`KotlinPlugin.kt`) resolves the option string to a slot via `getObject(obj).actions.indexOfFirst{...}`, then `r.bindObject(obj, slot+1, lineOfSightDistance, logic)`. `PluginRepository.bindObject` stores it in `objectPlugins[objId][opt]`.
3. Cache config: `StallThievingService.init` (`stall/StallThievingService.kt`) reads `../data/cfg/thieving/stalls.json` (path overridable via service prop `stalls`) with Gson into `List<StallEntry>`; resolves each entry's `objects`/`emptyObject` name strings to ints via `RSCM.getRSCM`. `StallEntry`/`StallLoot` are validated data classes in `stall/StallThievingData.kt` (require level≥1, respawnTicks≥1, loot non-empty, loot weights>0, etc.).
4. Dispatch (runtime): when the player walks to and clicks the object, `ObjectPathAction.walk` (`game-server/.../model/move/ObjectPathAction.kt`) queues a `TaskPriority.STANDARD` task, route-finds to the object, and on `route.success` faces the object and runs the bound logic; the item-on-object variant calls `player.world.plugins.executeObject(player, obj.getTransform(player), opt!!)` (verified caller). `PluginRepository.executeObject(p, id, opt)` looks up `objectPlugins[id][opt]` and runs it via `p.executePlugin(logic)` (verified callee).
   - NOTE: I read `ObjectPathAction.walk` and its `itemOnObjectPlugin` block (which calls `executeObject` for item-on-object). The plain object-option path that reaches the bound `onObjOption` logic is via `ObjectPathAction.walk(..., logic)` → `player.executePlugin(logic)`. UNKNOWN — the exact packet handler (OpLoc) that constructs the `ObjectPathAction.walk` call for a bare object-option click was not located; needs the OpLoc/object-click message handler read.
5. Plugin logic: the bound lambda does `player.queue { stealFromStall(this, player, obj, entry) }`. `StallThievingPlugin.stealFromStall` (suspend, takes `QueueTask`):
   - Reads current Thieving level `player.getSkills().getCurrentLevel(Skills.THIEVING)`.
   - Guards: object still spawned (`obj.isSpawned(world)`), level ≥ `entry.level`, inventory has room (`canReceiveLoot`).
   - `player.faceTile(obj.tile)`, `player.lock()`, `player.animate(Animation.THIEVING_STALL)`, `task.wait(2)` (2-tick delay), re-checks spawned, then `rewardPlayer` + `replaceWithEmpty`, `player.unlock()` in finally.
6. Engine calls (reward + respawn):
   - `rewardPlayer`: `player.addXp(Skills.THIEVING, entry.experience)`; rolls loot (`rollLoot` — single entry or weighted by `StallLoot.weight` via `world.randomDouble()`); `player.inventory.add(item, amount)`; `player.message(...)`.
   - `replaceWithEmpty`: `world.remove(obj)`, spawns a `DynamicObject(emptyId, ...)` in its place, then `world.queue { wait(entry.respawnTicks); remove empty; spawn original DynamicObject }` — the stall respawn timer lives on the WORLD queue, not the player.
- State & persistence: The only persistent effects are the XP gain (Thieving) and inventory items added — both live on the Player and are covered by normal player saving. The stall's empty/full state is transient world state (DynamicObject swap + world queue), NOT persisted.
- How it connects: uses Plugin loading (bindings), the queue/coroutine system (`player.queue`/`world.queue`/`QueueTask.wait`), movement/route-finding (`ObjectPathAction`), skills (`addXp`), inventory, and world object spawn/remove.
- Open questions:
  - `player.queue` / `world.queue` / `QueueTask.wait` coroutine mechanics — UNKNOWN (queue system not yet read).
  - `player.addXp` → level-up / `executeSkillLevelUp` chain — UNKNOWN.
  - `player.inventory.add` transaction semantics (the returned transaction is ignored here) — UNKNOWN.
  - The OpLoc object-click packet handler that initiates `ObjectPathAction.walk` for a normal option — UNKNOWN, needs human check.

## Questions answered
### Q: How does clicking "Steal-from" on a thieving stall work end-to-end?
- Answer: See "System: Event routing — object click → thieving stall". Summary: boot-time classpath scan instantiates `StallThievingPlugin`, which on world-init binds "steal-from" object options via `KotlinPlugin.onObjOption` → `PluginRepository.bindObject`. Stall data comes from `../data/cfg/thieving/stalls.json` loaded by `StallThievingService`. At runtime the object click drives `ObjectPathAction.walk` → `player.executePlugin(logic)` (and item-on-object via `PluginRepository.executeObject`), the logic queues a suspend task that checks level/space, animates, waits 2 ticks, grants Thieving XP + inventory loot, swaps the stall for its empty variant, and schedules the original object's respawn on the world queue after `respawnTicks`. Only XP and inventory persist; stall state is transient. (Files: `StallThievingPlugin.kt`, `StallThievingService.kt`, `StallThievingData.kt`, `KotlinPlugin.kt`, `PluginRepository.kt`, `ObjectPathAction.kt`.)

### Q: How are plugins discovered and loaded at startup?
- Answer: `Server.startGame` → `world.plugins.init` → `PluginRepository.loadPlugins` → `scanPackageForPlugins`, which uses ClassGraph to find all direct subclasses of `KotlinPlugin` on the classpath and instantiates each via its `(PluginRepository, World, Server)` constructor; the constructor's `init` block registers all bindings. Jar-directory scanning exists but is commented out/disabled. Then `loadServices` (init + postLoad on plugin-requested services) and `spawnEntities`. (Files: `Server.kt`, `PluginRepository.kt`, `KotlinPlugin.kt`.)

## Coverage
Explored (both sides read):
- Boot sequence in `Server.startGame` (config → cache → services → plugins → network).
- Plugin discovery/binding/dispatch registry (`PluginRepository`, `KotlinPlugin`).
- Thieving-stall trace from binding through reward/respawn.

NOT yet explored (highest-value next targets):
- `World.kt` — `loadServices` reflection (game.yml service instantiation), `init`/`postLoad`, `spawn`/`remove`/`queue`.
- The 600ms tick loop — likely `org.alter.game.service.GameService`; NOT READ. What runs per tick (player/npc processing, synchronization, timers).
- Queue/coroutine system — `model/queue/QueueTask`, `TaskPriority`, `player.queue`/`world.queue`, `QueueTask.wait`.
- `Plugin` class + `Pawn.executePlugin` (the receiver/coroutine mechanism every `executeX` relies on).
- Packet-in handlers — the OpLoc/object-click handler that starts `ObjectPathAction.walk`; also button/npc/item message routing. Location UNKNOWN.
- Combat resolution (attack roll → hit splat → XP drop) — not started.
- Item/inventory model in `game-api/` and `model/container/`; `player.inventory.add` semantics.
- Player save serialization — `saving/PlayerSaving`, `PlayerDetails`, `SaveFormatType`; what a save can/can't restore. Files identified in `Server.kt` imports but not read.
- Skills subsystem — `addXp`, level-up, `SkillSet`.
- game-plugins content inventory beyond thieving (breadth skim of `content/` subdirs not yet done).
- Other modules: `util/`, `http-api/`, `plugins/` (filestore, rscm, tools) — not opened.
