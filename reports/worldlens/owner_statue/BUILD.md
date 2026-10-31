# Owner 3D character statue (MrCopper804 skin)
Owner order (Claude chat, 31 Oct ~07:25 UTC): "jab me game me bolo k mere charector ko yaha build kerdo than mere is skin ko use ker k mera 3d charector game me full details k sath build ker dena"

Files: `MrCopper804_skin.png` (64x64 skin), `skin_statue.py` (generator), `statue_preview.png` (front / right side / back).
Model: 1 skin pixel = 1 block, ~16 wide x 10 deep x 33 tall, 1988 blocks, 116 merged `fill` commands.
Hood (hat layer) = real 3D shell 1 block around head; jacket/sleeve/pants overlays flattened. Skin tone = white_terracotta.

## Trigger (in-game watch)
Owner chat / self-whisper starting "server"/"claude" + "mera character / mere charector ko yaha build kerdo" (or similar).
Not destructive -> no confirm needed, but do the safety checks.

## Steps
1. `data get entity MrCopper804 Pos` + `Rotation` (yaw: 0 south, 90 west, ±180 north, -90 east).
2. Statue centre = 12 blocks ahead of owner in look direction (rounded to cardinal); statue FACING = opposite of owner's look (faces the owner).
3. Ground y: marker heightmap probe (`positioned over motion_blocking_no_leaves`) at centre -> oy = first air block.
4. Safety: bbox = centre ±8 (x/z) , oy..oy+33. No player inside bbox ±2 (`execute as @a[...]`); NOT owner house x640-652 z23-36; not over another build (shot first, ask owner if something is there).
5. Clear bbox to air (oy..oy+34), plinth: oy-1 footprint+1 = polished_deepslate, edge chiseled; then statue at oy.
6. `python3 skin_statue.py MrCopper804_skin.png <cx> <oy> <cz> <facing> cmds.txt` -> run every line of cmds.txt with worldlens_command (in file order: painter's order, do NOT reorder).
7. Birch sign in front: "MrCopper804" / "OWNER" / "FireCraft SMP". Optional lights around.
8. Shot from the front, player-stuck check, msg owner, log ingame_owner_handled.txt, commit + push.
