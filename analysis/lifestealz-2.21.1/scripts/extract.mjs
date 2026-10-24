// Decompile every LifeStealZ class through rea's JADX lane (MCP) and rebuild one .java per class.
import { mkdir, writeFile } from "node:fs/promises";
import { dirname, join } from "node:path";
import { startRea } from "./mcp.mjs";

const [reaBin, apk, outDir] = process.argv.slice(2);
const PREFIX = "com.zetaplugins.lifestealz.";
const EXCLUDE = "com.zetaplugins.lifestealz.libs.";

const rea = startRea(reaBin, {});
await rea.init();
const call = async (name, args) => {
  const r = await rea.call(name, args);
  if (r.isError) throw new Error(r.content?.[0]?.text?.slice(0, 500) ?? "tool error");
  return r.structuredContent.normalized_result;
};

const indent = (text, pad) => text.split("\n").map((l) => (l ? pad + l : l)).join("\n");
const manifest = { tool: "rea (JADX headless 0.7.1)", classes: [] };

async function renderClass(className, depth) {
  const info = await call("inspect_android_class", { path: apk, class_name: className });
  const entry = { class_name: className, methods: [], inner_classes: [] };
  const pad = "    ".repeat(depth);
  const simple = className.slice(className.lastIndexOf(".") + 1).split("$").pop();
  const out = [`${pad}// rea: class ${className}`, `${pad}class ${simple} {`];
  for (const f of info.fields) out.push(`${pad}    ${f.reported_type} ${f.name};`);
  if (info.fields.length) out.push("");
  for (const m of info.methods) {
    const rec = { name: m.name, signature: m.reported_signature, overload_index: m.overload_index };
    try {
      const r = await call("inspect_android_method", {
        path: apk, class_name: className, method_name: m.name, overload_index: m.overload_index,
      });
      Object.assign(rec, { representation: r.representation, body_status: r.body_status, fell_back: r.fell_back, markers: r.markers, source_status: r.source?.status });
      const text = r.source?.text ?? "";
      if (r.representation !== "java") out.push(`${pad}    // rea: ${m.reported_signature} -> ${r.representation} (${r.markers.join(", ") || "no markers"})`);
      if (r.source?.status && r.source.status !== "complete") out.push(`${pad}    // rea: source ${r.source.status}`);
      out.push(r.representation === "java" ? indent(text, pad) : indent(text.split("\n").map((l) => "// " + l).join("\n"), pad + "    "));
      if (!text) out.push(`${pad}    // rea: ${m.reported_signature} — no body (${r.body_status}; ${r.markers.join(", ")})`);
    } catch (e) {
      rec.error = String(e.message).slice(0, 300);
      out.push(`${pad}    // rea: FAILED to decompile ${m.reported_signature}: ${rec.error.replace(/\n/g, " ")}`);
    }
    out.push("");
    entry.methods.push(rec);
  }
  for (const inner of info.inner_classes ?? []) {
    const innerName = typeof inner === "string" ? inner : inner.name ?? inner.class_name;
    try {
      const { text, entry: innerEntry } = await renderClass(innerName, depth + 1);
      out.push(text, "");
      entry.inner_classes.push(innerEntry);
    } catch (e) {
      out.push(`${pad}    // rea: FAILED inner class ${innerName}: ${String(e.message).slice(0, 200)}`);
      entry.inner_classes.push({ class_name: innerName, error: String(e.message).slice(0, 300) });
    }
  }
  out.push(`${pad}}`);
  return { text: out.join("\n"), entry };
}

const search = await call("search_android_classes", { path: apk, query: PREFIX });
const classes = search.matches.filter((c) => c.startsWith(PREFIX) && !c.startsWith(EXCLUDE));
console.error(`total classes in jar: ${search.total_classes}; LifeStealZ classes: ${classes.length}`);

for (const [i, className] of classes.entries()) {
  try {
    const { text, entry } = await renderClass(className, 0);
    const pkg = className.slice(0, className.lastIndexOf("."));
    const header = [
      "// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).",
      "// Reconstructed method-by-method: class modifiers, extends/implements and imports",
      "// are not part of rea's output, so this file is for reading, not compiling.",
      "",
      `package ${pkg};`,
      "",
    ].join("\n");
    const file = join(outDir, ...className.split(".")) + ".java";
    await mkdir(dirname(file), { recursive: true });
    await writeFile(file, header + text + "\n");
    manifest.classes.push(entry);
    console.error(`[${i + 1}/${classes.length}] ${className} (${entry.methods.length} methods)`);
  } catch (e) {
    manifest.classes.push({ class_name: className, error: String(e.message).slice(0, 300) });
    console.error(`[${i + 1}/${classes.length}] FAILED ${className}: ${e.message.slice(0, 200)}`);
  }
}
await writeFile(join(outDir, "rea-manifest.json"), JSON.stringify(manifest, null, 2) + "\n");
rea.close();
