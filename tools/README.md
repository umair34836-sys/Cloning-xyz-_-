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
| 3D artist: Blender skills (94) | [`arjun988/blender-skills`](https://github.com/arjun988/blender-skills) | ~400 | `tools/blender-skills` | ✅ installed (texture-workflow, materials, realistic/voxel/pixel styles...) |
| 3D artist: CC Blender skill | [`RobLe3/cc-blender-skill`](https://github.com/RobLe3/cc-blender-skill) | — | `tools/cc-blender-skill` | ✅ installed (materials, lighting, look calibration) |
| 3D artist: Design skillstack | [`freshtechbro/claudedesignskills`](https://github.com/freshtechbro/claudedesignskills) | — | `tools/claudedesignskills` | ✅ installed (substance-3d-texturing PBR) |

## Submodules clone karna (fresh checkout ke baad)

```bash
git submodule update --init --recursive
```

## Note

- 3D artist skills (texture pack ke liye) bhi installed hain — seekhe hue rules `texture-pack/README.md` me.
- Saare 7 tools ab installed hain (user ne UI/UX Pro Max aur Graft ke exact URLs de diye).
