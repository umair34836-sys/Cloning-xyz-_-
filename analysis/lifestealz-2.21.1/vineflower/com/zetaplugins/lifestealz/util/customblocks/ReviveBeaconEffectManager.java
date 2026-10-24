package com.zetaplugins.lifestealz.util.customblocks;

import com.zetaplugins.lifestealz.LifeStealZ;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class ReviveBeaconEffectManager {
   private final LifeStealZ plugin;
   private final Map<Location, BukkitTask> idleParticleBeacons;
   private final Map<Location, BukkitTask> revivingParticleBeacons;
   private final Map<Location, Set<BlockDisplay>> lasers;
   private final Map<Location, BukkitTask> laserGrowTasks;
   private final Map<Location, BlockDisplay> decoyDisplays;
   private final Map<Location, BukkitTask> bossbarTasks;
   private final Map<Location, BossBar> bossBars;

   public ReviveBeaconEffectManager(LifeStealZ plugin) {
      this.plugin = plugin;
      this.idleParticleBeacons = new HashMap<>();
      this.revivingParticleBeacons = new HashMap<>();
      this.lasers = new HashMap<>();
      this.laserGrowTasks = new HashMap<>();
      this.decoyDisplays = new HashMap<>();
      this.bossbarTasks = new HashMap<>();
      this.bossBars = new HashMap<>();
   }

   public void startIdleEffects(final Location location, boolean showEnchantParticles, Material decoyMaterial) {
      if (!this.idleParticleBeacons.containsKey(this.getKey(location)) && !this.decoyDisplays.containsKey(this.getKey(location))) {
         this.applyMaterialDecoy(location, decoyMaterial);
         if (showEnchantParticles) {
            BukkitTask runnable = (new BukkitRunnable() {
               final Location center = location.clone().add(0.5, 1.0, 0.5);

               public void run() {
                  this.center.getWorld().spawnParticle(Particle.ENCHANT, this.center, 25, 0.6, 0.5, 0.6, 0.0);
               }
            }).runTaskTimer(this.plugin, 0L, 10L);
            this.idleParticleBeacons.put(this.getKey(location), runnable);
         }
      }
   }

   public void startRevivingEffects(
      final Location location,
      String target,
      boolean showLaser,
      boolean showParticleRing,
      final ParticleColor particleColor,
      Material innerLaserMaterial,
      Material outerLaserMaterial,
      int reviveTime
   ) {
      if (!this.revivingParticleBeacons.containsKey(location) && !this.lasers.containsKey(location)) {
         if (showLaser) {
            this.spawnBeaconLaser(location, innerLaserMaterial, outerLaserMaterial);
         }

         location.getWorld().playSound(location, Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
         if (showParticleRing) {
            BukkitTask runnable = (new BukkitRunnable() {
               final Location center = location.clone().add(0.5, 1.0, 0.5);

               public void run() {
                  ReviveBeaconEffectManager.this.spawnRing(this.center, particleColor);
               }
            }).runTaskTimer(this.plugin, 0L, 10L);
            this.revivingParticleBeacons.put(this.getKey(location), runnable);
         }

         if (this.plugin.getConfig().getBoolean("showBossbar")) {
            this.startBossbarTask(location, target, reviveTime);
         }
      }
   }

   private void startBossbarTask(final Location location, final String target, final int reviveTime) {
      if (!this.bossbarTasks.containsKey(this.getKey(location))) {
         final BossBar bossBar = Bukkit.createBossBar(
            "",
            this.parseBossbarColor(this.plugin.getConfig().getString("bossbarColor"), BarColor.RED),
            this.parseBossbarStyle(this.plugin.getConfig().getString("bossbarStyle"), BarStyle.SOLID),
            new BarFlag[0]
         );
         bossBar.setVisible(true);
         this.bossBars.put(this.getKey(location), bossBar);
         BukkitTask bossbarTask = (new BukkitRunnable() {
               int timeleft = reviveTime;

               public void run() {
                  if (this.timeleft <= 0) {
                     bossBar.setVisible(false);
                     bossBar.removeAll();
                     ReviveBeaconEffectManager.this.bossBars.remove(ReviveBeaconEffectManager.this.getKey(location));
                     this.cancel();
                  } else {
                     int days = this.timeleft / 86400;
                     int hours = this.timeleft % 86400 / 3600;
                     int minutes = (this.timeleft & 3600) / 60;
                     int seconds = this.timeleft % 60;
                     String hFormatted = String.format("%02d", hours);
                     String mFormatted = String.format("%02d", minutes);
                     String sFormatted = String.format("%02d", seconds);

                     for (Player p : Bukkit.getOnlinePlayers()) {
                        bossBar.addPlayer(p);
                     }

                     bossBar.setProgress((double)this.timeleft / (double)reviveTime);
                     String title = ReviveBeaconEffectManager.this.plugin
                        .getLanguageManager()
                        .getString("reviveBossbarTitle")
                        .replace("&target&", target)
                        .replace("&remainingD&", String.valueOf(days))
                        .replace("&remainingH&", hFormatted)
                        .replace("&remainingM&", mFormatted)
                        .replace("&remainingS&", sFormatted)
                        .replace("&locationX&", String.valueOf(location.getBlockX()))
                        .replace("&locationY&", String.valueOf(location.getBlockY()))
                        .replace("&locationZ&", String.valueOf(location.getBlockZ()))
                        .replace("&location&", location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
                     bossBar.setTitle(ChatColor.translateAlternateColorCodes('&', title));
                     this.timeleft--;
                  }
               }
            })
            .runTaskTimer(this.plugin, 0L, 20L);
         this.bossbarTasks.put(this.getKey(location), bossbarTask);
      }
   }

   public void stopBossbarTask(Location location) {
      BukkitTask task = this.bossbarTasks.remove(this.getKey(location));
      if (task != null) {
         task.cancel();
      }
   }

   public void removeBossbar(Location location) {
      BossBar bossBar = this.bossBars.remove(this.getKey(location));
      if (bossBar != null) {
         bossBar.setVisible(false);
         bossBar.removeAll();
      }
   }

   private void applyMaterialDecoy(Location location, Material decoyMaterial) {
      if (!this.decoyDisplays.containsKey(this.getKey(location))) {
         BlockDisplay display = (BlockDisplay)location.getWorld().spawn(location, BlockDisplay.class);
         display.setBlock(decoyMaterial.createBlockData());
         display.setPersistent(true);
         display.setBrightness(new Brightness(8, 8));
         this.decoyDisplays.put(this.getKey(location), display);
         this.animateDecoyGrowth(display);
      }
   }

   private void animateDecoyGrowth(final BlockDisplay display) {
      float targetSize = 1.01F;
      float initialHeight = 0.0F;
      float width = 1.01F;
      long tickInterval = 1L;
      float growSpeed = 0.25F;
      display.setTransformation(
         new Transformation(new Vector3f(-0.004999995F, 0.5F, -0.004999995F), new Quaternionf(), new Vector3f(1.01F, 0.0F, 1.01F), new Quaternionf())
      );
      (new BukkitRunnable() {
            float currentHeight = 0.0F;

            public void run() {
               this.currentHeight += 0.25F;
               if (this.currentHeight >= 1.01F) {
                  this.currentHeight = 1.01F;
                  this.cancel();
               }

               float translationY = 0.5F - this.currentHeight / 2.0F;
               display.setTransformation(
                  new Transformation(
                     new Vector3f(-0.004999995F, translationY, -0.004999995F),
                     new Quaternionf(),
                     new Vector3f(1.01F, this.currentHeight, 1.01F),
                     new Quaternionf()
                  )
               );
            }
         })
         .runTaskTimer(this.plugin, 0L, 1L);
   }

   private void spawnRing(Location center, ParticleColor particleColor) {
      double radius = 1.5;
      int points = 25;
      Location ringCenter = center.clone().add(0.0, -0.5, 0.0);

      for (int i = 0; i < points; i++) {
         double angle = (Math.PI * 2) * (double)i / (double)points;
         double xOffset = Math.cos(angle) * radius;
         double zOffset = Math.sin(angle) * radius;
         center.getWorld()
            .spawnParticle(Particle.DUST, ringCenter.clone().add(xOffset, 0.25, zOffset), 1, 0.0, 0.0, 0.0, new DustOptions(particleColor.getColor(), 1.2F));
      }
   }

   private void spawnBeaconLaser(Location location, Material innerMaterial, Material outerMaterial) {
      float finalHeight = 150.0F;
      float width1 = 0.3F;
      float width2 = 0.5F;
      float growSpeed = 1.0F;
      Location quartzLoc = location.clone().add(0.35F, 0.0, 0.35F);
      Location glassLoc = location.clone().add(0.25, 0.0, 0.25);
      final BlockDisplay quartz = (BlockDisplay)location.getWorld().spawn(quartzLoc, BlockDisplay.class);
      quartz.setBlock(innerMaterial.createBlockData());
      quartz.setPersistent(true);
      final BlockDisplay glass = (BlockDisplay)location.getWorld().spawn(glassLoc, BlockDisplay.class);
      glass.setBlock(outerMaterial.createBlockData());
      glass.setPersistent(true);
      this.lasers.put(this.getKey(location), Set.of(quartz, glass));
      Vector3f initialQuartzScale = new Vector3f(0.3F, 0.1F, 0.3F);
      Vector3f initialGlassScale = new Vector3f(0.5F, 0.1F, 0.5F);
      final Quaternionf noRotation = new Quaternionf();
      final Vector3f translation = new Vector3f(0.0F, 0.0F, 0.0F);
      quartz.setTransformation(new Transformation(translation, noRotation, initialQuartzScale, noRotation));
      glass.setTransformation(new Transformation(translation, noRotation, initialGlassScale, noRotation));
      BukkitTask lasergrowTask = (new BukkitRunnable() {
         float currentHeight = 0.1F;

         public void run() {
            this.currentHeight++;
            if (this.currentHeight >= 150.0F) {
               this.currentHeight = 150.0F;
               this.cancel();
            }

            quartz.setTransformation(new Transformation(translation, noRotation, new Vector3f(0.3F, this.currentHeight, 0.3F), noRotation));
            glass.setTransformation(new Transformation(translation, noRotation, new Vector3f(0.5F, this.currentHeight, 0.5F), noRotation));
         }
      }).runTaskTimer(this.plugin, 0L, 1L);
      this.laserGrowTasks.put(this.getKey(location), lasergrowTask);
   }

   public void stopIdlePArticles(Location location) {
      BukkitTask task = this.idleParticleBeacons.remove(this.getKey(location));
      if (task != null) {
         task.cancel();
      }
   }

   public void stopRevivingParticles(Location location) {
      BukkitTask task = this.revivingParticleBeacons.remove(this.getKey(location));
      if (task != null) {
         task.cancel();
      }
   }

   public void removeLaser(Location location) {
      Location key = this.getKey(location);
      BukkitTask growTask = this.laserGrowTasks.remove(key);
      if (growTask != null) {
         growTask.cancel();
      }

      final Set<BlockDisplay> displays = this.lasers.remove(key);
      if (displays != null && !displays.isEmpty()) {
         float collapseSpeed = 1.0F;
         (new BukkitRunnable() {
               float currentHeight = displays.stream().findFirst().map(d -> d.getTransformation().getScale().y).orElse(150.0F);
               final float initialHeight = ReviveBeaconEffectManager.super.currentHeight;

               public void run() {
                  this.currentHeight--;
                  if (this.currentHeight <= 0.0F) {
                     for (BlockDisplay display : displays) {
                        if (display != null) {
                           display.remove();
                        }
                     }

                     this.cancel();
                  } else {
                     for (BlockDisplay displayx : displays) {
                        if (displayx != null) {
                           Vector3f originalScale = displayx.getTransformation().getScale();
                           float width = originalScale.x;
                           float yTranslation = (this.initialHeight - this.currentHeight) / 2.0F;
                           displayx.setTransformation(
                              new Transformation(
                                 new Vector3f(0.0F, yTranslation, 0.0F), new Quaternionf(), new Vector3f(width, this.currentHeight, width), new Quaternionf()
                              )
                           );
                        }
                     }
                  }
               }
            })
            .runTaskTimer(this.plugin, 0L, 1L);
      }
   }

   public void removeDecoy(Location location) {
      BlockDisplay display = this.decoyDisplays.remove(this.getKey(location));
      if (display != null) {
         display.remove();
      }
   }

   public void clearAllEffects(Location location) {
      this.stopIdlePArticles(location);
      this.stopRevivingParticles(location);
      this.removeLaser(location);
      this.removeDecoy(location);
      this.stopBossbarTask(location);
      this.removeBossbar(location);
   }

   public void clearAllEffects() {
      for (BukkitTask task : this.idleParticleBeacons.values()) {
         task.cancel();
      }

      this.idleParticleBeacons.clear();

      for (BukkitTask task : this.revivingParticleBeacons.values()) {
         task.cancel();
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

      for (BukkitTask task : this.laserGrowTasks.values()) {
         task.cancel();
      }

      this.laserGrowTasks.clear();

      for (BlockDisplay displayx : this.decoyDisplays.values()) {
         if (displayx != null) {
            displayx.remove();
         }
      }

      this.decoyDisplays.clear();

      for (BukkitTask task : this.bossbarTasks.values()) {
         task.cancel();
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
      return new Location(location.getWorld(), (double)location.getBlockX(), (double)location.getBlockY(), (double)location.getBlockZ());
   }

   private BarColor parseBossbarColor(@Nullable String color, BarColor fallbackColor) {
      try {
         return color == null ? fallbackColor : BarColor.valueOf(color.toUpperCase());
      } catch (IllegalArgumentException var4) {
         return fallbackColor;
      }
   }

   private BarStyle parseBossbarStyle(@Nullable String style, BarStyle fallbackStyle) {
      try {
         return style == null ? fallbackStyle : BarStyle.valueOf(style.toUpperCase());
      } catch (IllegalArgumentException var4) {
         return fallbackStyle;
      }
   }
}
