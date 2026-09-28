package com.vulzm.vulzm.client.module.setting;

/** Menyimpan keycode GLFW. */
public class KeybindSetting extends ModuleSetting<Integer> {
    private int keyCode;

    public KeybindSetting(String id, String nameKey, int defaultKey) {
        super(id, nameKey);
        this.keyCode = defaultKey;
    }

    @Override public Integer get() { return keyCode; }
    @Override public void set(Integer v) { this.keyCode = v; }
}
