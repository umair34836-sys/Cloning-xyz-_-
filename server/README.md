# FireCraft SMP — server ke chhote setups

## Night Vision: `/nv` (aur `/trigger nv`)

Har player (Java + Bedrock/Geyser) Night Vision khud ON/OFF kar sakta hai.

**Jo console se lag chuka hai (2026-10-29):**
- Scoreboard objective `nv` (trigger) — koi bhi player `/trigger nv` likh kar ON/OFF kare.
- Command blocks `world` me spawn ke neeche: x 673→682, y -63, z 56 (repeating + 9 chain), chunk [42, 3] forceloaded.
  - `nv_on` tag wale players ko har tick Night Vision (infinite, bina particles) — maut/doodh ke baad bhi wapas.
- Gamerule `command_block_output false` (ops ki chat/console spam na ho).

**`/nv` shortcut ke liye (ek dafa):** `skript/nv.sk` ko server ke `plugins/Skript/scripts/` me upload karo, phir console:
```
sk reload nv
```

**Hatana ho to:** `sk disable nv` (agar lagaya), `fill 673 -63 56 682 -63 56 minecraft:deepslate`,
`forceload remove 675 57`, `scoreboard objectives remove nv`, `tag @a remove nv_on`.
