package com.vulzm.vulzm.client.module;

public enum ModuleCategory {
    ALL("category.vulzm.all"),
    HUD("category.vulzm.hud"),
    VISUAL("category.vulzm.visual"),
    MOVEMENT("category.vulzm.movement");

    private final String key;
    ModuleCategory(String key) { this.key = key; }
    public String key() { return key; }
}
