# LifeStealZ 2.21.1 — decompiled

Decompilation of `lifestealz-2.21.1.jar` (SHA-256
`c4d466f579ec91ef3dd51c01fbdcab3aa8cf98009489c229abe4c823a4dbaea1`), the
LifeSteal SMP plugin for Paper/Spigot by Kartoffelchipss / ZetaPlugins.

> **License:** LifeStealZ is open source under **GPL-3.0**
> (`META-INF/maven/.../pom.xml` → https://github.com/ZetaPlugins/LifeStealZ).
> This decompiled copy stays under the same license. The upstream repository
> has the original, commented source — prefer it for anything you build on.

## What's here

| Path | Contents |
|------|----------|
| `rea/` | **Primary output.** Decompiled with [REA](../../tools/rea) (`rea --mcp`, JADX lane, jadx-headless-mcp 0.7.1). One file per class, rebuilt method by method. |
| `rea/rea-manifest.json` | Per-method record from rea: signature, overload index, Java vs smali, body status, markers. |
| `vineflower/` | Same 100 classes decompiled with Vineflower 1.10.1 as complete compilation units (package, imports, class headers). Use alongside `rea/` when you need the full file shape. |
| `resources/` | `plugin.yml`, `config.yml`, `items.yml`, `storage.yml`, `geyser_players.yml`, `lang/*.yml` from the jar. |
| `scripts/` | `mcp.mjs` (tiny MCP stdio client) and `extract.mjs` (the rea extraction driver) to reproduce `rea/`. |

Only LifeStealZ's own code (`com.zetaplugins.lifestealz.*`) is included. The
jar also shades `com.zetaplugins.lifestealz.libs.faststats`, `com.zetaplugins.zetacore`
(incl. bStats) and HikariCP — those are third-party libraries and were left out.

## How rea was used

rea targets APKs, but its JADX engine reads `.class` files from any ZIP, so the
jar was analysed as a renamed copy (`lifestealz-2.21.1.apk`):

1. `search_android_classes` → 491 classes in the jar, 100 top-level LifeStealZ classes.
2. `inspect_android_class` for each class and its inner classes (19) → fields + method inventory.
3. `inspect_android_method` for every method with an explicit `overload_index`.

`inspect_android_package` fails by design (`AndroidManifest.xml not found`) — a
Bukkit plugin has no Android manifest; class/method tools are unaffected.

### Result

| | Count |
|---|---|
| Classes (top-level + inner) | 119 / 119 in the jar |
| Methods | 834, 0 errors |
| Java bodies | 767 |
| Smali fallback | 67 — lambdas (JADX already inlines them at their call sites), trivial constructors, enum `values()/valueOf()`, and `<clinit>` (whose code appears as field initialisers) |
| Abstract/interface methods (no body) | 35 |

**Limits of the rea view:** rea returns members, not whole files — so class
modifiers, `extends`/`implements`, imports and annotations are missing, and
field types are fully qualified. Each file says so in its header; use
`vineflower/` for that information.

## Plugin overview (from the decompiled code)

- **Entry point** `LifeStealZ` (`plugin.yml` → `main`). `onEnable` wires managers
  (language, config, storage, recipes, grace period, bypass, webhooks, caches),
  then auto-registers commands and listeners by package scan
  (`AutoCommandRegistrar` / `AutoEventRegistrar` from ZetaCore).
- **Commands** `commands/`: `/lifestealz` (`/lsz`) with subcommands
  (data import/export, debug, dev, giveItem, gracePeriod, hearts, help,
  recipe, reload, bypass status), plus `/eliminate`, `/revive`, `/withdraw`, `/heart`.
- **Core heart logic** `listeners/PlayerDeathListener`: on death the victim loses
  `heartsPerKill` (or loses to nature if `looseHeartsToNature`), the killer
  gains them up to `MaxHeartsManager.getMaxHearts` (optionally dropping heart
  items instead, `dropHeartsIfMax`); dropping to `minHearts` triggers
  elimination. Each step fires a cancellable custom event (`events/death/*`) so
  addons can intervene.
- **Storage** `storage/`: `Storage` → `SQLStorage` → `SQLiteStorage` /
  `MySQLSyntaxStorage` → `MySQLStorage` / `MariaDBStorage`, wrapped in
  `CachedStorage`. Type comes from `storage.yml`; invalid values fall back to SQLite.
- **Items & blocks** `util/customitems`, `util/customblocks`: heart/revive items
  defined in `items.yml`, recipes, and the revive-beacon block with display-entity
  effects.
- **Integrations**: PlaceholderAPI expansion, WorldGuard flag (`HeartLossFlag`),
  Geyser/Floodgate Bedrock support, public API (`api/LifeStealZAPI`) and an addon loader.
- **Outbound network**: update check against `https://api.modrinth.com/v2/project/`
  (project `l8Uv7FzS`), optional Discord webhooks (URL from config), bStats and
  FastStats metrics (FastStats token is hard-coded in `LifeStealZ`).

## Reproduce

```bash
cp lifestealz-2.21.1.jar lifestealz-2.21.1.apk
export REA_JADX_MCP_JAR=/path/jadx-headless-mcp-0.7.1-all.jar   # SHA-256 6e5eacf5…ff75
node scripts/extract.mjs "$(npm bin)/rea" "$PWD/lifestealz-2.21.1.apk" ./rea
```

Requires Node, JDK 21, `rea-agents` (6.7.0 used) and a procps `ps`.
