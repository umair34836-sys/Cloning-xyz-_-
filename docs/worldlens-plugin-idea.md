# WorldLens — Minecraft "camera" plugin (idea / spec)

Ek Paper plugin jo **bina server join kiye** console / RCON / HTTP se kisi bhi jagah ki
**picture (PNG)** bana de, taake admin ya Claude (AI) world ko visually dekh sake.

---

## 1. Basic info

| Cheez | Value |
|-------|-------|
| Naam | **WorldLens** (ya SnapCraft / CamCraft) |
| Platform | Paper 1.21.x (Spigot fallback optional), Java 21 |
| Build | Gradle (Kotlin DSL), sirf `paper-api` — **NMS nahi** (har update par tootega nahi) |
| Output | PNG + JSON metadata |
| Dependency | Koi nahi (CoreProtect / Discord optional) |

---

## 2. Commands

Sab commands console se bhi chalni chahiye (player hona zaroori nahi).

| Command | Kaam |
|---------|------|
| `/lens shot <world> <x> <y> <z> <yaw> <pitch> [w] [h] [fov]` | Us point se camera view ki picture |
| `/lens top <world> <x> <z> <radius>` | Upar se (top-down) map jaisi picture |
| `/lens iso <world> <x> <z> <radius>` | Isometric (tircha 3D) view — base/build dikhane ke liye best |
| `/lens orbit <world> <x> <y> <z> <distance>` | 4 directions (N/E/S/W) se 4 pictures ek grid me |
| `/lens player <name>` | Online player ki aankh se view (moderation ke liye) |
| `/lens cam add/remove/list <name>` | Fixed "CCTV cameras" save karo (spawn, shop, base) |
| `/lens cam shot <name>` | Saved camera ki picture |
| `/lens compare <name>` | Purani vs nayi picture — badlav **laal** rang me highlight (grief detection) |
| `/lens timelapse <cam> <interval>` | Har X minute picture → baad me GIF/MP4 |
| `/lens reload` | Config reload |

Permissions: `worldlens.shot`, `worldlens.player`, `worldlens.admin`, etc.

---

## 3. Render modes

1. **Perspective** — normal camera (yaw, pitch, FOV).
2. **Top-down** — har (x,z) column ka sabse upar wala block. Fast.
3. **Isometric** — 45° angle, buildings ki shape saaf dikhti hai.
4. **Panorama 360°** (v3) — equirectangular image.
5. **Cave / X-ray mode** (admin only) — stone transparent, ores/chests/spawners dikhen (x-ray cheaters ya hidden bases dhoondne ke liye).

---

## 4. Rendering kaise kaam karega (technical)

```
Command  ->  Main thread: required chunks ka ChunkSnapshot lo (thread-safe copy)
         ->  Async thread pool: har pixel ke liye ray cast (DDA voxel traversal)
         ->  Pehla non-air block mila -> uska rang + shading
         ->  BufferedImage -> PNG save / HTTP response / upload
```

- **ChunkSnapshot** use karo taake rendering async ho aur server lag na kare (TPS safe).
- **Unloaded chunks**: config se decide — skip karo (sky/fog dikhao) ya `getChunkAtAsync` se load karo.
- **Block colour**: ek built-in colour table (har Material ka average texture colour) — JSON file me,
  config se override ho sake. Fallback: Paper ka block map colour.
- **Shading**:
  - Face direction shading (upar = roshan, side = halka dark, neeche = dark)
  - Sky light + block light (snapshot se) → raat me torches ki roshni dikhe
  - Distance fog (door ki cheezein dhundli)
  - Paani / glass semi-transparent (ray aage chalti rahe, colour blend)
  - Sky gradient (time of day ke hisaab se) + nether/end ka alag sky
- **Entities**: players, mobs, item frames, armor stands ko chhote coloured box + naam tag ki tarah draw karo.
- **Anti-aliasing**: optional 2x supersampling.
- **Limits**: max resolution (e.g. 1920x1080), max view distance (e.g. 160 blocks), queue me ek waqt me 1–2 render.

### v3: Texture mode (optional)
Admin apni Minecraft client `.jar` se textures ek baar extract kare (plugin Mojang textures
ship nahi karega — copyright). Phir har ray hit par asli texture pixel sample ho → picture
real Minecraft jaisi lagegi.

---

## 5. Output

- `plugins/WorldLens/shots/<date>_<world>_<x>_<y>_<z>.png`
- Saath me `.json`:
  ```json
  {
    "world": "world", "x": 120, "y": 70, "z": -340, "yaw": 90, "pitch": 15,
    "time": "2026-10-25T14:00:00Z", "mode": "perspective",
    "top_blocks": {"STONE": 4210, "OAK_PLANKS": 812, "LAVA": 34},
    "entities": [{"type": "PLAYER", "name": "Ali", "x": 125, "y": 70, "z": -338}],
    "warnings": ["LAVA near spawn", "TNT x12"]
  }
  ```
  Ye JSON AI (Claude) ke liye bohot faida mand hai — picture + data dono milte hain.

---

## 6. Remote access (bina join kiye)

1. **Console / RCON** — sab commands.
2. **Built-in HTTP API** (chhota sa embedded server, token ke saath):
   ```
   GET /api/shot?world=world&x=120&y=70&z=-340&yaw=90&pitch=15&w=1280&h=720
   Header: Authorization: Bearer <token>
   -> image/png
   GET /api/top?world=world&x=0&z=0&radius=128
   GET /api/cam/<name>
   GET /api/info?...   -> sirf JSON (blocks/entities)
   ```
   Config me: port, token, allowed IPs, rate limit.
3. **Webhook upload** — picture Discord channel ya website admin panel par bhej do.
4. **Claude / MCP integration** — FireCraft admin MCP me tools add karo:
   `take_snapshot(world, x, y, z, yaw, pitch)`, `top_view(x, z, radius)`, `camera_shot(name)`,
   `compare_camera(name)`. Ye HTTP API call karein aur image return karein → Claude
   directly world dekh sakega.

---

## 7. Moderation features (Lifesteal SMP ke liye kaam ke)

- **Grief detection**: saved cameras ka har ghante auto-shot, pichli se compare, zyada badlav ho to Discord alert + diff image.
- **Report helper**: player report aaye to us location ki auto picture report ke saath attach.
- **CoreProtect hook** (optional): diff me badle hue blocks ke saath "kisne tora" bhi.
- **Base finder / X-ray mode**: admin-only, chests/spawners highlight.

---

## 8. Config (example)

```yaml
render:
  default-width: 1280
  default-height: 720
  max-width: 1920
  max-height: 1080
  max-distance: 160
  fov: 70
  supersampling: false
  load-unloaded-chunks: false
  threads: 2
  fog: true
  show-entities: true
output:
  folder: shots
  keep-days: 14
http:
  enabled: true
  port: 8765
  token: "CHANGE_ME"
  allowed-ips: ["127.0.0.1"]
  rate-limit-per-minute: 10
webhook:
  discord-url: ""
cameras: {}
```

---

## 9. Build phases (developer ko aise do)

| Phase | Kya banega |
|-------|-----------|
| **v1 (MVP)** | `/lens shot` + `/lens top`, async ChunkSnapshot renderer, colour table, face shading, PNG save, config, permissions |
| **v2** | Isometric, entities, light/fog/water, saved cameras, JSON metadata, HTTP API + token |
| **v3** | Compare/diff, timelapse, Discord webhook, MCP tools, texture mode, panorama, X-ray mode |

---

## 10. Acceptance checks (testing)

- Console se `/lens shot world 0 80 0 0 30` → 2 second ke andar PNG ban jaye.
- Render ke dauran TPS 19.5+ rahe (spark se check).
- Unloaded area par crash na ho.
- Galat token par HTTP 401, rate limit par 429.
- Paper 1.21.x ke latest build par chale, bina NMS.
