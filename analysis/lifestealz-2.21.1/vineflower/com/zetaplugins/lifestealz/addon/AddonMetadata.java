package com.zetaplugins.lifestealz.addon;

public class AddonMetadata {
   private final String name;
   private final String version;
   private final String author;
   private final String description;
   private final String configName;

   private AddonMetadata(AddonMetadata.Builder builder) {
      this.name = builder.name;
      this.version = builder.version;
      this.author = builder.author;
      this.description = builder.description;
      this.configName = builder.configName;
   }

   public String getName() {
      return this.name;
   }

   public String getVersion() {
      return this.version;
   }

   public String getAuthor() {
      return this.author;
   }

   public String getDescription() {
      return this.description;
   }

   public String getConfigName() {
      return this.configName;
   }

   public static class Builder {
      private String name;
      private String version;
      private String author;
      private String description;
      private String configName;

      public AddonMetadata.Builder name(String name) {
         this.name = name;
         return this;
      }

      public AddonMetadata.Builder version(String version) {
         this.version = version;
         return this;
      }

      public AddonMetadata.Builder author(String author) {
         this.author = author;
         return this;
      }

      public AddonMetadata.Builder description(String description) {
         this.description = description;
         return this;
      }

      public AddonMetadata.Builder config(String configName) {
         this.configName = configName;
         return this;
      }

      public AddonMetadata build() {
         if (this.name != null && this.version != null && this.author != null) {
            if (this.configName == null) {
               this.configName = this.name.toLowerCase().replaceAll("[^a-z0-9]", "");
            }

            return new AddonMetadata(this);
         } else {
            throw new IllegalStateException("Name, version, and author are required");
         }
      }
   }
}
