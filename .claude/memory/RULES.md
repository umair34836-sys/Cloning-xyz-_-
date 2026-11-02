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

## FCMC connector rule (2026-11-02, owner MrCopper804 via fcmc owner session)
- FireCraft MC (FCMC/HUB) ke saare kaam me SIRF FCMC connector use karo: `mcp__FCMC__worldlens_command/status/shot/top` aur `mcp__FCMC__discord_*`.
- `mcp__worldlens__*` (WorldLens connector) FCMC ke liye kabhi use NAHI — wo doosra server (FireCraft SMP) hai.

## FCMC manager active hours (owner, 2 Nov 2026)
- Claude manager ke liye sirf **subah 7 baje se shaam 5 baje (Pakistan time, PKT)** ON rahega = 02:00–11:59 UTC.
- Is waqt ke bahar FCMC manager chat check nahi hota. Cron: `* 2-11 * * *` (UTC).
- Manager (techshotgaming95yt) ko Discord DM se bata diya gaya hai.
