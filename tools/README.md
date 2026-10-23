# Tools

User ne jo tools install karne ko kaha, unki final status. Sab git **submodules** ke
tor par `tools/` me add kiye gaye hain (shallow, `--depth 1`).

| Tool (user ka naam) | Repo (submodule) | Stars (approx) | Path | Status |
|---------------------|------------------|----------------|------|--------|
| Agent reach | [`Panniantong/Agent-Reach`](https://github.com/Panniantong/Agent-Reach) | ~101k | `tools/agent-reach` | ✅ installed |
| Rea / reverse-engineering-anything | [`morluto/rea`](https://github.com/morluto/rea) (Reverse Engineer Anything) | ~371k | `tools/rea` | ✅ installed (confirmed) |
| Ecc | [`affaan-m/ECC`](https://github.com/affaan-m/ECC) (Everything Claude Code) | ~287k | `tools/ecc` | ✅ installed (best-guess) |
| Wishpher (Whisper) | [`openai/whisper`](https://github.com/openai/whisper) | ~111k | `tools/whisper` | ✅ installed |
| Agency agents | [`msitarzewski/agency-agents`](https://github.com/msitarzewski/agency-agents) | ~163k | `tools/agency-agents` | ✅ installed |
| UI/UX Pro Max | [`nextlevelbuilder/ui-ux-pro-max-skill`](https://github.com/nextlevelbuilder/ui-ux-pro-max-skill) | — | `tools/uiux-pro-max` | ✅ installed (user-confirmed) |
| GraftV / Graft | [`NanoNets/Graft`](https://github.com/NanoNets/Graft) | — | `tools/graft` | ✅ installed (user-confirmed) |

## Submodules clone karna (fresh checkout ke baad)

```bash
git submodule update --init --recursive
```

## Note

- Saare 7 tools ab installed hain (user ne UI/UX Pro Max aur Graft ke exact URLs de diye).
