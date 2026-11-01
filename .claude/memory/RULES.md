# Rules

Standing rules jo har session me follow karni hain. Ye user ki di hui hidayaat se banti hain.

---

1. **Memory always on** — Jab bhi user instruction de, usay `INSTRUCTIONS.md` me store karo.
2. **Rules store karo** — Koi bhi nayi rule bane to `RULES.md` me add karo.
3. **Learnings store karo** — Jo bhi naya seekho (repo structure, tool usage, project convention) usay `LEARNINGS.md` me likho.
4. **Mistakes store karo** — Jo bhi ghalti ho, `MISTAKES.md` me likho + usse bachne ka tareeqa. Same ghalti dobara na duhrao.
5. **Keep memory up to date** — Har kaam ke baad memory files refresh karo, phir commit + push karo.
6. **Branch discipline** — Development designated branch par: `ccr-7349d8d1-89knkk`. Bina explicit permission ke doosri branch par push na karo.
7. **High-star preference** — Jab tool/repo ambiguous ho to popular / high-star wala prefer karo, lekin confirm user se.
8. **No PR without asking** — Pull request tab tak na banao jab tak user explicitly na kahe.

## Boundaries (2026-10-23)
- Reverse-engineering / APK analysis sirf **educational ya owner-authorized** maqsad ke liye.
- NAHI karna: game outcome "prediction"/win-hacks, kisi aur ke live backend par chalne wala clone, doosre users ka data nikalna, ya fairness/RNG todna. Chahe "fraud niyat nahi" kaha jaye — mechanism cheating/abuse ho to decline.
- Online games server-authoritative hote hain → client decrypt se winner predict karna technically bhi namumkin; aisi request ko educational explanation me convert karo.

- (2026-11-01, owner) Har build ke baad analyze karo: WorldLens top + angled shot lo, floating blocks (slab/fence ke neeche support), gaps, bachi grass/dirt, doosre area me spill check karke fix karo.

## FireCraft MC server PLAN (owner, 2026-11-01) — HAMESHA follow karo
- Player join → HUB (sky island). HUB se 6 lobbies ka rasta: SURVIVAL, LIFESTEAL, PARKOUR, PVP PRACTICE, MINIGAMES, EVENT.
- Abhi koi lobby bani nahi → sab NPCs "Coming Soon". Jab koi lobby banegi: us NPC ka naam/action update karke lobby se connect karna (FancyNpcs action), owner ke kehne par.
- HUB (aur future lobbies) me normal players ke liye SAARI commands band (RTP bhi) — jab tak owner specific command allow na kare. Owner/manager (`*`) unaffected.
- HUB + lobbies me players: no build, no destroy, no PvP (WorldGuard).
- Area → lobby NPC mapping: Area1 Survival (survival_npc), Area2 Ancient City → LIFESTEAL, Area3 End → PARKOUR, Area4 Water World → MINIGAMES, Area5 Nether Valley → EVENT, Area6 → PVP PRACTICE (pvp_npc).
- Kaam aaram se, step by step.
