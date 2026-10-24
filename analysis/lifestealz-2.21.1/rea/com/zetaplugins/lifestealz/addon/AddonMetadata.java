// Decompiled with REA (morluto/rea) via its JADX lane (jadx-headless-mcp 0.7.1).
// Reconstructed method-by-method: class modifiers, extends/implements and imports
// are not part of rea's output, so this file is for reading, not compiling.

package com.zetaplugins.lifestealz.addon;
// rea: class com.zetaplugins.lifestealz.addon.AddonMetadata
class AddonMetadata {
    java.lang.String author;
    java.lang.String configName;
    java.lang.String description;
    java.lang.String name;
    java.lang.String version;

    private AddonMetadata(Builder builder) {
        this.name = builder.name;
        this.version = builder.version;
        this.author = builder.author;
        this.description = builder.description;
        this.configName = builder.configName;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getConfigName() {
        return this.configName;
    }

    public String getDescription() {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    // rea: class com.zetaplugins.lifestealz.addon.AddonMetadata.Builder
    class Builder {
        java.lang.String author;
        java.lang.String configName;
        java.lang.String description;
        java.lang.String name;
        java.lang.String version;

        // rea: <init>(): void -> smali (java_body_unavailable)
        // .method public <init>()V
        //     .max stack 1
        //     .max locals 1
        // 
        //     .local 0 "this" Lcom/zetaplugins/lifestealz/addon/AddonMetadata$Builder;
        //     .line 18
        //     aload 0
        //     invokespecial java/lang/Object <init> ()V
        //     return
        // .end method

            public Builder author(String author) {
                this.author = author;
                return this;
            }

            public AddonMetadata build() {
                if (this.name == null || this.version == null || this.author == null) {
                    throw new IllegalStateException("Name, version, and author are required");
                }
                if (this.configName == null) {
                    this.configName = this.name.toLowerCase().replaceAll("[^a-z0-9]", "");
                }
                return new AddonMetadata(this);
            }

            public Builder config(String configName) {
                this.configName = configName;
                return this;
            }

            public Builder description(String description) {
                this.description = description;
                return this;
            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder version(String version) {
                this.version = version;
                return this;
            }

    }

}
