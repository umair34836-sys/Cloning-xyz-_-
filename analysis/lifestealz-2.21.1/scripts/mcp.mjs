// Minimal MCP stdio client for `rea --mcp`.
import { spawn } from "node:child_process";
import readline from "node:readline";

export function startRea(reaBin, env) {
  const child = spawn(reaBin, ["--mcp"], { env: { ...process.env, ...env }, stdio: ["pipe", "pipe", "inherit"] });
  const pending = new Map();
  let nextId = 1;
  readline.createInterface({ input: child.stdout }).on("line", (line) => {
    let msg;
    try { msg = JSON.parse(line); } catch { return; }
    if (msg.id != null && pending.has(msg.id)) {
      const { resolve, reject } = pending.get(msg.id);
      pending.delete(msg.id);
      msg.error ? reject(new Error(JSON.stringify(msg.error))) : resolve(msg.result);
    }
  });
  const request = (method, params) => new Promise((resolve, reject) => {
    const id = nextId++;
    pending.set(id, { resolve, reject });
    child.stdin.write(JSON.stringify({ jsonrpc: "2.0", id, method, params }) + "\n");
  });
  const notify = (method, params) => child.stdin.write(JSON.stringify({ jsonrpc: "2.0", method, params }) + "\n");
  return {
    async init() {
      await request("initialize", { protocolVersion: "2025-06-18", capabilities: {}, clientInfo: { name: "lsz-decompile", version: "1" } });
      notify("notifications/initialized", {});
    },
    listTools: () => request("tools/list", {}),
    call: (name, args) => request("tools/call", { name, arguments: args }),
    close: () => child.stdin.end(),
  };
}
