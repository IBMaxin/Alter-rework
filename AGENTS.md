# AGENTS.md — Alter-rework Agent Instructions

## Project Overview

Alter-rework is an OSRS-style RuneScape private server, forked from RSMod, written in Kotlin. It targets OSRS cache revision 228 and uses RSProt for protocol handling. The server implements a plugin-based architecture where all game content lives in Kotlin plugins discovered via classpath scanning at boot. Current version is 0.0.5 (early development). Architecture: Kotlin 2.0.20 on JVM 17, Gradle 8.11 build, modular layout with engine (`game-server/`), generated definitions (`game-api/`), content plugins (`game-plugins/`), and dev tooling (`plugins/`).

## Toolchain

| Tool | Version | Notes |
|------|---------|-------|
| Kotlin | 2.0.20 | Language version 2.0, `-Xuse-fir-lt=false` compiler flag |
| Java | 17 | JVM toolchain target |
| Gradle | 8.11 | Wrapper properties point to 8.11 |
| OSRS Cache | b228 | Binary data in `data/cache/` — never commit or document contents |
| RSProt | 1.0.0-ALPHA-20250126 | Protocol library for OSRS 228 |

**Gradlew gotcha**: `gradlew.bat` exists (Windows). There is no `gradlew` shell script (Unix). On Windows, use `gradlew.bat`. If Gradle 8.11 is installed globally, you can use `gradle build` directly.

**Version catalog quirk**: `settings.gradle.kts` loads the catalog from `../gradle/libs.versions.toml` (outside the repo root), while a copy also exists at `gradle/libs.versions.toml` inside the repo. Do not "fix" the path without checking with a human.

## Build & Verification

```bash
# Build (Windows)
gradlew.bat build

# Build (if Gradle 8.11 is on PATH)
gradle build

# Run tests
gradlew.bat test

# Full install (creates config) — task lives in :game-server, not root
gradlew.bat :game-server:install

# Install sub-steps (RSA keygen, map decryption)
gradlew.bat :game-server:runRsaService
gradlew.bat :game-server:decryptMap

# Run the server (main class: org.alter.game.Launcher)
gradlew.bat :game-server:run
```

http-api frontend (Vue.js, in `http-api/`): `npm install`, then `npm run serve` (dev), `npm run build`, `npm run lint`.

Docker: a `Dockerfile` and `docker-compose.yml` exist at the repo root.

**"Done" means**: `gradlew.bat build` completes with BUILD SUCCESSFUL and no compilation errors. CI exists (`.github/workflows/build.yml` builds on push to main/dev and PRs; `qodana_code_quality.yml` runs Qodana), but local verification is still mandatory before push.

## Repository Map

| Path | Purpose |
|------|---------|
| `game-server/` | Engine core: `Launcher.kt`, `Server.kt`, plugin system, models, services, networking |
| `game-api/` | Generated entity definitions (`Skills`, `Animation`, `Varbit`, `cfg/` package) and DSL extensions |
| `game-plugins/` | ALL game content: skills, NPCs, combat, commands, mechanics, interfaces, items, objects |
| `game-plugins/src/main/kotlin/org/alter/plugins/content/` | Content root — skills/, npcs/, combat/, commands/, mechanics/, items/, objects/, magic/, interfaces/, areas/, weapons/ |
| `plugins/` | Dev tooling sub-modules: `filestore/`, `rscm/`, `tools/` |
| `util/` | Utility classes (`gg.rsmod.util.*`), planned to merge into game-server |
| `http-api/` | Vue.js frontend (not a Gradle module) — `npm run serve`/`build`/`lint` |
| `Dockerfile`, `docker-compose.yml` | Container deployment |
| `data/cfg/` | Game data: thieving JSON, RSCM name maps, spawns, item overrides |
| `data/saves/` | Player save files |
| `data/cache/` | OSRS binary cache — DO NOT touch |
| `game.yml` | Server config (copy from `game.example.yml`) |
| `dev-settings.yml` | Debug flags (copy from `dev-settings.example.yml`) |

## Code Conventions

- **Plugin classes**: Extend `KotlinPlugin(r, world, server)`, name `*Plugin.kt`, one concept per file
- **Service classes**: Implement `Service` interface, name `*Service.kt`
- **Data classes**: Name `*Data.kt` or `*Entry.kt`, load from JSON in `data/cfg/`
- **String IDs**: Use RSCM format — `"object.veg_stall"`, `"npc.man_3106"`, `"item.potato"`
- **Standard imports**: `org.alter.api.*`, `org.alter.api.cfg.*`, `org.alter.api.dsl.*`, `org.alter.api.ext.*`, `org.alter.game.*`, `org.alter.game.model.*`
- **Formatting**: ktlint 12.1.0 is declared in `gradle/libs.versions.toml` but the Gradle plugin is NOT applied anywhere — formatting is convention-only, no `ktlintCheck` task exists. PascalCase classes, camelCase functions, UPPER_SNAKE_CASE constants
- **Registration pattern**: In `init {}` block, call `loadService(...)`, `onWorldInit { ... }` to bind interactions at world init time

## Constraints & Pitfalls

- **`game-plugins/` only**: ALL new game content goes in `game-plugins/src/main/kotlin/org/alter/plugins/content/`. Never put content in `game-server/`.
- **Engine changes require approval**: `game-server/` modifications affect all players — get explicit human approval before touching.
- **Never commit `data/cache/`**: Binary cache files. Never reference cache contents in docs.
- **Never invent game data**: XP rates, drop rates, item IDs, NPC IDs — always use verified OSRS data or mark as `NEEDS HUMAN VERIFICATION`.
- **Ignore non-main branches**: Only `main` branch is active.
- **String IDs only**: Use RSCM string names (`"object.veg_stall"`), never raw integer IDs.
- **One plugin per file**: Each plugin class handles one feature/concept.
- **No secrets**: Never commit keys, tokens, passwords, or credentials.

## Definition of Done

1. Code compiles: `gradlew.bat build` passes with no errors
2. Diff reviewed by human
3. Human pushes to `main`

## Escalation Rules

- If game data is missing (XP rate, drop table, item ID) → **stop and ask**. Do not hardcode guesses.
- If unsure whether a change touches engine code → check if the file is in `game-server/` and ask.
- If build fails with unfamiliar error → report to human, do not workaround.
