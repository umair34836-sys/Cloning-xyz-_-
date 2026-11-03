# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Memory system (IMPORTANT — always on)

Is project me ek persistent memory system hai `.claude/memory/`. Har session me ye rules follow karo:

| File | Kaam |
|------|------|
| `.claude/memory/INSTRUCTIONS.md` | User ki har instruction yahan log karo |
| `.claude/memory/RULES.md` | Standing rules |
| `.claude/memory/LEARNINGS.md` | Jo naya seekho |
| `.claude/memory/MISTAKES.md` | Ghaltiyan + bachne ka tareeqa (same ghalti dobara na ho) |

Workflow har kaam ke baad:
1. Relevant memory file(s) update karo.
2. Commit karo clear message ke saath.
3. Push karo designated branch par.

## FCMC server rule (owner, 2 Nov 2026 — IMPORTANT)

FireCraft MC (FCMC) server ke **har session** me sirf **FCMC connector** (`mcp__FCMC__*`: worldlens_command / worldlens_status / worldlens_shot / worldlens_top / discord_*) use hoga.
**`mcp__worldlens__*` (WorldLens connector) FCMC kaam ke liye kabhi use mat karo** — wo doosra server (FireCraft SMP) hai.
Owner ke alfaaz: "fcmc server k her ek session me rule dalo k waha serf fcmc use hoga worldlens nhi".

## FCMC ENGLISH rule (owner, 3 Nov 2026 — IMPORTANT)

FCMC server par har player-facing cheez **English** me banao: holograms, text displays, NPC holo/messages, tellraw/title/actionbar, item names/lore, menus, kit lore, Discord topics/posts.
Owner ke alfaaz: "pore server ko hinglish se english me shift kerdoo ... her ek session ko samjha do k server ko english me agay se banana".
(Owner/manager ko session me ya tellraw me jawab Roman Urdu me de sakte ho — ye rule server content ke liye hai.)

## Branch

Development branch: `ccr-7349d8d1-89knkk`. Bina explicit permission ke doosri branch par push na karo. PR sirf tab banao jab user kahe.

## Tools

Collected/installed tools `tools/` directory me documented hain — dekho `tools/README.md`.
