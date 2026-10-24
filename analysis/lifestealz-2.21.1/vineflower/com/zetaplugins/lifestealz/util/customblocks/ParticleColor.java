package com.zetaplugins.lifestealz.util.customblocks;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Color;

public enum ParticleColor {
   WHITE(new ParticleColor.Bounds(230, 255), new ParticleColor.Bounds(230, 255), new ParticleColor.Bounds(230, 255)),
   GRAY(new ParticleColor.Bounds(100, 155), new ParticleColor.Bounds(100, 155), new ParticleColor.Bounds(100, 155)),
   RED(new ParticleColor.Bounds(200, 255), new ParticleColor.Bounds(0, 39), new ParticleColor.Bounds(0, 39)),
   ORANGE(new ParticleColor.Bounds(230, 255), new ParticleColor.Bounds(100, 150), new ParticleColor.Bounds(0, 19)),
   YELLOW(new ParticleColor.Bounds(230, 255), new ParticleColor.Bounds(230, 255), new ParticleColor.Bounds(0, 39)),
   GREEN(new ParticleColor.Bounds(0, 39), new ParticleColor.Bounds(200, 255), new ParticleColor.Bounds(0, 39)),
   BLUE(new ParticleColor.Bounds(0, 39), new ParticleColor.Bounds(0, 39), new ParticleColor.Bounds(200, 255)),
   PURPLE(new ParticleColor.Bounds(150, 230), new ParticleColor.Bounds(0, 39), new ParticleColor.Bounds(150, 230)),
   PINK(new ParticleColor.Bounds(230, 255), new ParticleColor.Bounds(100, 150), new ParticleColor.Bounds(150, 230));

   private final ParticleColor.Bounds redBounds;
   private final ParticleColor.Bounds greenBounds;
   private final ParticleColor.Bounds blueBounds;

   private ParticleColor(ParticleColor.Bounds redBounds, ParticleColor.Bounds greenBounds, ParticleColor.Bounds blueBounds) {
      this.redBounds = redBounds;
      this.greenBounds = greenBounds;
      this.blueBounds = blueBounds;
   }

   public Color getColor() {
      ThreadLocalRandom rand = ThreadLocalRandom.current();
      int red = this.redBounds.random(rand);
      int green = this.greenBounds.random(rand);
      int blue = this.blueBounds.random(rand);
      return Color.fromRGB(red, green, blue);
   }

   public static ParticleColor fromString(String colorName) {
      try {
         return valueOf(colorName.toUpperCase());
      } catch (IllegalArgumentException var2) {
         return WHITE;
      }
   }

   private static record Bounds(int min, int max) {
      private Bounds(int min, int max) {
         if (min >= 0 && max <= 255 && min <= max) {
            this.min = min;
            this.max = max;
         } else {
            throw new IllegalArgumentException("Color bounds must be in range 0-255 and min ≤ max");
         }
      }

      public int random(ThreadLocalRandom rand) {
         return rand.nextInt(this.min, this.max + 1);
      }
   }
}
