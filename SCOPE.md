# Foreman Scope

The single source of truth for what Foreman is, what it will build, and what it will
deliberately never build. Every issue and PR should be traceable to something here.

## What Foreman is

A self-contained, client-side Fabric mod that makes vanilla survival more readable to
plan and play: material calculators, area planners, and instrumentation. It is not a
content mod, a performance mod, or an automation mod.

## The one rule

Every feature has to survive this question:

> Does this place, break, mine, harvest, catch, or reveal anything the player could
> not already do or see themselves?

If yes, it does not belong in Foreman. Foreman computes and shows; the player still
swings the pickaxe, places the light, and reels in the rod.

**Occlusion nuance.** Drawing a marker on the player's *own* entity or action through
terrain is allowed (the fishing bite marker is the player's own bobber, readable through
walls and in the dark). Revealing *world* information the player has not seen is not:
no x-ray, no hidden-entity radar, no chest contents the player has not opened.

## Design principles

1. **One jar.** No hard dependency beyond Fabric API. No Cloth Config, no Mod Menu, no
   library mods. Config is plain JSON plus a first-party in-game screen (see #1).
2. **Compute and show.** No automation, ever.
3. **Self-contained lite spins.** Where a small feature is worth having and no good
   lightweight option exists, implement a Foreman-native version rather than requiring a
   companion mod.
4. **Coexist, do not collide.** Auto-detect installed companion mods and disable the
   overlapping Foreman-native feature, so users never get double HUDs or mixin conflicts.
   See the overlap policy below.
5. **State persists per world/server** and survives relogs (precedent: terraform
   selections).
6. **Lightweight by default.** Anything shipped must justify its tick/render cost.

## Architecture

- **Feature modules.** One class per feature, each exposing `init()` and registered in
  `ForemanClient` (precedent: `BobberGlowFeature`, `TerraformFeature`).
- **Shared planning primitives.** A single box-selection and block-scan utility is reused
  by every area-based feature (terraform, spawn-proof, storage layout) rather than
  reimplemented per mode.
- **HUD layer.** One overlay renderer for markers and readouts, so features share
  positioning and styling.
- **Config.** One `config/foreman.json`, one screen, one place to toggle features.

## Feature tiers

### Tier A - cheap lite spins (days each)

Small, client-only, no data or rendering engine. Candidates: status-effect timers, armor
and durability HUD, better ping display, coordinate/light/direction HUD, held-item info,
shulker box preview, zoom. These are the first "we replace the companion" features.

### Tier B - real subsystems (weeks each)

Plan-only tools built on the shared selection/scan primitives:

- **Spawn-proof planner.** Scan a base region, compute the exact light placement to kill
  hostile spawns, output the material list. Planning counterpart to Lighty.
- **Storage-room layout planner.** Compute a chest layout and labeling plan for a region
  from a category list. Never moves or sorts items.
- **Portal-linking calculator.** Nether/Overworld coordinate matching, with vanilla's
  linking rules. Read-only.
- **Shared selections.** Export/import a selection and its material list so a multiplayer
  group can agree on who brings what.
- **Terraform ghosts.** Multi-selection (#4), fill/reverse mode (#5), dig-here markers (#6).
- **Mining** (#7-#9) and **Farming** (#10-#11) as already scoped.

### Tier C - never reimplement (stay with companions)

Each of these is a multi-year, multi-person project. A "lite" version does not exist, and
attempting one would wreck both the maintenance budget and the mod's identity:

- Rendering engine (Sodium) and shader loader (Iris)
- Game-logic optimization (Lithium)
- Full world map / waypoints at scale (Xaero's)
- Schematic engine (Litematica)
- Full recipe dataset (JEI/EMI)

Foreman detects and coexists with these; it never competes with them.

## Companion overlap policy

When a known companion is present, Foreman disables its overlapping native feature (with a
manual override in config):

| Companion | Foreman backs off |
|---|---|
| Sodium / Iris / Lithium | (always left to companion) |
| Xaero's Minimap / World Map | map, waypoints, coordinates HUD |
| Jade / WTHIT | look-at block/entity info |
| ChestSort / Inventory Profiles / Stack to Nearby Chests | sorting, stacking |
| Shulker Box Tooltip | shulker preview |
| Zoomify / WI Zoom | zoom |
| Lighty / MiniHUD | light overlay (spawn-proof planner stays) |
| Better Ping Display | ping display |
| AppleSkin | hunger readouts |
| Status Effect Timer | effect timers |
| uku's Armor HUD | armor HUD |

## Roadmap

- **v1 (shipped).** Fishing bite marker; terraform material calculator.
- **v2.** Terraform ghosts (multi-selection, fill/reverse, dig-here) + mining Y-level lookup.
- **v3.** Mining tunnel coverage guide + farming layout blueprints.
- **v4 - Planning suite.** Spawn-proof planner, storage-room layout planner, portal-linking
  calculator, shared selections.
- **v5 - QoL lite pack.** Tier A spins, each with companion auto-disable.

## Out of scope

Automation of any kind (placement, mining, harvesting, fishing, crafting, AFK), x-ray and
hidden-information reveals, content/mobs/biomes, performance rendering, and any feature
whose only implementation path is reimplementing a Tier C mod.
