// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.util.customblocks;
// rea: class com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager
class ReviveBeaconEffectManager {
    java.util.Map<org.bukkit.Location, org.bukkit.boss.BossBar> bossBars;
    java.util.Map<org.bukkit.Location, org.bukkit.scheduler.BukkitTask> bossbarTasks;
    java.util.Map<org.bukkit.Location, org.bukkit.entity.BlockDisplay> decoyDisplays;
    java.util.Map<org.bukkit.Location, org.bukkit.scheduler.BukkitTask> idleParticleBeacons;
    java.util.Map<org.bukkit.Location, org.bukkit.scheduler.BukkitTask> laserGrowTasks;
    java.util.Map<org.bukkit.Location, java.util.Set<org.bukkit.entity.BlockDisplay>> lasers;
    com.zetaplugins.lifestealz.LifeStealZ plugin;
    java.util.Map<org.bukkit.Location, org.bukkit.scheduler.BukkitTask> revivingParticleBeacons;

    public ReviveBeaconEffectManager(LifeStealZ plugin) {
        this.plugin = plugin;
    }

    /* JADX WARN: Type inference failed for: r0v6, types: [com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager$4] */
    private void animateDecoyGrowth(final BlockDisplay display) {
        display.setTransformation(new Transformation(new Vector3f(-0.004999995f, 0.5f, -0.004999995f), new Quaternionf(), new Vector3f(1.01f, 0.0f, 1.01f), new Quaternionf()));
        new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.4
            float currentHeight = 0.0f;

            public void run() {
                this.currentHeight += 0.25f;
                if (this.currentHeight >= 1.01f) {
                    this.currentHeight = 1.01f;
                    cancel();
                }
                float translationY = 0.5f - (this.currentHeight / 2.0f);
                display.setTransformation(new Transformation(new Vector3f(-0.004999995f, translationY, -0.004999995f), new Quaternionf(), new Vector3f(1.01f, this.currentHeight, 1.01f), new Quaternionf()));
            }
        }.runTaskTimer(this.plugin, 0L, 1L);
    }

    private void applyMaterialDecoy(Location location, Material decoyMaterial) {
        if (this.decoyDisplays.containsKey(getKey(location))) {
            return;
        }
        BlockDisplay display = (BlockDisplay) location.getWorld().spawn(location, BlockDisplay.class);
        display.setBlock(decoyMaterial.createBlockData());
        display.setPersistent(true);
        display.setBrightness(new Display.Brightness(8, 8));
        this.decoyDisplays.put(getKey(location), display);
        animateDecoyGrowth(display);
    }

    public void clearAllEffects(Location location) {
        stopIdlePArticles(location);
        stopRevivingParticles(location);
        removeLaser(location);
        removeDecoy(location);
        stopBossbarTask(location);
        removeBossbar(location);
    }

    public void clearAllEffects() {
        for (BukkitTask task : this.idleParticleBeacons.values()) {
            task.cancel();
        }
        this.idleParticleBeacons.clear();
        for (BukkitTask task2 : this.revivingParticleBeacons.values()) {
            task2.cancel();
        }
        this.revivingParticleBeacons.clear();
        for (Set<BlockDisplay> displays : this.lasers.values()) {
            if (displays != null) {
                for (BlockDisplay display : displays) {
                    if (display != null) {
                        display.remove();
                    }
                }
            }
        }
        this.lasers.clear();
        for (BukkitTask task3 : this.laserGrowTasks.values()) {
            task3.cancel();
        }
        this.laserGrowTasks.clear();
        for (BlockDisplay display2 : this.decoyDisplays.values()) {
            if (display2 != null) {
                display2.remove();
            }
        }
        this.decoyDisplays.clear();
        for (BukkitTask task4 : this.bossbarTasks.values()) {
            task4.cancel();
        }
        this.bossbarTasks.clear();
        for (BossBar bossBar : this.bossBars.values()) {
            if (bossBar != null) {
                bossBar.setVisible(false);
                bossBar.removeAll();
            }
        }
        this.bossBars.clear();
    }

    private Location getKey(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private BarColor parseBossbarColor(@Nullable String color, BarColor fallbackColor) {
        if (color == null) {
            return fallbackColor;
        }
        try {
            return BarColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallbackColor;
        }
    }

    private BarStyle parseBossbarStyle(@Nullable String style, BarStyle fallbackStyle) {
        if (style == null) {
            return fallbackStyle;
        }
        try {
            return BarStyle.valueOf(style.toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallbackStyle;
        }
    }

    public void removeBossbar(Location location) {
        BossBar bossBar = this.bossBars.remove(getKey(location));
        if (bossBar != null) {
            bossBar.setVisible(false);
            bossBar.removeAll();
        }
    }

    public void removeDecoy(Location location) {
        BlockDisplay display = this.decoyDisplays.remove(getKey(location));
        if (display != null) {
            display.remove();
        }
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager$6] */
    public void removeLaser(Location location) {
        Location key = getKey(location);
        BukkitTask growTask = this.laserGrowTasks.remove(key);
        if (growTask != null) {
            growTask.cancel();
        }
        final Set<BlockDisplay> displays = this.lasers.remove(key);
        if (displays == null || displays.isEmpty()) {
            return;
        }
        new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.6
            float currentHeight;
            final float initialHeight;

            {
                this.currentHeight = ((Float) displays.stream().findFirst().map(d -> {
                    return Float.valueOf(d.getTransformation().getScale().y);
                }).orElse(Float.valueOf(150.0f))).floatValue();
                this.initialHeight = this.currentHeight;
            }

            public void run() {
                this.currentHeight -= 1.0f;
                if (this.currentHeight <= 0.0f) {
                    for (BlockDisplay display : displays) {
                        if (display != null) {
                            display.remove();
                        }
                    }
                    cancel();
                    return;
                }
                for (BlockDisplay display2 : displays) {
                    if (display2 != null) {
                        Vector3f originalScale = display2.getTransformation().getScale();
                        float width = originalScale.x;
                        float yTranslation = (this.initialHeight - this.currentHeight) / 2.0f;
                        display2.setTransformation(new Transformation(new Vector3f(0.0f, yTranslation, 0.0f), new Quaternionf(), new Vector3f(width, this.currentHeight, width), new Quaternionf()));
                    }
                }
            }
        }.runTaskTimer(this.plugin, 0L, 1L);
    }

    /* JADX WARN: Type inference failed for: r0v31, types: [com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager$5] */
    private void spawnBeaconLaser(Location location, Material innerMaterial, Material outerMaterial) {
        Location quartzLoc = location.clone().add(0.3499999940395355d, 0.0d, 0.3499999940395355d);
        Location glassLoc = location.clone().add(0.25d, 0.0d, 0.25d);
        final BlockDisplay quartz = location.getWorld().spawn(quartzLoc, BlockDisplay.class);
        quartz.setBlock(innerMaterial.createBlockData());
        quartz.setPersistent(true);
        final BlockDisplay glass = location.getWorld().spawn(glassLoc, BlockDisplay.class);
        glass.setBlock(outerMaterial.createBlockData());
        glass.setPersistent(true);
        this.lasers.put(getKey(location), Set.of(quartz, glass));
        Vector3f initialQuartzScale = new Vector3f(0.3f, 0.1f, 0.3f);
        Vector3f initialGlassScale = new Vector3f(0.5f, 0.1f, 0.5f);
        final Quaternionf noRotation = new Quaternionf();
        final Vector3f translation = new Vector3f(0.0f, 0.0f, 0.0f);
        quartz.setTransformation(new Transformation(translation, noRotation, initialQuartzScale, noRotation));
        glass.setTransformation(new Transformation(translation, noRotation, initialGlassScale, noRotation));
        BukkitTask lasergrowTask = new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.5
            float currentHeight = 0.1f;

            public void run() {
                this.currentHeight += 1.0f;
                if (this.currentHeight >= 150.0f) {
                    this.currentHeight = 150.0f;
                    cancel();
                }
                quartz.setTransformation(new Transformation(translation, noRotation, new Vector3f(0.3f, this.currentHeight, 0.3f), noRotation));
                glass.setTransformation(new Transformation(translation, noRotation, new Vector3f(0.5f, this.currentHeight, 0.5f), noRotation));
            }
        }.runTaskTimer(this.plugin, 0L, 1L);
        this.laserGrowTasks.put(getKey(location), lasergrowTask);
    }

    private void spawnRing(Location center, ParticleColor particleColor) {
        Location ringCenter = center.clone().add(0.0d, -0.5d, 0.0d);
        for (int i = 0; i < 25; i++) {
            double angle = (6.283185307179586d * ((double) i)) / ((double) 25);
            double xOffset = Math.cos(angle) * 1.5d;
            double zOffset = Math.sin(angle) * 1.5d;
            center.getWorld().spawnParticle(Particle.DUST, ringCenter.clone().add(xOffset, 0.25d, zOffset), 1, 0.0d, 0.0d, 0.0d, new Particle.DustOptions(particleColor.getColor(), 1.2f));
        }
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager$3] */
    private void startBossbarTask(final Location location, final String target, final int reviveTime) {
        if (this.bossbarTasks.containsKey(getKey(location))) {
            return;
        }
        final BossBar bossBar = Bukkit.createBossBar("", parseBossbarColor(this.plugin.getConfig().getString("bossbarColor"), BarColor.RED), parseBossbarStyle(this.plugin.getConfig().getString("bossbarStyle"), BarStyle.SOLID), new BarFlag[0]);
        bossBar.setVisible(true);
        this.bossBars.put(getKey(location), bossBar);
        BukkitTask bossbarTask = new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.3
            int timeleft;

            {
                this.timeleft = reviveTime;
            }

            public void run() {
                if (this.timeleft <= 0) {
                    bossBar.setVisible(false);
                    bossBar.removeAll();
                    ReviveBeaconEffectManager.this.bossBars.remove(ReviveBeaconEffectManager.this.getKey(location));
                    cancel();
                    return;
                }
                int days = this.timeleft / 86400;
                int hours = (this.timeleft % 86400) / 3600;
                int minutes = (this.timeleft & 3600) / 60;
                int seconds = this.timeleft % 60;
                String hFormatted = String.format("%02d", Integer.valueOf(hours));
                String mFormatted = String.format("%02d", Integer.valueOf(minutes));
                String sFormatted = String.format("%02d", Integer.valueOf(seconds));
                for (Player p : Bukkit.getOnlinePlayers()) {
                    bossBar.addPlayer(p);
                }
                bossBar.setProgress(((double) this.timeleft) / ((double) reviveTime));
                String title = ReviveBeaconEffectManager.this.plugin.getLanguageManager().getString("reviveBossbarTitle").replace("&target&", target).replace("&remainingD&", String.valueOf(days)).replace("&remainingH&", hFormatted).replace("&remainingM&", mFormatted).replace("&remainingS&", sFormatted).replace("&locationX&", String.valueOf(location.getBlockX())).replace("&locationY&", String.valueOf(location.getBlockY())).replace("&locationZ&", String.valueOf(location.getBlockZ())).replace("&location&", location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
                bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', title));
                this.timeleft--;
            }
        }.runTaskTimer(this.plugin, 0L, 20L);
        this.bossbarTasks.put(getKey(location), bossbarTask);
    }

    /* JADX WARN: Type inference failed for: r0v8, types: [com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager$1] */
    public void startIdleEffects(final Location location, boolean showEnchantParticles, Material decoyMaterial) {
        if (this.idleParticleBeacons.containsKey(getKey(location)) || this.decoyDisplays.containsKey(getKey(location))) {
            return;
        }
        applyMaterialDecoy(location, decoyMaterial);
        if (showEnchantParticles) {
            BukkitTask runnable = new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.1
                final Location center;

                {
                    this.center = location.clone().add(0.5d, 1.0d, 0.5d);
                }

                public void run() {
                    this.center.getWorld().spawnParticle(Particle.ENCHANT, this.center, 25, 0.6d, 0.5d, 0.6d, 0.0d);
                }
            }.runTaskTimer(this.plugin, 0L, 10L);
            this.idleParticleBeacons.put(getKey(location), runnable);
        }
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager$2] */
    public void startRevivingEffects(final Location location, String target, boolean showLaser, boolean showParticleRing, final ParticleColor particleColor, Material innerLaserMaterial, Material outerLaserMaterial, int reviveTime) {
        if (this.revivingParticleBeacons.containsKey(location) || this.lasers.containsKey(location)) {
            return;
        }
        if (showLaser) {
            spawnBeaconLaser(location, innerLaserMaterial, outerLaserMaterial);
        }
        location.getWorld().playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
        if (showParticleRing) {
            BukkitTask runnable = new BukkitRunnable() { // from class: com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.2
                final Location center;

                {
                    this.center = location.clone().add(0.5d, 1.0d, 0.5d);
                }

                public void run() {
                    ReviveBeaconEffectManager.this.spawnRing(this.center, particleColor);
                }
            }.runTaskTimer(this.plugin, 0L, 10L);
            this.revivingParticleBeacons.put(getKey(location), runnable);
        }
        if (this.plugin.getConfig().getBoolean("showBossbar")) {
            startBossbarTask(location, target, reviveTime);
        }
    }

    public void stopBossbarTask(Location location) {
        BukkitTask task = this.bossbarTasks.remove(getKey(location));
        if (task != null) {
            task.cancel();
        }
    }

    public void stopIdlePArticles(Location location) {
        BukkitTask task = this.idleParticleBeacons.remove(getKey(location));
        if (task != null) {
            task.cancel();
        }
    }

    public void stopRevivingParticles(Location location) {
        BukkitTask task = this.revivingParticleBeacons.remove(getKey(location));
        if (task != null) {
            task.cancel();
        }
    }

    // rea: class com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.AnonymousClass1
    class AnonymousClass1 {
        org.bukkit.Location center;
        com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager this$0;
        org.bukkit.Location val$location;

                    {
                        this.center = location.clone().add(0.5d, 1.0d, 0.5d);
                    }

                    public void run() {
                        this.center.getWorld().spawnParticle(Particle.ENCHANT, this.center, 25, 0.6d, 0.5d, 0.6d, 0.0d);
                    }

    }

    // rea: class com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.AnonymousClass2
    class AnonymousClass2 {
        org.bukkit.Location center;
        com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager this$0;
        org.bukkit.Location val$location;
        com.zetaplugins.lifestealz.util.customblocks.ParticleColor val$particleColor;

                    {
                        this.center = location.clone().add(0.5d, 1.0d, 0.5d);
                    }

                    public void run() {
                        ReviveBeaconEffectManager.this.spawnRing(this.center, particleColor);
                    }

    }

    // rea: class com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.AnonymousClass3
    class AnonymousClass3 {
        com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager this$0;
        int timeleft;
        org.bukkit.boss.BossBar val$bossBar;
        int val$countdown;
        org.bukkit.Location val$location;
        java.lang.String val$target;

                {
                    this.timeleft = reviveTime;
                }

                public void run() {
                    if (this.timeleft <= 0) {
                        bossBar.setVisible(false);
                        bossBar.removeAll();
                        ReviveBeaconEffectManager.this.bossBars.remove(ReviveBeaconEffectManager.this.getKey(location));
                        cancel();
                        return;
                    }
                    int days = this.timeleft / 86400;
                    int hours = (this.timeleft % 86400) / 3600;
                    int minutes = (this.timeleft & 3600) / 60;
                    int seconds = this.timeleft % 60;
                    String hFormatted = String.format("%02d", Integer.valueOf(hours));
                    String mFormatted = String.format("%02d", Integer.valueOf(minutes));
                    String sFormatted = String.format("%02d", Integer.valueOf(seconds));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        bossBar.addPlayer(p);
                    }
                    bossBar.setProgress(((double) this.timeleft) / ((double) reviveTime));
                    String title = ReviveBeaconEffectManager.this.plugin.getLanguageManager().getString("reviveBossbarTitle").replace("&target&", target).replace("&remainingD&", String.valueOf(days)).replace("&remainingH&", hFormatted).replace("&remainingM&", mFormatted).replace("&remainingS&", sFormatted).replace("&locationX&", String.valueOf(location.getBlockX())).replace("&locationY&", String.valueOf(location.getBlockY())).replace("&locationZ&", String.valueOf(location.getBlockZ())).replace("&location&", location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
                    bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', title));
                    this.timeleft--;
                }

    }

    // rea: class com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.AnonymousClass4
    class AnonymousClass4 {
        float currentHeight;
        com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager this$0;
        org.bukkit.entity.BlockDisplay val$display;

        // rea: <init>(com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager, org.bukkit.entity.BlockDisplay): void -> smali (java_body_unavailable)
        // .method <init>(Lcom/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager;Lorg/bukkit/entity/BlockDisplay;)V
        //     .param final mandated null
        //     .param final synthetic null
        //     .max stack 2
        //     .max locals 3
        // 
        //     .local 0 "this" Lcom/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$4;
        //     .local 1 "this$0" Lcom/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager;
        //     .line 223
        //     aload 0
        //     aload 1
        //     putfield com/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$4 this$0 Lcom/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager;
        //     aload 0
        //     aload 2
        //     putfield com/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$4 val$display Lorg/bukkit/entity/BlockDisplay;
        //     aload 0
        //     invokespecial org/bukkit/scheduler/BukkitRunnable <init> ()V
        //     .line 224
        //     aload 0
        //     fconst_0
        //     putfield com/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$4 currentHeight F
        //     return
        // .end method

                public void run() {
                    this.currentHeight += 0.25f;
                    if (this.currentHeight >= 1.01f) {
                        this.currentHeight = 1.01f;
                        cancel();
                    }
                    float translationY = 0.5f - (this.currentHeight / 2.0f);
                    display.setTransformation(new Transformation(new Vector3f(-0.004999995f, translationY, -0.004999995f), new Quaternionf(), new Vector3f(1.01f, this.currentHeight, 1.01f), new Quaternionf()));
                }

    }

    // rea: class com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.AnonymousClass5
    class AnonymousClass5 {
        float currentHeight;
        com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager this$0;
        org.bukkit.entity.BlockDisplay val$glass;
        org.joml.Quaternionf val$noRotation;
        org.bukkit.entity.BlockDisplay val$quartz;
        org.joml.Vector3f val$translation;

        // rea: <init>(com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager, org.bukkit.entity.BlockDisplay, org.joml.Vector3f, org.joml.Quaternionf, org.bukkit.entity.BlockDisplay): void -> smali (java_body_unavailable)
        // .method <init>(Lcom/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager;Lorg/bukkit/entity/BlockDisplay;Lorg/joml/Vector3f;Lorg/joml/Quaternionf;Lorg/bukkit/entity/BlockDisplay;)V
        //     .param final mandated null
        //     .param final synthetic null
        //     .param final synthetic null
        //     .param final synthetic null
        //     .param final synthetic null
        //     .max stack 2
        //     .max locals 6
        // 
        //     .local 0 "this" Lcom/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$5;
        //     .local 1 "this$0" Lcom/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager;
        //     .line 308
        //     aload 0
        //     aload 1
        //     putfield com/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$5 this$0 Lcom/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager;
        //     aload 0
        //     aload 2
        //     putfield com/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$5 val$quartz Lorg/bukkit/entity/BlockDisplay;
        //     aload 0
        //     aload 3
        //     putfield com/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$5 val$translation Lorg/joml/Vector3f;
        //     aload 0
        //     aload 4
        //     putfield com/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$5 val$noRotation Lorg/joml/Quaternionf;
        //     aload 0
        //     aload 5
        //     putfield com/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$5 val$glass Lorg/bukkit/entity/BlockDisplay;
        //     aload 0
        //     invokespecial org/bukkit/scheduler/BukkitRunnable <init> ()V
        //     .line 309
        //     aload 0
        //     ldc 0.1f
        //     putfield com/zetaplugins/lifestealz/util/customblocks/ReviveBeaconEffectManager$5 currentHeight F
        //     return
        // .end method

                public void run() {
                    this.currentHeight += 1.0f;
                    if (this.currentHeight >= 150.0f) {
                        this.currentHeight = 150.0f;
                        cancel();
                    }
                    quartz.setTransformation(new Transformation(translation, noRotation, new Vector3f(0.3f, this.currentHeight, 0.3f), noRotation));
                    glass.setTransformation(new Transformation(translation, noRotation, new Vector3f(0.5f, this.currentHeight, 0.5f), noRotation));
                }

    }

    // rea: class com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager.AnonymousClass6
    class AnonymousClass6 {
        float currentHeight;
        float initialHeight;
        com.zetaplugins.lifestealz.util.customblocks.ReviveBeaconEffectManager this$0;
        java.util.Set val$displays;

                {
                    this.currentHeight = ((Float) displays.stream().findFirst().map(d -> {
                        return Float.valueOf(d.getTransformation().getScale().y);
                    }).orElse(Float.valueOf(150.0f))).floatValue();
                    this.initialHeight = this.currentHeight;
                }

        // rea: lambda$$0(org.bukkit.entity.BlockDisplay): java.lang.Float -> smali (java_body_unavailable)
        // .method private static synthetic lambda$$0(Lorg/bukkit/entity/BlockDisplay;)Ljava/lang/Float;
        //     .max stack 1
        //     .max locals 1
        // 
        //     .local 0 "d" Lorg/bukkit/entity/BlockDisplay;
        //     .line 368
        //     aload 0
        //     invokeinterface org/bukkit/entity/BlockDisplay getTransformation ()Lorg/bukkit/util/Transformation;
        //     invokevirtual org/bukkit/util/Transformation getScale ()Lorg/joml/Vector3f;
        //     getfield org/joml/Vector3f y F
        //     invokestatic java/lang/Float valueOf (F)Ljava/lang/Float;
        //     areturn
        // .end method

                public void run() {
                    this.currentHeight -= 1.0f;
                    if (this.currentHeight <= 0.0f) {
                        for (BlockDisplay display : displays) {
                            if (display != null) {
                                display.remove();
                            }
                        }
                        cancel();
                        return;
                    }
                    for (BlockDisplay display2 : displays) {
                        if (display2 != null) {
                            Vector3f originalScale = display2.getTransformation().getScale();
                            float width = originalScale.x;
                            float yTranslation = (this.initialHeight - this.currentHeight) / 2.0f;
                            display2.setTransformation(new Transformation(new Vector3f(0.0f, yTranslation, 0.0f), new Quaternionf(), new Vector3f(width, this.currentHeight, width), new Quaternionf()));
                        }
                    }
                }

    }

}
