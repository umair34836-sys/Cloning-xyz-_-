# detail_test.py <hd-textures-dir> <out-prefix> <round-name> [N]
# Har texture ka "detail score": high-frequency energy (luminance - blur), local contrast,
# alag rangon ki ginti. Har category ke sab se kam-detail textures ki list + contact sheet.
import sys, os, json, numpy as np
from PIL import Image, ImageDraw
root, pre, rnd = sys.argv[1], sys.argv[2], sys.argv[3]
N = int(sys.argv[4]) if len(sys.argv) > 4 else 60
LUM = np.array([0.299, 0.587, 0.114])
def blur(a):
    p = np.pad(a, 2, mode="edge"); out = np.zeros_like(a)
    for dy in range(5):
        for dx in range(5): out += p[dy:dy + a.shape[0], dx:dx + a.shape[1]]
    return out / 25
rows = []
for r, _, fs in os.walk(root):
    for f in fs:
        if not f.endswith((".png", ".tga")): continue
        p = os.path.join(r, f); rel = os.path.relpath(p, root)
        im = np.array(Image.open(p).convert("RGBA")).astype(float)
        h, w = im.shape[:2]
        if h > w and h % w == 0: im = im[:w]
        m = im[..., 3] > 0
        if m.sum() < 16: continue
        L = im[..., :3] @ LUM
        hf = np.abs(L - blur(L))[m].mean()
        cols = len(np.unique(im[..., :3][m].astype(np.uint8), axis=0))
        top = rel.split(os.sep)[0]
        cat = "entity" if top == "entity" else ("item" if top in ("item", "items") else ("block" if m.all() else "cutout"))
        score = hf * (1 + np.log1p(cols) / 6)
        rows.append((cat, score, hf, cols, rel))
summary = {}
for cat in ("block", "cutout", "item", "entity"):
    rs = sorted([x for x in rows if x[0] == cat], key=lambda x: x[1])
    if not rs: continue
    sc = np.array([x[1] for x in rs])
    summary[cat] = dict(n=len(rs), median=round(float(np.median(sc)), 2), p10=round(float(np.percentile(sc, 10)), 2),
                        low=[x[4] for x in rs[:N]])
    cols_, T = 12, 84
    chunk = rs[:N]; nrow = -(-len(chunk) // cols_)
    sh = Image.new("RGBA", (cols_ * T, nrow * (T + 12)), (40, 40, 46, 255)); dr = ImageDraw.Draw(sh)
    for i, x in enumerate(chunk):
        im = Image.open(os.path.join(root, x[4])).convert("RGBA"); w, h = im.size
        if h > w: im = im.crop((0, 0, w, w)); h = w
        s = 64 / max(w, h); im = im.resize((max(1, int(w * s)), max(1, int(h * s))), Image.NEAREST)
        X, Y = (i % cols_) * T, (i // cols_) * (T + 12)
        sh.paste((62, 62, 70, 255), (X + 1, Y + 1, X + T - 1, Y + T + 11)); sh.alpha_composite(im, (X + (T - im.width) // 2, Y + 4 + (64 - im.height) // 2))
        dr.text((X + 2, Y + T - 12), os.path.basename(x[4])[:-4][:14], fill=(255, 230, 90, 255))
    sh.save(f"{pre}_{rnd}_{cat}.png")
json.dump(summary, open(f"{pre}_{rnd}.json", "w"), indent=1)
for cat, d in summary.items():
    print(f"[{rnd}] {cat:7s} n={d['n']:4d} median={d['median']:6.2f} p10={d['p10']:6.2f}  lowest: {', '.join(os.path.basename(x)[:-4] for x in d['low'][:12])}")
