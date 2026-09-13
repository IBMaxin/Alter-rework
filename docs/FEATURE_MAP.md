# Feature Map

This is a routing and navigation document: it tells you which Alter subsystem and which authoritative document to consult, so you can start work without searching the whole repository. It is not a feature tracker and must not become an independent feature inventory. [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) remains authoritative for current feature locations and implementation status.

## How to Use This Map

1. Identify the relevant subsystem in the tables below.
2. Open [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) for authoritative feature status and exact implementation locations.
3. Open [`ARCHITECTURE.md`](ARCHITECTURE.md) for module relationships and runtime/system design.
4. Open [`EXPLORATION.md`](EXPLORATION.md) for investigation evidence and unresolved questions.
5. Read the applicable `AGENTS.md` before modifying files: [`../AGENTS.md`](../AGENTS.md) for repository-wide rules, [`../game-plugins/AGENTS.md`](../game-plugins/AGENTS.md) for plugin/content rules.

## Documentation Authority

| Document | Authority |
|---|---|
| [`FEATURE_MAP.md`](FEATURE_MAP.md) | Navigation and routing only |
| [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) | Authoritative feature inventory, locations, and status |
| [`ARCHITECTURE.md`](ARCHITECTURE.md) | System architecture and module relationships |
| [`EXPLORATION.md`](EXPLORATION.md) | Investigation evidence and open questions |
| [`../AGENTS.md`](../AGENTS.md) | Repository-wide development rules |
| [`../game-plugins/AGENTS.md`](../game-plugins/AGENTS.md) | Plugin/content development rules |
| [`../README.md`](../README.md) | Project introduction and setup |

## System Map

Each row names where a broad concern lives and routes feature-specific details to [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md). This table carries no status information.

| Concern | Where to look | Feature details |
|---|---|---|
| Server and runtime | [`game-server/`](../game-server/); see [`ARCHITECTURE.md`](ARCHITECTURE.md) | [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) |
| Shared API | [`game-api/`](../game-api/) | [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) |
| Content plugins | [`game-plugins/`](../game-plugins/); see [`game-plugins/AGENTS.md`](../game-plugins/AGENTS.md) | [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) |
| Skills | [`game-plugins/.../content/skills/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/skills/) | [`CONTENT_INVENTORY.md` → Skills](CONTENT_INVENTORY.md#skills) |
| NPC content | [`game-plugins/.../content/npcs/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/npcs/) | [`CONTENT_INVENTORY.md` → NPCs](CONTENT_INVENTORY.md#npcs) |
| Combat | [`game-plugins/.../content/combat/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/combat/) | [`CONTENT_INVENTORY.md` → Combat](CONTENT_INVENTORY.md#combat) |
| General mechanics | [`game-plugins/.../content/mechanics/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/mechanics/) | [`CONTENT_INVENTORY.md` → Mechanics](CONTENT_INVENTORY.md#mechanics) |
| Interfaces | [`game-plugins/.../content/interfaces/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/interfaces/) | [`CONTENT_INVENTORY.md` → Interfaces](CONTENT_INVENTORY.md#interfaces) |
| Items and equipment | [`game-plugins/.../content/items/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/items/) | [`CONTENT_INVENTORY.md` → Items](CONTENT_INVENTORY.md#items) |
| Magic | [`game-plugins/.../content/magic/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/magic/) | [`CONTENT_INVENTORY.md` → Magic](CONTENT_INVENTORY.md#magic) |
| Objects and interactions | [`game-plugins/.../content/objects/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/objects/) | [`CONTENT_INVENTORY.md` → Objects](CONTENT_INVENTORY.md#objects) |
| Areas and locations | [`game-plugins/.../content/areas/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/areas/) | [`CONTENT_INVENTORY.md` → Areas](CONTENT_INVENTORY.md#areas) |
| Weapons and special attacks | [`game-plugins/.../content/weapons/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/weapons/) and [`game-plugins/.../content/combat/specialattack/`](../game-plugins/src/main/kotlin/org/alter/plugins/content/combat/specialattack/) | [`CONTENT_INVENTORY.md` → Weapons](CONTENT_INVENTORY.md#weapons) |
| Services | [`game-server/.../game/service/`](../game-server/src/main/kotlin/org/alter/game/service/) and [`game-plugins/.../service/`](../game-plugins/src/main/kotlin/org/alter/plugins/service/) | [`CONTENT_INVENTORY.md` → Services](CONTENT_INVENTORY.md#services-game-plugins) |
| Data-driven configuration | [`data/cfg/`](../data/cfg/) | [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) |
| Cache and filestore tooling | [`plugins/filestore/`](../plugins/filestore/), [`plugins/rscm/`](../plugins/rscm/), [`plugins/tools/`](../plugins/tools/) | [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) |
| Tests | [`game-server/src/test/`](../game-server/src/test/) and [`game-plugins/src/test/`](../game-plugins/src/test/) | [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) |

## Feature Categories

Category index for the inventory. Open [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) at the linked section for locations and status; do not record them here.

| Category | Inventory section |
|---|---|
| Skills | [`CONTENT_INVENTORY.md` → Skills](CONTENT_INVENTORY.md#skills) |
| NPCs | [`CONTENT_INVENTORY.md` → NPCs](CONTENT_INVENTORY.md#npcs) |
| Combat | [`CONTENT_INVENTORY.md` → Combat](CONTENT_INVENTORY.md#combat) |
| Commands | [`CONTENT_INVENTORY.md` → Commands](CONTENT_INVENTORY.md#commands) |
| Mechanics | [`CONTENT_INVENTORY.md` → Mechanics](CONTENT_INVENTORY.md#mechanics) |
| Interfaces | [`CONTENT_INVENTORY.md` → Interfaces](CONTENT_INVENTORY.md#interfaces) |
| Items | [`CONTENT_INVENTORY.md` → Items](CONTENT_INVENTORY.md#items) |
| Magic | [`CONTENT_INVENTORY.md` → Magic](CONTENT_INVENTORY.md#magic) |
| Objects | [`CONTENT_INVENTORY.md` → Objects](CONTENT_INVENTORY.md#objects) |
| Areas | [`CONTENT_INVENTORY.md` → Areas](CONTENT_INVENTORY.md#areas) |
| Weapons | [`CONTENT_INVENTORY.md` → Weapons](CONTENT_INVENTORY.md#weapons) |
| Services | [`CONTENT_INVENTORY.md` → Services](CONTENT_INVENTORY.md#services-game-plugins) |

## Data and Configuration

- Established data-driven configuration is located under `data/cfg/`.
- Before changing data, identify the owning loader, service, or plugin using [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) and source inspection.
- Do not infer format, ownership, or runtime behavior from a directory name.
- Do not document cache contents.

## Maintenance Rules

- Update this document only when high-level repository routing changes.
- Update [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md) when a feature's status or specific implementation location changes.
- Update [`ARCHITECTURE.md`](ARCHITECTURE.md) when module/system relationships change.
- Record evidence-backed investigation findings and unresolved questions in [`EXPLORATION.md`](EXPLORATION.md).
- Do not infer current behavior from TODO or changelog entries.
- Do not add feature-status claims to this document.

## Related Documents

- [`ARCHITECTURE.md`](ARCHITECTURE.md)
- [`CONTENT_INVENTORY.md`](CONTENT_INVENTORY.md)
- [`EXPLORATION.md`](EXPLORATION.md)
- [`../AGENTS.md`](../AGENTS.md)
- [`../game-plugins/AGENTS.md`](../game-plugins/AGENTS.md)
- [`../README.md`](../README.md)
