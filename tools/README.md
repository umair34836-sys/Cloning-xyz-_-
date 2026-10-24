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
| Jev (TypeSafe AI) — agent skills | [`typesafe-ai/skills`](https://github.com/typesafe-ai/skills) | ~2.8k | `tools/jev-skills` | ✅ installed (v0.6.2) |
| Jev — Python SDK | [`typesafe-ai/typesafe-sdk-python`](https://github.com/typesafe-ai/typesafe-sdk-python) | ~300 | `tools/jev-sdk-python` | ✅ installed (v0.7.5) |
| Jev — JS/TS SDK | [`typesafe-ai/typesafe-sdk-js`](https://github.com/typesafe-ai/typesafe-sdk-js) | ~280 | `tools/jev-sdk-js` | ✅ installed (v0.7.0) |

## Submodules clone karna (fresh checkout ke baad)

```bash
git submodule update --init --depth 1
```

`.gitmodules` me har submodule par `shallow = true` set hai, isliye `git clone --recurse-submodules` bhi sirf latest commit (depth 1) layega.

## Note

- Saare 7 original tools installed hain (user ne UI/UX Pro Max aur Graft ke exact URLs de diye).
- 2026-10-24: Re-verify kiya — saare 7 submodules `--depth 1` se clone hue aur pinned commits par checkout ho gaye.

## Jev (TypeSafe AI) use karna

Jev ek hosted "System One" decision model hai (TypeSafe AI): text/images par typed
sawal (`Choice`, `Noul` yes/no, `Score`, `Box`) ka jawab probabilities ke saath deta hai.
Ye cloud API hai, is liye **`TYPESAFE_API_KEY`** chahiye (https://typesafe.ai se banao).

```bash
pip install typesafe-sdk          # Python (import: typesafe_sdk)
npm install @typesafe-ai/sdk      # Node.js 20+
export TYPESAFE_API_KEY=...       # key kabhi commit na karo
```

```python
from typesafe_sdk import Choice, TypeSafeClient
with TypeSafeClient() as client:
    r = client.system_one(
        state={"document": "I was charged twice."},
        questions={"category": Choice(instructions="What is this ticket about?",
                                      criteria={"billing": None, "technical": None, "other": None})},
    )
print(r.choices["category"].choice)
```

Claude Code plugin (apni machine par):

```bash
claude plugin marketplace add typesafe-ai/skills
claude plugin install typesafe@typesafe-ai     # phir: /typesafe:typesafe-ai
```

Optional: [`typesafe-ai/system-one-adapter-python`](https://github.com/typesafe-ai/system-one-adapter-python) —
same client interface, lekin TypeSafe ki jagah kisi LLM API par chalta hai.
