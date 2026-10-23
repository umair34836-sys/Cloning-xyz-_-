# Tools

User ne jo tools install karne ko kaha, unki final status. Sab git **submodules** ke
tor par `tools/` me add kiye gaye hain (shallow, `--depth 1`).

| Tool (user ka naam) | Repo (submodule) | Stars (approx) | Path | Status |
|---------------------|------------------|----------------|------|--------|
| Agent reach | [`Panniantong/Agent-Reach`](https://github.com/Panniantong/Agent-Reach) | ~101k | `tools/agent-reach` | ✅ installed |
| Rea / reverse-engineering-anything | [`zhaoxuya520/reverse-skill`](https://github.com/zhaoxuya520/reverse-skill) | ~43k | `tools/reverse-skill` | ✅ installed (best-guess) |
| Ecc | [`affaan-m/ECC`](https://github.com/affaan-m/ECC) (Everything Claude Code) | ~287k | `tools/ecc` | ✅ installed (best-guess) |
| Wishpher (Whisper) | [`openai/whisper`](https://github.com/openai/whisper) | ~111k | `tools/whisper` | ✅ installed |
| Agency agents | [`msitarzewski/agency-agents`](https://github.com/msitarzewski/agency-agents) | ~163k | `tools/agency-agents` | ✅ installed |
| UI/UX Pro Max | [`prakharbhanu88-star/uiux-pro-max-skill`](https://github.com/prakharbhanu88-star/uiux-pro-max-skill) | ~8 | `tools/uiux-pro-max` | ⚠️ installed (best-guess, low confidence) |
| GraftV | — | — | — | ❌ **unresolved** — GitHub par koi clear match nahi mila. User se exact URL chahiye. |

## Submodules clone karna (fresh checkout ke baad)

```bash
git submodule update --init --recursive
```

## Note

- UI/UX Pro Max ka high-star canonical repo clear nahi tha; closest name-match pick ki.
- GraftV ke liye koi repo nahi mili — user URL de to add kar denge.
