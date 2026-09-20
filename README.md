# Foreman

**Foreman is a survival-legal Fabric mod for Minecraft 26.2** that plans, calculates, and highlights instead of automating. It never places, breaks, mines, harvests, or catches a single block or item for you, and it never reveals anything you couldn't already see by walking around and looking yourself. No x-ray, no reveal-through-walls, no auto-anything.

If you've ever wanted a **Minecraft fishing bite indicator** that's actually visible in a dark cave, or a **WorldEdit-style material calculator** that reads your real world instead of making you type dimensions into a website, this is that mod.

## Why Foreman exists

The Fabric mod ecosystem has redstone visualizers, auto-harvest bots, instant-edit terraforming brushes, and loot trackers — often several of each, some with millions of downloads. What's missing is **survival-legal planning and instrumentation**: tools that make the vanilla game more readable without changing how it's played. Foreman fills that specific gap.

*(The name comes from the trade, not the diagnostician — though Dr. Foreman would probably back a tool that runs the numbers before anyone touches anything.)*

**The rule that defines every feature:** Foreman computes and shows. You still swing the pickaxe, place the water, and reel in the rod by hand.

## Features

### 🎣 Fishing bobber glow (v1, on by default)
Vanilla's fishing bite cue is a small ripple animation that's genuinely hard to see at night, underground, or across a lake. Foreman watches the bobber's real bite state (the same flag the game itself uses to trigger the splash) and renders a glow outline the instant a fish bites — readable through walls, in the dark, from across the water. No mod currently does this despite it being a long-standing, widely-known player complaint.

### 🧱 Terraform material calculator (v1)
Select a region with two corners, WorldEdit-style — selection only, no editing. Foreman scans the **real block data** in that region and reports an exact material list (broken down by block type, converted into stacks and shulker-box counts), with player-configurable filters for air, water, and bedrock. Know exactly what to bring — or what you're about to haul away — before you swing a single tool.

### ⛏️ Mining Y-level lookup & coverage tracking (planned, v2)
Statistically correct dig depth per ore, plus a coverage tracker for a claimed mining area so you know if a spot is still worth digging.

### 🌾 Farming layout blueprints (planned, v3)
Optimal water spacing, tilled-area shape, and collection-point placement for a target crop and yield — rendered as placement ghosts, never auto-planted or auto-harvested.

## Requirements

- Minecraft **26.2**
- Fabric Loader **0.19.5+**
- Fabric API
- Java **25+**

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.2.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 26.2.
3. Download the latest Foreman release from the [Releases page](https://github.com/abduznik/foreman/releases) or [Modrinth](https://modrinth.com).
4. Drop both jars into your `mods` folder.

## Terraform mode controls

| Key | Action |
|---|---|
| `[` | Set selection corner 1 (looks at targeted block) |
| `]` | Set selection corner 2 |
| `\` | Scan selection and print the material list to chat |

Rebind any of these in **Options → Controls → Foreman**.

## Building from source

```
git clone https://github.com/abduznik/foreman.git
cd foreman
./gradlew build
```

Output jar lands in `build/libs/`.

## Contributing

Issues and pull requests are welcome. Every new feature has to survive one question: does this place, break, mine, harvest, catch, or reveal anything the player couldn't already do or see themselves? If yes, it doesn't belong in Foreman.

## License

CC0-1.0 — public domain. Do whatever you want with it.
